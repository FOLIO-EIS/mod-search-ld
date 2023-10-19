package org.folio.search.service.setter.bibframe;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toCollection;
import static org.folio.search.domain.dto.BibframeIdentifiersInner.TypeEnum.LCCN;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.folio.search.domain.dto.Bibframe;
import org.folio.search.domain.dto.BibframeIdentifiersInner;
import org.folio.search.service.setter.FieldProcessor;
import org.springframework.stereotype.Component;

@Component
public class BibframeLccnProcessor implements FieldProcessor<Bibframe, Set<String>> {

  @Override
  public Set<String> getFieldValue(Bibframe bibframe) {
    if (isNull(bibframe.getIdentifiers())) {
      return new HashSet<>();
    }
    return bibframe.getIdentifiers().stream()
      .filter(i -> i.getType().equals(LCCN))
      .map(BibframeIdentifiersInner::getValue)
      .filter(StringUtils::isNotBlank)
      .map(String::trim)
      .collect(toCollection(LinkedHashSet::new));
  }

}
