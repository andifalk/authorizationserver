package com.example.authorizationserver.oauth.client.dao;

import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RegisteredClientRepository extends JpaRepository<RegisteredClient, Long> {

  Optional<RegisteredClient> findOneByClientId(String clientId);

  Optional<RegisteredClient> findOneByIdentifier(UUID identifier);

  void deleteOneByIdentifier(UUID identifier);

  void deleteByClientId(String clientId);
}
