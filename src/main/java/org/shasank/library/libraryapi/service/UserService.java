package org.shasank.library.libraryapi.service;

import org.shasank.library.libraryapi.dto.UserDto;
import org.shasank.library.libraryapi.exception.BadCredentialsException;
import org.shasank.library.libraryapi.exception.EntityNotFoundException;
import org.shasank.library.libraryapi.model.Role;
import org.shasank.library.libraryapi.model.User;
import org.shasank.library.libraryapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User findByName(String userName) {
    return this.userRepository.findByUserName(userName)
        .orElseThrow(() -> new EntityNotFoundException("No user found with this username."));
  }

  public boolean login(String userName, String password) {
    User user = this.userRepository.findByUserName(userName).orElseThrow(() -> new BadCredentialsException("Invalid user credentials"));
    return (passwordEncoder.matches(user.getSalt() + password, user.getPassword()));
  }

  public UserDto save(String username, String password, List<Role> roles) {
    User user = new User(null, username, "salt", password, roles);
    return UserDto.from(this.userRepository.save(user));
  }
}
