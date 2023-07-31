package org.shasank.library.libraryapi.security;

import org.shasank.library.libraryapi.controller.AuthController;
import org.shasank.library.libraryapi.controller.BookController;
import org.shasank.library.libraryapi.security.filter.JWTAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web.ignoring().requestMatchers(AuthController.BASE_API + "/**");
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager,
                                                 JWTAuthenticationFilter jwtAuthenticationFilter,
                                                 AuthenticationProvider authenticationProvider) throws Exception {
    return http.csrf(AbstractHttpConfigurer::disable)
        .authenticationManager(authenticationManager)
        .authorizeHttpRequests((requests) -> requests
            .requestMatchers(AuthController.BASE_API + "/**", "/error")
            .permitAll()
            .requestMatchers(HttpMethod.POST, BookController.BASE_API)
            .hasAuthority("ROLE_LIBRARIAN")
            .requestMatchers(HttpMethod.GET,BookController.BASE_API)
            .hasAnyAuthority("ROLE_LIBRARIAN", "ROLE_STUDENT")
            .anyRequest()
            .authenticated())
        .sessionManagement(auth -> auth
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authenticationProvider(authenticationProvider)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

        .build();
//    return http.build();
  }
}
