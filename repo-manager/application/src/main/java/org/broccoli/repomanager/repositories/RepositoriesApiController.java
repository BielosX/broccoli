package org.broccoli.repomanager.repositories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.broccoli.api.RepositoriesApi;
import org.broccoli.model.CreateRepository;
import org.broccoli.model.Repositories;
import org.broccoli.model.Repository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RepositoriesApiController implements RepositoriesApi {
  private final RepositoriesService repositoriesService;

  @Override
  public ResponseEntity<Void> createNewRepository(Optional<CreateRepository> createRepository) {
    return RepositoriesApi.super.createNewRepository(createRepository);
  }

  @Override
  public ResponseEntity<Repositories> getRepositories(Optional<List<String>> language, Optional<Integer> stargazersGreaterThan, Optional<Integer> watchersGreaterThan) {
    return RepositoriesApi.super.getRepositories(language, stargazersGreaterThan, watchersGreaterThan);
  }

  @Override
  public ResponseEntity<Repository> getRepositoryByName(String name) {
    log.info("getRepositoryByName");
    Repository repository = repositoriesService.getRepository(name);
    return ResponseEntity.ok(repository);
  }
}
