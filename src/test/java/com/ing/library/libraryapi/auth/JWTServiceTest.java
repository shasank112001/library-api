package com.ing.library.libraryapi.auth;

import com.ing.library.libraryapi.model.Role;
import com.ing.library.libraryapi.model.User;
import com.ing.library.libraryapi.security.jwt.JWTService;
import io.jsonwebtoken.Claims;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
public class JWTServiceTest {

  @Test
  @DisplayName("generateJWTToken() should create token with username as subject and correct expiration date")
  public void shouldContainUserNameAsSubjectWithCorrectIssuedAtAndExpirationDate() {
    User user = new User(0, "test", "test", "test", List.of(new Role(0, "ROLE_LIBRARIAN")));
    JWTService jwtService = new JWTService();
    String token = jwtService.generateJWTToken(user);
    assertThat(jwtService.extractAllClaims(token))
        .extracting(Claims::getSubject)
        .isNotNull()
        .isEqualTo(user.getUsername());
    assertThat(jwtService.extractAllClaims(token))
        .extracting(Claims::getIssuedAt)
        .isNotNull()
        .isInstanceOf(Date.class)
        .asInstanceOf(InstanceOfAssertFactories.DATE)
        .isBefore(new Date(System.currentTimeMillis()));
    assertThat(jwtService.extractAllClaims(token))
        .extracting(Claims::getExpiration)
        .isNotNull()
        .isInstanceOf(Date.class)
        .asInstanceOf(InstanceOfAssertFactories.DATE)
        .isEqualTo(new Date(jwtService.extractAllClaims(token).getIssuedAt().getTime() + JWTService.EXPIRATION_DURATION));

  }

}
