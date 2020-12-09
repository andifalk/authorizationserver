package com.example.authorizationserver.security.user;

import com.example.authorizationserver.scim.dao.ScimUserEntityRepository;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<ScimUserEntity> {

    private final ScimUserEntityRepository scimUserEntityRepository;

    public SpringSecurityAuditorAware(ScimUserEntityRepository scimUserEntityRepository) {
        this.scimUserEntityRepository = scimUserEntityRepository;
    }

    @Override
    public Optional<ScimUserEntity> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(ScimUserEntity.class::cast)
                .flatMap(u -> scimUserEntityRepository.findOneByIdentifier(u.getIdentifier()));
    }
}
