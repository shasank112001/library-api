package com.ing.library.libraryapi.dto;

import com.ing.library.libraryapi.model.Book;

public record BookModificationDto(String authorName, String bookTitle, String isbn){
}
