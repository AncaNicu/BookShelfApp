package com.bookshelf.repository.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bookshelf.model.Genre;
import com.bookshelf.repository.interfaces.IGenreRepository;

public class InMemoryGenreRepository implements IGenreRepository
{
	private final Map<Integer, Genre> genres;
	private int nextId;
	
	public InMemoryGenreRepository()
	{
		this.genres = new HashMap<>();
		this.nextId = 1;
	}
	
	@Override
	public Genre addGenre(Genre genre) 
	{
		if(genre == null)
		{
			throw new IllegalArgumentException
			("The genre to be added cannot be null.");
		}
		
		genre.setGenreId(nextId);
		this.genres.put(this.nextId, genre);
		this.nextId++;
		
		return genre;
	}

	@Override
	public Optional<Genre> findGenreById(int genreId) 
	{
		return Optional.ofNullable(this.genres.get(genreId));
	}

	@Override
	public List<Genre> getAllGenres() 
	{
		return new ArrayList<>(this.genres.values());
	}

	@Override
	public boolean updateGenre(int genreId, Genre updatedGenre) 
	{
		if(updatedGenre == null)
		{
			throw new IllegalArgumentException
			("The genre to be updated cannot be null.");
		}
		
		if(!this.genres.containsKey(genreId))
		{
			return false;
		}
		
		updatedGenre.setGenreId(genreId);
		this.genres.put(genreId, updatedGenre);
		
		return true;
	}

	@Override
	public boolean deleteGenre(int genreId) 
	{
		return this.genres.remove(genreId) != null;
	}

}
