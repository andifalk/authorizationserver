package com.example.authorizationserver.oauth.client;

import com.example.authorizationserver.oauth.client.dao.RegisteredClientRepository;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegisteredClientService {

  private final RegisteredClientRepository registeredClientRepository;

  public RegisteredClientService(RegisteredClientRepository registeredClientRepository) {
    this.registeredClientRepository = registeredClientRepository;
  }

  public RegisteredClient findOneByClientId(String clientId) {
    return registeredClientRepository.findOneByClientId(clientId);
  }

  public List<RegisteredClient> findAll() {
    return registeredClientRepository.findAll();
  }

  public RegisteredClient save(RegisteredClient entity) {
    return registeredClientRepository.save(entity);
  }

  public void deleteByClientId(String clientId) {
    registeredClientRepository.deleteByClientId(clientId);
  }
}
