package com.ing.library.libraryapi.service;

import com.ing.library.libraryapi.dto.BookDto;
import com.ing.library.libraryapi.dto.BookModificationDto;
import com.ing.library.libraryapi.model.Book;
import com.ing.library.libraryapi.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

  private final BookRepository bookRepository;

  public List<BookDto> getAllBooks() {
    return this.bookRepository.findAll().stream().map(BookDto::from).toList();
  }
  public List<BookDto> filterByAuthorNameAndBookTitle(String authorName, String bookTitle) {
    return this.bookRepository.findByAuthorNameAndBookTitle(authorName, bookTitle).stream().map(BookDto::from).toList();
  }

  public BookDto addBook(BookModificationDto bookDto) {
    return BookDto.from(this.bookRepository.addBook(Book.from(bookDto)));
  }
}
