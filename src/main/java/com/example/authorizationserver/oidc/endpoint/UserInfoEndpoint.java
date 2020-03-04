package com.example.authorizationserver.oidc.endpoint;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userinfo")
public class UserInfoEndpoint {

  @GetMapping
  public String userInfo() {
    return null;
  }

}
