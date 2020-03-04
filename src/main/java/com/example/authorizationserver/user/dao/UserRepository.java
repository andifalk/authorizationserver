package com.example.authorizationserver.user.dao;

import com.example.authorizationserver.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User,Long> {

  Optional<User> findOneByIdentifier(UUID identifier);
  Optional<User> findOneByUsername(String username);
  Optional<User> findOneByEmail(String email);

}
