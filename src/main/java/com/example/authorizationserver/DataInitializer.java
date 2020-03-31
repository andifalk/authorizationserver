package com.example.authorizationserver;

import com.example.authorizationserver.oauth.client.dao.RegisteredClientRepository;
import com.example.authorizationserver.oauth.client.model.AccessTokenFormat;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.user.dao.UserRepository;
import com.example.authorizationserver.user.model.Address;
import com.example.authorizationserver.user.model.Gender;
import com.example.authorizationserver.user.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataInitializer implements CommandLineRunner {

  private static final Logger LOG = LoggerFactory.getLogger(DataInitializer.class);

  private final UserRepository userRepository;
  private final RegisteredClientRepository registeredClientRepository;

  private final PasswordEncoder passwordEncoder;

  public DataInitializer(
      UserRepository userRepository,
      RegisteredClientRepository registeredClientRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.registeredClientRepository = registeredClientRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  @Override
  public void run(String... args) {
    long countUsers = this.userRepository.count();
    if (countUsers == 0) {
      createUsers();
    }
    long countClients = this.registeredClientRepository.count();
    if (countClients == 0) {
      createClients();
    }
  }

  private void createUsers() {
    Set<User> users =
        Stream.of(
                new User(
                    UUID.randomUUID(),
                    Gender.MALE,
                    "Bruce",
                    "Wayne",
                    passwordEncoder.encode("wayne"),
                    "bruce.wayne@example.com",
                    "bwayne",
                    "0711-1234567",
                    Collections.singleton("library_user"),
                    new Address("Batmanstr.1", "70177", "Gotham", "N/A", "USA"),
                    LocalDateTime.now()),
                new User(
                    UUID.randomUUID(),
                    Gender.MALE,
                    "Clark",
                    "Kent",
                    passwordEncoder.encode("kent"),
                    "clark.kent@example.com",
                    "ckent",
                    "0711-222222",
                    Collections.singleton("library_admin"),
                    new Address("Batmanstr.1", "70177", "Gotham", "N/A", "USA"),
                    LocalDateTime.now()),
                new User(
                    UUID.randomUUID(),
                    Gender.MALE,
                    "Peter",
                    "Parker",
                    passwordEncoder.encode("parker"),
                    "peter.parker@example.com",
                    "pparker",
                    "0711-1234567",
                    Collections.singleton("library_curator"),
                    new Address("Batmanstr.1", "70177", "Gotham", "N/A", "USA"),
                    LocalDateTime.now()),
                new User(
                    UUID.randomUUID(),
                    Gender.MALE,
                    "Max",
                    "Root",
                    passwordEncoder.encode("admin"),
                    "max.root@example.com",
                    "admin",
                    "0711-1234567",
                    Collections.singleton("admin"),
                    new Address(
                        "Batmanstr.1", "70159", "Stuttgart", "Baden-Württemberg", "Germany"),
                    LocalDateTime.now()))
            .map(userRepository::save)
            .collect(Collectors.toSet());

    LOG.info("Created {} users", users.size());
  }

  private void createClients() {
    Set<RegisteredClient> registeredClients =
        Stream.of(
                new RegisteredClient(
                    UUID.randomUUID(),
                    "confidential-jwt",
                    passwordEncoder.encode("demo"),
                    true,
                    AccessTokenFormat.JWT,
                    Set.of(GrantType.AUTHORIZATION_CODE, GrantType.CLIENT_CREDENTIALS),
                    Collections.singleton(
                        "http://localhost:9090/demo-client/login/oauth2/code/demo"),
                    Collections.singleton("*")),
                new RegisteredClient(
                    UUID.randomUUID(),
                    "public-jwt",
                    passwordEncoder.encode("n/a"),
                    false,
                    AccessTokenFormat.JWT,
                    Set.of(GrantType.AUTHORIZATION_CODE),
                    Collections.singleton(
                        "http://localhost:9090/demo-client/login/oauth2/code/demo"),
                    Collections.singleton("*")),
                new RegisteredClient(
                    UUID.randomUUID(),
                    "confidential-opaque",
                    passwordEncoder.encode("demo"),
                    true,
                    AccessTokenFormat.OPAQUE,
                    Set.of(GrantType.AUTHORIZATION_CODE, GrantType.CLIENT_CREDENTIALS),
                    Collections.singleton(
                        "http://localhost:9090/demo-client/login/oauth2/code/demo"),
                    Collections.singleton("*")),
                new RegisteredClient(
                    UUID.randomUUID(),
                    "public-opaque",
                    passwordEncoder.encode("n/a"),
                    false,
                    AccessTokenFormat.OPAQUE,
                    Set.of(GrantType.AUTHORIZATION_CODE),
                    Collections.singleton(
                        "http://localhost:9090/demo-client/login/oauth2/code/demo"),
                    Collections.singleton("*")))
            .map(registeredClientRepository::save)
            .collect(Collectors.toSet());

    LOG.info("Created {} clients", registeredClients.size());
  }
}
