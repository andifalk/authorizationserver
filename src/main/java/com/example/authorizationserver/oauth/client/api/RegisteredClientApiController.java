package com.example.authorizationserver.oauth.client.api;

import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.api.resource.RegisteredClientResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/clients")
public class RegisteredClientApiController {

  private final RegisteredClientService registeredClientService;

  public RegisteredClientApiController(RegisteredClientService registeredClientService) {
    this.registeredClientService = registeredClientService;
  }

  @GetMapping
  public List<RegisteredClientResource> clients() {
    return registeredClientService.findAll().stream()
        .map(RegisteredClientResource::new)
        .collect(Collectors.toList());
  }
}
