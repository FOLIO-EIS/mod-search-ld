package org.folio.search.service.setter.bibframe;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toCollection;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.folio.search.domain.dto.Bibframe;
import org.folio.search.domain.dto.BibframeContributorsInner;
import org.folio.search.service.setter.FieldProcessor;
import org.springframework.stereotype.Component;

@Component
public class BibframeContributorProcessor implements FieldProcessor<Bibframe, Set<String>> {

  @Override
  public Set<String> getFieldValue(Bibframe bibframe) {
    if (isNull(bibframe.getContributors())) {
      return new HashSet<>();
    }
    return bibframe.getContributors().stream()
      .map(BibframeContributorsInner::getName)
      .filter(StringUtils::isNotBlank)
      .map(String::trim)
      .collect(toCollection(LinkedHashSet::new));
  }

}
