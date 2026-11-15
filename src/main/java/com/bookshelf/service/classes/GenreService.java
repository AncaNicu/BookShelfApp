package com.bookshelf.service.classes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.bookshelf.exception.DuplicateGenreException;
import com.bookshelf.model.Book;
import com.bookshelf.model.Genre;
import com.bookshelf.repository.interfaces.IBookRepository;
import com.bookshelf.repository.interfaces.IGenreRepository;
import com.bookshelf.service.interfaces.IGenreService;

public class GenreService implements IGenreService 
{
	private final IGenreRepository genreRepository;
	private final IBookRepository bookRepository;
	
	public GenreService(IGenreRepository genreRepository,
			IBookRepository bookRepository)
	{
		this.genreRepository = Objects.requireNonNull(
				genreRepository,
				"Genre repository cannot be null.");
		
		this.bookRepository = Objects.requireNonNull(
				bookRepository,
				"Book repository cannot be null.");
	}

	@Override
	public Genre addGenre(Genre genre) 
	{
		if(genre == null)
		{
			throw new IllegalArgumentException
			("The genre to be added cannot be null.");
		}
		
		if(genreRepository.existsDuplicate(genre))
		{
			throw new DuplicateGenreException
			("The genre already exists.");
		}
		
		return genreRepository.addGenre(genre);
	}

	@Override
	public Optional<Genre> findGenreById(int genreId) 
	{
		return genreRepository.findGenreById(genreId);
	}

	@Override
	public List<Genre> getAllGenres() 
	{
		return genreRepository.getAllGenres();
	}

	@Override
	public boolean updateGenre(int genreId, Genre updatedGenre) 
	{
		if(updatedGenre == null)
		{
			throw new IllegalArgumentException
			("The genre to be updated cannot be null.");
		}
		
		if(genreRepository.existsDuplicate(updatedGenre, genreId))
		{
			throw new DuplicateGenreException
			("The genre with the updated data already exists.");
		}
		
		return genreRepository.updateGenre(genreId, updatedGenre);
	}

	@Override
	public boolean deleteGenre(int genreId) 
	{		
		List<Book> books = bookRepository.getAllBooks();
		boolean deleted = genreRepository.deleteGenre(genreId);
		
		if(deleted)
		{
			for(Book b : books)
			{
				if(b.getGenreId() == genreId)
				{
					int bookId = b.getBookId();
					
					bookRepository.deleteBook(bookId);
				}
			}
		}
		
		return deleted;
	}

}
