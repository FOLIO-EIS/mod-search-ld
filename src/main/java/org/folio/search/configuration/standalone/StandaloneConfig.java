package org.folio.search.configuration.standalone;

import static org.folio.search.utils.Constants.FOLIO_PROFILE;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.folio.search.client.UserTenantsClient;
import org.folio.search.model.context.FolioExecutionContextBuilder;
import org.folio.search.service.TenantScopedExecutionService;
import org.folio.search.service.consortia.TenantProvider;
import org.folio.spring.scope.FolioExecutionContextSetter;
import org.folio.spring.tools.client.UsersClient;
import org.folio.spring.tools.systemuser.PrepareSystemUserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Log4j2
@Configuration
@RequiredArgsConstructor
@Profile("!" + FOLIO_PROFILE)
public class StandaloneConfig {

  @Value("${mod-search.default-schema}")
  private String defaultSchema;

  @Bean
  @Primary
  public TenantProvider tenantProvider() {
    return tenantId -> defaultSchema;
  }

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

  @Bean
  @Primary
  public PrepareSystemUserService prepareSystemUserService() {
    return new PrepareSystemUserService(null, null, null, null) {
      @Override
      public void setupSystemUser() {

      }

      @Override
      public Optional<UsersClient.User> getFolioUser(String username) {
        return Optional.empty();
      }
    };
  }
}
