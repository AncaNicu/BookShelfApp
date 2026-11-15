package com.bookshelf.ui.console;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.bookshelf.exception.DuplicateAuthorException;
import com.bookshelf.model.Author;
import com.bookshelf.service.interfaces.IAuthorService;

public class AuthorsMenu extends SecondaryMenu
{
	private final IAuthorService authorService;
	
	public AuthorsMenu(IAuthorService authorService,
			Scanner scanner) 
	{
		super(scanner);
		this.authorService = authorService;
	}

	@Override
	public void displayMenu()
	{
		System.out.println("AUTHORS MENU: ");
		System.out.println("\t1. Add an author");
		System.out.println("\t2. View all authors");
		System.out.println("\t3. Find an author by its id");
		System.out.println("\t4. Update an author");
		System.out.println("\t5. Delete an author");
		System.out.println("\t6. Go back to main menu");
		System.out.println("Choose an option: ");
	}

	@Override
	protected void handleAddOption() 
	{
		System.out.println("ADD AN AUTHOR OPERATION");
		
		try
		{
			Author author = readAuthorData();
			authorService.addAuthor(author);
			System.out.println("Author added successfully with id " + author.getAuthorId());
		}catch(DuplicateAuthorException e)
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
		System.out.println("VIEW ALL THE AUTHORS OPERATION");
		List<Author> authors = authorService.getAllAuthors();
		
		if(authors.size() == 0)
		{
			System.out.println("There is no author.");
		}
		else
		{
			int contor = 1;
			for(Author a : authors)
			{
				System.out.println("Author " + contor);
				displayAuthorData(a);
				System.out.println();
				contor++;
			}
		}
	}

	@Override
	protected void handleFindByIdOption() 
	{
		System.out.println("FIND AN AUTHOR BY ITS ID OPERATION");

		int id = readPositiveInteger("Id: ");
		
		Optional<Author> author = authorService.findAuthorById(id);
		
		if(author.isEmpty())
		{
			System.out.println("There is no author with this id.");
		}
		else
		{
			System.out.println("The author with id " + id + " has: ");
			displayAuthorData(author.get());
		}
	}

	@Override
	protected void handleUpdateOption() 
	{
		System.out.println("UPDATE AN AUTHOR OPERATION");
		
		int id = readPositiveInteger("Id: ");
		
		try
		{
			Author author = readAuthorData();
			
			boolean updated = authorService.updateAuthor(id, author);
			
			if(updated)
			{
				System.out.println("Author updated successfully.");
			}
			else
			{
				System.out.println("There is no author with this id.");
			}
		}catch(DuplicateAuthorException e)
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
		System.out.println("DELETE AN AUTHOR OPERATION");
		
		int id = readPositiveInteger("Id: ");
		
		boolean deleted = authorService.deleteAuthor(id);
		
		if(!deleted)
		{
			System.out.println("There is no author with this id.");
		}
		else
		{
			System.out.println("The author with id " + id + " was deleted successfully.");
		}
	}
	
	private Author readAuthorData()
	{
		System.out.print("First name: ");
		String firstName = scanner.nextLine().trim();
		
		System.out.print("Last name: ");
		String lastName = scanner.nextLine().trim();
		
		System.out.print("Nationality: ");
		String nationality = scanner.nextLine().trim();
		
		Author author = new Author(firstName, lastName, nationality);
		
		return author;
	}
	
	private void displayAuthorData(Author a)
	{
		System.out.println("id: " + a.getAuthorId());
		System.out.println("first name: " + a.getFirstName());
		System.out.println("last name: " + a.getLastName());
		System.out.println("nationality: " + a.getNationality());
	}
}
