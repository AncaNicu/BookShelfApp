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

import com.bookshelf.enums.ReadingStatus;
import com.bookshelf.exception.DuplicateAuthorException;
import com.bookshelf.exception.DuplicateBookException;
import com.bookshelf.model.Author;
import com.bookshelf.model.Book;
import com.bookshelf.model.Genre;
import com.bookshelf.repository.interfaces.IAuthorRepository;
import com.bookshelf.repository.interfaces.IBookRepository;
import com.bookshelf.repository.interfaces.IGenreRepository;
import com.bookshelf.service.classes.AuthorService;
import com.bookshelf.service.classes.BookService;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest 
{
	@Mock
	private IAuthorRepository authorRepoMock;
	@Mock
	private IBookRepository bookRepoMock;
	@Mock
	private IGenreRepository genreRepoMock;
	
	private BookService service;
	
	@BeforeEach
	void setUp()
	{
		service = new BookService(bookRepoMock, authorRepoMock, genreRepoMock);
	}
	
	//------------------ADD A BOOK TESTS
	
	@Test
	public void addBook_WhenBookIsValid()
	{
		//Arrange
		int authorId = 100;
		Author a = new Author("Agatha", "Christie", "British");
		int genreId = 122;
		Genre g = new Genre("Fantasy");
		
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, authorId, genreId, 3);
		
		when(authorRepoMock.findAuthorById(authorId)).thenReturn(Optional.of(a));
		when(genreRepoMock.findGenreById(genreId)).thenReturn(Optional.of(g));
		
		when(bookRepoMock.existsDuplicate(b)).thenReturn(false);
		
		when(bookRepoMock.addBook(b)).thenReturn(b);
		
		//Act
		Book result = service.addBook(b);
		
		//Assert
		assertSame(b, result);
		
		verify(bookRepoMock).existsDuplicate(b);
		verify(bookRepoMock).addBook(b);
	}
	
	@Test
	public void addBook_WhenBookIsInvalid()
	{
		//Arrange
		int invalidAuthorId = 100;
		int invalidGenreId = 111;
		
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, invalidAuthorId, invalidGenreId, 3);
		
		when(authorRepoMock.findAuthorById(invalidAuthorId)).thenReturn(Optional.empty());
		when(genreRepoMock.findGenreById(invalidGenreId)).thenReturn(Optional.empty());
						
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> service.addBook(b));
		
		String expectedMessage = "The author or the genre of the book does not exist.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		verify(bookRepoMock, never()).existsDuplicate(any(Book.class));
		verify(bookRepoMock, never()).addBook(any(Book.class));
	}
	
	@Test
	public void addBook_WhenDuplicatedBook()
	{
		//Arrange
		int authorId = 100;
		Author a = new Author("Agatha", "Christie", "British");
		int genreId = 122;
		Genre g = new Genre("Fantasy");
		
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, authorId, genreId, 3);
		
		when(authorRepoMock.findAuthorById(authorId)).thenReturn(Optional.of(a));
		when(genreRepoMock.findGenreById(genreId)).thenReturn(Optional.of(g));
		
		when(bookRepoMock.existsDuplicate(b)).thenReturn(true);

						
		//Act + Assert
		DuplicateBookException ex = assertThrows(DuplicateBookException.class,
				() -> service.addBook(b));
		
		String expectedMessage = "The book already exists.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		verify(bookRepoMock).existsDuplicate(b);
		verify(bookRepoMock, never()).addBook(any(Book.class));
	}
	
	@Test
	public void addBook_WhenBookIsNull()
	{						
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> service.addBook(null));
		
		String expectedMessage = "The book to be added cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		verify(bookRepoMock, never()).existsDuplicate(any(Book.class));
		verify(bookRepoMock, never()).addBook(any(Book.class));
	}
	
	//------------------FIND A BOOK TESTS
	
	@Test
	public void findBookById_WhenABookExists()
	{
		//Arrange
		int bId = 1;
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, 1, 1, 3);
		
		when(bookRepoMock.findBookById(bId)).thenReturn(Optional.of(b));
		
		//Act
		Optional<Book> foundBook = service.findBookById(bId);
		
		//Assert
		assertSame(b, foundBook.get());
		assertEquals(b.getTitle(), foundBook.get().getTitle());
		assertEquals(b.getPublicationYear(), foundBook.get().getPublicationYear());
		assertEquals(b.getTotalPages(), foundBook.get().getTotalPages());
		assertEquals(b.getStatus(), foundBook.get().getStatus());
		assertEquals(b.getRating(), foundBook.get().getRating());
		assertEquals(b.getAuthorId(), foundBook.get().getAuthorId());
		assertEquals(b.getGenreId(), foundBook.get().getGenreId());
		
		verify(bookRepoMock).findBookById(bId);
	}
	
	@Test
	public void findBookById_WhenBookDoesNotExist()
	{
		//Assert
		int bId = 1;
		
		when(bookRepoMock.findBookById(bId)).thenReturn(Optional.empty());
		
		//Act
		Optional<Book> foundBook = service.findBookById(bId);
		
		//Assert
		assertEquals(Optional.empty(), foundBook);
		
		verify(bookRepoMock).findBookById(bId);
	}
	
	//------------------GET ALL BOOKS TESTS
	
	@Test
	public void getAllBooks_WhenBooksExist()
	{
		//Assert
		Book b1 = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, 1, 1, 3);
		Book b2 = new Book("Murder on the Orient Express", 1934, 310,
				ReadingStatus.COMPLETED, 2, 2, 4);
		
		when(bookRepoMock.getAllBooks()).thenReturn(List.of(b1, b2));
		
		//Act
		List<Book> books = service.getAllBooks();
		
		//Assert
		assertEquals(2, books.size());
		
		assertEquals(b1.getTitle(), books.get(0).getTitle());
		assertEquals(b1.getPublicationYear(), books.get(0).getPublicationYear());
		assertEquals(b1.getTotalPages(), books.get(0).getTotalPages());
		assertEquals(b1.getStatus(), books.get(0).getStatus());
		assertEquals(b1.getRating(), books.get(0).getRating());
		assertEquals(b1.getAuthorId(), books.get(0).getAuthorId());
		assertEquals(b1.getGenreId(), books.get(0).getGenreId());
		
		assertEquals(b2.getTitle(), books.get(1).getTitle());
		assertEquals(b2.getPublicationYear(), books.get(1).getPublicationYear());
		assertEquals(b2.getTotalPages(), books.get(1).getTotalPages());
		assertEquals(b2.getStatus(), books.get(1).getStatus());
		assertEquals(b2.getRating(), books.get(1).getRating());
		assertEquals(b2.getAuthorId(), books.get(1).getAuthorId());
		assertEquals(b2.getGenreId(), books.get(1).getGenreId());
		
		verify(bookRepoMock).getAllBooks();
	}
	
	@Test
	public void getAllBooks_WhenBooksDoNotExist()
	{
		//Assert
		when(bookRepoMock.getAllBooks()).thenReturn(List.of());
		
		//Act
		List<Book> books = service.getAllBooks();
		
		//Assert
		assertTrue(books.isEmpty());
		
		verify(bookRepoMock).getAllBooks();
	}
	
	//------------------UPDATE A BOOK TESTS
	
	@Test
	public void updateBook_WhenBookIsValid()
	{
		//Arrange
		int bId = 1;
		int authorId = 100;
		Author a = new Author("Agatha", "Christie", "British");
		int genreId = 122;
		Genre g = new Genre("Fantasy");
		
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, authorId, genreId, 3);
		
		when(authorRepoMock.findAuthorById(authorId)).thenReturn(Optional.of(a));
		when(genreRepoMock.findGenreById(genreId)).thenReturn(Optional.of(g));
		
		when(bookRepoMock.existsDuplicate(b, bId)).thenReturn(false);
		
		when(bookRepoMock.updateBook(bId, b)).thenReturn(true);
		
		//Act
		boolean result = service.updateBook(bId, b);
		
		//Assert
		assertTrue(result);
		
		verify(bookRepoMock).existsDuplicate(b, bId);
		verify(bookRepoMock).updateBook(bId, b);
	}
	
	@Test
	public void updateBook_WhenBookIsInvalid()
	{
		//Arrange
		int invalidAuthorId = 100;
		int invalidGenreId = 111;
		
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, invalidAuthorId, invalidGenreId, 3);
		
		when(authorRepoMock.findAuthorById(invalidAuthorId)).thenReturn(Optional.empty());
		when(genreRepoMock.findGenreById(invalidGenreId)).thenReturn(Optional.empty());
						
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> service.addBook(b));
		
		String expectedMessage = "The author or the genre of the book does not exist.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		verify(bookRepoMock, never()).existsDuplicate(any(Book.class), anyInt());
		verify(bookRepoMock, never()).updateBook(anyInt(), any(Book.class));
	}
	
	@Test
	public void updateBook_WhenDuplicatedBook()
	{
		//Arrange
		int bId = 1;
		int authorId = 100;
		Author a = new Author("Agatha", "Christie", "British");
		int genreId = 122;
		Genre g = new Genre("Fantasy");
		
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, authorId, genreId, 3);
		
		when(authorRepoMock.findAuthorById(authorId)).thenReturn(Optional.of(a));
		when(genreRepoMock.findGenreById(genreId)).thenReturn(Optional.of(g));
		
		when(bookRepoMock.existsDuplicate(b, bId)).thenReturn(true);

						
		//Act + Assert
		DuplicateBookException ex = assertThrows(DuplicateBookException.class,
				() -> service.updateBook(bId, b));
		
		String expectedMessage = "The book with the updated data already exists.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		verify(bookRepoMock).existsDuplicate(b, bId);
		verify(bookRepoMock, never()).updateBook(anyInt(), any(Book.class));
	}
	
	@Test
	public void updateBook_WhenBookIsNull()
	{	
		//Arrange
		int bId = 1;
				
		//Act + Assert
		IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
				() -> service.updateBook(bId, null));
		
		String expectedMessage = "The book to be updated cannot be null.";
		String actualMessage = ex.getMessage();
		
		assertEquals(expectedMessage, actualMessage);
		
		verify(bookRepoMock, never()).existsDuplicate(any(Book.class), anyInt());
		verify(bookRepoMock, never()).updateBook(anyInt(), any(Book.class));
	}
	
	@Test
	public void updateBook_WhenBookDoesNotExist()
	{
		//Arrange
		int bId = 1;
		int authorId = 100;
		Author a = new Author("Agatha", "Christie", "British");
		int genreId = 122;
		Genre g = new Genre("Fantasy");
		
		Book b = new Book("The Wonderful Wizard of Oz", 1900, 200,
				ReadingStatus.COMPLETED, authorId, genreId, 3);
		
		when(authorRepoMock.findAuthorById(authorId)).thenReturn(Optional.of(a));
		when(genreRepoMock.findGenreById(genreId)).thenReturn(Optional.of(g));
		
		when(bookRepoMock.existsDuplicate(b, bId)).thenReturn(false);
		
		when(bookRepoMock.updateBook(bId, b)).thenReturn(false);
		
		//Act
		boolean result = service.updateBook(bId, b);
		
		//Assert
		assertFalse(result);
		
		verify(bookRepoMock).existsDuplicate(b, bId);
		verify(bookRepoMock).updateBook(bId, b);
	}
	
	//------------------DELETE A BOOK TESTS
	
	@Test
	public void deleteBook_WhenBookExists()
	{
		//Arrange
		int bId = 1;
				
		when(bookRepoMock.deleteBook(bId)).thenReturn(true);
		
		//Act
		boolean result = service.deleteBook(bId);
		
		//Assert
		assertTrue(result);
		
		verify(bookRepoMock).deleteBook(bId);
	}
	
	@Test
	public void deleteBook_WhenBookDoesNotExist()
	{
		//Arrange
		int bId = 1;
				
		when(bookRepoMock.deleteBook(bId)).thenReturn(false);
		
		//Act
		boolean result = service.deleteBook(bId);
		
		//Assert
		assertFalse(result);
		
		verify(bookRepoMock).deleteBook(bId);
	}
}
