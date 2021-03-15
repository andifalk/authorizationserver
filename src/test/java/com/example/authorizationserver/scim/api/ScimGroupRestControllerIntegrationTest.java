package com.example.authorizationserver.scim.api;

import com.example.authorizationserver.scim.api.resource.ScimGroupResource;
import com.example.authorizationserver.scim.api.resource.mapper.ScimGroupListResourceMapper;
import com.example.authorizationserver.scim.api.resource.mapper.ScimGroupResourceMapper;
import com.example.authorizationserver.scim.model.ScimEmailEntity;
import com.example.authorizationserver.scim.model.ScimGroupEntity;
import com.example.authorizationserver.scim.model.ScimUserEntity;
import com.example.authorizationserver.scim.model.ScimUserGroupEntity;
import com.example.authorizationserver.scim.service.ScimService;
import com.example.authorizationserver.security.client.RegisteredClientDetailsService;
import com.example.authorizationserver.security.user.EndUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({RestDocumentationExtension.class})
@WebMvcTest(ScimGroupRestController.class)
class ScimGroupRestControllerIntegrationTest {

    @MockBean
    private EndUserDetailsService endUserDetailsService;

    @MockBean
    private RegisteredClientDetailsService registeredClientDetailsService;

    @MockBean
    private ScimService scimService;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {

        @Bean
        ScimGroupResourceMapper scimGroupResourceMapper() {
            return new ScimGroupResourceMapper();
        }

        @Bean
        ScimGroupListResourceMapper scimGroupListResourceMapper() {
            return new ScimGroupListResourceMapper();
        }
    }

    @BeforeEach
    public void setUp(
            WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider restDocumentation) {
        this.mockMvc =
                MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .apply(
                                documentationConfiguration(restDocumentation)
                                        .uris().withPort(9090).and().operationPreprocessors()
                                        .withRequestDefaults(prettyPrint())
                                        .withResponseDefaults(prettyPrint()))
                        .build();
    }

    @Test
    void getGroups() throws Exception {

        ScimGroupEntity scimGroupEntity = new ScimGroupEntity(UUID.randomUUID(), "123", "test_group", null);
        ReflectionTestUtils.setField(scimGroupEntity, "version", 1L);
        given(scimService.findAllGroups())
                .willReturn(
                        List.of(scimGroupEntity));
        mockMvc
                .perform(get(ScimGroupRestController.GROUP_ENDPOINT))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("getAllGroups"));
    }

    @Test
    void getGroup() throws Exception {
        UUID groupIdentifier = UUID.randomUUID();
        ScimGroupEntity scimGroupEntity = new ScimGroupEntity(groupIdentifier, "123", "test_group", null);
        ReflectionTestUtils.setField(scimGroupEntity, "version", 1L);
        given(scimService.findGroupByIdentifier(groupIdentifier))
                .willReturn(
                        Optional.of(scimGroupEntity));
        mockMvc
                .perform(get(ScimGroupRestController.GROUP_ENDPOINT + "/{groupId}", groupIdentifier))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("getGroup"));
    }

    @Test
    void createGroup() throws Exception {
        UUID groupIdentifier = UUID.randomUUID();
        ScimGroupEntity scimGroupEntity = new ScimGroupEntity(groupIdentifier, "123", "test_group", null);
        ReflectionTestUtils.setField(scimGroupEntity, "version", 1L);
        given(scimService.createGroup(any())).willReturn(scimGroupEntity);

        ScimGroupResource scimGroupResource = new ScimGroupResourceMapper().mapEntityToResource(scimGroupEntity, "http://localhost:9090/api/Groups/" + groupIdentifier);
        mockMvc
                .perform(
                        post(ScimGroupRestController.GROUP_ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(scimGroupResource)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("createGroup"));
    }

    @Test
    void deleteGroup() throws Exception {
        UUID groupIdentifier = UUID.randomUUID();
        mockMvc
                .perform(delete(ScimGroupRestController.GROUP_ENDPOINT + "/{groupid}", groupIdentifier))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("deleteGroup"));
    }

    @Test
    void updateGroup() throws Exception {
        UUID groupIdentifier = UUID.randomUUID();
        ScimGroupEntity scimGroupEntity = new ScimGroupEntity(groupIdentifier, "123", "test_group", null);
        ReflectionTestUtils.setField(scimGroupEntity, "version", 1L);
        given(scimService.updateGroup(any())).willReturn(scimGroupEntity);

        ScimGroupResource scimGroupResource = new ScimGroupResourceMapper().mapEntityToResource(scimGroupEntity, "http://localhost:9090/api/Groups/" + groupIdentifier);

        mockMvc
                .perform(put(ScimGroupRestController.GROUP_ENDPOINT + "/{groupid}", groupIdentifier)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scimGroupResource)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("updateGroup"));
    }

    @Test
    void addMemberToGroup() throws Exception {
        UUID userIdentifier = UUID.randomUUID();
        UUID groupIdentifier = UUID.randomUUID();

        ScimGroupEntity scimGroupEntity = new ScimGroupEntity(groupIdentifier, "123", "test_group", null);
        ReflectionTestUtils.setField(scimGroupEntity, "version", 1L);

        when(scimService.findGroupByIdentifier(any())).thenReturn(Optional.of(scimGroupEntity));

        mockMvc
                .perform(put(ScimGroupRestController.GROUP_ENDPOINT + "/{groupid}/members/{userId}", groupIdentifier, userIdentifier)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("addMemberToGroup"));
    }

    @Test
    void removeMemberFromGroup() throws Exception {
        UUID userIdentifier = UUID.randomUUID();
        UUID groupIdentifier = UUID.randomUUID();

        ScimGroupEntity scimGroupEntity = new ScimGroupEntity(groupIdentifier, "123", "test_group", null);
        ReflectionTestUtils.setField(scimGroupEntity, "version", 1L);

        when(scimService.findGroupByIdentifier(any())).thenReturn(Optional.of(scimGroupEntity));

        mockMvc
                .perform(delete(ScimGroupRestController.GROUP_ENDPOINT + "/{groupid}/members/{userId}", groupIdentifier, userIdentifier)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andDo(document("removeMemberFromGroup"));
    }
}
