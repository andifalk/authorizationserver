package com.example.authorizationserver.scim.dao;

import com.example.authorizationserver.scim.model.ScimUserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ScimUserEntityRepository extends JpaRepository<ScimUserEntity, Long> {

    @EntityGraph(attributePaths = {"emails", "phoneNumbers", "ims", "photos", "addresses", "groups", "groups.group", "roles", "entitlements", "x509Certificates"})
    Optional<ScimUserEntity> findOneByIdentifier(UUID identifier);

    @EntityGraph(attributePaths = {"emails", "phoneNumbers", "ims", "photos", "addresses", "groups", "groups.group", "roles", "entitlements", "x509Certificates"})
    Optional<ScimUserEntity> findOneByUserName(String username);

    void deleteOneByIdentifier(UUID identifier);

}
