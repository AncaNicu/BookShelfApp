package com.bookshelf.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bookshelf.exception.DuplicateGenreException;
import com.bookshelf.model.Genre;
import com.bookshelf.repository.interfaces.IGenreRepository;
import com.bookshelf.service.classes.GenreService;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest 
{
	@Mock
	private IGenreRepository repoMock;
	
	private GenreService service;
	
	@BeforeEach
	void setUp()
	{
		service = new GenreService(repoMock);
	}
	
	//------------------ADD A GENRE TESTS
	
	@Test
	public void addGenre_WhenGenreIsValid()
	{
		//Arrange
		Genre g = new Genre("Fantasy");
		
		when(repoMock.existsDuplicate(g)).thenReturn(false);
		
		when(repoMock.addGenre(g)).thenReturn(g);
		
		//Act
		Genre result = service.addGenre(g);
		
		//Assert
		assertSame(g, result);
		
		verify(repoMock).existsDuplicate(g);
		verify(repoMock).addGenre(g);
	}
	
	@Test
	public void addGenre_WhenDuplicateGenre()
	{
		//Arrange
		Genre g = new Genre("Fantasy");
		
		when(repoMock.existsDuplicate(g)).thenReturn(true);
		
		//Act + Assert
		DuplicateGenreException ex = assertThrows(DuplicateGenreException.class,
				() -> service.addGenre(g));
		
		String expectedMessage = "The genre already exists.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		verify(repoMock).existsDuplicate(g);
		verify(repoMock, never()).addGenre(g);
	}
	
	@Test
	public void addGenre_WhenNullGenre()
	{		
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> service.addGenre(null));
		
		String expectedMessage = "The genre to be added cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		verify(repoMock, never()).existsDuplicate(any(Genre.class));
		verify(repoMock, never()).addGenre(any(Genre.class));
	}
	
	//------------------FIND A GENRE TESTS
	
	@Test
	public void findGenreById_WhenGenreExists()
	{
		//Assert
		int gId = 1;
		Genre g = new Genre("Fantasy");
		
		when(repoMock.findGenreById(gId)).thenReturn(Optional.of(g));
		
		//Act
		Optional<Genre> foundGenre = service.findGenreById(gId);
		
		//Assert
		assertSame(g, foundGenre.get());
		assertEquals(g.getName(), foundGenre.get().getName());
		
		verify(repoMock).findGenreById(gId);
	}
	
	@Test
	public void findGenreById_WhenGenreDoesNotExist()
	{
		//Assert
		int gId = 1;
		
		when(repoMock.findGenreById(gId)).thenReturn(Optional.empty());
		
		//Act
		Optional<Genre> foundGenre = service.findGenreById(gId);
		
		//Assert
		assertEquals(Optional.empty(), foundGenre);
		
		verify(repoMock).findGenreById(gId);
	}
	
	//------------------GET ALL GENRES TESTS
	
	@Test
	public void getAllGenres_WhenGenresExist()
	{
		//Assert
		Genre g1 = new Genre("Fantasy");
		Genre g2 = new Genre("Adventure");
		
		when(repoMock.getAllGenres()).thenReturn(List.of(g1, g2));
		
		//Act
		List<Genre> genres = service.getAllGenres();
		
		//Assert
		assertEquals(2, genres.size());
		
		assertEquals(g1.getName(), genres.get(0).getName());
		
		assertEquals(g2.getName(), genres.get(1).getName());
		
		verify(repoMock).getAllGenres();
	}
	
	@Test
	public void getAllGenres_WhenGenresDoNotExist()
	{
		//Assert
		when(repoMock.getAllGenres()).thenReturn(List.of());
		
		//Act
		List<Genre> genres = service.getAllGenres();
		
		//Assert
		assertTrue(genres.isEmpty());
		
		verify(repoMock).getAllGenres();
	}
	
	//------------------UPDATE A GENRE TESTS
	
	@Test
	public void updateGenre_WhenGenreIsValid()
	{
		//Arrange
		int gId = 1;
		Genre updatedG = new Genre("Fantasy");
		
		when(repoMock.existsDuplicate(updatedG, gId)).thenReturn(false);
		
		when(repoMock.updateGenre(gId, updatedG)).thenReturn(true);
		
		//Act
		boolean result = service.updateGenre(gId, updatedG);
		
		//Assert
		assertTrue(result);
		
		verify(repoMock).existsDuplicate(updatedG, gId);
		verify(repoMock).updateGenre(gId, updatedG);
	}
	
	@Test
	public void updateGenre_WhenDuplicateGenre()
	{
		//Arrange
		int gId = 1;
		Genre updatedG = new Genre("Fantasy");
		
		when(repoMock.existsDuplicate(updatedG, gId)).thenReturn(true);
		
		//Act + Assert
		DuplicateGenreException ex = assertThrows(DuplicateGenreException.class,
				() -> service.updateGenre(gId, updatedG));
		
		String expectedMessage = "The genre with the updated data already exists.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		verify(repoMock).existsDuplicate(updatedG, gId);
		verify(repoMock, never()).updateGenre(gId, updatedG);
	}
	
	@Test
	public void updateGenre_WhenNullGenre()
	{		
		//Arrange
		int gId = 1;
		
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> service.updateGenre(gId, null));
		
		String expectedMessage = "The genre to be updated cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		verify(repoMock, never()).existsDuplicate(any(Genre.class));
		verify(repoMock, never()).updateGenre(anyInt(), any(Genre.class));
	}
	
	@Test
	public void updateGenre_WhenGenreDoesNotExist()
	{
		//Arrange
		int gId = 1;
		Genre updatedG = new Genre("Fantasy");
		
		when(repoMock.existsDuplicate(updatedG, gId)).thenReturn(false);
		
		when(repoMock.updateGenre(gId, updatedG)).thenReturn(false);
		
		//Act
		boolean result = service.updateGenre(gId, updatedG);
		
		//Assert
		assertFalse(result);
		
		verify(repoMock).existsDuplicate(updatedG, gId);
		verify(repoMock).updateGenre(gId, updatedG);
	}
	
	//------------------DELETE A GENRE TESTS
	
	@Test
	public void deleteGenre_WhenGenreExists()
	{
		//Arrange
		int gId = 1;
				
		when(repoMock.deleteGenre(gId)).thenReturn(true);
		
		//Act
		boolean result = service.deleteGenre(gId);
		
		//Assert
		assertTrue(result);
		
		verify(repoMock).deleteGenre(gId);
	}
	
	@Test
	public void deleteGenre_WhenAuthorGenreDoesNotExist()
	{
		//Arrange
		int gId = 1;
				
		when(repoMock.deleteGenre(gId)).thenReturn(false);
		
		//Act
		boolean result = service.deleteGenre(gId);
		
		//Assert
		assertFalse(result);
		
		verify(repoMock).deleteGenre(gId);
	}
}
