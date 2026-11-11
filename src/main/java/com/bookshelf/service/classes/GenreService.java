package com.bookshelf.service.classes;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.bookshelf.exception.DuplicateGenreException;
import com.bookshelf.model.Genre;
import com.bookshelf.repository.interfaces.IGenreRepository;
import com.bookshelf.service.interfaces.IGenreService;

public class GenreService implements IGenreService 
{
	private final IGenreRepository genreRepository;
	
	public GenreService(IGenreRepository genreRepository)
	{
		this.genreRepository = Objects.requireNonNull(
				genreRepository,
				"Genre repository cannot be null.");
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
		return genreRepository.deleteGenre(genreId);
	}

}
