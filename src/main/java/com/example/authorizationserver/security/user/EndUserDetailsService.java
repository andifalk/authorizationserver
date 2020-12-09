package com.example.authorizationserver.security.user;

import com.example.authorizationserver.scim.service.ScimService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Qualifier("endUserDetailsService")
@Service
public class EndUserDetailsService implements UserDetailsService {

    private final ScimService scimService;

    public EndUserDetailsService(ScimService scimService) {
        this.scimService = scimService;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.scimService
                .findUserByUserName(username)
                .map(EndUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("No user found"));
    }
}
