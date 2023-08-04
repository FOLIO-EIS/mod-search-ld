package org.folio.search.configuration;

import static org.folio.search.utils.Constants.FOLIO_PROFILE;
import static org.springframework.http.ResponseEntity.notFound;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.folio.search.client.UserTenantsClient;
import org.folio.search.model.context.FolioExecutionContextBuilder;
import org.folio.search.service.TenantScopedExecutionService;
import org.folio.search.service.consortia.TenantProvider;
import org.folio.spring.liquibase.FolioSpringLiquibase;
import org.folio.spring.scope.FolioExecutionContextSetter;
import org.folio.tenant.domain.dto.TenantAttributes;
import org.folio.tenant.rest.resource.TenantApi;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.ResponseEntity;

@Log4j2
@Configuration
@RequiredArgsConstructor
@Profile("!" + FOLIO_PROFILE)
public class StandaloneConfig implements ApplicationListener<ContextRefreshedEvent> {

  private final FolioSpringLiquibase folioSpringLiquibase;
  @Value("${mod-search.default-schema}")
  private String defaultSchema;

  @Override
  @SneakyThrows
  public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
    log.log(Level.INFO, "Standalone mode is on, activating default DB schema [{}]", defaultSchema);
    folioSpringLiquibase.setDefaultSchema(defaultSchema);
    folioSpringLiquibase.performLiquibaseUpdate();
  }

  @Bean(name = "folioTenantController")
  @Primary
  public TenantApi dummyTenantController() {
    return new TenantApi() {
      @Override
      public ResponseEntity<Void> deleteTenant(String operationId) {
        return notFound().build();
      }

      @Override
      public ResponseEntity<String> getTenant(String operationId) {
        return notFound().build();
      }

      @Override
      public ResponseEntity<Void> postTenant(@Valid TenantAttributes tenantAttributes) {
        return notFound().build();
      }
    };
  }

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

}
