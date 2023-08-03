package org.folio.search.configuration;

import static org.folio.search.utils.Constants.FOLIO_PROFILE;

import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.folio.search.client.UserTenantsClient;
import org.folio.search.model.context.FolioExecutionContextBuilder;
import org.folio.search.service.TenantScopedExecutionService;
import org.folio.spring.scope.FolioExecutionContextSetter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile("!" + FOLIO_PROFILE)
public class StandaloneConfig {

  @Bean
  public UserTenantsClient userTenantsClient() {
    return tenantId -> new UserTenantsClient.UserTenants(List.of(new UserTenantsClient.UserTenant(tenantId)));
  }

  @Bean
  public TenantScopedExecutionService tenantScopedExecutionService(FolioExecutionContextBuilder contextBuilder) {
    return new TenantScopedExecutionService() {
      @Override
      @SneakyThrows
      public <T> T executeTenantScoped(String tenantId, Callable<T> job) {
        try (var fex = new FolioExecutionContextSetter(contextBuilder.dbOnlyContext(tenantId))) {
          return job.call();
        }
      }

      @Override
      public void executeAsyncTenantScoped(String tenantId, Runnable job) {
        try (var fex = new FolioExecutionContextSetter(contextBuilder.dbOnlyContext(tenantId))) {
          job.run();
        }
      }
    };
  }

}
