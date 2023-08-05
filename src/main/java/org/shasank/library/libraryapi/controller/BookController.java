package org.shasank.library.libraryapi.controller;

import lombok.RequiredArgsConstructor;
import org.shasank.library.libraryapi.dto.BookDto;
import org.shasank.library.libraryapi.dto.BookModificationDto;
import org.shasank.library.libraryapi.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = BookController.BASE_API, produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class BookController {

  public static final String BASE_API = "/library/api/v1/books";
  private final BookService bookService;

  @PreAuthorize("hasAnyRole('LIBRARIAN', 'STUDENT')")
  @GetMapping()
  public List<BookDto> getAllBooks(@RequestParam(required = false, defaultValue = "") String authorName,
                                   @RequestParam(required = false, defaultValue = "") String bookTitle,
                                   @RequestParam(required = false, defaultValue = "false") boolean unique) {
    return bookService.filterByAuthorNameAndBookTitle(authorName, bookTitle, unique);
  }

  @PreAuthorize("hasRole('LIBRARIAN')")
  @PostMapping()
  @ResponseStatus(HttpStatus.CREATED)
  public BookDto addBook(@RequestBody BookModificationDto bookDto) {
    return bookService.addBook(bookDto);
  }
}
