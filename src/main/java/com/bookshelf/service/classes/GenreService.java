package com.bookshelf.service.classes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.bookshelf.exception.DuplicateGenreException;
import com.bookshelf.model.Book;
import com.bookshelf.model.Genre;
import com.bookshelf.service.interfaces.IGenreService;
import com.bookshelf.unitofwork.interfaces.IUnitOfWork;
import com.bookshelf.unitofwork.interfaces.IUnitOfWorkFactory;

public class GenreService implements IGenreService 
{	
	private final IUnitOfWorkFactory unitOfWorkFactory;
	
	public GenreService(IUnitOfWorkFactory unitOfWorkFactory)
	{
		this.unitOfWorkFactory = Objects.requireNonNull(
				unitOfWorkFactory,
				"Unit of Work Factory cannot be null.");
	}

	@Override
	public Genre addGenre(Genre genre) 
	{
		if(genre == null)
		{
			throw new IllegalArgumentException
			("The genre to be added cannot be null.");
		}
		
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			if(unitOfWork.genres().existsDuplicate(genre))
			{
				throw new DuplicateGenreException
				("The genre already exists.");
			}
			
			Genre addedGenre = unitOfWork.genres().addGenre(genre);
			
			unitOfWork.commit();
			
			return addedGenre;
		}
	}

	@Override
	public Optional<Genre> findGenreById(int genreId) 
	{
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			return unitOfWork.genres().findGenreById(genreId);
		}
	}

	@Override
	public List<Genre> getAllGenres() 
	{
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			return unitOfWork.genres().getAllGenres();
		}
	}

	@Override
	public boolean updateGenre(int genreId, Genre updatedGenre) 
	{
		if(updatedGenre == null)
		{
			throw new IllegalArgumentException
			("The genre to be updated cannot be null.");
		}
		
		try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
		{
			if(unitOfWork.genres().existsDuplicate(updatedGenre, genreId))
			{
				throw new DuplicateGenreException
				("The genre with the updated data already exists.");
			}
			
			boolean updated = unitOfWork.genres().updateGenre(genreId, updatedGenre);
			
			if(updated)
			{
				unitOfWork.commit();
			}
			
			return updated;
		}
	}

	@Override
	public boolean deleteGenre(int genreId) 
	{	
	    try(IUnitOfWork unitOfWork = unitOfWorkFactory.create())
	    {
	    	if(unitOfWork.genres().findGenreById(genreId).isEmpty())
	    	{
	    		return false;
	    	}
	    	
	    	List<Book> books = unitOfWork.books().getAllBooks();
	    	
	    	for(Book b : books)
	    	{
	    		if(b.getGenreId() == genreId)
	    		{
	    			boolean deleted = unitOfWork.books().deleteBook(
	    					b.getBookId());
	    			
					if(!deleted)
					{
	                    throw new IllegalStateException(
	                            "Could not delete a book belonging to the genre.");
					}
	    		}
	    	}
	    	
	    	boolean deleted = unitOfWork.genres().deleteGenre(genreId);
	    	
			if(!deleted)
			{
                throw new IllegalStateException(
                        "Could not delete the genre.");
			}
			
			unitOfWork.commit();
			
			return true;
	    }
	}

}
