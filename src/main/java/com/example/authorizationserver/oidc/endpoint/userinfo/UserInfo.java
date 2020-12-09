package com.example.authorizationserver.oidc.endpoint.userinfo;

import com.example.authorizationserver.scim.model.ScimEmailEntity;
import com.example.authorizationserver.scim.model.ScimPhoneNumberEntity;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.api.resource.AddressResource;

import java.util.Set;
import java.util.stream.Collectors;

public class UserInfo {

    private String sub;

    private String name;

    private String given_name;

    private String family_name;

    private String middle_name;

    private String nickname;

    private String preferred_username;

    private String website;

    private String profile;

    private String picture;

    private Set<String> emails;

    private String gender;

    private String birthdate;

    private String zoneinfo;

    private String locale;

    private Set<String> phone_numbers;

    private Set<AddressResource> addresses;

    private Set<String> groups;

    private Set<String> entitlements;

    private Set<String> roles;

    private String updated_at;

    private String error;

    private String error_description;

    public UserInfo() {
    }

    public UserInfo(String subject) {
        this.sub = subject;
        this.name = subject;
    }

    public UserInfo(String error, String error_description) {
        this.error = error;
        this.error_description = error_description;
    }

    public UserInfo(ScimUserEntity user) {
        this.addresses = user.getAddresses().stream().map(AddressResource::new).collect(Collectors.toSet());
        this.emails = user.getEmails().stream().map(ScimEmailEntity::getEmail).collect(Collectors.toSet());
        this.given_name = user.getGivenName();
        this.family_name = user.getFamilyName();
        this.preferred_username = user.getUserName();
        this.name = user.getUserName();
        this.phone_numbers = user.getPhoneNumbers().stream().map(ScimPhoneNumberEntity::getPhone).collect(Collectors.toSet());
        this.sub = user.getIdentifier().toString();
        this.locale = user.getLocale();
        this.groups = user.getGroups().stream().map(ug -> ug.getGroup().getDisplayName()).collect(Collectors.toSet());
        this.entitlements = user.getEntitlements();
        this.roles = user.getRoles();
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGiven_name() {
        return given_name;
    }

    public void setGiven_name(String given_name) {
        this.given_name = given_name;
    }

    public String getFamily_name() {
        return family_name;
    }

    public void setFamily_name(String family_name) {
        this.family_name = family_name;
    }

    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPreferred_username() {
        return preferred_username;
    }

    public void setPreferred_username(String preferred_username) {
        this.preferred_username = preferred_username;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    public Set<String> getPhone_numbers() {
        return phone_numbers;
    }

    public void setPhone_numbers(Set<String> phone_numbers) {
        this.phone_numbers = phone_numbers;
    }

    public Set<AddressResource> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<AddressResource> addresses) {
        this.addresses = addresses;
    }

    public Set<String> getGroups() {
        return groups;
    }

    public void setGroups(Set<String> groups) {
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

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getZoneinfo() {
        return zoneinfo;
    }

    public void setZoneinfo(String zoneinfo) {
        this.zoneinfo = zoneinfo;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getError() {
        return error;
    }

    public String getError_description() {
        return error_description;
    }

    @Override
    public String toString() {
        return "UserInfo{"
                + "sub='"
                + sub
                + '\''
                + ", name='"
                + name
                + '\''
                + ", given_name='"
                + given_name
                + '\''
                + ", family_name='"
                + family_name
                + '\''
                + ", middle_name='"
                + middle_name
                + '\''
                + ", nickname='"
                + nickname
                + '\''
                + ", preferred_username='"
                + preferred_username
                + '\''
                + ", website='"
                + website
                + '\''
                + ", profile='"
                + profile
                + '\''
                + ", picture='"
                + picture
                + '\''
                + ", emails='"
                + emails
                + '\''
                + ", birthdate='"
                + birthdate
                + '\''
                + ", zoneinfo='"
                + zoneinfo
                + '\''
                + ", locale='"
                + locale
                + '\''
                + ", phone_numbers='"
                + phone_numbers
                + ", addresses="
                + addresses
                + ", groups="
                + groups
                + ", entitlements="
                + entitlements
                + ", roles="
                + roles
                + ", updated_at='"
                + updated_at
                + '\''
                + '}';
    }
}
