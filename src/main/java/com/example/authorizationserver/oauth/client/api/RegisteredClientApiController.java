package com.example.authorizationserver.oauth.client.api;

import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.api.resource.ModifyRegisteredClientResource;
import com.example.authorizationserver.oauth.client.api.resource.RegisteredClientResource;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(RegisteredClientApiController.ENDPOINT)
public class RegisteredClientApiController {

  public static final String ENDPOINT = "/api/clients";

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

  @GetMapping("/{clientId}")
  public ResponseEntity<RegisteredClientResource> client(@PathVariable("clientId") UUID clientId) {
    return registeredClientService.findOneByIdentifier(clientId)
            .map(c -> ResponseEntity.ok(new RegisteredClientResource(c)))
            .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<RegisteredClientResource> registerNewClient(
          @RequestBody @Valid ModifyRegisteredClientResource modifyRegisteredClientResource, HttpServletRequest httpServletRequest) {

    RegisteredClient registeredClient = new RegisteredClient(modifyRegisteredClientResource);
    registeredClient = this.registeredClientService.create(registeredClient);

    URI uri =
            ServletUriComponentsBuilder.fromContextPath(httpServletRequest)
                    .path("/api/clients/{clientId}")
                    .buildAndExpand(registeredClient.getIdentifier())
                    .toUri();
    return ResponseEntity.created(uri).body(new RegisteredClientResource(registeredClient));
  }

  @PutMapping("/{clientId}")
  public ResponseEntity<RegisteredClientResource> update(@PathVariable("clientId") UUID clientId,
                                             @Valid @RequestBody ModifyRegisteredClientResource modifyRegisteredClientResource) {
    return registeredClientService
            .update(clientId, new RegisteredClient(modifyRegisteredClientResource))
            .map(RegisteredClientResource::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/{clientId}")
  public ResponseEntity<Void> deleteUser(@PathVariable("clientId") UUID clientId) {
    registeredClientService.deleteOneByIdentifier(clientId);
    return ResponseEntity.noContent().build();
  }

}
