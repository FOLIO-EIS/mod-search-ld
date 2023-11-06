package org.folio.search.service.setter.bibframe;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toCollection;
import static org.folio.search.domain.dto.BibframeIdentifiersInner.TypeEnum.ISBN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.folio.search.domain.dto.Bibframe;
import org.folio.search.domain.dto.BibframeIdentifiersInner;
import org.folio.search.service.setter.FieldProcessor;
import org.folio.search.service.setter.instance.IsbnProcessor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BibframeIsbnProcessor implements FieldProcessor<Bibframe, Set<String>> {

  private final IsbnProcessor isbnProcessor;

  @Override
  public Set<String> getFieldValue(Bibframe bibframe) {
    if (isNull(bibframe.getIdentifiers())) {
      return new HashSet<>();
    }
    return bibframe.getIdentifiers().stream()
      .filter(i -> ISBN.equals(i.getType()))
      .map(BibframeIdentifiersInner::getValue)
      .map(this::normalizeIsbn)
      .flatMap(Collection::stream)
      .collect(toCollection(LinkedHashSet::new));
  }

  public List<String> normalizeIsbn(String value) {
    if (isNull(value)) {
      return new ArrayList<>();
    }
    return isbnProcessor.normalizeIsbn(value);
  }

}
