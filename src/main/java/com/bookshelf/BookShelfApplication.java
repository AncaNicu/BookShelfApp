package com.bookshelf;

import com.bookshelf.service.interfaces.*;
import com.bookshelf.ui.console.MainMenu;
import com.bookshelf.service.classes.*;
import com.bookshelf.repository.interfaces.*;

import java.util.Scanner;

import com.bookshelf.repository.inmemory.*;

public class BookShelfApplication 
{
	public static void main(String[] args)
	{
		IAuthorRepository authorRepo = new InMemoryAuthorRepository();
		IBookRepository bookRepo = new InMemoryBookRepository();
		IGenreRepository genreRepo = new InMemoryGenreRepository();
		
		IAuthorService authorService = new AuthorService(authorRepo, bookRepo);
		IBookService bookService = new BookService(bookRepo, authorRepo, genreRepo);
		IGenreService genreService = new GenreService(genreRepo, bookRepo);
		
		Scanner scanner = new Scanner(System.in);
		
		MainMenu mainMenu = new MainMenu(authorService, bookService, 
				genreService, scanner);
		
		mainMenu.start();
		
		scanner.close();
	}
}
