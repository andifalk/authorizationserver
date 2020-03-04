package com.example.authorizationserver.oauth.client.dao;

import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegisteredClientRepository extends JpaRepository<RegisteredClient,Long> {

  RegisteredClient findOneByClientId(String clientId);

  void deleteByClientId(String clientId);

}
