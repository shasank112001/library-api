package com.ing.library.libraryapi.security.jwt;

import com.ing.library.libraryapi.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
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
  private static final String SECRET_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1gsAvHdgcz0QqRSlD+AijYbwVHFNfYSc0xxLdCxNqJbYug/rNb07OKTwF810Q+j5L09qFFsODIU/FlovKQdulZertmUFFiOAp6Gk4Z2pTkHqfu8EwGgPkLzsBEp+G8lralmII7DpdKd84mlK1/0s/TdbI4CxkakgO2ROiEthRWUeQCUzNpzYZRNFNwzLNWHvBw3Estsfa8l1Y32ZAJYZmCXk8mSprJfJPJbOVxSW3rYZCf8uEO2UMnaB+Z/yZakBNPXt34qUAMW5LNErIxsX0pv69UgGwUwAWb0TT5YnJjHfpDt/rJOVc7thCJ8oXq5a7XEhNe5oK0VLiLMA9HYAaQIDAQAB";
  private static final long EXPIRATION_DURATION = 10 * 60 * 1000;

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
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DURATION))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public String generateJWTToken(User user, Map<String, Object> claims) {
    return Jwts.builder()
        .addClaims(claims)
        .setSubject(user.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_DURATION))
        .signWith(getSigningKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    return resolver.apply(this.extractAllClaims(token));
  }
  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSigningKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
  }

}
