package com.example.authorizationserver.oauth.client.api;

import com.example.authorizationserver.oauth.client.RegisteredClientService;
import com.example.authorizationserver.oauth.client.api.resource.ModifyRegisteredClientResource;
import com.example.authorizationserver.oauth.client.model.AccessTokenFormat;
import com.example.authorizationserver.oauth.client.model.RegisteredClient;
import com.example.authorizationserver.oauth.common.GrantType;
import com.example.authorizationserver.scim.service.ScimService;
import com.example.authorizationserver.security.client.RegisteredClientDetailsService;
import com.example.authorizationserver.security.user.EndUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RegisteredClientApiController.class)
class RegisteredClientApiControllerIntegrationTest {

    @MockBean
    private EndUserDetailsService endUserDetailsService;

    @MockBean
    private RegisteredClientDetailsService registeredClientDetailsService;

    @MockBean
    private RegisteredClientService registeredClientService;

    @MockBean
    private ScimService scimService;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp(
            WebApplicationContext webApplicationContext) {
        this.mockMvc =
                MockMvcBuilders.webAppContextSetup(webApplicationContext)
                        .build();
    }

    @Test
    void findAllClients() throws Exception {
        given(registeredClientService.findAll())
                .willReturn(
                        List.of(
                                new RegisteredClient(
                                        UUID.randomUUID(),
                                        "clientid",
                                        "clientsecret",
                                        true,
                                        AccessTokenFormat.JWT,
                                        Set.of(GrantType.AUTHORIZATION_CODE),
                                        Set.of("redirect"),
                                        Set.of("cors"))));
        mockMvc
                .perform(get(RegisteredClientApiController.ENDPOINT))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void findSingleClient() throws Exception {
        UUID userIdentifier = UUID.randomUUID();
        given(registeredClientService.findOneByIdentifier(userIdentifier))
                .willReturn(
                        Optional.of(
                                new RegisteredClient(
                                        UUID.randomUUID(),
                                        "clientid",
                                        "clientsecret",
                                        true,
                                        AccessTokenFormat.JWT,
                                        Set.of(GrantType.AUTHORIZATION_CODE),
                                        Set.of("redirect"),
                                        Set.of("cors"))));
        mockMvc
                .perform(get(RegisteredClientApiController.ENDPOINT + "/{clientId}", userIdentifier))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void registerClient() throws Exception {
        UUID clientIdentifier = UUID.randomUUID();
        RegisteredClient registeredClient =
                new RegisteredClient(
                        clientIdentifier,
                        "clientid",
                        "clientsecret",
                        true,
                        AccessTokenFormat.JWT,
                        Set.of(GrantType.AUTHORIZATION_CODE),
                        Set.of("redirect"),
                        Set.of("cors"));
        given(registeredClientService.create(any())).willReturn(registeredClient);

        ModifyRegisteredClientResource modifyRegisteredClientResource = new ModifyRegisteredClientResource(registeredClient);
        mockMvc
                .perform(
                        post(RegisteredClientApiController.ENDPOINT)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(modifyRegisteredClientResource)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void updateClient() throws Exception {
        UUID clientIdentifier = UUID.randomUUID();
        RegisteredClient registeredClient =
                new RegisteredClient(
                        clientIdentifier,
                        "clientid",
                        "clientsecret",
                        true,
                        AccessTokenFormat.JWT,
                        Set.of(GrantType.AUTHORIZATION_CODE),
                        Set.of("redirect"),
                        Set.of("cors"));
        given(registeredClientService.update(any(), any())).willReturn(Optional.of(registeredClient));

        ModifyRegisteredClientResource modifyRegisteredClientResource = new ModifyRegisteredClientResource(registeredClient);
        mockMvc
                .perform(
                        put(RegisteredClientApiController.ENDPOINT + "/{clientId}", clientIdentifier)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(modifyRegisteredClientResource)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void deleteClient() throws Exception {
        UUID userIdentifier = UUID.randomUUID();
        mockMvc
                .perform(delete(RegisteredClientApiController.ENDPOINT + "/{clientId}", userIdentifier))
                .andDo(print())
                .andExpect(status().is2xxSuccessful());
    }
}