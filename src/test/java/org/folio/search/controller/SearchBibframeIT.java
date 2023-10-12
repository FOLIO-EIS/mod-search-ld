package org.folio.search.controller;

import static org.folio.search.sample.SampleBibframe.getBibframeSampleAsMap;
import static org.folio.search.sample.SampleBibframe.getBibframeSampleId;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.folio.search.domain.dto.Bibframe;
import org.folio.search.support.base.BaseIntegrationTest;
import org.folio.spring.test.type.IntegrationTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@IntegrationTest
class SearchBibframeIT extends BaseIntegrationTest {

  @BeforeAll
  static void prepare() {
    setUpTenant(Bibframe.class, getBibframeSampleAsMap());
  }

  @AfterAll
  static void cleanUp() {
    removeTenant();
  }

  @DisplayName("search by bibframe (single bibframe is found)")
  @ParameterizedTest(name = "[{0}] {1}")
  @CsvSource({
    "1, cql.allRecords = 1",
    "2, title all \"titleAbc\"",
    "3, title any \"titleAbc\"",
    "4, title any \"def\"",
    "5, title any \"titleAbc def\"",
    "6, title any \"titleAbc XXX\"",
    "7, title = \"titleAbc\"",
    "8, title = \"titleAbc def\"",
    "9, title == \"titleAbc def\"",
    "10, title <> \"titleXXX\"",
    "11, title ==/string \"titleAbc def\"",
    "12, title = \"title*\"",
    "13, title = \"*\"",
    "14, isbn = \"*\"",
    "15, isbn = \"1234567890123\"",
    "16, isbn = \"1234*\"",
    "17, isbn == \"1234567890123\"",
    "18, isbn ==/string \"1234567890123\"",
    "19, isbn any \"1234567890123\"",
    "20, isbn any \"1234567890123 XXX\"",
    "21, isbn all \"1234567890123\"",
    "22, isbn <> \"1234\"",
    "23, lccn = \"*\"",
    "24, lccn = \"20232023\"",
    "25, lccn = \"2023*\"",
    "26, lccn == \"20232023\"",
    "27, lccn ==/string \"20232023\"",
    "28, lccn any \"20232023\"",
    "29, lccn any \"20232023 XXX\"",
    "30, lccn all \"20232023\"",
    "31, lccn <> \"2023\""
  })
  void searchByBibframe_parameterized_singleResult(int index, String query) throws Throwable {
    doSearchByBibframe(query)
      .andExpect(jsonPath("$.totalRecords", is(1)))
      .andExpect(jsonPath("$.content[0].id", is(getBibframeSampleId())));
  }

  @DisplayName("search by bibframe (nothing is found)")
  @ParameterizedTest(name = "[{0}] {1}")
  @CsvSource({
    "1, title ==/string \"titleAbc\"",
    "2, title ==/string \"def\"",
    "3, title == \"def titleAbc\"",
    "4, title == \"titleAbcdef def\"",
    "5, title all \"titleAbcdef\"",
    "6, title any \"titleAbcdef\"",
    "7, title = \"titleAbcdef\"",
    "8, title <> \"titleAbc\"",
    "9, isbn ==/string \"1234\"",
    "10, isbn == \"1234\"",
    "11, isbn any \"1234\"",
    "12, isbn any \"12345678901231\"",
    "13, isbn all \"1234\"",
    "14, isbn = \"1234\"",
    "15, isbn <> \"1234567890123\"",
    "16, lccn ==/string \"2023\"",
    "17, lccn == \"2023\"",
    "18, lccn any \"2023\"",
    "19, lccn any \"202320231\"",
    "20, lccn all \"2023\"",
    "21, lccn = \"2023\"",
    "22, lccn <> \"20232023\""
  })
  void searchByBibframe_parameterized_zeroResults(int index, String query) throws Throwable {
    doSearchByBibframe(query)
      .andExpect(jsonPath("$.totalRecords", is(0)));
  }

}
