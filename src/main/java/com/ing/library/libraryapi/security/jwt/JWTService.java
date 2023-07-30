package com.ing.library.libraryapi.security.jwt;

import com.ing.library.libraryapi.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JWTService {

  private static final ConcurrentHashMap<String, Boolean> invalidatedTokens = new ConcurrentHashMap<String, Boolean>();
//  RSA 2048bit key
  private final JWTConfigurationProperties configurationProperties;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public Date extractExpirationDate(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public boolean isTokenValid(String token, User user) {
    Claims claims = extractAllClaims(token);
    return !invalidatedTokens.containsKey(token) && null != user && claims.getExpiration().after(new Date(System.currentTimeMillis()));
  }

  public void invalidateToken(String token) {
    invalidatedTokens.put(token, Boolean.TRUE);
  }

  public boolean isTokenExpired(String token) {
    return extractClaim(token, Claims::getExpiration).before(new Date(System.currentTimeMillis()));
  }


  public String generateJWTToken(User user) {
    return Jwts.builder()
        .setSubject(user.getUsername() )
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + configurationProperties.getExpirationDuration()))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateJWTToken(User user, Map<String, Object> claims) {
    return Jwts.builder()
        .addClaims(claims)
        .setSubject(user.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + configurationProperties.getExpirationDuration()))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    return resolver.apply(this.extractAllClaims(token));
  }
  public Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(configurationProperties.getSecretKey()));
  }

}
