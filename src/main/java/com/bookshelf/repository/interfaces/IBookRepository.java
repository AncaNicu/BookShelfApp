package com.bookshelf.repository.interfaces;
import java.util.List;
import java.util.Optional;

import com.bookshelf.model.Book;

public interface IBookRepository 
{
	Book addBook(Book book);
	boolean existsDuplicate(Book book);
	boolean existsDuplicate(Book book, int excludedBookId);
	Optional<Book> findBookById(int bookId);
	List<Book> getAllBooks();
	boolean updateBook(int bookId, Book updatedBook);
	boolean deleteBook(int bookId);
}
