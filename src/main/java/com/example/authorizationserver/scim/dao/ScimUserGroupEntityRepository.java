package com.example.authorizationserver.scim.dao;

import com.example.authorizationserver.scim.model.ScimUserGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ScimUserGroupEntityRepository extends JpaRepository<ScimUserGroupEntity, Long> {

    @Query("select ge from ScimUserGroupEntity ge where ge.user.identifier = :userIdentifier and ge.group.identifier = :groupIdentifier")
    List<ScimUserGroupEntity> findAllBy(@Param("userIdentifier") UUID userIdentifier, @Param("groupIdentifier") UUID groupIdentifier);

    void deleteOneByGroup_IdentifierAndUser_Identifier(UUID userIdentifier, UUID groupIdentifier);
}
