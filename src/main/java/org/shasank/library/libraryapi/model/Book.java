package org.shasank.library.libraryapi.model;

import org.shasank.library.libraryapi.dto.BookModificationDto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Book {
  private Integer id;
  private String authorName;
  private String bookTitle;
  private String isbn;

  public static Book from(BookModificationDto bookModificationDto) {
    return new Book(null, bookModificationDto.authorName(), bookModificationDto.bookTitle(), bookModificationDto.isbn());
  }
}
