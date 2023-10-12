package org.folio.search.sample;

import static org.folio.search.utils.JsonConverter.MAP_TYPE_REFERENCE;
import static org.folio.search.utils.TestUtils.readJsonFromFile;

import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SampleBibframe {

  private static final Map<String, Object> BIBFRAME_AS_MAP =
    readJsonFromFile("/samples/bibframe/bibframe.json", MAP_TYPE_REFERENCE);

  public static Map<String, Object> getBibframeSampleAsMap() {
    return BIBFRAME_AS_MAP;
  }

  public static String getBibframeSampleId() {
    return (String) BIBFRAME_AS_MAP.get("id");
  }
}
