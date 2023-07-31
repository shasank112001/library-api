package org.shasank.library.libraryapi.controller;

import org.shasank.library.libraryapi.dto.Credentials;
import org.shasank.library.libraryapi.exception.BadCredentialsException;
import org.shasank.library.libraryapi.security.jwt.JWTService;
import org.shasank.library.libraryapi.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = AuthController.BASE_API)
public class AuthController {
  public static final String BASE_API = "/library/api/v1/auth";
  private final JWTService jwtService;
  private final UserService userService;

  @PostMapping("/login")
  public ResponseEntity<String> login(@RequestBody(required = true) Credentials userCredentials) {
    try {
      if (userService.login(userCredentials.username(), userCredentials.password())) {
        var jwtToken = jwtService.generateJWTToken(userService.findByName(userCredentials.username()));
        return ResponseEntity.ok().header("Authorization", "Bearer " + jwtToken).build();
      } else {
        throw new BadCredentialsException("Invalid Credentials");
      }
    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

  }

  @GetMapping("/logout")
  @ResponseStatus(HttpStatus.OK)
  public void logout(HttpServletRequest request) {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      this.jwtService.invalidateToken(authHeader.substring(7));
    }
  }
}
