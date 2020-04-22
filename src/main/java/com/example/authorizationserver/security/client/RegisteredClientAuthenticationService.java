package com.example.authorizationserver.security.client;

import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisteredClientAuthenticationService {

  private final DaoAuthenticationProvider daoAuthenticationProvider;

  public RegisteredClientAuthenticationService(PasswordEncoder passwordEncoder,
                                               @Qualifier("registeredClientDetailsService") UserDetailsService userDetailsService) {
    this.daoAuthenticationProvider = new DaoAuthenticationProvider();
    this.daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
    this.daoAuthenticationProvider.setUserDetailsService(userDetailsService);
  }

  public RegisteredClient authenticate(String username, String password) throws AuthenticationException {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    Authentication authentication = this.daoAuthenticationProvider.authenticate(authenticationToken);
    return (RegisteredClient) authentication.getPrincipal();
  }
}
