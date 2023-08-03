package org.folio.search.controller;

import static java.util.Objects.nonNull;

import lombok.RequiredArgsConstructor;
import org.folio.search.domain.dto.Authority;
import org.folio.search.domain.dto.AuthoritySearchResult;
import org.folio.search.domain.dto.Bibframe;
import org.folio.search.domain.dto.BibframeSearchResult;
import org.folio.search.domain.dto.Instance;
import org.folio.search.domain.dto.InstanceSearchResult;
import org.folio.search.model.service.CqlSearchRequest;
import org.folio.search.rest.resource.SearchApi;
import org.folio.search.service.SearchService;
import org.folio.search.service.consortia.TenantProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class SearchController implements SearchApi {

  private final SearchService searchService;
  private final TenantProvider tenantProvider;

  @Override
  public ResponseEntity<InstanceSearchResult> searchInstances(String tenantId, String query, Integer limit,
                                                              Integer offset, Boolean expandAll) {
    tenantId = tenantProvider.getTenant(tenantId);
    var searchRequest = CqlSearchRequest.of(Instance.class, tenantId, query, limit, offset, expandAll);
    var result = searchService.search(searchRequest);
    return ResponseEntity.ok(new InstanceSearchResult()
      .instances(result.getRecords())
      .totalRecords(result.getTotalRecords()));
  }

  @Override
  public ResponseEntity<AuthoritySearchResult> searchAuthorities(
    String tenant, String query, Integer limit, Integer offset, Boolean expandAll, Boolean includeNumberOfTitles) {

    tenant = tenantProvider.getTenant(tenant);
    var searchRequest = CqlSearchRequest.of(
      Authority.class, tenant, query, limit, offset, expandAll, includeNumberOfTitles);
    var result = searchService.search(searchRequest);
    return ResponseEntity.ok(new AuthoritySearchResult()
      .authorities(result.getRecords())
      .totalRecords(result.getTotalRecords()));
  }

  @Override
  public ResponseEntity<BibframeSearchResult> searchBibframe(String tenant, String query, Integer limit,
                                                             Integer offset) {
    tenant = tenantProvider.getTenant(tenant);
    limit = nonNull(limit) ? limit : 10;
    var searchRequest = CqlSearchRequest.of(
      Bibframe.class, tenant, query, limit, offset, true, true);
    var result = searchService.search(searchRequest);
    return ResponseEntity.ok(new BibframeSearchResult()
      .searchQuery(query)
      .content(result.getRecords())
      .sortBy("publications.dateOfPublication")
      .sortOrder(BibframeSearchResult.SortOrderEnum.DESC)
      .pageNumber(0)
      .totalPages(result.getTotalRecords() / limit)
      .totalElements(result.getTotalRecords())
    );
  }
}
