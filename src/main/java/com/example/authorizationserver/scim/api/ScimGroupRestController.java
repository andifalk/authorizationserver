package com.example.authorizationserver.scim.api;

import com.example.authorizationserver.scim.api.resource.ScimGroupListResource;
import com.example.authorizationserver.scim.api.resource.ScimGroupResource;
import com.example.authorizationserver.scim.api.resource.mapper.ScimGroupListResourceMapper;
import com.example.authorizationserver.scim.api.resource.mapper.ScimGroupResourceMapper;
import com.example.authorizationserver.scim.model.ScimGroupEntity;
import com.example.authorizationserver.scim.service.ScimService;
import org.springframework.http.ResponseEntity;
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
public class ScimGroupRestController {

    public static final String GROUP_ENDPOINT = "/api/Groups";

    private final ScimService scimService;

    private final ScimGroupResourceMapper scimGroupResourceMapper;

    private final ScimGroupListResourceMapper scimGroupListResourceMapper;

    public ScimGroupRestController(ScimService scimService, ScimGroupResourceMapper scimGroupResourceMapper, ScimGroupListResourceMapper scimGroupListResourceMapper) {
        this.scimService = scimService;
        this.scimGroupResourceMapper = scimGroupResourceMapper;
        this.scimGroupListResourceMapper = scimGroupListResourceMapper;
    }

    @GetMapping(GROUP_ENDPOINT)
    public List<ScimGroupListResource> getAllGroups() {
        return scimService.findAllGroups().stream().map(ue -> {
            URI location =
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path(GROUP_ENDPOINT + "/{groupId}")
                            .buildAndExpand(ue.getIdentifier())
                            .toUri();
            return scimGroupListResourceMapper.mapEntityToResource(ue, location.toASCIIString());}).collect(Collectors.toList());
    }

    @GetMapping(GROUP_ENDPOINT + "/{groupId}")
    public ResponseEntity<ScimGroupResource> getGroup(@PathVariable("groupId") UUID groupIdentifier) {
        return scimService.findGroupByIdentifier(groupIdentifier).map(ue -> {
            URI location =
                    ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path(GROUP_ENDPOINT + "/{groupId}")
                            .buildAndExpand(ue.getIdentifier())
                            .toUri();
            return ResponseEntity.ok().location(location).body(
                    scimGroupResourceMapper.mapEntityToResource(ue, location.toASCIIString()));})
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(GROUP_ENDPOINT)
    public ResponseEntity<ScimGroupResource> createGroup(@Valid @RequestBody ScimGroupResource scimGroupResource) {
        ScimGroupEntity scimGroupEntity = scimGroupResourceMapper.mapResourceToEntity(scimGroupResource);
        scimGroupEntity = scimService.createGroup(scimGroupEntity);
        URI location =
                ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path(GROUP_ENDPOINT + "/{userId}")
                        .buildAndExpand(scimGroupEntity.getIdentifier())
                        .toUri();
        return ResponseEntity.ok().location(location).body(scimGroupResourceMapper.mapEntityToResource(scimGroupEntity, location.toASCIIString()));
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping(GROUP_ENDPOINT + "/{groupId}")
    public void deleteGroup(@PathVariable("groupId") UUID groupIdentifier) {
        scimService.deleteGroup(groupIdentifier);
    }
}
