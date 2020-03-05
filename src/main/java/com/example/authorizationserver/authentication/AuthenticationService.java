package com.example.authorizationserver.authentication;

import com.example.authorizationserver.user.model.User;
import com.example.authorizationserver.user.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

  private static final String DEFAULT_PASSWORD = "GbmH7Zh:$vb56RR§4Hnlkmn";

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private String encodedDefaultPassword;

  public AuthenticationService(UserService userService, PasswordEncoder passwordEncoder) {
    this.userService = userService;
    this.passwordEncoder = passwordEncoder;
    this.encodedDefaultPassword = passwordEncoder.encode(DEFAULT_PASSWORD);
  }

  public User authenticate(String username, String password) throws BadCredentialsException {
    Optional<User> userOptional = userService.findOneByUsername(username);
    if (userOptional.isPresent()) {
      if (passwordEncoder.matches(password, userOptional.get().getPassword())) {
        return userOptional.get();
      } else {
        throw new BadCredentialsException("User/Password not correct");
      }
    } else {
      passwordEncoder.matches(DEFAULT_PASSWORD, encodedDefaultPassword);
      throw new BadCredentialsException("User/Password not correct");
    }
  }
}
