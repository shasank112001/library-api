package com.ing.library.libraryapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.library.libraryapi.controller.AuthController;
import com.ing.library.libraryapi.controller.BookController;
import com.ing.library.libraryapi.dto.BookDto;
import com.ing.library.libraryapi.dto.BookModificationDto;
import com.ing.library.libraryapi.dto.Credentials;
import com.ing.library.libraryapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest()
public class BookControllerIT {

  @Autowired
  MockMvc mockMvc;

  @Autowired
  UserRepository userRepository;

  static ObjectMapper mapper;
  static Credentials studentCredentials;
  static Credentials librarianCredentials;
  static Credentials serviceCredentials;

  @BeforeAll
  public static void initialize() {
    mapper = new ObjectMapper();
    studentCredentials = new Credentials("student", "student");
    librarianCredentials = new Credentials("librarian101", "admin");
    serviceCredentials = new Credentials("service", "service");

  }

  @Test
  @DisplayName("Should return 403 if any user without ROLE_LIBRARIAN tries to add a book")
  void shouldReturn403IfUserTryingToAddABookIsNotALibrarian() throws Exception {
    String jwtToken= mockMvc.perform(post(AuthController.BASE_API + "/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(studentCredentials)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse().getHeader("authorization");
    mockMvc.perform(post(BookController.BASE_API)
            .header("authorization", jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(new BookModificationDto("new", "new", "new"))))
        .andExpect(status().isForbidden());
    jwtToken= mockMvc.perform(post(AuthController.BASE_API + "/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(serviceCredentials)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse().getHeader("authorization");
    mockMvc.perform(post(BookController.BASE_API)
            .header("authorization", jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(new BookModificationDto("new", "new", "new"))))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return 201 and a valid BookDto if Librarian adds a book")
  void shouldReturnBookDtoIfLibrarianAddsABook() throws Exception {
    String jwtToken= mockMvc.perform(post(AuthController.BASE_API + "/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(librarianCredentials)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse().getHeader("authorization");
    BookModificationDto modificationDto = new BookModificationDto("new", "new", "new");
    BookDto bookDto = mapper.readValue(mockMvc.perform(post(BookController.BASE_API)
            .header("authorization", jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(modificationDto)))
        .andExpect(status().isCreated()).andReturn().getResponse().getContentAsByteArray(), BookDto.class);
    assertThat(bookDto).extracting(BookDto::id).isNotNull();
    assertThat(bookDto).extracting(BookDto::bookTitle).isNotNull().isEqualTo(modificationDto.bookTitle());
    assertThat(bookDto).extracting(BookDto::authorName).isNotNull().isEqualTo(modificationDto.authorName());
    assertThat(bookDto).extracting(BookDto::isbn).isNotNull().isEqualTo(modificationDto.isbn());
  }

  @Test
  @DisplayName("Should return 403 if any user without ROLE_LIBRARIAN or ROLE_STUDENT tries to get a book")
  void shouldReturn403IfUserTryingToGetABookIsNotALibrarianOrStudent() throws Exception {
    String jwtToken= mockMvc.perform(post(AuthController.BASE_API + "/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(serviceCredentials)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse().getHeader("authorization");
    mockMvc.perform(get(BookController.BASE_API)
            .header("authorization", jwtToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());
  }

  @Test
  @DisplayName("Should return 200 if Student or Librarian tries to get a book")
  void shouldReturn200IfUserTryingToAddABookIsLibrarianOrStudent() throws Exception {
    String jwtToken= mockMvc.perform(post(AuthController.BASE_API + "/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(studentCredentials)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse().getHeader("authorization");
    mockMvc.perform(get(BookController.BASE_API)
            .header("authorization", jwtToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
    jwtToken= mockMvc.perform(post(AuthController.BASE_API + "/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(librarianCredentials)))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse().getHeader("authorization");
    mockMvc.perform(get(BookController.BASE_API)
            .header("authorization", jwtToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }
}
