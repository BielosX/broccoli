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
  public ResponseEntity<Void> createNewRepository(final Optional<CreateRepository> createRepository) {
    return RepositoriesApi.super.createNewRepository(createRepository);
  }

  @Override
  public ResponseEntity<Repositories> getRepositories(final Optional<List<String>> language,
                                                      final Optional<Integer> stargazersGreaterThan,
                                                      final Optional<Integer> watchersGreaterThan) {
    return RepositoriesApi.super.getRepositories(language, stargazersGreaterThan, watchersGreaterThan);
  }

  @Override
  public ResponseEntity<Repository> getRepositoryByName(final String name) {
    log.info("getRepositoryByName");
    final Repository repository = repositoriesService.getRepository(name);
    return ResponseEntity.ok(repository);
  }
}
