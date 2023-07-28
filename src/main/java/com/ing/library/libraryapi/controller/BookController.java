package com.ing.library.libraryapi.controller;

import com.ing.library.libraryapi.dto.BookDto;
import com.ing.library.libraryapi.dto.BookModificationDto;
import com.ing.library.libraryapi.model.Book;
import com.ing.library.libraryapi.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("ing/api/v1/library/books")
@RestController
@RequiredArgsConstructor
public class BookController {

  private final BookService bookService;

  @GetMapping()
  public List<BookDto> getAllBooks(@RequestParam(required = false, defaultValue = "") String authorName, @RequestParam(required = false, defaultValue = "") String bookTitle) {
    System.out.println(SecurityContextHolder.getContext().getAuthentication());
    return bookService.filterByAuthorNameAndBookTitle(authorName, bookTitle);
  }

  @PostMapping()
  public BookDto addBook(@RequestBody BookModificationDto bookDto) {
    return bookService.addBook(bookDto);
  }
}