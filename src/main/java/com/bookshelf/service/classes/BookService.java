package com.bookshelf.service.classes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.bookshelf.exception.DuplicateBookException;
import com.bookshelf.model.Author;
import com.bookshelf.model.Book;
import com.bookshelf.model.Genre;
import com.bookshelf.repository.interfaces.IAuthorRepository;
import com.bookshelf.repository.interfaces.IBookRepository;
import com.bookshelf.repository.interfaces.IGenreRepository;
import com.bookshelf.service.interfaces.IBookService;

public class BookService implements IBookService 
{
	private final IBookRepository bookRepository;
	private final IAuthorRepository authorRepository;
	private final IGenreRepository genreRepository;
	
	public BookService(IBookRepository bookRepository,
			IAuthorRepository authorRepository,
			IGenreRepository genreRepository)
	{
		this.bookRepository = Objects.requireNonNull(
				bookRepository,
				"Book repository cannot be null.");
		
		this.authorRepository = Objects.requireNonNull(
				authorRepository,
				"Author repository cannot be null.");
		
		this.genreRepository = Objects.requireNonNull(
				genreRepository,
				"Genre repository cannot be null.");
	}
	
	private void invalidAuthorOrGenre(Book book)
	{
		int authorId = book.getAuthorId();
		int genreId = book.getGenreId();
		
		Optional<Author> author = authorRepository.findAuthorById(authorId);
		Optional<Genre> genre = genreRepository.findGenreById(genreId);
		
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
		
		invalidAuthorOrGenre(book);
		
		if(bookRepository.existsDuplicate(book))
		{
			throw new DuplicateBookException
			("The book already exists.");
		}
		
		return bookRepository.addBook(book);
	}

	@Override
	public Optional<Book> findBookById(int bookId) 
	{
		return bookRepository.findBookById(bookId);
	}

	@Override
	public List<Book> getAllBooks() 
	{
		return bookRepository.getAllBooks();
	}

	@Override
	public boolean updateBook(int bookId, Book updatedBook) 
	{
		if(updatedBook == null)
		{
			throw new IllegalArgumentException
			("The book to be updated cannot be null.");
		}
		
		invalidAuthorOrGenre(updatedBook);
		
		if(bookRepository.existsDuplicate(updatedBook, bookId))
		{
			throw new DuplicateBookException
			("The book with the updated data already exists.");
		}
		
		return bookRepository.updateBook(bookId, updatedBook);
	}

	@Override
	public boolean deleteBook(int bookId) 
	{
		return bookRepository.deleteBook(bookId);
	}

}
