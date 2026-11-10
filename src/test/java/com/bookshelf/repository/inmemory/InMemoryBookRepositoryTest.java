package com.bookshelf.repository.inmemory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.bookshelf.model.Book;
import com.bookshelf.repository.interfaces.IBookRepository;
import com.bookshelf.enums.ReadingStatus;

public class InMemoryBookRepositoryTest 
{
	private IBookRepository repository;
	
	@BeforeEach
	public void setUp()
	{
		repository = new InMemoryBookRepository();
	}
	
	//--------------------------ADD BOOK TESTS
	@Test
	public void addBook_AssignsId()
	{
		//Arrange
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, 1, 1, 3);
		
		//Act
		Book addedB = repository.addBook(b);
		
		//Assert
		assertEquals(1, addedB.getBookId());
		assertTrue(b.checkEquality(addedB));
	}
	
	@Test
	public void addMultipleBooks_AssignsUniqueIds()
	{
		//Arrange
		Book b1 = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, 10, 10, 3);
		Book b2 = new Book("Murder on the Orient Express", 1934, 310,
				ReadingStatus.COMPLETED, 20, 20, 4);
		
		//Act
		Book addedB1 = repository.addBook(b1);
		Book addedB2 = repository.addBook(b2);
		
		//Assert
		assertEquals(1, addedB1.getBookId());
		assertTrue(b1.checkEquality(addedB1));
		
		assertEquals(2, addedB2.getBookId());
		assertTrue(b2.checkEquality(addedB2));
		
		assertEquals(10, addedB1.getAuthorId());
		assertEquals(10, addedB1.getGenreId());

		assertEquals(20, addedB2.getAuthorId());
		assertEquals(20, addedB2.getGenreId());
	}
	
	@Test
	public void addNullBook_ThrowsException()
	{		
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> repository.addBook(null));
		
		String expectedMessage = "The book to be added cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	//--------------------------FIND A BOOK TESTS
	
	@Test
	public void findBookById_WhenBookExists()
	{
		//Arrange
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, 1, 1, 3);
		Book addedB = repository.addBook(b);
		int bId = addedB.getBookId();
		
		//Act
		Optional<Book> result = repository.findBookById(bId);
		
		//Assert
		assertTrue(result.isPresent());
		assertEquals(bId, result.get().getBookId());
		assertTrue(b.checkEquality(result.get()));
	}
	
	@Test
	public void findBookById_WhenBookDoesNotExist()
	{
		//Act
		Optional<Book> result = repository.findBookById(1);
		
		//Assert
		assertTrue(result.isEmpty());
	}
	
	//--------------------------GET ALL BOOKS TESTS
	
	@Test
	public void getAllBooks_WhenRepositoryIsEmpty()
	{
		//Act
		List<Book> books = repository.getAllBooks();
		
		//Assert
		assertTrue(books.isEmpty());
	}
	
	@Test
	public void getAllBooks_WhenBooksExist()
	{
		//Arrange
		Book b1 = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, 1, 1, 3);
		Book b2 = new Book("Murder on the Orient Express", 1934, 310,
				ReadingStatus.COMPLETED, 2, 2, 4);

		repository.addBook(b1);
		repository.addBook(b2);
		
		//Act
		List<Book> books = repository.getAllBooks();
		
		//Assert
		assertEquals(2, books.size());
		assertTrue(books.contains(b1));
		assertTrue(books.contains(b2));
	}
	
	//--------------------------UPDATE A BOOK TESTS
	@Test
	public void updateBook_WhenBookExists()
	{
		//Arrange
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, 1, 1, 3);
		Book updatedB = new Book("Murder on the Orient Express", 1934, 310,
				ReadingStatus.COMPLETED, 2, 2, 4);
		
		Book addedB = repository.addBook(b);
		int bId = addedB.getBookId();
		
		//Act + Assert
		boolean updated = repository.updateBook(bId, updatedB);
		assertTrue(updated);
		Optional<Book> result = repository.findBookById(bId);
		assertTrue(result.isPresent());
		assertEquals(bId, 
				result.get().getBookId());
		assertTrue(updatedB.checkEquality(result.get()));
	}
	
	@Test
	public void updateBook_WhenBookDoesNotExist()
	{
		//Arrange
		Book updatedB = new Book("Murder on the Orient Express", 1934, 310,
				ReadingStatus.COMPLETED, 2, 2, 4);
		
		//Act + Assert
		assertFalse(repository.updateBook(1, updatedB));
	}
	
	@Test
	public void updateNullBook_ThrowsException()
	{
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> repository.updateBook(1, null));
		
		String expectedMessage = "The book to be updated cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
	}
	
	//--------------------------DELETE A BOOK TESTS
	@Test 
	public void deleteBook_WhenBookExists()
	{
		//Arrange
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, 1, 1, 3);
		
		Book addedB = repository.addBook(b);
		int bId = addedB.getBookId();
		
		//Act + Assert
		assertTrue(repository.deleteBook(bId));
		Optional<Book> result = repository.findBookById(addedB.getBookId());
		assertTrue(result.isEmpty());
	}
	
	@Test
	public void doNotReuseAnId_WhenDeleting()
	{
		//Arrange
		Book b1 = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, 1, 1, 3);
		Book b2 = new Book("Murder on the Orient Express", 1934, 310,
				ReadingStatus.COMPLETED, 2, 2, 4);

		Book addedB1 = repository.addBook(b1);
		int bIdBeforeDeleting = addedB1.getBookId();
		
		//Act + Assert
		repository.deleteBook(bIdBeforeDeleting);
		Book addedB2 = repository.addBook(b2);
		int bIdAfterDelete = addedB2.getBookId();
		
		assertEquals(1, bIdBeforeDeleting);
		assertEquals(2, bIdAfterDelete);
	}
	
	@Test 
	public void deleteBook_WhenBookDoesNotExist()
	{	
		//Act + Assert
		assertFalse(repository.deleteBook(1));
	}
}
