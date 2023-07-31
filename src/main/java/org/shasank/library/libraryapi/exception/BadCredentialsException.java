package org.shasank.library.libraryapi.exception;

import org.springframework.security.core.AuthenticationException;

public class BadCredentialsException extends AuthenticationException {

  public BadCredentialsException(String message) {
    super(message);
  }

  public BadCredentialsException(String message, Throwable cause) {
    super(message, cause);
  }
}
