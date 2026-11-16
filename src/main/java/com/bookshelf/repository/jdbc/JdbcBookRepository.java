package com.bookshelf.repository.jdbc;

import java.util.List;
import java.util.Optional;

import com.bookshelf.model.Book;
import com.bookshelf.repository.interfaces.IBookRepository;

public class JdbcBookRepository implements IBookRepository {

	@Override
	public Book addBook(Book book) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsDuplicate(Book book) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsDuplicate(Book book, int excludedBookId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<Book> findBookById(int bookId) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<Book> getAllBooks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateBook(int bookId, Book updatedBook) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteBook(int bookId) {
		// TODO Auto-generated method stub
		return false;
	}

}
