package com.ing.library.libraryapi.controller;

import com.ing.library.libraryapi.dto.Credentials;
import com.ing.library.libraryapi.exception.BadCredentialsException;
import com.ing.library.libraryapi.security.jwt.JWTService;
import com.ing.library.libraryapi.service.UserService;
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
  public static final String BASE_API = "/ing/api/v1/library/auth";
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
