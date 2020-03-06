package com.example.authorizationserver.user.api;

import com.example.authorizationserver.user.api.resource.CreateUserResource;
import com.example.authorizationserver.user.api.resource.UserResource;
import com.example.authorizationserver.user.model.User;
import com.example.authorizationserver.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

@Validated
@RestController
@RequestMapping("/api/users")
public class UserApiController {

  private final UserService userService;

  public UserApiController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public List<UserResource> users() {
    return userService.findAll().stream().map(UserResource::new).collect(Collectors.toList());
  }

  @GetMapping("/{userId}")
  public ResponseEntity<UserResource> user(@PathVariable("userId") UUID userId) {
    return userService
        .findOneByIdentifier(userId)
        .map(UserResource::new)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<UserResource> create(
      @Valid @RequestBody CreateUserResource createUserResource,
      HttpServletRequest httpServletRequest) {
    User user = userService.save(new User(createUserResource));
    URI uri =
        ServletUriComponentsBuilder.fromContextPath(httpServletRequest)
            .path("/api/users/{userId}")
            .buildAndExpand(user.getIdentifier())
            .toUri();
    return ResponseEntity.created(uri).body(new UserResource(user));
  }
}
