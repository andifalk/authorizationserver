package com.example.authorizationserver;

import com.example.authorizationserver.oauth.client.dao.RegisteredClientRepository;
import com.example.authorizationserver.oauth.client.model.AccessTokenFormat;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
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
    createUsers();
    createClients();
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
                    Collections.singleton(
                        new Address("Batmanstr.1", "70177", "Gotham", "N/A", "USA"))),
                new User(
                    UUID.randomUUID(),
                    Gender.MALE,
                    "Clark",
                    "Kent",
                    passwordEncoder.encode("kent"),
                    "clark.kent@example.com",
                    "ckent",
                    "0711-222222",
                    Collections.singleton("library_user"),
                    Collections.singleton(
                        new Address("Batmanstr.1", "70177", "Gotham", "N/A", "USA"))),
                new User(
                    UUID.randomUUID(),
                    Gender.MALE,
                    "Peter",
                    "Parker",
                    passwordEncoder.encode("parker"),
                    "peter.parker@example.com",
                    "pparker",
                    "0711-1234567",
                    Collections.singleton("library_user"),
                    Collections.singleton(
                        new Address("Batmanstr.1", "70177", "Gotham", "N/A", "USA"))),
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
                    Collections.singleton(
                        new Address(
                            "Batmanstr.1", "70159", "Stuttgart", "Baden-Württemberg", "Germany"))))
            .map(userRepository::save)
            .collect(Collectors.toSet());

    LOG.info("Created {} users", users.size());
  }

  private void createClients() {
    Set<RegisteredClient> registeredClients =
        Stream.of(
                new RegisteredClient(
                    UUID.randomUUID(),
                    "confidential-demo",
                    "demo",
                    true,
                    true,
                    true,
                    AccessTokenFormat.JWT,
                    Collections.singleton(
                        "http://localhost:9090/demo-client/login/oauth2/code/demo"),
                    Collections.singleton("*")),
                new RegisteredClient(
                    UUID.randomUUID(),
                    "public-demo",
                    null,
                    false,
                    false,
                    false,
                    AccessTokenFormat.JWT,
                    Collections.singleton(
                        "http://localhost:9090/demo-client/login/oauth2/code/demo"),
                    Collections.singleton("*")),
                new RegisteredClient(
                    UUID.randomUUID(),
                    "opaque-demo",
                    null,
                    false,
                    false,
                    false,
                    AccessTokenFormat.OPAQUE,
                    Collections.singleton(
                        "http://localhost:9090/demo-client/login/oauth2/code/demo"),
                    Collections.singleton("*")))
            .map(registeredClientRepository::save)
            .collect(Collectors.toSet());

    LOG.info("Created {} clients", registeredClients.size());
  }
}
