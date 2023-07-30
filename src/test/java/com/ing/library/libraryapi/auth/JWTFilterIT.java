package com.ing.library.libraryapi.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.library.libraryapi.controller.AuthController;
import com.ing.library.libraryapi.controller.BookController;
import com.ing.library.libraryapi.dto.Credentials;
import com.ing.library.libraryapi.repository.UserRepository;
import com.ing.library.libraryapi.security.jwt.JWTConfigurationProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest()
public class JWTFilterIT {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  UserRepository userRepository;

  static ObjectMapper mapper;

  @BeforeAll
  public static void initialize() {
    mapper = new ObjectMapper();
  }

  @Test
  @DisplayName("Should allow user to reach logout endpoint without any token")
  void testShouldAllowAccessToAuthEndpoint() throws Exception {
    mockMvc.perform(get(AuthController.BASE_API + "/logout")
        .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
  }

  @Test
  @DisplayName("Should return 401 if user does not exist")
  void testShouldReturn401IfUser() throws Exception {
    Credentials invalidCredentials = new Credentials("invalid", "invalid");
    mockMvc.perform(post(AuthController.BASE_API + "/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(invalidCredentials))).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return 401 if user exists but credentials are incorrect")
  void testShouldReturn401IfUserExistsButCredentialsAreInvalid() throws Exception {
    Credentials invalidCredentials = new Credentials("librarian101", "invalid");
    mockMvc.perform(post(AuthController.BASE_API + "/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(invalidCredentials))).andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return jwt token in authorisation header if user login is successfull")
  void testShouldAReturnJWTTokenOnSuccessfulLogin() throws Exception {
    Credentials validCredentials = new Credentials("librarian101", "admin");
    MvcResult result = mockMvc.perform(post(AuthController.BASE_API + "/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(mapper.writeValueAsString(validCredentials)))
        .andExpect(status().isOk())
        .andReturn();
    assertThat(result.getResponse().getHeader("authorization"))
        .isNotNull()
        .startsWith("Bearer ");
  }

  @Test
  @DisplayName("Should return 401 if jwt token is invalid")
  void testShouldAReturn401IfJWTTokenIsInvalidOrMalformed() throws Exception {
    Credentials validCredentials = new Credentials("librarian101", "admin");
    String jwtToken= mockMvc.perform(post(AuthController.BASE_API + "/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(validCredentials)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse().getHeader("authorization");
    jwtToken = jwtToken.substring(0, 10) + (char)(jwtToken.charAt(10) + 1) + jwtToken.substring(11);
    mockMvc.perform(get(BookController.BASE_API)
            .header("authorization", jwtToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @DisplayName("Should return 403 if jwt token is not present on protected route")
  void testShouldAReturn403IfTokenIsAbsent() throws Exception {
    mockMvc.perform(get(BookController.BASE_API)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }
}
