package com.example.authorizationserver.scim.api.resource.mapper;

import com.example.authorizationserver.scim.api.resource.ScimGroupListResource;
import com.example.authorizationserver.scim.api.resource.ScimMetaResource;
import com.example.authorizationserver.scim.model.ScimGroupEntity;
import org.springframework.stereotype.Component;

@Component
public class ScimGroupListResourceMapper {

    public ScimGroupListResource mapEntityToResource(ScimGroupEntity scimGroupEntity, String location) {
        return new ScimGroupListResource(new
                ScimMetaResource("Group", scimGroupEntity.getCreatedDate().isPresent()? scimGroupEntity.getCreatedDate().get() : null,
                scimGroupEntity.getLastModifiedDate().isPresent() ? scimGroupEntity.getLastModifiedDate().get() : null,
                scimGroupEntity.getVersion().toString(), location), scimGroupEntity.getIdentifier(),
                scimGroupEntity.getExternalId(), scimGroupEntity.getDisplayName());
    }

}
