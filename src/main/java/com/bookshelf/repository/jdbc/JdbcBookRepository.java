package com.bookshelf.repository.jdbc;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.bookshelf.database.JdbcConnectionScope;
import com.bookshelf.enums.ReadingStatus;
import com.bookshelf.exception.DataAccessException;
import com.bookshelf.model.Book;
import com.bookshelf.repository.interfaces.IBookRepository;

public class JdbcBookRepository implements IBookRepository {

	private final Connection sharedConnection;
	
	//constructor for a repo that manages its own conn
	public JdbcBookRepository()
	{
		this.sharedConnection = null;
	}
	
	//constructor for a repo that uses a shared conn
	//used by Unit of Work
	public JdbcBookRepository(Connection sharedConnection)
	{
		this.sharedConnection = Objects.requireNonNull(sharedConnection);
	}
	
	@Override
	public Book addBook(Book book) 
	{
		String sql = "INSERT INTO Books (Title, PublicationYear, TotalPages, " +
			"Status, AuthorId, GenreId, Rating) VALUES (?, ?, ?, ?, ?, ?, ?);";
		
		try(JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
		{
			statement.setString(1, book.getTitle());
			statement.setInt(2, book.getPublicationYear());
			statement.setInt(3, book.getTotalPages());
			statement.setString(4, book.getStatus().toString());
			statement.setInt(5, book.getAuthorId());
			statement.setInt(6, book.getGenreId());
			
			if (book.getRating() == null)
			{
			    statement.setNull(7, java.sql.Types.INTEGER);
			}
			else
			{
			    statement.setInt(7, book.getRating());
			}
			
			int affectedRows = statement.executeUpdate();
			
			if(affectedRows != 1)
			{
				throw new DataAccessException(
                        "Adding book did not insert exactly one row.", null);
			}
			
			try(ResultSet generatedKeys = statement.getGeneratedKeys())
			{
				if(generatedKeys.next())
				{
					int generatedId = generatedKeys.getInt(1);
					book.setBookId(generatedId);
				}
				else
				{
                    throw new DataAccessException(
                            "Adding book failed. No generated ID was returned.",
                            null);
				}
			}
			
			return book;
		}
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not add book.", e);
		}
	}

	@Override
	public boolean existsDuplicate(Book book) 
	{
		String sql = "SELECT 1 FROM Books WHERE Title = ? AND PublicationYear = ? " +
				"AND TotalPages = ? AND AuthorId = ? AND GenreId = ?;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql))
		{
			statement.setString(1, book.getTitle());
			statement.setInt(2, book.getPublicationYear());
			statement.setInt(3, book.getTotalPages());
			statement.setInt(4, book.getAuthorId());
			statement.setInt(5, book.getGenreId());
			
			try(ResultSet result = statement.executeQuery())
			{
				return result.next();
			}
		}
		catch (SQLException e)
		{
			throw new DataAccessException("Could not check if the book already exists.", e);
		}
	}

	@Override
	public boolean existsDuplicate(Book book, int excludedBookId) 
	{
		String sql = "SELECT 1 FROM Books WHERE Title = ? AND PublicationYear = ? " +
				"AND TotalPages = ? AND AuthorId = ? AND GenreId = ? AND BookId <> ?;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql))
		{
			statement.setString(1, book.getTitle());
			statement.setInt(2, book.getPublicationYear());
			statement.setInt(3, book.getTotalPages());
			statement.setInt(4, book.getAuthorId());
			statement.setInt(5, book.getGenreId());
			statement.setInt(6, excludedBookId);
			
			try(ResultSet result = statement.executeQuery())
			{
				return result.next();
			}
		}
		catch (SQLException e)
		{
			throw new DataAccessException("Could not check if the book already exists.", e);
		}
	}

	@Override
	public Optional<Book> findBookById(int bookId) 
	{
		String sql = "SELECT BookId, Title, PublicationYear, TotalPages, " +
				"Status, AuthorId, GenreId, Rating FROM Books WHERE BookId = ?;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql))
		{
			statement.setInt(1,  bookId);
			
			try(ResultSet result = statement.executeQuery())
			{
				if(result.next())
				{
					int id = result.getInt("BookId");
					String title = result.getString("Title");
					int publicationYear = result.getInt("PublicationYear");
					int totalPages = result.getInt("TotalPages");
					String status = result.getString("Status");
					int authorId = result.getInt("AuthorId");
					int genreId = result.getInt("GenreId");
					Integer rating = result.getObject("Rating", Integer.class);
					
					ReadingStatus rStatus = ReadingStatus.valueOf(status);
					
					Book book = new Book(title, publicationYear, totalPages,
							rStatus, authorId, genreId, rating);
					book.setBookId(id);
					
					return Optional.of(book);
				}
				else
				{
					return Optional.empty();
				}
			}
		}
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not find a book by its id.", e);
		}
	}

	@Override
	public List<Book> getAllBooks() 
	{
		List<Book> books = new ArrayList<>();
		
		String sql = "SELECT BookId, Title, PublicationYear, TotalPages, " +
				"Status, AuthorId, GenreId, Rating FROM Books;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery())
		{
			while(resultSet.next())
			{
				int id = resultSet.getInt("BookId");
				String title = resultSet.getString("Title");
				int publicationYear = resultSet.getInt("PublicationYear");
				int totalPages = resultSet.getInt("TotalPages");
				String status = resultSet.getString("Status");
				int authorId = resultSet.getInt("AuthorId");
				int genreId = resultSet.getInt("GenreId");
				Integer rating = resultSet.getObject("Rating", Integer.class);
				
				ReadingStatus rStatus = ReadingStatus.valueOf(status);
				
				Book book = new Book(title, publicationYear, totalPages,
						rStatus, authorId, genreId, rating);
				book.setBookId(id);
				
				books.add(book);
			}
			
			return books;
		}
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not get all books.", e);
		}
	}

	@Override
	public boolean updateBook(int bookId, Book updatedBook) 
	{
		String sql = "UPDATE Books SET Title = ?, PublicationYear = ?, TotalPages = ?, " +
				"Status = ?, AuthorId = ?, GenreId = ?, Rating = ? WHERE BookId = ?;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql))
		{
			statement.setString(1, updatedBook.getTitle());
			statement.setInt(2, updatedBook.getPublicationYear());
			statement.setInt(3, updatedBook.getTotalPages());
			statement.setString(4, updatedBook.getStatus().toString());
			statement.setInt(5, updatedBook.getAuthorId());
			statement.setInt(6, updatedBook.getGenreId());
			
			if (updatedBook.getRating() == null)
			{
			    statement.setNull(7, java.sql.Types.INTEGER);
			}
			else
			{
			    statement.setInt(7, updatedBook.getRating());
			}
			
			statement.setInt(8, bookId);
			
			int affectedRows = statement.executeUpdate();
			
			if(affectedRows == 0)
			{
				return false;
			}
			if(affectedRows == 1)
			{
				return true;
			}

			throw new DataAccessException(
                    "Updating book did not change exactly one row.", null);
		}
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not update book.", e);
		}
	}

	@Override
	public boolean deleteBook(int bookId) 
	{
		String sql = "DELETE FROM Books WHERE BookId = ?;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql))
		{
			statement.setInt(1, bookId);
			
			int affectedRows = statement.executeUpdate();
			
			if(affectedRows == 0)
			{
				return false;
			}
			if(affectedRows == 1)
			{
				return true;
			}
			
			throw new DataAccessException(
                    "Deleting book did not delete exactly one row.", null);
		}
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not delete book.", e);
		}
	}

}
