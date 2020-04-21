package com.example.authorizationserver.oidc.endpoint.userinfo;

import com.example.authorizationserver.user.api.resource.AddressResource;
import com.example.authorizationserver.user.model.User;

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

  private String email;

  private Boolean email_verified;

  private String gender;

  private String birthdate;

  private String zoneinfo;

  private String locale;

  private String phone_number;

  private Boolean phone_number_verified;

  private AddressResource address;

  private String updated_at;

  private String error;

  private String error_description;

  public UserInfo() {}

  public UserInfo(String subject) {
    this.sub = subject;
    this.name = subject;
  }

  public UserInfo(String error, String error_description) {
    this.error = error;
    this.error_description = error_description;
  }

  public UserInfo(User user) {
    this.address = new AddressResource(user.getAddress());
    this.email = user.getEmail();
    this.given_name = user.getFirstName();
    this.family_name = user.getLastName();
    this.preferred_username = user.getUsername();
    this.name = user.getUsername();
    this.gender = user.getGender().name();
    this.email_verified = true;
    this.phone_number = user.getPhone();
    this.phone_number_verified = true;
    this.sub = user.getIdentifier().toString();
    this.locale = "DE";
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

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public Boolean isEmail_verified() {
    return email_verified;
  }

  public void setEmail_verified(Boolean email_verified) {
    this.email_verified = email_verified;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
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

  public String getPhone_number() {
    return phone_number;
  }

  public void setPhone_number(String phone_number) {
    this.phone_number = phone_number;
  }

  public Boolean isPhone_number_verified() {
    return phone_number_verified;
  }

  public void setPhone_number_verified(Boolean phone_number_verified) {
    this.phone_number_verified = phone_number_verified;
  }

  public AddressResource getAddress() {
    return address;
  }

  public void setAddress(AddressResource address) {
    this.address = address;
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
        + ", email='"
        + email
        + '\''
        + ", email_verified="
        + email_verified
        + ", gender='"
        + gender
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
        + ", phone_number='"
        + phone_number
        + '\''
        + ", phone_number_verified="
        + phone_number_verified
        + ", address="
        + address
        + ", updated_at='"
        + updated_at
        + '\''
        + '}';
  }
}
