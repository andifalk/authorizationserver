package com.example.authorizationserver.oauth.client.web;

import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.api.resource.RegisteredClientResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class RegisteredClientController {

  private final RegisteredClientService registeredClientService;

  @Autowired
  public RegisteredClientController(RegisteredClientService registeredClientService) {
    this.registeredClientService = registeredClientService;
  }

  @ModelAttribute("allClients")
  public List<RegisteredClientResource> populateUsers() {
    return this.registeredClientService.findAll().stream()
        .map(RegisteredClientResource::new)
        .collect(Collectors.toList());
  }

  @GetMapping("/admin/clientlist")
  public String findAll() {
    return "clientlist";
  }
}
