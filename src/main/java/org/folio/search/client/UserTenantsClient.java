package org.folio.search.client;

import static org.folio.search.utils.Constants.FOLIO_PROFILE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("user-tenants")
@Profile(FOLIO_PROFILE)
public interface UserTenantsClient {

  @GetMapping(produces = APPLICATION_JSON_VALUE)
  UserTenants getUserTenants(@RequestParam("tenantId") String tenantId);

  record UserTenants(List<UserTenant> userTenants) { }

  record UserTenant(String centralTenantId) { }
}
