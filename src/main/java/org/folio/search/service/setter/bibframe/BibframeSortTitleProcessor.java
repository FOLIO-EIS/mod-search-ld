package org.folio.search.service.setter.bibframe;

import static java.util.Objects.isNull;

import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.folio.search.domain.dto.Bibframe;
import org.folio.search.domain.dto.BibframeTitlesInner;
import org.folio.search.service.setter.FieldProcessor;
import org.springframework.stereotype.Component;

@Component
public class BibframeSortTitleProcessor implements FieldProcessor<Bibframe, String> {

  @Override
  public String getFieldValue(Bibframe bibframe) {
    if (isNull(bibframe.getTitles())) {
      return null;
    }
    return bibframe.getTitles().stream()
      .map(BibframeTitlesInner::getValue)
      .filter(StringUtils::isNotBlank)
      .collect(Collectors.joining());
  }

}
