package org.shasank.library.libraryapi.security.handler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class UnauthorizedAccessHandler implements AccessDeniedHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
    try {
      response.setContentType("application/json;charset=UTF-8");
      response.setStatus(HttpStatus.FORBIDDEN.value());
      response.getWriter().write(new JSONObject()
          .put("timestamp", LocalDateTime.now())
          .put("status", HttpStatus.FORBIDDEN.value())
          .put("error", HttpStatus.FORBIDDEN.getReasonPhrase())
          .put("message", "Access Denied")
          .put("path", request.getServletPath())
          .toString());
    } catch (JSONException ex) {
      throw new RuntimeException(ex);
    }
  }
}
