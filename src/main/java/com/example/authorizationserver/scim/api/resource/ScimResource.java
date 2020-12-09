package com.example.authorizationserver.scim.api.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ScimResource implements Serializable {

    @NotNull
    @NotEmpty
    private List<String> schemas = new ArrayList<>();

    @Valid
    private ScimMetaResource meta;

    private UUID identifier;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 50)
    private String externalId;

    public ScimResource() {
    }

    public ScimResource(List<String> schemas, ScimMetaResource meta, UUID identifier, String externalId) {
        this.schemas = schemas;
        this.meta = meta;
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

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }

    public ScimMetaResource getMeta() {
        return meta;
    }

    public void setMeta(ScimMetaResource meta) {
        this.meta = meta;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("schemas", schemas)
                .append("meta", meta)
                .append("identifier", identifier)
                .append("externalId", externalId)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimResource that = (ScimResource) o;

        return new EqualsBuilder()
                .append(schemas, that.schemas)
                .append(meta, that.meta)
                .append(identifier, that.identifier)
                .append(externalId, that.externalId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(schemas)
                .append(meta)
                .append(identifier)
                .append(externalId)
                .toHashCode();
    }
}
