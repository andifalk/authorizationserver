package com.example.authorizationserver.security.client;

import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Qualifier("registeredClientDetailsService")
@Service
public class RegisteredClientDetailsService implements UserDetailsService {

  private final RegisteredClientService registeredClientService;

  public RegisteredClientDetailsService(RegisteredClientService registeredClientService) {
    this.registeredClientService = registeredClientService;
  }

  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    RegisteredClient registeredClient = this.registeredClientService.findOneByClientId(username);
    if (registeredClient != null) {
      return new RegisteredClientDetails(registeredClient);
    } else {
      throw new UsernameNotFoundException(String.format("No client found for '%s'", username));
    }
  }
}
