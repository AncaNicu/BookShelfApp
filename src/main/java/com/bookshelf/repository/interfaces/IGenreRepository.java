package com.bookshelf.repository.interfaces;

import java.util.List;
import java.util.Optional;

import com.bookshelf.model.Genre;

public interface IGenreRepository 
{
	Genre addGenre(Genre genre);
	Optional<Genre> findGenreById(int genreId);
	List<Genre> getAllGenres();
	boolean updateGenre(int genreId, Genre updatedGenre);
	boolean deleteGenre(int genreId);
}
