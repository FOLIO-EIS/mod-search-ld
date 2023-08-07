package org.folio.search.service;

import java.util.concurrent.Callable;
import lombok.SneakyThrows;
import org.springframework.scheduling.annotation.Async;

public interface TenantScopedExecutionService {

  /**
   * Executes given job tenant scoped.
   *
   * @param tenantId - The tenant name.
   * @param job - Job to be executed in tenant scope.
   * @param <T> - Optional return value for the job.
   * @return Result of job.
   * @throws RuntimeException - Wrapped exception from the job.
   */
  @SneakyThrows
  <T> T executeTenantScoped(String tenantId, Callable<T> job);

  /**
   * Executes given job in scope of tenant asynchronously.
   *
   * @param tenantId - The tenant name.
   * @param job - Job to be executed in tenant scope.
   */
  @Async
  void executeAsyncTenantScoped(String tenantId, Runnable job);
}
