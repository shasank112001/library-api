package org.shasank.library.libraryapi.security;

import org.shasank.library.libraryapi.exception.EntityNotFoundException;
import org.shasank.library.libraryapi.model.Role;
import org.shasank.library.libraryapi.model.User;
import org.shasank.library.libraryapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

  private final UserRepository userRepository;

  @Bean
  public UserDetailsService userDetailsService() {
    return new UserDetailsService() {
      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUserName(username)
            .orElseThrow(() -> new EntityNotFoundException("No user found with this username."));

      }
    };
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService());
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
  @Bean
  public CommandLineRunner setup() {
    return args -> {
      Role librarianRole = new Role(0, "ROLE_LIBRARIAN");
      Role studentRole = new Role(1, "ROLE_STUDENT");
      Role serviceRole = new Role(2, "ROLE_SERVICE");
      userRepository.save(new User(null, "librarian101", "salt", passwordEncoder().encode("salt" + "admin"), List.of(librarianRole)));
      userRepository.save(new User(null, "studentLibrarian", "salt", passwordEncoder().encode("salt" + "studentAdmin"), List.of(librarianRole, studentRole)));
      userRepository.save(new User(null, "service", "salt", passwordEncoder().encode("salt" + "service"), List.of(serviceRole)));
      userRepository.save(new User(null, "student", "salt", passwordEncoder().encode("salt" + "student"), List.of(studentRole)));
      userRepository.printStatus();
    };
  }
}
