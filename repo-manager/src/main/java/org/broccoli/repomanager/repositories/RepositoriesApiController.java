package org.broccoli.repomanager.repositories;

import lombok.extern.slf4j.Slf4j;
import org.broccoli.api.RepositoriesApi;
import org.broccoli.model.Repositories;
import org.broccoli.model.Repository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
public class RepositoriesApiController implements RepositoriesApi {
  @Override
  public ResponseEntity<Void> createNewRepository() {
    log.info("createNewRepository");
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Repositories> getRepositories() {
    log.info("getRepositories");
    return ResponseEntity.ok().build();
  }

  @Override
  public ResponseEntity<Repository> getRepositoryByName(String name) {
    log.info("getRepositoryByName");
    return ResponseEntity.ok().build();
  }
}
