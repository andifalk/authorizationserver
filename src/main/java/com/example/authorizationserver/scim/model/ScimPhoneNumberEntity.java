package com.example.authorizationserver.scim.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
public class ScimPhoneNumberEntity extends AbstractPersistable<Long> implements Serializable {

    @NotNull
    @Size(min = 5, max = 50)
    private String phone;

    @NotNull
    @Size(min = 1, max = 50)
    private String type;

    public ScimPhoneNumberEntity() {
    }

    public ScimPhoneNumberEntity(@NotNull @Size(min = 5, max = 50) String phone, @NotNull @Size(min = 1, max = 50) String type) {
        this.phone = phone;
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
                .append("phone", phone)
                .append("type", type)
                .toString();
    }
}
