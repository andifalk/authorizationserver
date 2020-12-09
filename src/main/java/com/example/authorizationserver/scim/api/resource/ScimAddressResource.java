package com.example.authorizationserver.scim.api.resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

public class ScimAddressResource implements Serializable {

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
    private boolean primary;

    public ScimAddressResource() {
    }

    public ScimAddressResource(String streetAddress, String locality, String region, String postalCode, String country, String type, boolean primary) {
        this.streetAddress = streetAddress;
        this.locality = locality;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
        this.type = type;
        this.primary = primary;
    }

    public String formatted() {
        return ""
                + (StringUtils.isNotBlank(streetAddress) ? streetAddress + "\n" : "")
                + (StringUtils.isNotBlank(locality) ? locality + " " : "")
                + (StringUtils.isNotBlank(postalCode) ? postalCode + "\n" : "")
                + (StringUtils.isNotBlank(country) ? country : "");
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

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
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
                .append("primary", primary)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimAddressResource that = (ScimAddressResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(primary, that.primary)
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
                .append(primary)
                .toHashCode();
    }
}
