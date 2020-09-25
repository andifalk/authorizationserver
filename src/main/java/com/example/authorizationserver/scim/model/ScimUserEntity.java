package com.example.authorizationserver.scim.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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

    @Size(max = 100)
    private String employeeNumber;

    @Size(max = 100)
    private String costCenter;

    @Size(max = 100)
    private String organisation;

    @Size(max = 100)
    private String division;

    @Size(max = 100)
    private String department;

    @ManyToOne
    private ScimUserEntity manager;

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
            orphanRemoval = true
    )
    private Set<ScimUserGroupEntity> groups = new HashSet<>();

    @ElementCollection(fetch = EAGER)
    private Set<String> entitlements = new HashSet<>();

    @ElementCollection(fetch = EAGER)
    private Set<String> roles = new HashSet<>();

    @ElementCollection(fetch = EAGER)
    private Set<String> x509Certificates = new HashSet<>();

    public ScimUserEntity() {
    }

    public ScimUserEntity(UUID identifier, String externalId, String userName, String familyName,
                          String givenName, boolean active,
                          String password, Set<ScimEmailEntity> emails, Set<ScimPhoneNumberEntity> phoneNumbers,
                          Set<ScimImsEntity> ims, Set<ScimAddressEntity> addresses,
                          Set<ScimUserGroupEntity> groups, Set<String> entitlements, Set<String> roles) {
        this(identifier, externalId, userName, familyName, givenName, null, null, null,
                null, null, null, null, null, null, null,
                active, null, null, null, null, null, null,
                password, emails, phoneNumbers, ims, null, addresses, groups, entitlements, roles, null);
    }

    public ScimUserEntity(UUID identifier, String externalId, String userName, String familyName,
                          String givenName, String middleName, String honorificPrefix, String honorificSuffix,
                          String nickName, URI profileUrl, String title, String userType, String preferredLanguage,
                          String locale, String timezone, boolean active, String employeeNumber, String costCenter,
                          String organisation, String division, String department, ScimUserEntity manager,
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
        this.employeeNumber = employeeNumber;
        this.costCenter = costCenter;
        this.organisation = organisation;
        this.division = division;
        this.department = department;
        this.manager = manager;
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

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public ScimUserEntity getManager() {
        return manager;
    }

    public void setManager(ScimUserEntity manager) {
        this.manager = manager;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("userName", userName)
                .append("familyName", familyName)
                .append("givenName", givenName)
                .append("middleName", middleName)
                .append("honorificPrefix", honorificPrefix)
                .append("honorificSuffix", honorificSuffix)
                .append("nickName", nickName)
                .append("profileUrl", profileUrl)
                .append("title", title)
                .append("userType", userType)
                .append("preferredLanguage", preferredLanguage)
                .append("locale", locale)
                .append("timezone", timezone)
                .append("active", active)
                .append("employeeNumber", employeeNumber)
                .append("costCenter", costCenter)
                .append("organisation", organisation)
                .append("division", division)
                .append("department", department)
                .append("manager", manager)
                .append("password", password)
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

        ScimUserEntity that = (ScimUserEntity) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(active, that.active)
                .append(userName, that.userName)
                .append(familyName, that.familyName)
                .append(givenName, that.givenName)
                .append(middleName, that.middleName)
                .append(honorificPrefix, that.honorificPrefix)
                .append(honorificSuffix, that.honorificSuffix)
                .append(nickName, that.nickName)
                .append(profileUrl, that.profileUrl)
                .append(title, that.title)
                .append(userType, that.userType)
                .append(preferredLanguage, that.preferredLanguage)
                .append(locale, that.locale)
                .append(timezone, that.timezone)
                .append(employeeNumber, that.employeeNumber)
                .append(costCenter, that.costCenter)
                .append(organisation, that.organisation)
                .append(division, that.division)
                .append(department, that.department)
                .append(manager, that.manager)
                .append(password, that.password)
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
                .append(userName)
                .append(familyName)
                .append(givenName)
                .append(middleName)
                .append(honorificPrefix)
                .append(honorificSuffix)
                .append(nickName)
                .append(profileUrl)
                .append(title)
                .append(userType)
                .append(preferredLanguage)
                .append(locale)
                .append(timezone)
                .append(active)
                .append(employeeNumber)
                .append(costCenter)
                .append(organisation)
                .append(division)
                .append(department)
                .append(manager)
                .append(password)
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
