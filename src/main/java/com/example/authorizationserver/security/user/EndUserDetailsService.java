package com.example.authorizationserver.security.user;

import com.example.authorizationserver.user.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Qualifier("endUserDetailsService")
@Service
public class EndUserDetailsService implements UserDetailsService {

  private final UserService userService;

  public EndUserDetailsService(UserService userService) {
    this.userService = userService;
  }

  @Transactional(readOnly = true)
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return this.userService
        .findOneByUsername(username)
        .map(EndUserDetails::new)
        .orElseThrow(() -> new UsernameNotFoundException("No user found"));
  }
}
