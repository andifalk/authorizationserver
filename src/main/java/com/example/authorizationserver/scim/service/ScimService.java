package com.example.authorizationserver.scim.service;

import com.example.authorizationserver.scim.api.resource.ScimUserResource;
import com.example.authorizationserver.scim.dao.ScimGroupEntityRepository;
import com.example.authorizationserver.scim.dao.ScimUserEntityRepository;
import com.example.authorizationserver.scim.dao.ScimUserGroupEntityRepository;
import com.example.authorizationserver.scim.model.ScimAddressEntity;
import com.example.authorizationserver.scim.model.ScimGroupEntity;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.model.ScimUserGroupEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.IdGenerator;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ScimService {

    private final ScimUserEntityRepository scimUserEntityRepository;
    private final ScimGroupEntityRepository scimGroupEntityRepository;
    private final ScimUserGroupEntityRepository scimUserGroupEntityRepository;
    private final IdGenerator idGenerator;

    public ScimService(ScimUserEntityRepository scimUserEntityRepository, ScimGroupEntityRepository scimGroupEntityRepository, ScimUserGroupEntityRepository scimUserGroupEntityRepository, IdGenerator idGenerator) {
        this.scimUserEntityRepository = scimUserEntityRepository;
        this.scimGroupEntityRepository = scimGroupEntityRepository;
        this.scimUserGroupEntityRepository = scimUserGroupEntityRepository;
        this.idGenerator = idGenerator;
    }

    public Optional<ScimUserEntity> findUserByIdentifier(UUID identifier) {
        return scimUserEntityRepository.findOneByIdentifier(identifier);
    }

    public Optional<ScimUserEntity> findUserByUserName(String username) {
        return scimUserEntityRepository.findOneByUserName(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ScimUserEntity> findAllUsers() {
        return scimUserEntityRepository.findAll();
    }

    @Transactional
    public ScimGroupEntity createGroup(ScimGroupEntity scimGroupEntity) {
        if (scimGroupEntity.getIdentifier() == null) {
            scimGroupEntity.setIdentifier(idGenerator.generateId());
        }
        return scimGroupEntityRepository.save(scimGroupEntity);
    }

    @Transactional
    public ScimGroupEntity updateGroup(ScimGroupEntity scimGroupEntity) {
        scimGroupEntityRepository.findOneByIdentifier(scimGroupEntity.getIdentifier()).map(
            ge -> {
                ge.setDisplayName(scimGroupEntity.getDisplayName());
                return scimGroupEntityRepository.save(ge);
            }
        ).orElseThrow(() -> new ScimGroupNotFoundException(scimGroupEntity.getIdentifier()));
        return scimGroupEntityRepository.save(scimGroupEntity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteGroup(UUID groupIdentifier) {
        scimGroupEntityRepository.deleteOneByIdentifier(groupIdentifier);
    }

    public Optional<ScimGroupEntity> findGroupByIdentifier(UUID identifier) {
        return scimGroupEntityRepository.findOneByIdentifier(identifier);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<ScimGroupEntity> findAllGroups() {
        return scimGroupEntityRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ScimUserEntity createUser(ScimUserEntity scimUserEntity) {
        if (scimUserEntity.getIdentifier() == null) {
            scimUserEntity.setIdentifier(idGenerator.generateId());
        }
        return scimUserEntityRepository.save(scimUserEntity);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public ScimUserEntity updateUser(UUID userIdentifier, ScimUserResource scimUserResource) {
        return scimUserEntityRepository.findOneByIdentifier(userIdentifier)
                .map(ue -> {
                    ue.setActive(scimUserResource.isActive());
                    ue.setUserName(scimUserResource.getUserName());
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
                    ue.setRoles(scimUserResource.getRoles());
                    ue.setEntitlements(scimUserResource.getEntitlements());
                    ue.setUserType(scimUserResource.getUserType());
                    if (ue.getAddresses() != null && !ue.getAddresses().isEmpty()) {
                        ue.setAddresses(
                                ue.getAddresses()
                                        .stream()
                                        .map(ra -> new ScimAddressEntity(
                                                ra.getStreetAddress(), ra.getLocality(),
                                                ra.getRegion(), ra.getPostalCode(),
                                                ra.getCountry(), ra.getType(), ra.isPrimaryAddress())).collect(Collectors.toSet()));
                    }
                    return scimUserEntityRepository.save(ue);
                }).orElseThrow(() -> new ScimUserNotFoundException(userIdentifier));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUser(UUID userIdentifier) {
        scimUserEntityRepository.deleteOneByIdentifier(userIdentifier);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void addUserGroupMapping(UUID userIdentifier, UUID groupIdentifier) {
        scimUserEntityRepository.findOneByIdentifier(userIdentifier).map(
            user ->
                scimGroupEntityRepository.findOneByIdentifier(groupIdentifier).map(
                    group -> scimUserGroupEntityRepository.save(new ScimUserGroupEntity(user, group))
                ).orElseThrow(() -> new ScimGroupNotFoundException(groupIdentifier))

        ).orElseThrow(() -> new ScimUserNotFoundException(userIdentifier));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void removeUserGroupMapping(UUID userIdentifier, UUID groupIdentifier) {
        scimGroupEntityRepository.findOneByIdentifier(groupIdentifier).map(
                group ->
                        group.getMembers()
                                .stream()
                                .filter(p ->
                                        p.getUser().getIdentifier().equals(userIdentifier) && p.getGroup().getIdentifier().equals(groupIdentifier)
                                ).findFirst().map(
                                        uge -> group.getMembers().remove(uge)
                        ).orElseThrow(() -> new ScimUserNotFoundException(userIdentifier))
        ).orElseThrow(() -> new ScimUserNotFoundException(userIdentifier));
    }
}
