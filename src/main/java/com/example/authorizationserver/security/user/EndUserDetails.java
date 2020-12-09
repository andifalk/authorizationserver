package com.example.authorizationserver.security.user;

import com.example.authorizationserver.scim.model.ScimUserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class EndUserDetails extends ScimUserEntity implements UserDetails {

  public EndUserDetails(ScimUserEntity user) {
    super(user.getIdentifier(), null, user.getUserName(),user.getFamilyName(),user.getGivenName(),user.isActive(), user.getPassword(),
            user.getEmails(), user.getPhoneNumbers(), user.getIms(), user.getAddresses(), user.getGroups(), user.getEntitlements(), user.getRoles());
    super.setId(user.getId());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase())).collect(Collectors.toList());
  }

  @Override
  public String getUsername() {
    return getUserName();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
