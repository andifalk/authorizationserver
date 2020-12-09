package com.example.authorizationserver.scim.api.resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.List;
import java.util.UUID;

public class ScimUserListResource extends ScimResource {

    public static final String SCIM_USER_SCHEMA = "urn:ietf:params:scim:schemas:core:2.0:User";
    public static final String SCIM_ENTERPRISE_USER_SCHEMA = "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User";

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



    public ScimUserListResource() {
    }

    public ScimUserListResource(ScimMetaResource meta, UUID identifier, String externalId, String userName, String familyName,
                                String givenName, String middleName, String honorificPrefix, String honorificSuffix,
                                String nickName, URI profileUrl, String title, String userType, String preferredLanguage,
                                String locale, String timezone, boolean active) {
        super(List.of(SCIM_USER_SCHEMA), meta, identifier, externalId);
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
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScimUserListResource that = (ScimUserListResource) o;

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
                .toHashCode();
    }
}
