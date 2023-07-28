package com.ing.library.libraryapi.security.filter;


import com.ing.library.libraryapi.controller.AuthController;
import com.ing.library.libraryapi.exception.BadCredentialsException;
import com.ing.library.libraryapi.model.User;
import com.ing.library.libraryapi.security.jwt.JWTService;
import com.ing.library.libraryapi.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

  private final JWTService jwtService;
  private final UserService userService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    final String authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      filterChain.doFilter(request, response);
      return;
    }
    try {
      final String jwt = authHeader.substring(7);
      final String userName = jwtService.extractUsername(jwt);
      if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        User user = this.userService.findByName(userName);
        if (jwtService.isTokenValid(jwt, user)) {
          UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
              user,
              null,
              user.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          System.out.println("jwt is valid");
          SecurityContextHolder.getContext().setAuthentication(authToken);
        } else {
          System.out.println("jwt is invalid");
          throw new BadCredentialsException("token is invalid");
        }
      }
    } catch (ExpiredJwtException | AuthenticationException e) {
      response.sendError(401, "Invalid Token");
      return;
    }

    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request)
      throws ServletException {
    String path = request.getRequestURI();
    return path.contains(AuthController.BASE_API);
  }
}
