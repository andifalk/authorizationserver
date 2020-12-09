package com.example.authorizationserver.scim.model;

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
                .appendSuper(super.toString())
                .append("email", email)
                .append("type", type)
                .append("primary", primaryEmail)
                .toString();
    }
}
