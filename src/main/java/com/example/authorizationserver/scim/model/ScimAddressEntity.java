package com.example.authorizationserver.scim.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.AbstractPersistable;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
public class ScimAddressEntity extends AbstractPersistable<Long> implements Serializable {

    @Size(max = 100)
    private String streetAddress;

    @Size(max = 100)
    private String locality;

    @Size(max = 100)
    private String region;

    @Size(max = 100)
    private String postalCode;

    @Size(max = 2)
    @Pattern(regexp = "^[A-Z]{2}$")
    private String country;

    @Size(max = 100)
    private String type;

    @NotNull
    private boolean primaryAddress;

    public ScimAddressEntity() {
    }

    public ScimAddressEntity(String streetAddress, String locality, String region, String postalCode, String country, String type, boolean primaryAddress) {
        this.streetAddress = streetAddress;
        this.locality = locality;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
        this.type = type;
        this.primaryAddress = primaryAddress;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimaryAddress() {
        return primaryAddress;
    }

    public void setPrimaryAddress(boolean primary) {
        this.primaryAddress = primary;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("streetAddress", streetAddress)
                .append("locality", locality)
                .append("region", region)
                .append("postalCode", postalCode)
                .append("country", country)
                .append("type", type)
                .append("primary", primaryAddress)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimAddressEntity that = (ScimAddressEntity) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(primaryAddress, that.primaryAddress)
                .append(streetAddress, that.streetAddress)
                .append(locality, that.locality)
                .append(region, that.region)
                .append(postalCode, that.postalCode)
                .append(country, that.country)
                .append(type, that.type)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(streetAddress)
                .append(locality)
                .append(region)
                .append(postalCode)
                .append(country)
                .append(type)
                .append(primaryAddress)
                .toHashCode();
    }
}
