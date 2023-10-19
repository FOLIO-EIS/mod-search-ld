package org.folio.search.controller;

import lombok.RequiredArgsConstructor;
import org.folio.search.domain.dto.Bibframe;
import org.folio.search.domain.dto.BibframeSearchResult;
import org.folio.search.model.service.CqlSearchRequest;
import org.folio.search.rest.resource.SearchApi;
import org.folio.search.service.SearchService;
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

  @Override
  public ResponseEntity<BibframeSearchResult> searchBibframe(String tenant, String query, Integer limit,
                                                             Integer offset) {
    var searchRequest = CqlSearchRequest.of(
      Bibframe.class, tenant, query, limit, offset, true);
    var result = searchService.search(searchRequest);
    return ResponseEntity.ok(new BibframeSearchResult()
      .searchQuery(query)
      .content(result.getRecords())
      .pageNumber(divPlusOneIfRemainder(offset, limit))
      .totalPages(divPlusOneIfRemainder(result.getTotalRecords(), limit))
      .totalRecords(result.getTotalRecords())
    );
  }

  private int divPlusOneIfRemainder(int one, int two) {
    var modulo = one % two;
    return one / two + (modulo > 0 ? 1 : 0);
  }
}
