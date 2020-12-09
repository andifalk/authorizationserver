package com.example.authorizationserver.scim.api.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

public class ScimRefResource implements Serializable {

    @NotNull
    private UUID value;

    @NotNull
    @NotEmpty
    @Size(min = 1, max = 255)
    private String display;

    @NotNull
    private URI $ref;

    public ScimRefResource() {
    }

    public ScimRefResource(UUID value, URI $ref, String display) {
        this.value = value;
        this.display = display;
        this.$ref = $ref;
    }

    public UUID getValue() {
        return value;
    }

    public void setValue(UUID value) {
        this.value = value;
    }

    public URI get$ref() {
        return $ref;
    }

    public void set$ref(URI $ref) {
        this.$ref = $ref;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("value", value)
                .append("display", display)
                .append("$ref", $ref)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimRefResource that = (ScimRefResource) o;

        return new EqualsBuilder()
                .append(value, that.value)
                .append(display, that.display)
                .append($ref, that.$ref)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(value)
                .append(display)
                .append($ref)
                .toHashCode();
    }
}
