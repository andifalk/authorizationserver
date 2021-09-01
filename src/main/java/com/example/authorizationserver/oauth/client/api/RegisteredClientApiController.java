package com.example.authorizationserver.oauth.client.api;

import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.api.resource.ModifyRegisteredClientResource;
import com.example.authorizationserver.oauth.client.api.resource.RegisteredClientResource;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

  @Operation(
          summary = "Retrieves list of registered clients",
          tags = {"Client Registration"},
          parameters = {
                  @Parameter(name = "groupId", description = "The identifier of the group", required = true, example = "4b2889df-3af6-4ad5-a889-4816c0ed8869"),
                  @Parameter(name = "userId", description = "The identifier of the user", required = true, example = "4b2889df-3af6-4ad5-a889-4816c0ed8869")
          }
  )
  @GetMapping
  public List<RegisteredClientResource> clients() {
    return registeredClientService.findAll().stream()
        .map(RegisteredClientResource::new)
        .collect(Collectors.toList());
  }

  @Operation(
          summary = "Retrieves a single registered client",
          tags = {"Client Registration"},
          parameters = {
                  @Parameter(name = "clientId", description = "The identifier of the client", required = true, example = "4b2889df-3af6-4ad5-a889-4816c0ed8869")
          }
  )
  @GetMapping("/{clientId}")
  public ResponseEntity<RegisteredClientResource> client(@PathVariable("clientId") UUID clientId) {
    return registeredClientService.findOneByIdentifier(clientId)
            .map(c -> ResponseEntity.ok(new RegisteredClientResource(c)))
            .orElse(ResponseEntity.notFound().build());
  }

  @Operation(
          summary = "Registers a new client",
          tags = {"Client Registration"}
  )
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

  @Operation(
          summary = "Updates a single registered client",
          tags = {"Client Registration"},
          parameters = {
                  @Parameter(name = "clientId", description = "The identifier of the client", required = true, example = "4b2889df-3af6-4ad5-a889-4816c0ed8869")
          }
  )
  @PutMapping("/{clientId}")
  public ResponseEntity<RegisteredClientResource> update(@PathVariable("clientId") UUID clientId,
                                             @Valid @RequestBody ModifyRegisteredClientResource modifyRegisteredClientResource) {
    return registeredClientService
            .update(clientId, new RegisteredClient(modifyRegisteredClientResource))
            .map(RegisteredClientResource::new)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
  }

  @Operation(
          summary = "Deletes a registered client",
          tags = {"Client Registration"},
          parameters = {
                  @Parameter(name = "clientId", description = "The identifier of the client", required = true, example = "4b2889df-3af6-4ad5-a889-4816c0ed8869")
          }
  )
  @DeleteMapping("/{clientId}")
  public ResponseEntity<Void> deleteUser(@PathVariable("clientId") UUID clientId) {
    registeredClientService.deleteOneByIdentifier(clientId);
    return ResponseEntity.noContent().build();
  }

}
