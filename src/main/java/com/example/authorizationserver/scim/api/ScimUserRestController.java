package com.example.authorizationserver.scim.api;

import com.example.authorizationserver.scim.api.resource.CreateScimUserResource;
import com.example.authorizationserver.scim.api.resource.ScimUserListResource;
import com.example.authorizationserver.scim.api.resource.ScimUserResource;
import com.example.authorizationserver.scim.api.resource.mapper.CreateScimUserResourceMapper;
import com.example.authorizationserver.scim.api.resource.mapper.ScimUserListResourceMapper;
import com.example.authorizationserver.scim.api.resource.mapper.ScimUserResourceMapper;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.service.ScimService;
import com.example.authorizationserver.security.user.EndUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.NO_CONTENT;

@Validated
@RestController
public class ScimUserRestController {

    public static final String USER_ENDPOINT = "/api/Users";
    public static final String ME_ENDPOINT = "/api/Me";

    private final ScimService scimService;

    private final ScimUserResourceMapper scimUserResourceMapper;

    private final ScimUserListResourceMapper scimUserListResourceMapper;

    private final CreateScimUserResourceMapper createScimUserResourceMapper;

    public ScimUserRestController(ScimService scimService, ScimUserResourceMapper scimUserResourceMapper, ScimUserListResourceMapper scimUserListResourceMapper, CreateScimUserResourceMapper createScimUserResourceMapper) {
        this.scimService = scimService;
        this.scimUserResourceMapper = scimUserResourceMapper;
        this.scimUserListResourceMapper = scimUserListResourceMapper;
        this.createScimUserResourceMapper = createScimUserResourceMapper;
    }

    @Operation(
            summary = "Creates a new user",
            tags = {"SCIM Users and Groups"}
    )
    @PostMapping(USER_ENDPOINT)
    public ResponseEntity<ScimUserResource> createUser(@Valid @RequestBody CreateScimUserResource createScimUserResource) {
        ScimUserEntity scimUserEntity = createScimUserResourceMapper.mapResourceToEntity(createScimUserResource);
        scimUserEntity = scimService.createUser(scimUserEntity);
        URI location =
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(USER_ENDPOINT + "/{userId}")
                        .buildAndExpand(scimUserEntity.getIdentifier())
                        .toUri();
        return ResponseEntity.ok().location(location).body(scimUserResourceMapper.mapEntityToResource(scimUserEntity, location.toASCIIString()));
    }

    @Operation(
            summary = "Retrieves list of users",
            tags = {"SCIM Users and Groups"}
    )
    @GetMapping(USER_ENDPOINT)
    public List<ScimUserListResource> getAllUsers() {
        return scimService.findAllUsers().stream().map(ue -> {
            URI location =
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(USER_ENDPOINT + "/{userId}")
                        .buildAndExpand(ue.getIdentifier())
                        .toUri();
            return scimUserListResourceMapper.mapEntityToResource(ue, location.toASCIIString());}).collect(Collectors.toList());
    }

    @Operation(
            summary = "Retrieves a single user",
            tags = {"SCIM Users and Groups"},
            parameters = {
                    @Parameter(name = "userId", description = "The identifier of the user", required = true, example = "4b2889df-3af6-4ad5-a889-4816c0ed8869")
            }
    )
    @GetMapping(USER_ENDPOINT + "/{userId}")
    public ResponseEntity<ScimUserResource> getUser(@PathVariable("userId") UUID userIdentifier) {
        return scimService.findUserByIdentifier(userIdentifier).map(ue -> {
            URI location =
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path(USER_ENDPOINT + "/{userId}")
                            .buildAndExpand(ue.getIdentifier())
                            .toUri();
            return ResponseEntity.ok().location(location).body(scimUserResourceMapper.mapEntityToResource(ue, location.toASCIIString()));})
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Updates a single user",
            tags = {"SCIM Users and Groups"},
            parameters = {
                    @Parameter(name = "userId", description = "The identifier of the user", required = true, example = "4b2889df-3af6-4ad5-a889-4816c0ed8869")
            }
    )
    @PutMapping(USER_ENDPOINT + "/{userId}")
    public ResponseEntity<ScimUserResource> updateUser(@PathVariable("userId") UUID userIdentifier, @RequestBody @Valid ScimUserResource scimUserResource) {
        URI location =
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(USER_ENDPOINT + "/{userId}")
                        .buildAndExpand(userIdentifier)
                        .toUri();

        ScimUserEntity ue = scimService.updateUser(userIdentifier, scimUserResource);
        return ResponseEntity.ok().location(location).body(scimUserResourceMapper.mapEntityToResource(ue, location.toASCIIString()));
    }

    @Operation(
            summary = "Deletes a single user",
            tags = {"SCIM Users and Groups"},
            parameters = {
                    @Parameter(name = "userId", description = "The identifier of the user", required = true, example = "4b2889df-3af6-4ad5-a889-4816c0ed8869")
            }
    )
    @ResponseStatus(NO_CONTENT)
    @DeleteMapping(USER_ENDPOINT + "/{userId}")
    public void deleteUser(@PathVariable("userId") UUID userIdentifier) {
        scimService.deleteUser(userIdentifier);
    }


    @Operation(
            summary = "Retrieves the authenticated user",
            tags = {"SCIM Users and Groups"}
    )
    @GetMapping(ME_ENDPOINT)
    public ResponseEntity<ScimUserResource> getAuthenticatedUser(@AuthenticationPrincipal EndUserDetails user) {
        return scimService.findUserByIdentifier(user.getIdentifier()).map(ue -> {
            URI location =
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path(USER_ENDPOINT + "/{userId}")
                            .buildAndExpand(ue.getIdentifier())
                            .toUri();
            return ResponseEntity.ok().location(location).body(scimUserResourceMapper.mapEntityToResource(ue, location.toASCIIString()));})
                .orElse(ResponseEntity.notFound().build());
    }
}
