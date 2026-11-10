package com.bookshelf.repository.inmemory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bookshelf.model.Genre;
import com.bookshelf.repository.interfaces.IGenreRepository;

public class InMemoryGenreRepositoryTest 
{
	private IGenreRepository repository;
	
	@BeforeEach
	public void setUp()
	{
		repository = new InMemoryGenreRepository();
	}
	
	//--------------------------ADD GENRE TESTS
	@Test
	public void addGenre_AssignsId()
	{
		//Arrange
		Genre genre = new Genre("Thriller");
		
		//Act
		Genre addedGenre = repository.addGenre(genre);
		
		//Assert
		assertEquals(1, addedGenre.getGenreId());
		assertTrue(genre.checkEquality(addedGenre));
	}
	
	@Test
	public void addMultipleGenres_AssignsUniqueIds()
	{
		//Arrange
		Genre g1 = new Genre("Crime");
		Genre g2 = new Genre("Thriller");
		
		//Act
		Genre addedG1 = repository.addGenre(g1);
		Genre addedG2 = repository.addGenre(g2);
		
		//Assert
		assertEquals(1, addedG1.getGenreId());
		assertTrue(g1.checkEquality(addedG1));
		
		assertEquals(2, addedG2.getGenreId());
		assertTrue(g2.checkEquality(addedG2));
	}
	
	@Test
	public void addNullGenre_ThrowsException()
	{
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> repository.addGenre(null));
		
		String expectedMessage = "The genre to be added cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	//--------------------------FIND A GENRE TESTS
	@Test
	public void findGenreById_WhenGenreExists()
	{
		//Arrange
		Genre g = new Genre("Thriller");
		repository.addGenre(g);
		
		//Act
		Optional<Genre> result = repository.findGenreById(1);
		
		//Assert
		assertTrue(result.isPresent());
		assertEquals(1, result.get().getGenreId());
		assertTrue(g.checkEquality(result.get()));
	}
	
	@Test
	public void findGenreById_WhenGenreDoesNotExist()
	{
		//Act
		Optional<Genre> result = repository.findGenreById(1);
		
		//Assert
		assertTrue(result.isEmpty());
	}
	
	//--------------------------GET ALL GENRES TESTS
	@Test
	public void getAllGenres_WhenRepositoryIsEmpty()
	{
		//Act
		List<Genre> genres = repository.getAllGenres();
		
		//Assert
		assertTrue(genres.isEmpty());
	}
	
	@Test
	public void getAllGenres_WhenGenresExist()
	{
		//Arrange
		Genre g1 = new Genre("Crime");
		Genre g2 = new Genre("Thriller");
		
		repository.addGenre(g1);
		repository.addGenre(g2);
		
		//Act
		List<Genre> genres = repository.getAllGenres();
		
		//Assert
		assertEquals(2, genres.size());
		assertTrue(genres.contains(g1));
		assertTrue(genres.contains(g2));
	}
	
	//--------------------------UPDATE A GENRE TESTS
	@Test
	public void updateGenre_WhenGenreExists()
	{
		//Arrange
		Genre g = new Genre("Crime");
		Genre updatedG = new Genre("Thriller");

		Genre addedG = repository.addGenre(g);
		int gId = addedG.getGenreId();
		
		//Act + Assert
		boolean updated = repository.updateGenre(gId, updatedG);
		assertTrue(updated);
		Optional<Genre> result = repository.findGenreById(gId);
		assertTrue(result.isPresent());
		assertEquals(gId, result.get().getGenreId());
		assertTrue(updatedG.checkEquality(result.get()));
	}
	
	@Test
	public void updateGenre_WhenGenreDoesNotExist()
	{
		//Arrange
		Genre updatedG = new Genre("Crime");
		
		//Act + Assert
		assertFalse(repository.updateGenre(1, updatedG));
	}
	
	@Test
	public void updateNullGenre_ThrowsException()
	{
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> repository.updateGenre(1, null));
		
		String expectedMessage = "The genre to be updated cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	//--------------------------DELETE A GENRE TESTS
	@Test 
	public void deleteGenre_WhenGenreExists()
	{
		//Arrange
		Genre g = new Genre("Thriller");
		Genre addedG = repository.addGenre(g);
		int gId = addedG.getGenreId();
		
		//Act + Assert
		assertTrue(repository.deleteGenre(gId));
		Optional<Genre> result = repository.findGenreById(gId);
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void doNotReuseAnId_WhenDeleting()
	{
		//Arrange
		Genre g1 = new Genre("Crime");
		Genre g2 = new Genre("Thriller");
		
		Genre addedG1 = repository.addGenre(g1);
		int gIdBeforeDelete = addedG1.getGenreId();
		
		//Act + Assert
		repository.deleteGenre(gIdBeforeDelete);
		Genre addedG2 = repository.addGenre(g2);
		int gIdAfterDelete = addedG2.getGenreId();
		
		assertEquals(1, gIdBeforeDelete);
		assertEquals(2, gIdAfterDelete);
	}
	
	@Test 
	public void deleteGenre_WhenGenreDoesNotExist()
	{	
		//Act + Assert
		assertFalse(repository.deleteGenre(1));
	}
}
