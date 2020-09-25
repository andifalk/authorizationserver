package com.example.authorizationserver.scim.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
public class ScimEmailEntity extends AbstractPersistable<Long> implements Serializable {

    @NotNull
    @Email
    private String email;

    @NotNull
    private String type;

    @NotNull
    private boolean primaryEmail;

    public ScimEmailEntity() {
    }

    public ScimEmailEntity(@NotNull @Email String email, @NotNull String type, @NotNull boolean primaryEmail) {
        this.email = email;
        this.type = type;
        this.primaryEmail = primaryEmail;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimaryEmail() {
        return primaryEmail;
    }

    public void setPrimaryEmail(boolean primary) {
        this.primaryEmail = primary;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("email", email)
                .append("type", type)
                .append("primary", primaryEmail)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimEmailEntity that = (ScimEmailEntity) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(primaryEmail, that.primaryEmail)
                .append(email, that.email)
                .append(type, that.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(email)
                .append(type)
                .append(primaryEmail)
                .toHashCode();
    }
}
