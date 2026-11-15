package com.bookshelf.ui.console;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.bookshelf.exception.DuplicateGenreException;
import com.bookshelf.model.Genre;
import com.bookshelf.service.interfaces.IGenreService;

public class GenresMenu extends SecondaryMenu
{
	private final IGenreService genreService;
	
	public GenresMenu(IGenreService genreService, 
			Scanner scanner) 
	{
		super(scanner);
		this.genreService = genreService;
	}
	
	@Override
	public void displayMenu()
	{
		System.out.println("GENRES MENU: ");
		System.out.println("\t1. Add a genre");
		System.out.println("\t2. View all genres");
		System.out.println("\t3. Find a genre by its id");
		System.out.println("\t4. Update a genre");
		System.out.println("\t5. Delete a genre");
		System.out.println("\t6. Go back to main menu");
		System.out.println("Choose an option: ");
	}

	@Override
	protected void handleAddOption() 
	{
		System.out.println("ADD A GENRE OPERATION");
		System.out.print("Name: ");
		String name = scanner.nextLine().trim();
		
		try
		{
			Genre genre = new Genre(name);
			genreService.addGenre(genre);
			System.out.println("Genre added successfully with id " + genre.getGenreId());
		}catch(DuplicateGenreException e)
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
		System.out.println("VIEW ALL THE GENRES OPERATION");
		List<Genre> genres = genreService.getAllGenres();
		
		if(genres.size() == 0)
		{
			System.out.println("There is no genre.");
		}
		else
		{
			int contor = 1;
			for(Genre g : genres)
			{
				System.out.println("Genre " + contor);
				System.out.println("id: " + g.getGenreId());
				System.out.println("name: " + g.getName());
				System.out.println();
				contor++;
			}
		}
	}

	@Override
	protected void handleFindByIdOption() 
	{
		System.out.println("FIND A GENRE BY ITS ID OPERATION");

		int id = readPositiveInteger("Id: ");
		
		Optional<Genre> genre = genreService.findGenreById(id);
		
		if(genre.isEmpty())
		{
			System.out.println("There is no genre with this id.");
		}
		else
		{
			System.out.println("The genre with id " + id + " is: " + genre.get().getName());
		}
	}

	@Override
	protected void handleUpdateOption() 
	{
		System.out.println("UPDATE A GENRE OPERATION");
		
		int id = readPositiveInteger("Id: ");
		
		System.out.print("New name: ");
		String name = scanner.nextLine().trim();
		
		try
		{
			Genre genre = new Genre(name);
			boolean updated = genreService.updateGenre(id, genre);
			
			if(updated)
			{
				System.out.println("Genre updated successfully.");
			}
			else
			{
				System.out.println("There is no genre with this id.");
			}
		}catch(DuplicateGenreException e)
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
		System.out.println("DELETE A GENRE OPERATION");
		
		int id = readPositiveInteger("Id: ");
		
		boolean deleted = genreService.deleteGenre(id);
		
		if(!deleted)
		{
			System.out.println("There is no genre with this id.");
		}
		else
		{
			System.out.println("The genre with id " + id + " was deleted successfully.");
		}
	}
}
