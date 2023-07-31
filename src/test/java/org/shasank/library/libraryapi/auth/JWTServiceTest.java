package org.shasank.library.libraryapi.auth;

import org.shasank.library.libraryapi.model.Role;
import org.shasank.library.libraryapi.model.User;
import org.shasank.library.libraryapi.security.jwt.JWTConfigurationProperties;
import org.shasank.library.libraryapi.security.jwt.JWTService;
import io.jsonwebtoken.Claims;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class JWTServiceTest {

  @Mock
  JWTConfigurationProperties jwtConfigurationProperties;

  @InjectMocks
  JWTService jwtService;

  @Test
  @DisplayName("generateJWTToken() should create token with username as subject and correct expiration date")
  public void shouldContainUserNameAsSubjectWithCorrectIssuedAtAndExpirationDate() {
    User user = new User(0, "test", "test", "test", List.of(new Role(0, "ROLE_LIBRARIAN")));
    int expirationDuration = 60000;
    when(jwtConfigurationProperties.getExpirationDuration()).thenReturn(expirationDuration);
    when(jwtConfigurationProperties.getSecretKey()).thenReturn("azdTpXmCv7g+bsroQK62nraQG1PAiEtwxoodSscl0btpPS/X5ve4/tF8j9wKWMg4rmKTC/V5Jph3jKzte7okXBfWzM4pF7IbsP+41Yj1P3ogOEX+48Knyt+Djpac2+ZqbHhHaRi4A5hFWObP8nK5S9lxj50c2s70UMu+YHa/EZHsouxwbfyv0RE70eWea1m8UWIwq3bTVnPfpsUUGdnzrg8VO/wlyAyXCF3kmHw29IbDVGGNP2ys+cKe0wGovbYt7r3s6RTh9lLTz1LuTTLuREwzKgfxbzufCtH4sPRFoYT737pewmDd8W2cxvfbR2FI5dmxlhLnb1JqDXupzPJTWEXMza8jZJciYi5W9IX+79Q=\n");
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
        .isEqualTo(new Date(jwtService.extractAllClaims(token).getIssuedAt().getTime() + expirationDuration));

  }

}
