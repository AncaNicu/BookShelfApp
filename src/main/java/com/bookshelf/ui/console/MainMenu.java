package com.bookshelf.ui.console;

import java.util.Scanner;

import com.bookshelf.service.interfaces.IAuthorService;
import com.bookshelf.service.interfaces.IBookService;
import com.bookshelf.service.interfaces.IGenreService;

public class MainMenu extends Menu
{
	private final IAuthorService authorService;
	private final IBookService bookService;
	private final IGenreService genreService;
	
	private static final int EXIT_OPTION = 0;
	
	public MainMenu(IAuthorService authorService,
			IBookService bookService,
			IGenreService genreService,
			Scanner scanner)
	{
		super(scanner);
		this.authorService = authorService;
		this.bookService = bookService;
		this.genreService = genreService;
	}
	
	@Override
	public void start()
	{
		int option = -1;
		Menu secondaryMenu;
		
		while(option != EXIT_OPTION)
		{
			displayMenu();
			option = handleIntInput();
			
			switch(option)
			{
			case 0:
				break;
			case 1:
				secondaryMenu = new BooksMenu(bookService, authorService,
						genreService, scanner);
				secondaryMenu.start();
				break;
			case 2:
				secondaryMenu = new AuthorsMenu(authorService, scanner);
				secondaryMenu.start();
				break;
			case 3:
				secondaryMenu = new GenresMenu(genreService, scanner);
				secondaryMenu.start();
				break;
			default:
				System.out.println("Please enter a valid option.");
				break;
			}
		}
	}

	@Override
	protected void displayMenu() 
	{
		System.out.println("CHOOSE AN OPTION:\n");
		
		System.out.println("\t1. Manage books");
		System.out.println("\t2. Manage authors");
		System.out.println("\t3. Manage genres");
		System.out.println("\t0. Exit");
	}
}
