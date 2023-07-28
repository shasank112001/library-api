package com.ing.library.libraryapi.repository;

import com.ing.library.libraryapi.model.Role;
import com.ing.library.libraryapi.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class UserRepository {

  private static final Map<String, User> userByUserName = new ConcurrentHashMap<>();
  private static int ID = 0;

  public Optional<User> findByUserName(String userName) {
    return userByUserName.containsKey(userName) ? Optional.of(userByUserName.get(userName)) : Optional.empty();
  }

  public User save(User user) {
    if (userByUserName.containsKey(user.getUsername())) {
      user.setId(userByUserName.get(user.getUsername()).getId());
    } else {
      user.setId(ID++);
    }
    userByUserName.put(user.getUsername(), user);
    return user;
  }

  public void printStatus() {
    System.out.println(userByUserName);
  }
}
