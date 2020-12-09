package com.example.authorizationserver.scim.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.net.URI;

@Entity
public class ScimPhotoEntity extends AbstractPersistable<Long> implements Serializable {

    @NotNull
    private URI photoUrl;

    @NotNull
    @Size(min = 1, max = 50)
    private String type;

    public ScimPhotoEntity() {
    }

    public ScimPhotoEntity(URI photoUrl, String type) {
        this.photoUrl = photoUrl;
        this.type = type;
    }

    public URI getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(URI photoUrl) {
        this.photoUrl = photoUrl;
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
                .appendSuper(super.toString())
                .append("photoUrl", photoUrl)
                .append("type", type)
                .toString();
    }
}
