package com.example.authorizationserver.token.opaque;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class OpaqueTokenService {

  public String createToken() {
    return RandomStringUtils.random(48, true, true);
  }

}
