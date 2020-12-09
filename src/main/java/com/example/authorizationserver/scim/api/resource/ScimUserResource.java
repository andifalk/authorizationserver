package com.example.authorizationserver.scim.api.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ScimUserResource extends ScimUserListResource {

    private Set<ScimEmailResource> emails = new HashSet<>();

    private Set<ScimPhoneNumberResource> phoneNumbers = new HashSet<>();

    private Set<ScimImsResource> ims = new HashSet<>();

    private Set<ScimPhotoResource> photos = new HashSet<>();

    private Set<ScimAddressResource> addresses = new HashSet<>();

    private Set<ScimRefResource> groups = new HashSet<>();

    private Set<String> entitlements = new HashSet<>();

    private Set<String> roles = new HashSet<>();

    private Set<String> x509Certificates = new HashSet<>();

    public ScimUserResource() {
    }

    public ScimUserResource(ScimMetaResource meta, UUID identifier, String externalId, String userName, String familyName,
                            String givenName, String middleName, String honorificPrefix, String honorificSuffix,
                            String nickName, URI profileUrl, String title, String userType, String preferredLanguage,
                            String locale, String timezone, boolean active,
                            Set<ScimEmailResource> emails, Set<ScimPhoneNumberResource> phoneNumbers,
                            Set<ScimImsResource> ims, Set<ScimPhotoResource> photos, Set<ScimAddressResource> addresses,
                            Set<ScimRefResource> groups, Set<String> entitlements, Set<String> roles,
                            Set<String> x509Certificates) {
        super(meta, identifier, externalId, userName, familyName, givenName, middleName, honorificPrefix,
                honorificSuffix, nickName, profileUrl, title, userType, preferredLanguage, locale, timezone, active);
        this.emails = emails;
        this.phoneNumbers = phoneNumbers;
        this.ims = ims;
        this.photos = photos;
        this.addresses = addresses;
        this.groups = groups;
        this.entitlements = entitlements;
        this.roles = roles;
        this.x509Certificates = x509Certificates;
    }

    public Set<ScimEmailResource> getEmails() {
        return emails;
    }

    public void setEmails(Set<ScimEmailResource> emails) {
        this.emails = emails;
    }

    public Set<ScimPhoneNumberResource> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<ScimPhoneNumberResource> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Set<ScimImsResource> getIms() {
        return ims;
    }

    public void setIms(Set<ScimImsResource> ims) {
        this.ims = ims;
    }

    public Set<ScimPhotoResource> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<ScimPhotoResource> photos) {
        this.photos = photos;
    }

    public Set<ScimAddressResource> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<ScimAddressResource> addresses) {
        this.addresses = addresses;
    }

    public Set<ScimRefResource> getGroups() {
        return groups;
    }

    public void setGroups(Set<ScimRefResource> groups) {
        this.groups = groups;
    }

    public Set<String> getEntitlements() {
        return entitlements;
    }

    public void setEntitlements(Set<String> entitlements) {
        this.entitlements = entitlements;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getX509Certificates() {
        return x509Certificates;
    }

    public void setX509Certificates(Set<String> x509Certificates) {
        this.x509Certificates = x509Certificates;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("emails", emails)
                .append("phoneNumbers", phoneNumbers)
                .append("ims", ims)
                .append("photos", photos)
                .append("addresses", addresses)
                .append("groups", groups)
                .append("entitlements", entitlements)
                .append("roles", roles)
                .append("x509Certificates", x509Certificates)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimUserResource that = (ScimUserResource) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(emails, that.emails)
                .append(phoneNumbers, that.phoneNumbers)
                .append(ims, that.ims)
                .append(photos, that.photos)
                .append(addresses, that.addresses)
                .append(groups, that.groups)
                .append(entitlements, that.entitlements)
                .append(roles, that.roles)
                .append(x509Certificates, that.x509Certificates)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(emails)
                .append(phoneNumbers)
                .append(ims)
                .append(photos)
                .append(addresses)
                .append(groups)
                .append(entitlements)
                .append(roles)
                .append(x509Certificates)
                .toHashCode();
    }
}
