package com.example.authorizationserver.scim.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
public class ScimImsEntity extends AbstractPersistable<Long> implements Serializable {

    @NotNull
    @Size(min = 5, max = 50)
    private String ims;

    @NotNull
    @Size(min = 1, max = 50)
    private String type;

    public ScimImsEntity() {
    }

    public ScimImsEntity(@NotNull @Size(min = 5, max = 50) String ims, @NotNull @Size(min = 1, max = 50) String type) {
        this.ims = ims;
        this.type = type;
    }

    public String getIms() {
        return ims;
    }

    public void setIms(String ims) {
        this.ims = ims;
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
                .append("ims", ims)
                .append("type", type)
                .toString();
    }
}
