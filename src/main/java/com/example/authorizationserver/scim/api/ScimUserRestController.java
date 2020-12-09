package com.example.authorizationserver.scim.api;

import com.example.authorizationserver.scim.api.resource.CreateScimUserResource;
import com.example.authorizationserver.scim.api.resource.ScimUserListResource;
import com.example.authorizationserver.scim.api.resource.ScimUserResource;
import com.example.authorizationserver.scim.api.resource.mapper.CreateScimUserResourceMapper;
import com.example.authorizationserver.scim.api.resource.mapper.ScimUserListResourceMapper;
import com.example.authorizationserver.scim.api.resource.mapper.ScimUserResourceMapper;
import com.example.authorizationserver.scim.model.ScimAddressEntity;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.service.ScimService;
import com.example.authorizationserver.security.user.EndUserDetails;
import org.springframework.http.HttpStatus;
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

    @PutMapping(USER_ENDPOINT + "/{userId}")
    public ResponseEntity<ScimUserResource> updateUser(@PathVariable("userId") UUID userIdentifier, @RequestBody @Valid ScimUserResource scimUserResource) {
        return scimService.findUserByIdentifier(userIdentifier).map(ue -> {
            URI location =
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path(USER_ENDPOINT + "/{userId}")
                            .buildAndExpand(ue.getIdentifier())
                            .toUri();
            ue.setActive(scimUserResource.isActive());
            ue.setFamilyName(scimUserResource.getFamilyName());
            ue.setGivenName(scimUserResource.getGivenName());
            ue.setHonorificPrefix(scimUserResource.getHonorificPrefix());
            ue.setHonorificSuffix(scimUserResource.getHonorificSuffix());
            ue.setLocale(scimUserResource.getLocale());
            ue.setMiddleName(scimUserResource.getMiddleName());
            ue.setNickName(scimUserResource.getNickName());
            ue.setPreferredLanguage(scimUserResource.getPreferredLanguage());
            ue.setProfileUrl(scimUserResource.getProfileUrl());
            ue.setTimezone(scimUserResource.getTimezone());
            ue.setTitle(scimUserResource.getTitle());
            ue.setExternalId(scimUserResource.getExternalId());
            if (ue.getAddresses() != null && !ue.getAddresses().isEmpty()) {
                ue.setAddresses(
                        ue.getAddresses()
                                .stream()
                                .map(ra -> new ScimAddressEntity(
                                        ra.getStreetAddress(), ra.getLocality(),
                                        ra.getRegion(), ra.getPostalCode(),
                                        ra.getCountry(), ra.getType(), ra.isPrimaryAddress())).collect(Collectors.toSet()));
            }
            ue = scimService.updateUser(ue);
            return ResponseEntity.ok().location(location).body(scimUserResourceMapper.mapEntityToResource(ue, location.toASCIIString()));
        }).orElse(ResponseEntity.notFound().build());
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping(USER_ENDPOINT + "/{userId}")
    public void deleteUser(@PathVariable("userId") UUID userIdentifier) {
        scimService.deleteUser(userIdentifier);
    }


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
