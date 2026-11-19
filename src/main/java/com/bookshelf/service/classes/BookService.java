package com.bookshelf.service.classes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.bookshelf.exception.DuplicateBookException;
import com.bookshelf.model.Author;
import com.bookshelf.model.Book;
import com.bookshelf.model.Genre;
import com.bookshelf.service.interfaces.IBookService;
import com.bookshelf.unitofwork.interfaces.IUnitOfWork;
import com.bookshelf.unitofwork.interfaces.IUnitOfWorkFactory;

public class BookService implements IBookService 
{
	private final IUnitOfWorkFactory unitOfWorkFactory;
	
	public BookService(IUnitOfWorkFactory unitOfWorkFactory)
	{
		this.unitOfWorkFactory = Objects.requireNonNull(
				unitOfWorkFactory,
				"Unit of Work Factory cannot be null.");
	}
	
	private void invalidAuthorOrGenre(Book book, IUnitOfWork unitOfWork)
	{
		int authorId = book.getAuthorId();
		int genreId = book.getGenreId();
		
		Optional<Author> author = unitOfWork.authors().findAuthorById(authorId);
		Optional<Genre> genre = unitOfWork.genres().findGenreById(genreId);
		
		if(author.isEmpty() || genre.isEmpty())
		{
			throw new IllegalArgumentException
			("The author or the genre of the book does not exist.");
		}
	}

	@Override
	public Book addBook(Book book) 
	{	
		if(book == null)
		{
			throw new IllegalArgumentException
			("The book to be added cannot be null.");
		}
		
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			invalidAuthorOrGenre(book, unitOfWork);
			
			if(unitOfWork.books().existsDuplicate(book))
			{
				throw new DuplicateBookException
				("The book already exists.");
			}
			
			Book addedBook = unitOfWork.books().addBook(book);
			
			unitOfWork.commit();
			
			return addedBook;
		}
	}

	@Override
	public Optional<Book> findBookById(int bookId) 
	{
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			return unitOfWork.books().findBookById(bookId);
		}
	}

	@Override
	public List<Book> getAllBooks() 
	{
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			return unitOfWork.books().getAllBooks();
		}
	}

	@Override
	public boolean updateBook(int bookId, Book updatedBook) 
	{
		if(updatedBook == null)
		{
			throw new IllegalArgumentException
			("The book to be updated cannot be null.");
		}
		
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			invalidAuthorOrGenre(updatedBook, unitOfWork);
			
			if(unitOfWork.books().existsDuplicate(updatedBook, bookId))
			{
				throw new DuplicateBookException
				("The book with the updated data already exists.");
			}
			
			boolean updated =  unitOfWork.books().updateBook(bookId, updatedBook);
			
			if(updated)
			{
				unitOfWork.commit();
			}
			
			return updated;
		}
	}

	@Override
	public boolean deleteBook(int bookId) 
	{
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			boolean deleted = unitOfWork.books().deleteBook(bookId);
			
			if(deleted)
			{
				unitOfWork.commit();
			}
			
			return deleted;
		}
	}

}
