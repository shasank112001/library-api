package com.ing.library.libraryapi.service;

import com.ing.library.libraryapi.dto.UserDto;
import com.ing.library.libraryapi.exception.EntityNotFoundException;
import com.ing.library.libraryapi.model.Role;
import com.ing.library.libraryapi.model.User;
import com.ing.library.libraryapi.repository.UserRepository;
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
    User user = this.findByName(userName);
    return (passwordEncoder.matches(user.getSalt() + password, user.getPassword()));
  }

  public UserDto save(String username, String password, List<Role> roles) {
    User user = new User(null, username, "salt", password, roles);
    return UserDto.from(this.userRepository.save(user));
  }
}
