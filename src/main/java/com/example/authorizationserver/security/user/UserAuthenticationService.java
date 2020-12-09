package com.example.authorizationserver.security.user;

import com.example.authorizationserver.scim.model.ScimUserEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationService {

  private final DaoAuthenticationProvider daoAuthenticationProvider;

  public UserAuthenticationService(PasswordEncoder passwordEncoder,
                                   @Qualifier("endUserDetailsService") UserDetailsService userDetailsService) {
    this.daoAuthenticationProvider = new DaoAuthenticationProvider();
    this.daoAuthenticationProvider.setPasswordEncoder(passwordEncoder);
    this.daoAuthenticationProvider.setUserDetailsService(userDetailsService);
  }

  public ScimUserEntity authenticate(String username, String password) throws AuthenticationException {
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
    Authentication authentication = this.daoAuthenticationProvider.authenticate(authenticationToken);
    return (ScimUserEntity) authentication.getPrincipal();
  }
}
