package com.example.authorizationserver.scim.dao;

import com.example.authorizationserver.scim.model.ScimGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScimGroupEntityRepository extends JpaRepository<ScimGroupEntity, Long> {

    ScimGroupEntity findOneByIdentifier(UUID identifier);
}
