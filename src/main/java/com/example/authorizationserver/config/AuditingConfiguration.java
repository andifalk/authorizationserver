package com.example.authorizationserver.config;

import com.example.authorizationserver.scim.dao.ScimUserEntityRepository;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.security.user.SpringSecurityAuditorAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class AuditingConfiguration {

    private final ScimUserEntityRepository scimUserEntityRepository;

    public AuditingConfiguration(ScimUserEntityRepository scimUserEntityRepository) {
        this.scimUserEntityRepository = scimUserEntityRepository;
    }

    @Bean
    public AuditorAware<ScimUserEntity> auditorProvider() {
        return new SpringSecurityAuditorAware(scimUserEntityRepository);
    }

}
