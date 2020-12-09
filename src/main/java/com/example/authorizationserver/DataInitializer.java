package com.example.authorizationserver;

import com.example.authorizationserver.oauth.client.dao.RegisteredClientRepository;
import com.example.authorizationserver.oauth.client.model.AccessTokenFormat;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.scim.dao.ScimGroupEntityRepository;
import com.example.authorizationserver.scim.dao.ScimUserEntityRepository;
import com.example.authorizationserver.scim.dao.ScimUserGroupEntityRepository;
import com.example.authorizationserver.scim.model.ScimEmailEntity;
import com.example.authorizationserver.scim.model.ScimGroupEntity;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.model.ScimUserGroupEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializer.class);

    private static final UUID ADMIN_ID = UUID.fromString("55bc7a01-3db2-46cd-95f9-c4f8d4ae9557");
    private static final UUID BWAYNE_ID = UUID.fromString("39fd95ec-4f6f-47f7-92f0-16bbb7f832ee");
    private static final UUID PPARKER_ID = UUID.fromString("6a9b4a56-a375-4343-aa69-b78fc93bd3fe");
    private static final UUID CKENT_ID = UUID.fromString("00fe6926-6523-444c-850d-beba24d30da0");
    private static final UUID LIBRARY_USER_ID = UUID.fromString("355382ea-9c94-40d4-a2dc-5c4feb0b2554");
    private static final UUID LIBRARY_CURATOR_ID = UUID.fromString("39a92127-8d78-46ee-85aa-30b5ff4246d6");
    private static final UUID LIBRARY_ADMIN_ID = UUID.fromString("88ae8d70-9b17-41b5-853e-7c7308d98b0c");

    private final ScimUserEntityRepository scimUserEntityRepository;
    private final ScimGroupEntityRepository scimGroupEntityRepository;
    private final RegisteredClientRepository registeredClientRepository;
    private final ScimUserGroupEntityRepository scimUserGroupEntityRepository;

    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            ScimUserEntityRepository scimUserEntityRepository, ScimGroupEntityRepository scimGroupEntityRepository, RegisteredClientRepository registeredClientRepository,
            ScimUserGroupEntityRepository scimUserGroupEntityRepository, PasswordEncoder passwordEncoder) {
        this.scimUserEntityRepository = scimUserEntityRepository;
        this.scimGroupEntityRepository = scimGroupEntityRepository;
        this.registeredClientRepository = registeredClientRepository;
        this.scimUserGroupEntityRepository = scimUserGroupEntityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void run(String... args) {
        long countUsers = this.scimUserEntityRepository.count();
        if (countUsers == 0) {
            createGroups();
            createAdminUser();
            createUsers();
            createGroupMappings();
        }

        long countClients = this.registeredClientRepository.count();
        if (countClients == 0) {
            createClients();
        }
    }

    private void createGroups() {
        Set<ScimGroupEntity> groups =
                Stream.of(
                        new ScimGroupEntity(LIBRARY_USER_ID, null, "library_user", null),
                        new ScimGroupEntity(LIBRARY_CURATOR_ID, null, "library_curator", null),
                        new ScimGroupEntity(LIBRARY_ADMIN_ID, null, "library_admin", null)
                ).map(scimGroupEntityRepository::save).collect(Collectors.toSet());

        LOG.info("Created {} SCIM groups", groups.size());
    }

    private void createAdminUser() {
        ScimUserEntity adminUser = new ScimUserEntity(
                ADMIN_ID,
                "admin",
                "Max",
                "Root",
                true,
                passwordEncoder.encode("admin"),
                Set.of(new ScimEmailEntity("max.root@example.com", "work", true)),
                null,
                Set.of("USER", "ADMIN"));

        adminUser = scimUserEntityRepository.save(adminUser);

        LOG.info("Created {} SCIM admin user", adminUser);
    }

    private void createUsers() {

        Set<ScimUserEntity> users =
                Stream.of(
                        new ScimUserEntity(
                                BWAYNE_ID,
                                "bwayne",
                                "Wayne",
                                "Bruce",
                                true,
                                passwordEncoder.encode("wayne"),
                                Collections.singleton(new ScimEmailEntity("bruce.wayne@example.com", "work", true)),
                                null,
                                Collections.singleton("USER")),
                        new ScimUserEntity(
                                CKENT_ID,
                                "ckent",
                                "Kent",
                                "Clark",
                                true,
                                passwordEncoder.encode("kent"),
                                Collections.singleton(new ScimEmailEntity("clark.kent@example.com", "work", true)),
                                null,
                                Collections.singleton("USER")),
                        new ScimUserEntity(
                                PPARKER_ID,
                                "pparker",
                                "Parker",
                                "Peter",
                                true,
                                passwordEncoder.encode("parker"),
                                Collections.singleton(new ScimEmailEntity("peter.parker@example.com", "work", true)),
                                null,
                                Collections.singleton("USER")))
                        .map(scimUserEntityRepository::save)
                        .collect(Collectors.toSet());

        LOG.info("Created {} SCIM users", users.size());
    }

    private void createGroupMappings() {
        scimUserEntityRepository.findOneByIdentifier(BWAYNE_ID).map(
                u ->
                        scimGroupEntityRepository.findOneByIdentifier(LIBRARY_USER_ID).map(g -> {
                            ScimUserGroupEntity scimUserGroupEntity = new ScimUserGroupEntity(u, g);
                            scimUserGroupEntityRepository.save(scimUserGroupEntity);
                            return g;
                        })
        );
        scimUserEntityRepository.findOneByIdentifier(PPARKER_ID).map(
                u ->
                        scimGroupEntityRepository.findOneByIdentifier(LIBRARY_CURATOR_ID).map(g -> {
                            ScimUserGroupEntity scimUserGroupEntity = new ScimUserGroupEntity(u, g);
                            scimUserGroupEntityRepository.save(scimUserGroupEntity);
                            return g;
                        })
        );
        scimUserEntityRepository.findOneByIdentifier(CKENT_ID).map(
                u ->
                        scimGroupEntityRepository.findOneByIdentifier(LIBRARY_ADMIN_ID).map(g -> {
                            ScimUserGroupEntity scimUserGroupEntity = new ScimUserGroupEntity(u, g);
                            scimUserGroupEntityRepository.save(scimUserGroupEntity);
                            return g;
                        })
        );

        LOG.info("Created SCIM user/group mappings");
    }


    private void createClients() {
        Set<RegisteredClient> registeredClients =
                Stream.of(
                        new RegisteredClient(
                                UUID.randomUUID(),
                                "confidential-jwt",
                                passwordEncoder.encode("demo"),
                                true,
                                AccessTokenFormat.JWT,
                                Set.of(GrantType.AUTHORIZATION_CODE, GrantType.CLIENT_CREDENTIALS, GrantType.PASSWORD, GrantType.REFRESH_TOKEN),
                                Collections.singleton(
                                        "http://localhost:8080/demo-client/login/oauth2/code/demo"),
                                Collections.singleton("*")),
                        new RegisteredClient(
                                UUID.randomUUID(),
                                "public-jwt",
                                passwordEncoder.encode("n/a"),
                                false,
                                AccessTokenFormat.JWT,
                                Set.of(GrantType.AUTHORIZATION_CODE),
                                Collections.singleton(
                                        "http://localhost:8080/demo-client/login/oauth2/code/demo"),
                                Collections.singleton("*")),
                        new RegisteredClient(
                                UUID.randomUUID(),
                                "public-jwt-angular",
                                passwordEncoder.encode("n/a"),
                                false,
                                AccessTokenFormat.JWT,
                                Set.of(GrantType.AUTHORIZATION_CODE),
                                Collections.singleton(
                                        "http://localhost:4200/index.html"),
                                Collections.singleton("http://localhost:4200")),
                        new RegisteredClient(
                                UUID.randomUUID(),
                                "confidential-opaque",
                                passwordEncoder.encode("demo"),
                                true,
                                AccessTokenFormat.OPAQUE,
                                Set.of(GrantType.AUTHORIZATION_CODE, GrantType.CLIENT_CREDENTIALS, GrantType.PASSWORD, GrantType.REFRESH_TOKEN),
                                Collections.singleton(
                                        "http://localhost:8080/demo-client/login/oauth2/code/demo"),
                                Collections.singleton("*")),
                        new RegisteredClient(
                                UUID.randomUUID(),
                                "public-opaque",
                                passwordEncoder.encode("n/a"),
                                false,
                                AccessTokenFormat.OPAQUE,
                                Set.of(GrantType.AUTHORIZATION_CODE),
                                Collections.singleton(
                                        "http://localhost:8080/demo-client/login/oauth2/code/demo"),
                                Collections.singleton("*")))
                        .map(registeredClientRepository::save)
                        .collect(Collectors.toSet());

        LOG.info("Created {} clients", registeredClients.size());
    }
}
