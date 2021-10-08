package org.folio.search.cql;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.folio.search.utils.SearchQueryUtils.isBoolQuery;
import static org.folio.search.utils.SearchQueryUtils.isDisjunctionFilterQuery;
import static org.folio.search.utils.SearchQueryUtils.isFilterQuery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.folio.search.exception.SearchServiceException;
import org.folio.search.model.types.SearchType;
import org.folio.search.service.metadata.SearchFieldProvider;
import org.springframework.stereotype.Component;
import org.z3950.zing.cql.CQLBooleanNode;
import org.z3950.zing.cql.CQLNode;
import org.z3950.zing.cql.CQLParseException;
import org.z3950.zing.cql.CQLParser;
import org.z3950.zing.cql.CQLSortNode;
import org.z3950.zing.cql.CQLTermNode;

/**
 * Convert a CQL query into a elasticsearch query.
 *
 * <p> Contextual Query Language (CQL) Specification:
 * <a href="https://www.loc.gov/standards/sru/cql/spec.html">https://www.loc.gov/standards/sru/cql/spec.html</a>
 * </p>
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class CqlSearchQueryConverter {

  private final CqlSortProvider cqlSortProvider;
  private final CqlTermQueryConverter cqlTermQueryConverter;
  private final SearchFieldProvider searchFieldProvider;

  /**
   * Converts given CQL search query value to the elasticsearch {@link SearchSourceBuilder} object.
   *
   * @param query cql query to parse
   * @param resource resource name
   * @return search source as {@link SearchSourceBuilder} object with query and sorting conditions
   */
  public SearchSourceBuilder convert(String query, String resource) {
    try {
      var cqlNode = new CQLParser().parse(query);
      return toCriteria(cqlNode, resource);
    } catch (CQLParseException | IOException e) {
      throw new SearchServiceException(String.format(
        "Failed to parse cql query [cql: '%s', resource: %s]", query, resource), e);
    }
  }

  private SearchSourceBuilder toCriteria(CQLNode node, String resource) {
    var queryBuilder = new SearchSourceBuilder();

    if (node instanceof CQLSortNode) {
      cqlSortProvider.getSort((CQLSortNode) node, resource).forEach(queryBuilder::sort);
    }

    var boolQuery = convertToQuery(node, resource);
    return queryBuilder.query(enhanceQuery(boolQuery, resource));
  }

  private QueryBuilder convertToQuery(CQLNode node, String resource) {
    var cqlNode = node;
    if (node instanceof CQLSortNode) {
      cqlNode = ((CQLSortNode) node).getSubtree();
    }
    if (cqlNode instanceof CQLTermNode) {
      return cqlTermQueryConverter.getQuery((CQLTermNode) cqlNode, resource);
    }
    if (cqlNode instanceof CQLBooleanNode) {
      return convertToBoolQuery((CQLBooleanNode) cqlNode, resource);
    }
    throw new UnsupportedOperationException(String.format(
      "Failed to parse CQL query. Node with type '%s' is not supported.", node.getClass().getSimpleName()));
  }

  private BoolQueryBuilder convertToBoolQuery(CQLBooleanNode node, String resource) {
    var operator = node.getOperator();
    switch (operator) {
      case OR:
        return flattenBoolQuery(node, resource, BoolQueryBuilder::should);
      case AND:
        return flattenBoolQuery(node, resource, BoolQueryBuilder::must);
      case NOT:
        return boolQuery()
          .must(convertToQuery(node.getLeftOperand(), resource))
          .mustNot(convertToQuery(node.getRightOperand(), resource));
      default:
        throw new UnsupportedOperationException(String.format(
          "Failed to parse CQL query. Operator '%s' is not supported.", operator.name()));
    }
  }

  private BoolQueryBuilder flattenBoolQuery(CQLBooleanNode node, String resource,
    Function<BoolQueryBuilder, List<QueryBuilder>> conditionProvider) {
    var rightOperandQuery = convertToQuery(node.getRightOperand(), resource);
    var leftOperandQuery = convertToQuery(node.getLeftOperand(), resource);

    var boolQuery = boolQuery();
    if (isBoolQuery(leftOperandQuery)) {
      var leftOperandBoolQuery = (BoolQueryBuilder) leftOperandQuery;
      var leftBoolQueryConditions = conditionProvider.apply(leftOperandBoolQuery);
      if (leftBoolQueryConditions.size() >= 2) {
        var resultBoolQueryConditions = conditionProvider.apply(boolQuery);
        resultBoolQueryConditions.addAll(leftBoolQueryConditions);
        resultBoolQueryConditions.add(rightOperandQuery);
        return boolQuery;
      }
    }

    var conditions = conditionProvider.apply(boolQuery);
    conditions.add(leftOperandQuery);
    conditions.add(rightOperandQuery);
    return boolQuery;
  }

  private QueryBuilder enhanceQuery(QueryBuilder query, String resource) {
    Predicate<String> filterFieldCheck = field -> isFilterField(field, resource);
    if (isDisjunctionFilterQuery(query, filterFieldCheck)) {
      return boolQuery().filter(query);
    }
    if (isBoolQuery(query)) {
      return enhanceBoolQuery((BoolQueryBuilder) query, filterFieldCheck);
    }

    return isFilterQuery(query, filterFieldCheck) ? boolQuery().filter(query) : query;
  }

  private BoolQueryBuilder enhanceBoolQuery(BoolQueryBuilder query, Predicate<String> filterFieldPredicate) {
    var mustQueryConditions = new ArrayList<QueryBuilder>();
    var mustConditions = query.must();
    for (var innerQuery : mustConditions) {
      if (isFilterInnerQuery(innerQuery, filterFieldPredicate)) {
        query.filter(innerQuery);
      } else {
        mustQueryConditions.add(innerQuery);
      }
    }
    mustConditions.clear();
    mustConditions.addAll(mustQueryConditions);
    return query;
  }

  private boolean isFilterInnerQuery(QueryBuilder query, Predicate<String> filterFieldPredicate) {
    return isFilterQuery(query, filterFieldPredicate) || isDisjunctionFilterQuery(query, filterFieldPredicate);
  }

  private boolean isFilterField(String fieldName, String resource) {
    return searchFieldProvider.getPlainFieldByPath(resource, fieldName)
      .filter(fieldDescription -> fieldDescription.hasType(SearchType.FILTER))
      .isPresent();
  }
}
