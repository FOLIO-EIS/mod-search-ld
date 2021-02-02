package org.folio.search.controller;

import static org.folio.search.sample.SampleInstances.getSemanticWeb;
import static org.folio.search.support.base.ApiEndpoints.searchInstancesByQuery;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.folio.search.support.base.BaseIntegrationTest;
import org.folio.search.utils.types.IntegrationTest;
import org.junit.jupiter.api.Test;

@IntegrationTest
class SearchInstanceIT extends BaseIntegrationTest {

  @Test
  void canSearchByInstanceId_wildcard() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("id=={value}"), "5bf370e0*a0a39")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id", is(getSemanticWeb().getId())));
  }

  @Test
  void canSearchByTitle_title() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("title all {value}"), "semantic")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id", is(getSemanticWeb().getId())));
  }

  @Test
  void canSearchByTitle_series() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("title all {value}"), "cooperative")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id", is(getSemanticWeb().getId())));
  }

  @Test
  void canSearchByTitle_seriesPartialMatch() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("title all {value}"), "cooperate")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id", is(getSemanticWeb().getId())));
  }

  @Test
  void canSearchByTitle_partOfTitle() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("title all {value}"), "primers")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id", is(getSemanticWeb().getId())));
  }

  @Test
  void canSearchByTitle_alternativeTitle() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("title all {value}"), "alternative")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id", is(getSemanticWeb().getId())));
  }

  @Test
  void canSearchByPublisher_byAbbreviate() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("publisher all {value}"), "MIT")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id", is(getSemanticWeb().getId())));
  }

  @Test
  void canSearchByPublisher_byWord() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("publisher all {value}"), "press")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id", is(getSemanticWeb().getId())));
  }

  @Test
  void canSearchByContributorsName() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("contributors.name all {value}"), "frank")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id",
        is(getSemanticWeb().getId())))
      .andExpect(jsonPath("instances[0].contributors[0].name",
        is("Antoniou, Grigoris")))
      .andExpect(jsonPath("instances[0].contributors[1].name",
        is("Van Harmelen, Frank")));
  }

  @Test
  void canSearchByContributors() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("contributors all {value}"), "grigoris")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id", is(getSemanticWeb().getId())));
  }

  @Test
  void canSearchByKeyword_matchesTitle() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("keyword all {value}"), "primer")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id", is(getSemanticWeb().getId())));
  }

  @Test
  void canSearchBySubjects() throws Exception {
    mockMvc.perform(get(searchInstancesByQuery("subjects all {value}"), "semantic")
      .headers(defaultHeaders()))
      .andExpect(status().isOk())
      .andExpect(jsonPath("totalRecords", is(1)))
      .andExpect(jsonPath("instances[0].id", is(getSemanticWeb().getId())));
  }
}
