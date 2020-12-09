package com.example.authorizationserver.scim.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.EAGER;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class ScimUserEntity extends ScimResourceEntity {

    @Column(unique = true)
    @NotNull
    @NotBlank
    @Size(min = 3, max = 50)
    private String userName;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 100)
    private String familyName;

    @NotNull
    @NotBlank
    @Size(min = 1, max = 100)
    private String givenName;

    @Size(max = 100)
    private String middleName;

    @Size(max = 100)
    private String honorificPrefix;

    @Size(max = 100)
    private String honorificSuffix;

    @Size(max = 100)
    private String nickName;

    private URI profileUrl;

    @Size(max = 100)
    private String title;

    @Size(max = 100)
    private String userType;

    @Size(max = 50)
    private String preferredLanguage;

    @Size(max = 50)
    private String locale;

    @Size(max = 50)
    private String timezone;

    @NotNull
    private boolean active;

    @NotNull
    @NotBlank
    @Size(min = 8, max = 255)
    private String password;

    @OneToMany(cascade = ALL)
    private Set<ScimEmailEntity> emails = new HashSet<>();

    @OneToMany(cascade = ALL)
    private Set<ScimPhoneNumberEntity> phoneNumbers = new HashSet<>();

    @OneToMany(cascade = ALL)
    private Set<ScimImsEntity> ims = new HashSet<>();

    @OneToMany(cascade = ALL)
    private Set<ScimPhotoEntity> photos = new HashSet<>();

    @OneToMany(cascade = ALL)
    private Set<ScimAddressEntity> addresses = new HashSet<>();

    @OneToMany(
            mappedBy = "user",
            cascade = ALL,
            orphanRemoval = true,
            fetch = EAGER
    )
    private Set<ScimUserGroupEntity> groups = new HashSet<>();

    @ElementCollection
    private Set<String> entitlements = new HashSet<>();

    @ElementCollection
    private Set<String> roles = new HashSet<>();

    @ElementCollection
    private Set<String> x509Certificates = new HashSet<>();

    public ScimUserEntity() {
    }

    public ScimUserEntity(UUID identifier) {
        this(identifier,  null, null, null, true, null, null, null, null);
    }

    public ScimUserEntity(UUID identifier, String userName, String familyName,
                          String givenName, boolean active,
                          String password, Set<ScimEmailEntity> emails,
                          Set<ScimUserGroupEntity> groups, Set<String> roles) {
        this(identifier, identifier.toString(), userName, familyName, givenName, null, null, null,
                null, null, null, null, null, null, null,
                active, password, emails, null, null, null, null, groups, null, roles, null);
    }

    public ScimUserEntity(UUID identifier, String externalId, String userName, String familyName,
                          String givenName, boolean active,
                          String password, Set<ScimEmailEntity> emails, Set<ScimPhoneNumberEntity> phoneNumbers,
                          Set<ScimImsEntity> ims, Set<ScimAddressEntity> addresses,
                          Set<ScimUserGroupEntity> groups, Set<String> entitlements, Set<String> roles) {
        this(identifier, externalId, userName, familyName, givenName, null, null, null,
                null, null, null, null, null, null, null,
                active, password, emails, phoneNumbers, ims, null, addresses, groups, entitlements, roles, null);
    }

    public ScimUserEntity(UUID identifier, String externalId, String userName, String familyName,
                          String givenName, String middleName, String honorificPrefix, String honorificSuffix,
                          String nickName, URI profileUrl, String title, String userType, String preferredLanguage,
                          String locale, String timezone, boolean active,
                          String password, Set<ScimEmailEntity> emails, Set<ScimPhoneNumberEntity> phoneNumbers,
                          Set<ScimImsEntity> ims, Set<ScimPhotoEntity> photos, Set<ScimAddressEntity> addresses,
                          Set<ScimUserGroupEntity> groups, Set<String> entitlements, Set<String> roles,
                          Set<String> x509Certificates) {
        super(identifier, externalId);
        this.userName = userName;
        this.familyName = familyName;
        this.givenName = givenName;
        this.middleName = middleName;
        this.honorificPrefix = honorificPrefix;
        this.honorificSuffix = honorificSuffix;
        this.nickName = nickName;
        this.profileUrl = profileUrl;
        this.title = title;
        this.userType = userType;
        this.preferredLanguage = preferredLanguage;
        this.locale = locale;
        this.timezone = timezone;
        this.active = active;
        this.password = password;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getHonorificPrefix() {
        return honorificPrefix;
    }

    public void setHonorificPrefix(String honorificPrefix) {
        this.honorificPrefix = honorificPrefix;
    }

    public String getHonorificSuffix() {
        return honorificSuffix;
    }

    public void setHonorificSuffix(String honorificSuffix) {
        this.honorificSuffix = honorificSuffix;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public URI getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(URI profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(String preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<ScimEmailEntity> getEmails() {
        return emails;
    }

    public void setEmails(Set<ScimEmailEntity> emails) {
        this.emails = emails;
    }

    public Set<ScimImsEntity> getIms() {
        return ims;
    }

    public void setIms(Set<ScimImsEntity> ims) {
        this.ims = ims;
    }

    public Set<ScimPhoneNumberEntity> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(Set<ScimPhoneNumberEntity> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public Set<ScimPhotoEntity> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<ScimPhotoEntity> photos) {
        this.photos = photos;
    }

    public Set<ScimAddressEntity> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<ScimAddressEntity> addresses) {
        this.addresses = addresses;
    }

    public Set<ScimUserGroupEntity> getGroups() {
        return groups;
    }

    public void setGroups(Set<ScimUserGroupEntity> groups) {
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

    public String getDisplayName() {
        if (StringUtils.isNotBlank(familyName) && StringUtils.isNotBlank(givenName)) {
            return String.format("%s %s", givenName, familyName);
        } else if (StringUtils.isNotBlank(nickName) && StringUtils.isNotBlank(familyName)) {
            return String.format("%s %s", nickName, familyName);
        } else {
            return userName;
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("userName", userName)
                .append("familyName", familyName)
                .append("givenName", givenName)
                .toString();
    }
}
