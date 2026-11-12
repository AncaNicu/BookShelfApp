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

import com.bookshelf.exception.DuplicateAuthorException;
import com.bookshelf.model.Author;
import com.bookshelf.repository.interfaces.IAuthorRepository;
import com.bookshelf.service.classes.AuthorService;

@ExtendWith(MockitoExtension.class)
public class AuthorServiceTest 
{
	@Mock
	private IAuthorRepository repoMock;
	
	private AuthorService service;
	
	@BeforeEach
	void setUp()
	{
		service = new AuthorService(repoMock);
	}
	
	//------------------ADD AN AUTHOR TESTS
	
	@Test
	public void addAuthor_WhenAuthorIsValid()
	{
		//Arrange
		Author a = new Author("Agatha", "Christie", "British");
		
		when(repoMock.existsDuplicate(a)).thenReturn(false);
		
		when(repoMock.addAuthor(a)).thenReturn(a);
		
		//Act
		Author result = service.addAuthor(a);
		
		//Assert
		//check if the reference of the result and the 
		//initial author are the same
		assertSame(a, result);
		
		//check that each method was called once
		verify(repoMock).existsDuplicate(a);
		verify(repoMock).addAuthor(a);
	}
	
	@Test
	public void addAuthor_WhenDuplicateAuthor()
	{
		//Arrange
		Author a = new Author("Agatha", "Christie", "British");
		
		when(repoMock.existsDuplicate(a)).thenReturn(true);
		
		//Act + Assert
		//chech if it throws an exception
		DuplicateAuthorException ex = assertThrows(DuplicateAuthorException.class,
				() -> service.addAuthor(a));
		
		String expectedMessage = "The author already exists.";
		String actualMessage = ex.getMessage();
		
		//with the proper message
		assertEquals(expectedMessage, actualMessage);
		
		//and repoMock.existsDuplicates() to have been called once
		verify(repoMock).existsDuplicate(a);
		//and repoMock.addAuthor() to have been called never
		verify(repoMock, never()).addAuthor(a);
	}
	
	@Test
	public void addAuthor_WhenNullAuthor()
	{		
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> service.addAuthor(null));
		
