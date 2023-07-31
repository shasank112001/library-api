package org.shasank.library.libraryapi.dto;

import org.shasank.library.libraryapi.model.Book;

public record BookDto (Integer id, String authorName, String bookTitle, String isbn){

  public static BookDto from(Book book) {
    return new BookDto(book.getId(), book.getAuthorName(), book.getBookTitle(), book.getIsbn());
  }
}
