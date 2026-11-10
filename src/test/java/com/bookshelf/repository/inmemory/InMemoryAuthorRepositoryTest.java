package com.bookshelf.repository.inmemory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bookshelf.model.Author;
import com.bookshelf.repository.interfaces.IAuthorRepository;


public class InMemoryAuthorRepositoryTest 
{
	private IAuthorRepository repository;
	
	@BeforeEach
	public void setUp()
	{
		repository = new InMemoryAuthorRepository();
	}
	
	//--------------------------ADD AUTHOR TESTS
	
	//check if the id was correctly added to the new author
	//and the added author is the same as the parameter
	@Test
	public void addAuthor_AssignsId()
	{
		//Arrange
		Author author = new Author("Lewis", "Carroll", "British");
		
		//Act
		Author addedAuthor = repository.addAuthor(author);
		
		//Assert
		assertEquals(1, addedAuthor.getAuthorId());
		assertTrue(author.checkEquality(addedAuthor));
	}
	
	//checks if the id counter increases correctly
	//when adding multiple authors
	@Test
	public void addMultipleAuthors_AssignsUniqueIds()
	{
		//Arrange
		Author author1 = new Author("Lewis", "Carroll", "British");
		Author author2 = new Author("Emily", "Bronte", "British");
		
		//Act
		Author addedAuthor1 = repository.addAuthor(author1);
		Author addedAuthor2 = repository.addAuthor(author2);
		
		//Assert
		assertEquals(1, addedAuthor1.getAuthorId());
		assertTrue(author1.checkEquality(addedAuthor1));
		
		assertEquals(2, addedAuthor2.getAuthorId());
		assertTrue(author2.checkEquality(addedAuthor2));
	}
	
	//checks if it throws an exception
	//when adding a null author
	@Test
	public void addNullAuthor_ThrowsException()
	{		
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> repository.addAuthor(null));
		
		String expectedMessage = "The author to be added cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	//--------------------------FIND AN AUTHOR TESTS
	
	//check if an author was returned and
	//the returned author is correct
	@Test
	public void findAuthorById_WhenAuthorExists()
	{
		//Arrange
		Author author = new Author("Lewis", "Carroll", "British");
		repository.addAuthor(author);
		
		//Act
		Optional<Author> result = repository.findAuthorById(1);
		
		//Assert
		assertTrue(result.isPresent());
		assertEquals(1, result.get().getAuthorId());
		//assertEquals("Lewis", result.get().getFirstName());
		//assertEquals("Carroll", result.get().getLastName());
		//assertEquals("British", result.get().getNationality());
		assertTrue(author.checkEquality(result.get()));
	}
	
	//checks if there is no author returned 
	//when id doesn't exist
	@Test
	public void findAuthorById_WhenAuthorDoesNotExist()
	{
		//Act
		Optional<Author> result = repository.findAuthorById(1);
		
		//Assert
		assertTrue(result.isEmpty());
	}
	
	//--------------------------GET ALL AUTHORS TESTS
	
	//checks if it returns an empty list when there are no authors
	@Test
	public void getAllAuthors_WhenRepositoryIsEmpty()
	{
		//Act
		List<Author> authors = repository.getAllAuthors();
		
		//Assert
		assertTrue(authors.isEmpty());
	}
	
	//checks if the list of authors returned is correct
	@Test
	public void getAllAuthors_WhenAuthorsExist()
	{
		//Arrange
		Author author1 = new Author("Lewis", "Carroll", "British");
		Author author2 = new Author("Emily", "Bronte", "British");

		repository.addAuthor(author1);
		repository.addAuthor(author2);
		
		//Act
		List<Author> authors = repository.getAllAuthors();
		
		//Assert
		assertEquals(2, authors.size());
		assertTrue(authors.contains(author1));
		assertTrue(authors.contains(author2));
	}
	
	//--------------------------UPDATE AN AUTHOR TESTS
	
	//check if the author was updated successfully
	//when the author exists
	@Test
	public void updateAuthor_WhenAuthorExists()
	{
		//Arrange
		Author author = new Author("Lewis", "Carroll", "British");
		Author updatedAuthor = new Author("Emily", "Bronte", "British");
		
		Author addedAuthor = repository.addAuthor(author);
		int authorId = addedAuthor.getAuthorId();
		
		//Act + Assert
		boolean updated = repository.updateAuthor(authorId, updatedAuthor);
		assertTrue(updated);
		Optional<Author> result = repository.findAuthorById(authorId);
		assertTrue(result.isPresent());
		assertEquals(authorId, 
				result.get().getAuthorId());
		assertTrue(updatedAuthor.checkEquality(result.get()));
	}
	
	//check if it returns false if the id 
	//of the author to be updated doesn't exist
	@Test
	public void updateAuthor_WhenAuthorDoesNotExist()
	{
		//Arrange
		Author updatedAuthor = new Author("Emily", "Bronte", "British");
		
		//Act + Assert
		assertFalse(repository.updateAuthor(1, updatedAuthor));
	}
	
	//check if it throws an exception 
	//when the updated author is null
	@Test
	public void updateNullAuthor_ThrowsException()
	{
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> repository.updateAuthor(1, null));
		
		String expectedMessage = "The author to be updated cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	//--------------------------DELETE AN AUTHOR TESTS
	
	//check if it returns true and deletes the author
	// when deleting an existing author
	@Test 
	public void deleteAuthor_WhenAuthorExists()
	{
		//Arrange
		Author author = new Author("Lewis", "Carroll", "British");		
		Author addedAuthor = repository.addAuthor(author);
		int authorId = addedAuthor.getAuthorId();
		
		//Act + Assert
		assertTrue(repository.deleteAuthor(authorId));
		Optional<Author> result = repository.findAuthorById(addedAuthor.getAuthorId());
		assertTrue(result.isEmpty());
	}
	
	//check if the id of a new inserted author
	//is unique
	@Test
	public void doNotReuseAnId_WhenDeleting()
	{
		//Arrange
	    Author author1 = new Author("Lewis", "Carroll", "British");
	    Author author2 = new Author("Emily", "Bronte", "British");
		
	    Author addedAuthor1 = repository.addAuthor(author1);
		int authorIdBeforeDelete = addedAuthor1.getAuthorId();
		
		//Act + Assert
		repository.deleteAuthor(authorIdBeforeDelete);
		Author addedAuthor2 = repository.addAuthor(author2);
		int authorIdAfterDelete = addedAuthor2.getAuthorId();
		
		assertEquals(1, authorIdBeforeDelete);
		assertEquals(2, authorIdAfterDelete);
	}
	
	//check if it returns false when deleting
	//a non-existing author
	@Test 
	public void deleteAuthor_WhenAuthorDoesNotExist()
	{	
		//Act + Assert
		assertFalse(repository.deleteAuthor(1));
	}
}
