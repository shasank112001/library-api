package org.shasank.library.libraryapi.service;

import org.shasank.library.libraryapi.dto.BookDto;
import org.shasank.library.libraryapi.dto.BookModificationDto;
import org.shasank.library.libraryapi.model.Book;
import org.shasank.library.libraryapi.repository.BookRepository;
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
  public List<BookDto> filterByAuthorNameAndBookTitle(String authorName, String bookTitle, boolean unique) {
    return this.bookRepository.findByAuthorNameAndBookTitle(authorName, bookTitle, unique).stream().map(BookDto::from).toList();
  }

  public BookDto addBook(BookModificationDto bookDto) {
    return BookDto.from(this.bookRepository.addBook(Book.from(bookDto)));
  }
}
