package com.example.authorizationserver.scim.api.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ScimMetaResource implements Serializable {

    private String resourceType;

    private LocalDateTime created;

    private LocalDateTime lastModified;

    private String version;

    private String location;

    public ScimMetaResource() {
    }

    public ScimMetaResource(String resourceType, LocalDateTime created, LocalDateTime lastModified, String version, String location) {
        this.resourceType = resourceType;
        this.created = created;
        this.lastModified = lastModified;
        this.version = version;
        this.location = location;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("resourceType", resourceType)
                .append("created", created)
                .append("lastModified", lastModified)
                .append("version", version)
                .append("location", location)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimMetaResource that = (ScimMetaResource) o;

        return new EqualsBuilder()
                .append(resourceType, that.resourceType)
                .append(created, that.created)
                .append(lastModified, that.lastModified)
                .append(version, that.version)
                .append(location, that.location)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(resourceType)
                .append(created)
                .append(lastModified)
                .append(version)
                .append(location)
                .toHashCode();
    }
}
