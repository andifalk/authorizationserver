package com.example.authorizationserver.scim.api.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class ScimEmailResource implements Serializable {

    @NotNull
    @Email
    private String value;

    @NotNull
    private String type;

    private boolean primary;

    public ScimEmailResource() {
    }

    public ScimEmailResource(String value, String type, boolean primary) {
        this.value = value;
        this.type = type;
        this.primary = primary;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
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
                .append("primary", primary)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimEmailResource that = (ScimEmailResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(primary, that.primary)
                .append(value, that.value)
                .append(type, that.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(value)
                .append(type)
                .append(primary)
                .toHashCode();
    }
}