		String expectedMessage = "The author to be added cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		//check that repoMock.existsDuplicates() to have been called never
		verify(repoMock, never()).existsDuplicate(any(Author.class));
		//and repoMock.addAuthor() to have been called never
		verify(repoMock, never()).addAuthor(any(Author.class));
	}
	
	//------------------FIND AN AUTHOR TESTS
	
	@Test
	public void findAuthorById_WhenAuthorExists()
	{
		//Arrange
		int aId = 1;
		Author a = new Author("Agatha", "Christie", "British");
		
		when(repoMock.findAuthorById(aId)).thenReturn(Optional.of(a));
		
		//Act
		Optional<Author> foundAuthor = service.findAuthorById(aId);
		
		//Assert
		assertSame(a, foundAuthor.get());
		assertEquals(a.getFirstName(), foundAuthor.get().getFirstName());
		assertEquals(a.getLastName(), foundAuthor.get().getLastName());
		assertEquals(a.getNationality(), foundAuthor.get().getNationality());
		
		verify(repoMock).findAuthorById(aId);
	}
	
	@Test
	public void findAuthorById_WhenAuthorDoesNotExist()
	{
		//Assert
		int aId = 1;
		
		when(repoMock.findAuthorById(aId)).thenReturn(Optional.empty());
		
		//Act
		Optional<Author> foundAuthor = service.findAuthorById(aId);
		
		//Assert
		assertEquals(Optional.empty(), foundAuthor);
		
		verify(repoMock).findAuthorById(aId);
	}
	
	//------------------GET ALL AUTHORS TESTS
	
	@Test
	public void getAllAuthors_WhenAuthorsExist()
	{
		//Assert
		Author a1 = new Author("Agatha", "Christie", "British");
		Author a2 = new Author("Emily", "Dickinson", "British");
		
		when(repoMock.getAllAuthors()).thenReturn(List.of(a1, a2));
		
		//Act
		List<Author> authors = service.getAllAuthors();
		
		//Assert
		assertEquals(2, authors.size());
		
		assertEquals(a1.getFirstName(), authors.get(0).getFirstName());
		assertEquals(a1.getLastName(), authors.get(0).getLastName());
		assertEquals(a1.getNationality(), authors.get(0).getNationality());
		
		assertEquals(a2.getFirstName(), authors.get(1).getFirstName());
		assertEquals(a2.getLastName(), authors.get(1).getLastName());
		assertEquals(a2.getNationality(), authors.get(1).getNationality());
		
		verify(repoMock).getAllAuthors();
	}
	
	@Test
	public void getAllAuthors_WhenAuthorsDoNotExist()
	{
		//Assert
		when(repoMock.getAllAuthors()).thenReturn(List.of());
		
		//Act
		List<Author> authors = service.getAllAuthors();
		
		//Assert
		assertTrue(authors.isEmpty());
		
		verify(repoMock).getAllAuthors();
	}
	
	//------------------UPDATE AN AUTHOR TESTS
	
	@Test
	public void updateAuthor_WhenAuthorIsValid()
	{
		//Arrange
		int aId = 1;
		Author updatedA = new Author("Agatha", "Christie", "British");
		
		when(repoMock.existsDuplicate(updatedA, aId)).thenReturn(false);
		
		when(repoMock.updateAuthor(aId, updatedA)).thenReturn(true);
		
		//Act
		boolean result = service.updateAuthor(aId, updatedA);
		
		//Assert
		assertTrue(result);
		
		verify(repoMock).existsDuplicate(updatedA, aId);
		verify(repoMock).updateAuthor(aId, updatedA);
	}
	
	@Test
	public void updateAuthor_WhenDuplicateAuthor()
	{
		//Arrange
		int aId = 1;
		Author updatedA = new Author("Agatha", "Christie", "British");
		
		when(repoMock.existsDuplicate(updatedA, aId)).thenReturn(true);
		
		//Act + Assert
		//chech if it throws an exception
		DuplicateAuthorException ex = assertThrows(DuplicateAuthorException.class,
				() -> service.updateAuthor(aId, updatedA));
		
		String expectedMessage = "The author with the updated data already exists.";
		String actualMessage = ex.getMessage();
		
		//with the proper message
		assertEquals(expectedMessage, actualMessage);
		
		//and repoMock.existsDuplicates() to have been called once
		verify(repoMock).existsDuplicate(updatedA, aId);
		//and repoMock.addAuthor() to have been called never
		verify(repoMock, never()).updateAuthor(aId, updatedA);
	}
	
	@Test
	public void updateAuthor_WhenNullAuthor()
	{		
		//Arrange
		int aId = 1;
		
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> service.updateAuthor(aId, null));
		
		String expectedMessage = "The author to be updated cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		//check that repoMock.existsDuplicates() to have been called never
		verify(repoMock, never()).existsDuplicate(any(Author.class));
		//and repoMock.addAuthor() to have been called never
		verify(repoMock, never()).updateAuthor(anyInt(), any(Author.class));
	}
	
	@Test
	public void updateAuthor_WhenAuthorDoesNotExist()
	{
		//Arrange
		int aId = 1;
		Author updatedA = new Author("Agatha", "Christie", "British");
		
		when(repoMock.existsDuplicate(updatedA, aId)).thenReturn(false);
		
		when(repoMock.updateAuthor(aId, updatedA)).thenReturn(false);
		
		//Act
		boolean result = service.updateAuthor(aId, updatedA);
		
		//Assert
		assertFalse(result);
		
		verify(repoMock).existsDuplicate(updatedA, aId);
		verify(repoMock).updateAuthor(aId, updatedA);
	}
	
	//------------------DELETE AN AUTHOR TESTS
	
	@Test
	public void deleteAuthor_WhenAuthorExists()
	{
		//Arrange
		int aId = 1;
				
		when(repoMock.deleteAuthor(aId)).thenReturn(true);
		
		//Act
		boolean result = service.deleteAuthor(aId);
		
		//Assert
		assertTrue(result);
		
		verify(repoMock).deleteAuthor(aId);
	}
	
	@Test
	public void deleteAuthor_WhenAuthorDoesNotExist()
	{
		//Arrange
		int aId = 1;
				
		when(repoMock.deleteAuthor(aId)).thenReturn(false);
		
		//Act
		boolean result = service.deleteAuthor(aId);
		
		//Assert
		assertFalse(result);
		
		verify(repoMock).deleteAuthor(aId);
	}
}
