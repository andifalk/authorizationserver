package com.example.authorizationserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;

@Configuration
public class IdGeneratorConfiguration {

  @Bean
  public IdGenerator idGenerator() {
    return new JdkIdGenerator();
  }
}
