package org.shasank.library.libraryapi.repository;

import org.shasank.library.libraryapi.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class BookRepository {

  private static ConcurrentHashMap<String, List<Book>> booksByIsbn = new ConcurrentHashMap<>();
  private static int id = 0;

  public List<Book> findAll() {
    return booksByIsbn.values().stream().map(books -> books.get(0)).collect(Collectors.toList());
  }
  public Book addBook(Book book) {
    if (!booksByIsbn.containsKey(book.getIsbn())) {
      booksByIsbn.put(book.getIsbn(), new ArrayList<>());
    }
    book.setId(id++);
    booksByIsbn.get(book.getIsbn()).add(book);
    return book;
  }

  public List<Book> findByAuthorNameAndBookTitle(String authorNameSearchString, String bookTitleSearchString) {
    return booksByIsbn.values().stream()
        .map(books -> books.get(0))
        .filter(book -> book.getBookTitle().toLowerCase().contains(bookTitleSearchString.toLowerCase())
        && book.getAuthorName().toLowerCase().contains(authorNameSearchString.toLowerCase()))
        .toList();
  }
}
