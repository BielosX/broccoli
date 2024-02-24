package org.broccoli.repomanager.repositories;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import org.github.client.api.ReposApi;
import org.github.client.api.UsersApi;
import org.github.client.auth.HttpBearerAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RepositoriesConfiguration {

  private final static List<Module> MODULES = List.of(
          new JavaTimeModule()
  );

  @Value("${github.url}")
  private String githubUrl;

  @Value("${github.token}")
  private String token;

  private Feign.Builder feignCommon() {
    final HttpBearerAuth bearerAuth = new HttpBearerAuth("Bearer");
    bearerAuth.setBearerToken(token);
    return Feign.builder()
            .client(new OkHttpClient())
            .decoder(new JacksonDecoder(MODULES))
            .encoder(new JacksonEncoder(MODULES))
            .requestInterceptor(bearerAuth);
  }

  @Bean
  public ReposApi reposApi() {
    return feignCommon()
            .target(ReposApi.class, githubUrl);
  }

  @Bean
  public UsersApi usersApi() {
    return feignCommon()
            .target(UsersApi.class, githubUrl);
  }
}
