package com.bookshelf.ui.console;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import com.bookshelf.enums.ReadingStatus;
import com.bookshelf.exception.DuplicateBookException;
import com.bookshelf.model.Author;
import com.bookshelf.model.Book;
import com.bookshelf.model.Genre;
import com.bookshelf.service.interfaces.IAuthorService;
import com.bookshelf.service.interfaces.IBookService;
import com.bookshelf.service.interfaces.IGenreService;

public class BooksMenu extends SecondaryMenu
{
	private final IBookService bookService;
	private final IAuthorService authorService;
	private final IGenreService genreService;
	
	public BooksMenu(IBookService bookService,
			IAuthorService authorService,
			IGenreService genreService,
			Scanner scanner)
	{
		super(scanner);
		this.bookService = bookService;
		this.authorService = authorService;
		this.genreService = genreService;
	}
	
	@Override
	public void displayMenu()
	{
		System.out.println("BOOKS MENU: ");
		System.out.println("\t1. Add a book");
		System.out.println("\t2. View all books");
		System.out.println("\t3. Find a book by its id");
		System.out.println("\t4. Update a book");
		System.out.println("\t5. Delete a book");
		System.out.println("\t6. Go back to main menu");
		System.out.println("Choose an option: ");
	}

	@Override
	protected void handleAddOption() 
	{
		System.out.println("ADD A BOOK OPERATION");
		
		if(!authorsAndGenresExist())
		{
			System.out.println("There is no genre or author to perform add operation.");
			return;
		}
		
		try
		{
			Book book = readBookData();
			bookService.addBook(book);
			System.out.println("Book added successfully with id " + book.getBookId());
		}catch(DuplicateBookException e)
		{
			System.out.println(e.getMessage());
		}catch(IllegalArgumentException e)
		{
			System.out.println(e.getMessage());
		}
	}

	@Override
	protected void handleViewAllOption() 
	{
		System.out.println("VIEW ALL THE BOOKS OPERATION");
		List<Book> books = bookService.getAllBooks();
		
		if(books.isEmpty())
		{
			System.out.println("There is no book.");
		}
		else
		{
			int contor = 1;
			for(Book b : books)
			{
				System.out.println("Book " + contor);
				displayBookDetails(b);
				System.out.println();
				contor++;
			}
		}
	}

	@Override
	protected void handleFindByIdOption() 
	{
		System.out.println("FIND A BOOK BY ITS ID OPERATION");

		int id = readPositiveInteger("Id: ");
		
		Optional<Book> book = bookService.findBookById(id);
		
		if(book.isEmpty())
		{
			System.out.println("There is no book with this id.");
		}
		else
		{
			System.out.println("The book with id " + id + " has: ");
			displayBookDetails(book.get());
		}
	}

	@Override
	protected void handleUpdateOption() 
	{
		System.out.println("UPDATE A BOOK OPERATION");
		
		if(!authorsAndGenresExist())
		{
			System.out.println("There is no genre or author to perform update operation.");
			return;
		}
		
		int id = readPositiveInteger("Id: ");
		
		System.out.print("New book data: ");
		
		try
		{
			Book book = readBookData();
			boolean updated = bookService.updateBook(id, book);
			
			if(updated)
			{
				System.out.println("Book updated successfully.");
			}
			else
			{
				System.out.println("There is no book with this id.");
			}
		}catch(DuplicateBookException e)
		{
			System.out.println(e.getMessage());
		}catch(IllegalArgumentException e)
		{
			System.out.println(e.getMessage());
		}
	}

	@Override
	protected void handleDeleteOption() 
	{
		System.out.println("DELETE A BOOK OPERATION");
		
		int id = readPositiveInteger("Id: ");
		
		boolean deleted = bookService.deleteBook(id);
		
		if(!deleted)
		{
			System.out.println("There is no book with this id.");
		}
		else
		{
			System.out.println("The book with id " + id + " was deleted successfully.");
		}
	}
	
