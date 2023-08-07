package org.folio.search.configuration.standalone;

import static org.folio.search.utils.Constants.FOLIO_PROFILE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.folio.search.client.AuthnClient;
import org.folio.spring.integration.XOkapiHeaders;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
@Profile("!" + FOLIO_PROFILE)
public class StandaloneDummyOkapiController {

  @PostMapping(value = "/login", consumes = APPLICATION_JSON_VALUE)
  ResponseEntity<String> getApiKey(@RequestBody AuthnClient.UserCredentials credentials) {
    var headers = new HttpHeaders();
    headers.add(XOkapiHeaders.TOKEN, "aa.bb.cc");
    return new ResponseEntity<>(headers, HttpStatusCode.valueOf(200));
  }

}
