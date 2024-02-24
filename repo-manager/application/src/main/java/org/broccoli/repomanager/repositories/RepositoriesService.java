package org.broccoli.repomanager.repositories;

import lombok.RequiredArgsConstructor;
import org.broccoli.model.Repository;
import org.github.client.api.ReposApi;
import org.github.client.api.UsersApi;
import org.github.client.model.FullRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepositoriesService {
  private final ReposApi reposApi;
  private final UsersApi usersApi;

  public Repository getRepository(final String repoName) {
    final String userName = usersApi.usersGetAuthenticated().getLogin();
    final FullRepository response = reposApi.reposGet(userName, repoName);
    return Repository.builder()
            .name(response.getName())
            .stargazers(response.getStargazersCount())
            .watchers(response.getWatchers())
            .build();
  }
}
