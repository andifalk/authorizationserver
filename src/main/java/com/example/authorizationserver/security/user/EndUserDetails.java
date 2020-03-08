package com.example.authorizationserver.security.user;

import com.example.authorizationserver.user.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class EndUserDetails extends User implements UserDetails {

  public EndUserDetails(User user) {
    super(user.getIdentifier(),user.getGender(),user.getFirstName(),user.getLastName(),user.getPassword(),
            user.getEmail(),user.getUsername(),user.getPhone(),user.getGroups(), user.getAddress(), user.getUpdatedAt());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
    authorities.addAll(getGroups().stream()
            .map(group -> new SimpleGrantedAuthority("ROLE_" + group.toUpperCase()))
            .collect(Collectors.toList()));
    return authorities;
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
