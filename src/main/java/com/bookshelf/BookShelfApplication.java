package com.bookshelf;

import com.bookshelf.service.interfaces.*;
import com.bookshelf.ui.console.MainMenu;
import com.bookshelf.service.classes.*;
import com.bookshelf.repository.interfaces.*;
import com.bookshelf.repository.jdbc.JdbcAuthorRepository;
import com.bookshelf.repository.jdbc.JdbcBookRepository;
import com.bookshelf.repository.jdbc.JdbcGenreRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import com.bookshelf.database.DatabaseConnection;
import com.bookshelf.model.Genre;
import com.bookshelf.repository.inmemory.*;

public class BookShelfApplication 
{
	public void inMemoryOperation()
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
	
	public void jdbcOperation()
	{		
		IAuthorRepository authorRepo = new JdbcAuthorRepository();
		IBookRepository bookRepo = new JdbcBookRepository();
		IGenreRepository genreRepo = new JdbcGenreRepository();
		
		IAuthorService authorService = new AuthorService(authorRepo, bookRepo);
		IBookService bookService = new BookService(bookRepo, authorRepo, genreRepo);
		IGenreService genreService = new GenreService(genreRepo, bookRepo);
		
		Scanner scanner = new Scanner(System.in);
		
		MainMenu mainMenu = new MainMenu(authorService, bookService, 
				genreService, scanner);
		
		mainMenu.start();
		
		scanner.close();
	}
	
    public static void main(String[] args)
    {
        JdbcGenreRepository repository = new JdbcGenreRepository();

        // Use a unique name so repeated test runs don't conflict.
        String testName = "JDBC Test Genre " + System.currentTimeMillis();

        Genre genre = new Genre(testName);
        int genreId = 0;

        try
        {
            // 1. Check that the genre doesn't exist yet.
            System.out.println(
                    "Duplicate before insert: " +
                    repository.existsDuplicate(genre));

            // 2. Insert the genre.
            Genre insertedGenre = repository.addGenre(genre);
            genreId = insertedGenre.getGenreId();

            System.out.println("Inserted genre ID: " + genreId);

            // 3. Check duplicate detection.
            System.out.println(
                    "Duplicate after insert: " +
                    repository.existsDuplicate(genre));

            System.out.println(
                    "Duplicate excluding its own ID: " +
                    repository.existsDuplicate(genre, genreId));

            // 4. Find the genre by ID.
            Optional<Genre> foundGenre =
                    repository.findGenreById(genreId);

            System.out.println("Found genre: " + foundGenre);

            // 5. Retrieve all genres.
            List<Genre> genres = repository.getAllGenres();

            System.out.println("All genres:");
            for (Genre currentGenre : genres)
            {
                System.out.println(
                        currentGenre.getGenreId() + " - " +
                        currentGenre.getName());
            }

            // 6. Update the genre.
            Genre updatedGenre = new Genre(testName + " Updated");

            boolean updated =
                    repository.updateGenre(genreId, updatedGenre);

            System.out.println("Updated: " + updated);

            // Verify the update.
            System.out.println(
                    "After update: " +
                    repository.findGenreById(genreId));

            // 7. Delete the genre.
            boolean deleted = repository.deleteGenre(genreId);

            System.out.println("Deleted: " + deleted);

            // 8. Verify that it no longer exists.
            System.out.println(
                    "Found after deletion: " +
                    repository.findGenreById(genreId));
        }
        finally
        {
            // Clean up in case an earlier operation fails.
            if (genreId > 0)
            {
                repository.deleteGenre(genreId);
            }
        }
    }
}
