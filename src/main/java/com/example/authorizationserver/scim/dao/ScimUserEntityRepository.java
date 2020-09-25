package com.example.authorizationserver.scim.dao;

import com.example.authorizationserver.scim.model.ScimUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScimUserEntityRepository extends JpaRepository<ScimUserEntity, Long> {

    ScimUserEntity findOneByIdentifier(UUID identifier);

    ScimUserEntity findOneByUserName(String username);

}
