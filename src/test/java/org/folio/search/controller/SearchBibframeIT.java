package org.folio.search.controller;

import static org.folio.search.sample.SampleBibframe.getBibframe2SampleAsMap;
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
    setUpTenant(Bibframe.class, 2, getBibframeSampleAsMap(), getBibframe2SampleAsMap());
  }

  @AfterAll
  static void cleanUp() {
    removeTenant();
  }

  @DisplayName("search by bibframe (all 2 bibframe are found)")
  @ParameterizedTest(name = "[{0}] {1}")
  @CsvSource({
    "1, cql.allRecords = 1",
    "2, title all \"titleAbc\"",
    "3, title any \"titleAbc\"",
    "4, title any \"titleAbc def\"",
    "5, title any \"titleAbc XXX\"",
    "6, title = \"titleAbc\"",
    "7, title <> \"titleXXX\"",
    "8, title = \"title*\"",
    "9, title = \"*\"",
    "10, isbn <> \"1234\"",
    "11, lccn <> \"2023\"",
    "12, contributor all \"common\"",
    "13, contributor any \"common\"",
    "16, contributor = \"common\"",
    "17, contributor <> \"commonXXX\"",
    "18, contributor = \"com*\"",
    "19, contributor = \"*\"",
    "20, (title all \"titleAbc\") sortBy title",
    "21, title all \"titleAbc\" sortBy title",
    "22, title all \"titleAbc\" sortBy title/sort.ascending",
    "23, title all \"titleAbc\" sortBy title/sort.descending",
  })
  void searchByBibframe_parameterized_allResults(int index, String query) throws Throwable {
    var asc = query.contains("titleAbc def") || query.contains("sortBy") && !query.contains("descending");
    doSearchByBibframe(query)
      .andExpect(jsonPath("$.totalRecords", is(2)))
      .andExpect(jsonPath("$.content[0].titles[0].value", is(asc ? "titleAbc def" : "titleAbc xyz")))
      .andExpect(jsonPath("$.content[1].titles[0].value", is(asc ? "titleAbc xyz" : "titleAbc def")));
  }

  @DisplayName("search by bibframe (single bibframe is found)")
  @ParameterizedTest(name = "[{0}] {1}")
  @CsvSource({
    "1, title any \"def\"",
    "2, title = \"titleAbc def\"",
    "3, title == \"titleAbc def\"",
    "4, title ==/string \"titleAbc def\"",
    "5, isbn = \"*\"",
    "6, isbn = \"1234567890123\"",
    "7, isbn = \"1234*\"",
    "8, isbn == \"1234567890123\"",
    "9, isbn ==/string \"1234567890123\"",
    "10, isbn any \"1234567890123\"",
    "11, isbn any \"1234567890123 XXX\"",
    "12, isbn all \"1234567890123\"",
    "13, lccn = \"*\"",
    "14, lccn = \"2023202345\"",
    "15, lccn = \"2023*\"",
    "16, lccn == \"2023202345\"",
    "17, lccn ==/string \"2023202345\"",
    "18, lccn any \"2023202345\"",
    "19, lccn any \"2023202345 XXX\"",
    "20, lccn all \"2023202345\"",
    "21, contributor = Family",
    "22, contributor == Meeting",
    "23, contributor ==/string Organization",
    "24, contributor any Person",
    "25, contributor all Family"
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
    "3, title ==/string \"xyz\"",
    "4, title == \"def titleAbc\"",
    "5, title == \"titleAbcdef def\"",
    "6, title all \"titleAbcdef\"",
    "7, title any \"titleAbcdef\"",
    "8, title = \"titleAbcdef\"",
    "9, title <> \"titleAbc\"",
    "10, isbn ==/string \"1234\"",
    "11, isbn == \"1234\"",
    "12, isbn any \"1234\"",
    "13, isbn any \"12345678901231\"",
    "14, isbn all \"1234\"",
    "15, isbn = \"1234\"",
    "16, lccn ==/string \"2023\"",
    "17, lccn == \"2023\"",
    "18, lccn any \"2023\"",
    "19, lccn any \"202320231\"",
    "20, lccn all \"2023\"",
    "21, lccn = \"2023\"",
    "22, contributor ==/string \"Famil\"",
    "23, contributor ==/string \"Meeting1\"",
    "24, contributor ==/string \"rganizatio\"",
    "25, contributor == \"Person common\"",
    "26, contributor == \"common Person\"",
    "27, contributor all \"comm\"",
    "28, contributor any \"comm\"",
    "29, contributor = \"comm\"",
    "30, contributor <> \"common\"",
  })
  void searchByBibframe_parameterized_zeroResults(int index, String query) throws Throwable {
    doSearchByBibframe(query)
      .andExpect(jsonPath("$.totalRecords", is(0)));
  }

}
