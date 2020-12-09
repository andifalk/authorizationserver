package com.example.authorizationserver.scim.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.AbstractAuditable;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.UUID;

@MappedSuperclass
public abstract class ScimResourceEntity extends AbstractAuditable<ScimUserEntity, Long> implements Serializable {

    @Version
    private Long version;

    @NotNull
    private UUID identifier;

    @Size(max = 50)
    private String externalId;

    public ScimResourceEntity() {
    }

    public ScimResourceEntity(UUID identifier, String externalId) {
        this.identifier = identifier;
        this.externalId = externalId;
    }

    public UUID getIdentifier() {
        return identifier;
    }

    public void setIdentifier(UUID identifier) {
        this.identifier = identifier;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Long getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("identifier", identifier)
                .append("externalId", externalId)
                .append("version", version)
                .toString();
    }
}
