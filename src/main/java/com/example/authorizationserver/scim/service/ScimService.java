package com.example.authorizationserver.scim.service;

import com.example.authorizationserver.scim.dao.ScimGroupEntityRepository;
import com.example.authorizationserver.scim.dao.ScimUserEntityRepository;
import com.example.authorizationserver.scim.dao.ScimUserGroupEntityRepository;
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
    public ScimUserEntity updateUser(ScimUserEntity scimUserEntity) {
        return scimUserEntityRepository.findOneByIdentifier(scimUserEntity.getIdentifier())
                .map(ue -> {
                    ue.setActive(scimUserEntity.isActive());
                    ue.setGroups(scimUserEntity.getGroups());
                    ue.setAddresses(scimUserEntity.getAddresses());
                    ue.setEmails(scimUserEntity.getEmails());
                    ue.setEntitlements(scimUserEntity.getEntitlements());
                    ue.setFamilyName(scimUserEntity.getFamilyName());
                    ue.setGivenName(scimUserEntity.getGivenName());
                    ue.setHonorificPrefix(scimUserEntity.getHonorificPrefix());
                    ue.setHonorificSuffix(scimUserEntity.getHonorificSuffix());
                    ue.setIms(scimUserEntity.getIms());
                    ue.setLocale(scimUserEntity.getLocale());
                    ue.setMiddleName(scimUserEntity.getMiddleName());
                    ue.setNickName(scimUserEntity.getNickName());
                    ue.setPassword(scimUserEntity.getPassword());
                    ue.setPhoneNumbers(scimUserEntity.getPhoneNumbers());
                    ue.setPhotos(scimUserEntity.getPhotos());
                    ue.setPreferredLanguage(scimUserEntity.getPreferredLanguage());
                    ue.setProfileUrl(scimUserEntity.getProfileUrl());
                    ue.setRoles(scimUserEntity.getRoles());
                    ue.setTimezone(scimUserEntity.getTimezone());
                    ue.setTitle(scimUserEntity.getTitle());
                    ue.setUserName(scimUserEntity.getUserName());
                    ue.setUserType(scimUserEntity.getUserType());
                    ue.setX509Certificates(scimUserEntity.getX509Certificates());
                    ue.setExternalId(scimUserEntity.getExternalId());
                    return scimUserEntityRepository.save(ue);
                }).orElseThrow(() -> new ScimUserNotFoundException(scimUserEntity.getIdentifier()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUser(UUID userIdentifier) {
        scimUserEntityRepository.deleteOneByIdentifier(userIdentifier);
    }

    public void addUserGroupMapping(UUID userIdentifier, UUID groupIdentifier) {
        scimUserEntityRepository.findOneByIdentifier(userIdentifier).map(
            user ->
                scimGroupEntityRepository.findOneByIdentifier(groupIdentifier).map(
                    group -> scimUserGroupEntityRepository.save(new ScimUserGroupEntity(user, group))
                ).orElseThrow(() -> new ScimGroupNotFoundException(groupIdentifier))

        ).orElseThrow(() -> new ScimUserNotFoundException(userIdentifier));
    }
}
