package com.bookshelf.repository.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bookshelf.model.Book;
import com.bookshelf.repository.interfaces.IBookRepository;

public class InMemoryBookRepository implements IBookRepository 
{
	private final Map<Integer, Book> books;
	private int nextId;
	
	public InMemoryBookRepository() 
	{
		this.books = new HashMap<>();
		this.nextId = 1;
	}

	@Override
	public Book addBook(Book book) 
	{
		book.setBookId(nextId);
		this.books.put(nextId, book);
		this.nextId++;
		
		return book;
	}

	@Override
	public Optional<Book> findBookById(int bookId) 
	{
		return Optional.ofNullable(this.books.get(bookId));
	}

	@Override
	public List<Book> getAllBooks() 
	{
		return new ArrayList<>(this.books.values());
	}

	@Override
	public boolean updateBook(int bookId, Book updatedBook) 
	{
		if(!this.books.containsKey(bookId))
		{
			return false;
		}
		
		updatedBook.setBookId(bookId);
		this.books.put(bookId, updatedBook);
		
		return true;
	}

	@Override
	public boolean deleteBook(int bookId) 
	{
		return this.books.remove(bookId) != null;
	}

	@Override
	public boolean existsDuplicate(Book book) 
	{
		for(Book b : books.values())
		{
			if(b.checkEquality(book))
			{
				return true;
			}
		}
		
		return false;
	}

	@Override
	public boolean existsDuplicate(Book book, int excludedBookId) 
	{
		for(Book b : books.values())
		{
			if(b.checkEquality(book) && b.getBookId() != excludedBookId)
			{
				return true;
			}
		}
		
		return false;
	}

}
