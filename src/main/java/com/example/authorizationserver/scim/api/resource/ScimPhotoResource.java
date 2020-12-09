package com.example.authorizationserver.scim.api.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.net.URI;

public class ScimPhotoResource implements Serializable {

    @NotNull
    private URI value;

    @NotNull
    @Size(min = 1, max = 50)
    private String type;

    public ScimPhotoResource() {
    }

    public ScimPhotoResource(URI value, String type) {
        this.value = value;
        this.type = type;
    }

    public URI getValue() {
        return value;
    }

    public void setValue(URI value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("value", value)
                .append("type", type)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimPhotoResource that = (ScimPhotoResource) o;

        return new EqualsBuilder()
                .append(value, that.value)
                .append(type, that.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(value)
                .append(type)
                .toHashCode();
    }
}
