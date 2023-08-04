package org.folio.search.service.setter.bibframe;

import static java.util.stream.Collectors.toCollection;
import static org.folio.search.domain.dto.BibframeIdentifiersInner.TypeEnum.LCCN;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import org.folio.search.domain.dto.Bibframe;
import org.folio.search.domain.dto.BibframeIdentifiersInner;
import org.folio.search.service.setter.FieldProcessor;
import org.springframework.stereotype.Component;

@Component
public class BibframeLccnProcessor implements FieldProcessor<Bibframe, Set<String>> {

  @Override
  public Set<String> getFieldValue(Bibframe bibframe) {
    return bibframe.getIdentifiers().stream()
      .filter(i -> i.getType().equals(LCCN))
      .map(BibframeIdentifiersInner::getValue)
      .filter(Objects::nonNull)
      .map(String::trim)
      .collect(toCollection(LinkedHashSet::new));
  }

}
