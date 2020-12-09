package com.example.authorizationserver.scim.api.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

public class ScimGroupListResource extends ScimResource {

    public static final String SCIM_GROUP_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:Group";

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 255)
    private String displayName;

    public ScimGroupListResource() {
    }

    public ScimGroupListResource(ScimMetaResource meta, UUID identifier, String externalId, String displayName) {
        super(List.of(SCIM_GROUP_SCHEMA), meta, identifier, externalId);
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("displayName", displayName)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimGroupListResource that = (ScimGroupListResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(displayName, that.displayName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(displayName)
                .toHashCode();
    }
}
