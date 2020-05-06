package com.example.authorizationserver.user.api;

import com.example.authorizationserver.security.client.RegisteredClientDetailsService;
import com.example.authorizationserver.security.user.EndUserDetailsService;
import com.example.authorizationserver.user.api.resource.CreateUserResource;
import com.example.authorizationserver.user.model.Address;
import com.example.authorizationserver.user.model.Gender;
import com.example.authorizationserver.user.model.User;
import com.example.authorizationserver.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(UserApiController.class)
class UserApiControllerIntegrationTest {

  @MockBean private EndUserDetailsService endUserDetailsService;

  @MockBean private RegisteredClientDetailsService registeredClientDetailsService;

  @MockBean private UserService userService;

  private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

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
  void users() throws Exception {
    given(userService.findAll())
        .willReturn(
            List.of(
                new User(
                    UUID.randomUUID(),
                    Gender.MALE,
                    "Max",
                    "Muster",
                    "secret",
                    "test@example.com",
                    "mmuser",
                    "12345",
                    Set.of("user"),
                    new Address("street", "12345", "city", "state", "country"),
                    LocalDateTime.now())));
    mockMvc
        .perform(get(UserApiController.ENDPOINT))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andDo(document("getAllUsers"));
  }

  @Test
  void user() throws Exception {
    UUID userIdentifier = UUID.randomUUID();
    given(userService.findOneByIdentifier(userIdentifier))
        .willReturn(
            Optional.of(
                new User(
                    userIdentifier,
                    Gender.MALE,
                    "Max",
                    "Muster",
                    "secret",
                    "test@example.com",
                    "mmuser",
                    "12345",
                    Set.of("user"),
                    new Address("street", "12345", "city", "state", "country"),
                    LocalDateTime.now())));
    mockMvc
        .perform(get(UserApiController.ENDPOINT + "/{userid}", userIdentifier))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andDo(document("getUser"));
  }

  @Test
  void create() throws Exception {
    UUID userIdentifier = UUID.randomUUID();
    User user =
        new User(
            userIdentifier,
            Gender.MALE,
            "Max",
            "Muster",
            "secret",
            "test@example.com",
            "mmuser",
            "12345",
            Set.of("user"),
            new Address("street", "12345", "city", "state", "country"),
            LocalDateTime.now());
    given(userService.create(any())).willReturn(user);

    CreateUserResource createUserResource = new CreateUserResource(user);
    mockMvc
        .perform(
            post(UserApiController.ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserResource)))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andDo(document("createUser"));
  }

  @Test
  void update() throws Exception {
    UUID userIdentifier = UUID.randomUUID();
    User user =
        new User(
            userIdentifier,
            Gender.MALE,
            "Max",
            "Muster",
            "secret",
            "test@example.com",
            "mmuser",
            "12345",
            Set.of("user"),
            new Address("street", "12345", "city", "state", "country"),
            LocalDateTime.now());
    given(userService.update(any(), any())).willReturn(Optional.of(user));

    CreateUserResource createUserResource = new CreateUserResource(user);
    mockMvc
        .perform(
            put(UserApiController.ENDPOINT + "/{userid}", userIdentifier)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserResource)))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andDo(document("updateUser"));
  }

  @Test
  void deleteUser() throws Exception {
    UUID userIdentifier = UUID.randomUUID();
    mockMvc
        .perform(delete(UserApiController.ENDPOINT + "/{userid}", userIdentifier))
        .andDo(print())
        .andExpect(status().is2xxSuccessful())
        .andDo(document("deleteUser"));
  }
}
