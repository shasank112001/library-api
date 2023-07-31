package org.shasank.library.libraryapi.dto;

import org.shasank.library.libraryapi.model.Role;
import org.shasank.library.libraryapi.model.User;

import java.util.List;

public record UserDto(int id, String username, List<Role> roles) {

  public static UserDto from(User user) {
    return new UserDto(user.getId(), user.getUsername(), user.getRoles());
  }

}
