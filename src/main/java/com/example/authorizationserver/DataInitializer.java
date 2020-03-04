package com.example.authorizationserver;

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

  private final PasswordEncoder passwordEncoder;

  public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  @Override
  public void run(String... args) throws Exception {
    createUsers();
  }

  private void createUsers() {
    Set<User> users = Stream.of(
            new User(UUID.randomUUID(), Gender.MALE, "Bruce", "Wayne", passwordEncoder.encode("wayne"),
                    "bruce.wayne@example.com", "bwayne", "0711-1234567",
                    Collections.singleton("library_user"), Collections.singleton(new Address("Batmanstr.1", "70177", "Gotham", "N/A", "USA")))
    ).map(userRepository::save).collect(Collectors.toSet());

    LOG.info("Created {} users", users.size());
  }
}
