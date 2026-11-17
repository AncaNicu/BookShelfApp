package com.bookshelf.service.classes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.bookshelf.exception.DuplicateAuthorException;
import com.bookshelf.model.Author;
import com.bookshelf.model.Book;
import com.bookshelf.repository.interfaces.IAuthorRepository;
import com.bookshelf.repository.interfaces.IBookRepository;
import com.bookshelf.service.interfaces.IAuthorService;

public class AuthorService implements IAuthorService 
{
	//final prevents the field to be reassigned after construction
	private final IAuthorRepository authorRepository;
	private final IBookRepository bookRepository;
	
	//constructor dependency injection
	public AuthorService(IAuthorRepository authorRepository,
			IBookRepository bookRepository)
	{
		//requireNonNull prevents the service to be created with
		//a null dependency
		this.authorRepository = Objects.requireNonNull(
				authorRepository,
				"Author repository cannot be null.");
		
		this.bookRepository = Objects.requireNonNull(
				bookRepository,
				"Book repository cannot be null.");
	}

	@Override
	public Author addAuthor(Author author)
	{
		if(author == null)
		{
			throw new IllegalArgumentException
			("The author to be added cannot be null.");
		}
		
		if(authorRepository.existsDuplicate(author))
		{
			throw new DuplicateAuthorException
			("The author already exists.");
		}
		
		return authorRepository.addAuthor(author);
	}

	@Override
	public Optional<Author> findAuthorById(int authorId) 
	{
		return authorRepository.findAuthorById(authorId);
	}

	@Override
	public List<Author> getAllAuthors() 
	{
		return authorRepository.getAllAuthors();
	}

	@Override
	public boolean updateAuthor(int authorId, Author updatedAuthor) 
	{		
		if(updatedAuthor == null)
		{
			throw new IllegalArgumentException
			("The author to be updated cannot be null.");
		}
		
		if(authorRepository.existsDuplicate(updatedAuthor, authorId))
		{
			throw new DuplicateAuthorException
			("The author with the updated data already exists.");
		}
		
		return authorRepository.updateAuthor(authorId, updatedAuthor);
	}

	@Override
	public boolean deleteAuthor(int authorId) 
	{
		//check if the author exists
	    if (authorRepository.findAuthorById(authorId).isEmpty())
	    {
	        return false;
	    }
	    
		//delete all books with the author to be deleted
		List<Book> books = bookRepository.getAllBooks();
		for(Book b : books)
		{
			if(b.getAuthorId() == authorId)
			{
				int bookId = b.getBookId();
				
				bookRepository.deleteBook(bookId);
			}
		}

		return authorRepository.deleteAuthor(authorId);
	}

}
