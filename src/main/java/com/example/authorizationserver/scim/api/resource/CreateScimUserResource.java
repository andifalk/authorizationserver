package com.example.authorizationserver.scim.api.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.net.URI;
import java.util.Set;
import java.util.UUID;

public class CreateScimUserResource extends ScimUserResource {

  @NotNull
  @NotBlank
  @Size(min = 8, max = 255)
  private String password;

  public CreateScimUserResource() {
  }

  public CreateScimUserResource(ScimMetaResource meta, UUID identifier, String externalId, String userName,
                                String familyName, String givenName, String middleName, String honorificPrefix,
                                String honorificSuffix, String nickName, URI profileUrl, String title, String userType,
                                String preferredLanguage, String locale, String timezone, boolean active, String password,
                                Set<ScimEmailResource> emails, Set<ScimPhoneNumberResource> phoneNumbers,
                                Set<ScimImsResource> ims, Set<ScimPhotoResource> photos, Set<ScimAddressResource> addresses,
                                Set<ScimRefResource> groups, Set<String> entitlements, Set<String> roles,
                                Set<String> x509Certificates) {
    super(meta, identifier, externalId, userName, familyName, givenName, middleName, honorificPrefix, honorificSuffix, nickName, profileUrl, title, userType, preferredLanguage, locale, timezone, active, emails, phoneNumbers, ims, photos, addresses, groups, entitlements, roles, x509Certificates);
    this.password = password;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("password", password)
            .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    CreateScimUserResource that = (CreateScimUserResource) o;

    return new EqualsBuilder()
            .appendSuper(super.equals(o))
            .append(password, that.password)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
            .appendSuper(super.hashCode())
            .append(password)
            .toHashCode();
  }
}
