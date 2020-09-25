package com.example.authorizationserver.scim.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
                .append("ims", ims)
                .append("type", type)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimImsEntity that = (ScimImsEntity) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(ims, that.ims)
                .append(type, that.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(ims)
                .append(type)
                .toHashCode();
    }
}
