package org.shasank.library.libraryapi.controller;

import org.shasank.library.libraryapi.dto.BookDto;
import org.shasank.library.libraryapi.dto.BookModificationDto;
import org.shasank.library.libraryapi.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = BookController.BASE_API, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class BookController {

  public static final String BASE_API = "/library/api/v1/books";
  private final BookService bookService;

  @GetMapping()
  public List<BookDto> getAllBooks(@RequestParam(required = false, defaultValue = "") String authorName, @RequestParam(required = false, defaultValue = "") String bookTitle) {
    System.out.println(SecurityContextHolder.getContext().getAuthentication());
    return bookService.filterByAuthorNameAndBookTitle(authorName, bookTitle);
  }

  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public BookDto addBook(@RequestBody BookModificationDto bookDto) {
    return bookService.addBook(bookDto);
  }
}