	private Integer readRating()
	{
		System.out.print("Rating (1-5, or press Enter to skip): ");
		
		boolean validInput = false;
		String input = scanner.nextLine().trim();
		
		Integer rating = null;
		
		while(!validInput)
		{		
			if(input.equals(""))
			{
				return null;
			}
			
			try
			{
				rating = Integer.parseInt(input);
				
				if(rating < 1 || rating > 5)
				{
					System.out.println("Please enter a integer between 1 and 5.");
					System.out.print("Rating (1-5, or press Enter to skip): ");
					input = scanner.nextLine().trim();
				}
				else
				{
					validInput = true;
				}
				
			}catch (NumberFormatException e) 
			{
				System.out.println("Please enter a valid rating.");
				System.out.print("Rating (integer between 1 and 5): ");
				input = scanner.nextLine().trim();
			}
		}
		
		return rating;
	}
	
	private ReadingStatus readStatus()
	{
		System.out.println("Choose a status: ");
		System.out.println("1. to read");
		System.out.println("2. reading");
		System.out.println("3. completed");
		
		boolean validStatus = false;
		int option = handleIntInput();
		ReadingStatus status;
		
		while(!validStatus)
		{			
			if(option < 1 || option > ReadingStatus.values().length)
			{
				System.out.println("Please enter a integer between 1 and 3.");
				System.out.print("Choose a status: ");
				option = handleIntInput();
			}
			else
			{
				validStatus = true;
			}
		}
		
		status = ReadingStatus.values()[option - 1];
		return status;
	}

	private Book readBookData()
	{
		System.out.print("Title: ");
		String title = scanner.nextLine().trim();
		int publicationYear = readPositiveInteger("Publication year: ");
		int totalPages = readPositiveInteger("Total pages: ");
		ReadingStatus status = readStatus();
		int authorId = chooseAuthorId();
		int genreId = chooseGenreId();
		Integer rating = null;
		
		if(status == ReadingStatus.COMPLETED)
		{
			rating = readRating();
		}

		Book book = new Book(title, publicationYear, totalPages,
				status, authorId, genreId, rating);
		
		return book;
	}
	
	private void displayBookDetails(Book b)
	{
		System.out.println("id: " + b.getBookId());
		System.out.println("title: " + b.getTitle());
		System.out.println("publication year: " + b.getPublicationYear());
		System.out.println("total pages: " + b.getTotalPages());
		System.out.println("status: " + b.getStatus());
		System.out.println("author id: " + b.getAuthorId());
		System.out.println("genre id: " + b.getGenreId());
		System.out.println("rating: " + b.getRating());
	}
	
	private int chooseAuthorId()
	{
		System.out.println("Choose an author: ");
		
		List<Author> authors = authorService.getAllAuthors();
		
		Set<Integer> idSet = new HashSet<>();
		
		for(Author a : authors)
		{
			System.out.println(a.getAuthorId() + ". " + 
					a.getFirstName() + " " + a.getLastName());
			idSet.add(a.getAuthorId());
		}
		
		boolean validId = false;
		
		System.out.print("Author id: ");
		int id = handleIntInput();
		
		while(!validId)
		{
			if(!idSet.contains(id))
			{
				System.out.println("Please enter a valid author id.");
				System.out.print("Author id: ");
				id = handleIntInput();
			}
			else
			{
				validId = true;
			}
		}
		
		return id;
	}
	
	private int chooseGenreId()
	{
		System.out.println("Choose a genre: ");
		
		List<Genre> genres = genreService.getAllGenres();
		
		Set<Integer> idSet = new HashSet<>();
		
		for(Genre g : genres)
		{
			System.out.println(g.getGenreId() + ". " + g.getName());
			idSet.add(g.getGenreId());
		}
		
		boolean validId = false;
		
		System.out.print("Genre id: ");
		int id = handleIntInput();
		
		while(!validId)
		{
			if(!idSet.contains(id))
			{
				System.out.println("Please enter a valid genre id.");
				System.out.print("Genre id: ");
				id = handleIntInput();
			}
			else
			{
				validId = true;
			}
		}
		
		return id;
	}
	
	private boolean authorsAndGenresExist()
	{
		List<Author> authors = authorService.getAllAuthors();
		List<Genre> genres = genreService.getAllGenres();
		
		if(authors.isEmpty() || genres.isEmpty())
		{
			return false;
		}
		return true;
	}
}
