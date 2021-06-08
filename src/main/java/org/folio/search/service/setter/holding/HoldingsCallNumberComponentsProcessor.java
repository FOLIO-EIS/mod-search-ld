package org.folio.search.service.setter.holding;

import static java.util.stream.Collectors.toSet;
import static org.folio.search.utils.SearchUtils.getEffectiveCallNumber;
import static org.folio.search.utils.SearchUtils.toSafeStream;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.folio.search.domain.dto.Instance;
import org.folio.search.service.setter.FieldProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HoldingsCallNumberComponentsProcessor implements FieldProcessor<Instance, Set<String>> {

  @Override
  public Set<String> getFieldValue(Instance instance) {
    return toSafeStream(instance.getHoldings())
      .map(hr -> getEffectiveCallNumber(hr.getCallNumberPrefix(), hr.getCallNumber(), hr.getCallNumberSuffix()))
      .filter(StringUtils::isNotBlank)
      .collect(toSet());
  }
}
