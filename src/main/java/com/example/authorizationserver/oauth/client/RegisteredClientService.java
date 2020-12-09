package com.example.authorizationserver.oauth.client;

import com.example.authorizationserver.oauth.client.dao.RegisteredClientRepository;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Transactional(readOnly = true)
@Service
public class RegisteredClientService {

  private final RegisteredClientRepository registeredClientRepository;

  public RegisteredClientService(RegisteredClientRepository registeredClientRepository) {
    this.registeredClientRepository = registeredClientRepository;
  }

  public Optional<RegisteredClient> findOneByClientId(String clientId) {
    return registeredClientRepository.findOneByClientId(clientId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  public List<RegisteredClient> findAll() {
    return registeredClientRepository.findAll();
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public RegisteredClient create(RegisteredClient entity) {
    return registeredClientRepository.save(entity);
  }

  @PreAuthorize("hasRole('ADMIN')")
  public Optional<RegisteredClient> findOneByIdentifier(UUID identifier) {
    return registeredClientRepository.findOneByIdentifier(identifier);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public void deleteOneByIdentifier(UUID identifier) {
    registeredClientRepository.deleteOneByIdentifier(identifier);
  }

  @Transactional
  public void deleteByClientId(String clientId) {
    registeredClientRepository.deleteByClientId(clientId);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public Optional<RegisteredClient> update(UUID clientId, RegisteredClient registeredClientForUpdate) {
    return findOneByIdentifier(clientId).map(c -> {
      c.setAccessTokenFormat((registeredClientForUpdate.getAccessTokenFormat()));
      c.setClientSecret(registeredClientForUpdate.getClientSecret());
      c.setConfidential(registeredClientForUpdate.isConfidential());
      c.setCorsUris(registeredClientForUpdate.getCorsUris());
      c.setGrantTypes(registeredClientForUpdate.getGrantTypes());
      c.setRedirectUris(registeredClientForUpdate.getRedirectUris());
      return Optional.of(registeredClientRepository.save(c));
    }).orElse(Optional.empty());
  }
}
