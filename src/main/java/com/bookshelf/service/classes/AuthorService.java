package com.bookshelf.service.classes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.bookshelf.exception.DuplicateAuthorException;
import com.bookshelf.model.Author;
import com.bookshelf.model.Book;
import com.bookshelf.service.interfaces.IAuthorService;
import com.bookshelf.unitofwork.interfaces.IUnitOfWork;
import com.bookshelf.unitofwork.interfaces.IUnitOfWorkFactory;

public class AuthorService implements IAuthorService 
{
	//final prevents the field to be reassigned after construction	
	private final IUnitOfWorkFactory unitOfWorkFactory;
	
	//constructor dependency injection
	public AuthorService(IUnitOfWorkFactory unitOfWorkFactory)
	{
		//requireNonNull prevents the service to be created with
		//a null dependency
		this.unitOfWorkFactory = Objects.requireNonNull(
				unitOfWorkFactory,
				"Unit of Work Factory cannot be null.");
	}

	@Override
	public Author addAuthor(Author author)
	{
		if(author == null)
		{
			throw new IllegalArgumentException
			("The author to be added cannot be null.");
		}
		
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			if(unitOfWork.authors().existsDuplicate(author))
			{
				throw new DuplicateAuthorException
				("The author already exists.");
			}
			
			Author addedAuthor = unitOfWork.authors().addAuthor(author);
			
			//commit the changes
			unitOfWork.commit();
			
			//return the added author
			return addedAuthor;
		}
	}

	@Override
	public Optional<Author> findAuthorById(int authorId) 
	{
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			return unitOfWork.authors().findAuthorById(authorId);
		}
	}

	@Override
	public List<Author> getAllAuthors() 
	{
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			return unitOfWork.authors().getAllAuthors();
		}
	}

	@Override
	public boolean updateAuthor(int authorId, Author updatedAuthor) 
	{		
		if(updatedAuthor == null)
		{
			throw new IllegalArgumentException
			("The author to be updated cannot be null.");
		}
		
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			if(unitOfWork.authors().existsDuplicate(updatedAuthor, authorId))
			{
				throw new DuplicateAuthorException
				("The author with the updated data already exists.");
			}
			
			boolean updated = unitOfWork.authors().updateAuthor(authorId, updatedAuthor);
			
			if(updated)
			{
				unitOfWork.commit();
			}
			
			return updated;
		}
	}

	@Override
	public boolean deleteAuthor(int authorId) 
	{
		//try to create a new unit of work
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			//check if the author exists
			if(unitOfWork.authors().findAuthorById(authorId).isEmpty())
			{
				return false;
			}
			
			//gets the books by the author and deletes them
			List<Book> books = unitOfWork.books().getAllBooks();
			
			for(Book b : books)
			{
				if(b.getAuthorId() == authorId)
				{
					boolean deleted = unitOfWork.books().deleteBook(
							b.getBookId());
					
					//throws an exception to prevfent the transaction
					//from being committed
					if(!deleted)
					{
	                    throw new IllegalStateException(
	                            "Could not delete a book belonging to the author.");
					}
				}
			}
			
			//deletes the author
			boolean deleted = unitOfWork.authors().deleteAuthor(authorId);
			
			//throws an exception to prevfent the transaction
			//from being committed
			if(!deleted)
			{
                throw new IllegalStateException(
                        "Could not delete the author.");
			}
			
			//commit the changes
			unitOfWork.commit();
			
			return true;
		}
	}
}
