package org.shasank.library.libraryapi.security.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties used to create JWT tokens.
 */
@Configuration
@ConfigurationProperties(prefix = "app.security.jwt")
@Getter
@Setter
public class JWTConfigurationProperties {

  private String secretKey;

  private Integer expirationDuration;
}
