package org.folio.search.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.folio.search.domain.dto.LanguageConfig;
import org.folio.search.exception.ValidationException;
import org.folio.search.model.config.LanguageConfigEntity;
import org.folio.search.repository.LanguageConfigRepository;
import org.folio.search.service.metadata.ResourceDescriptionService;
import org.folio.search.utils.types.UnitTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@UnitTest
@ExtendWith(MockitoExtension.class)
class LanguageConfigServiceTest {
  private static final String SUPPORTED_LANGUAGE_CODE = "supported";
  private static final String UNSUPPORTED_LANGUAGE_CODE = "unsupported";
  @Mock
  private LanguageConfigRepository configRepository;
  @Mock
  private ResourceDescriptionService descriptionService;
  @InjectMocks
  private LanguageConfigService configService;

  @Test
  void canAddLanguageConfig() {
    when(configRepository.count()).thenReturn(0L);
    when(descriptionService.isSupportedLanguage(SUPPORTED_LANGUAGE_CODE)).thenReturn(true);
    when(configRepository.save(any(LanguageConfigEntity.class)))
      .thenReturn(new LanguageConfigEntity(UUID.randomUUID(), SUPPORTED_LANGUAGE_CODE));

    final var saveResult = configService.create(new LanguageConfig().code(SUPPORTED_LANGUAGE_CODE));

    assertThat(saveResult.getId(), notNullValue());
    assertThat(saveResult.getCode(), is(SUPPORTED_LANGUAGE_CODE));
  }

  @Test
  void cannotAddConfigIfLanguageIsNotSupported() {
    assertThrows(ValidationException.class,
      () -> configService.create(new LanguageConfig().code(UNSUPPORTED_LANGUAGE_CODE)));
  }

  @Test
  void cannotAddConfigIfThereIsAlready5Languages() {
    when(configRepository.count()).thenReturn(5L);
    when(descriptionService.isSupportedLanguage(SUPPORTED_LANGUAGE_CODE)).thenReturn(true);

    assertThrows(ValidationException.class,
      () -> configService.create(new LanguageConfig().code(SUPPORTED_LANGUAGE_CODE)));
  }
}
