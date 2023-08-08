package org.folio.search.configuration.properties;

import static org.folio.search.utils.Constants.FOLIO_PROFILE;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile(FOLIO_PROFILE)
@Getter
public class OkapiConfigurationProperties {
  private final String okapiUrl;

  public OkapiConfigurationProperties(@Value("${okapi.url}") String okapiUrl) {
    this.okapiUrl = okapiUrl;
  }
}
