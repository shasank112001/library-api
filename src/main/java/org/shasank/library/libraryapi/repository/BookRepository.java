package org.shasank.library.libraryapi.repository;

import org.shasank.library.libraryapi.model.Book;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class BookRepository {

  private static ConcurrentHashMap<String, List<Book>> booksByIsbn = new ConcurrentHashMap<>();
  private static int id = 0;

  public List<Book> findAll() {
    booksByIsbn.values().forEach(books -> System.out.println(books.size()));
    return booksByIsbn.values().stream().flatMap(Collection::stream).toList();
  }

  public Book addBook(Book book) {
    if (!booksByIsbn.containsKey(book.getIsbn())) {
      booksByIsbn.put(book.getIsbn(), new ArrayList<>());
    }
    book.setId(id++);
    booksByIsbn.get(book.getIsbn()).add(book);
    return book;
  }

  public List<Book> findByAuthorNameAndBookTitle(String authorNameSearchString, String bookTitleSearchString, boolean unique) {
    return (unique ? booksByIsbn.values().stream().map(books -> books.get(0)) : booksByIsbn.values().stream().flatMap(Collection::stream))
        .filter(book -> book.getBookTitle().toLowerCase().contains(bookTitleSearchString.toLowerCase())
            && book.getAuthorName().toLowerCase().contains(authorNameSearchString.toLowerCase()))
        .toList();
  }
}
