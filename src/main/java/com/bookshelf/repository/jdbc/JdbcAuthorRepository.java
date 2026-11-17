package com.bookshelf.repository.jdbc;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.bookshelf.database.DatabaseConnection;
import com.bookshelf.exception.DataAccessException;
import com.bookshelf.model.Author;
import com.bookshelf.repository.interfaces.IAuthorRepository;

public class JdbcAuthorRepository implements IAuthorRepository 
{

	@Override
	public Author addAuthor(Author author) 
	{
		String sql = "INSERT INTO Authors (FirstName, LastName, Nationality)\r\n"
				+ "VALUES (?, ?, ?);";
		
		try (Connection connection = DatabaseConnection.getConnection();
			PreparedStatement statement = connection.
						prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
		{
			statement.setString(1, author.getFirstName());
			statement.setString(2, author.getLastName());
			statement.setString(3, author.getNationality());
			
			int affectedRows = statement.executeUpdate();
			
			if(affectedRows != 1)
			{
				throw new DataAccessException(
                        "Adding author did not insert exactly one row.", null);
			}
			
			try(ResultSet generatedKeys = statement.getGeneratedKeys())
			{		
				if(generatedKeys.next())
				{	
					int generatedId = generatedKeys.getInt("AuthorId");
					
					author.setAuthorId(generatedId);
				}
				else
				{
                    throw new DataAccessException(
                            "Adding author failed. No generated ID was returned.",
                            null);
				}
			}
						
			return author;
		}
		catch (SQLException e)
		{
			throw new DataAccessException("Could not add author.", e);
		}
	}

	@Override
	public boolean existsDuplicate(Author author) 
	{
		String sql = "SELECT 1 FROM Authors WHERE FirstName = ? " +
				"AND LastName = ? AND Nationality = ?;";
		
		try(Connection connection = DatabaseConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setString(1,  author.getFirstName());
			statement.setString(2,  author.getLastName());
			statement.setString(3,  author.getNationality());
			
			try(ResultSet result = statement.executeQuery())
			{
				return result.next();
			}
		}
		catch (SQLException e)
		{
			throw new DataAccessException("Could not check if the author already exists.", e);
		}
	}

	@Override
	public boolean existsDuplicate(Author author, int excludedAuthorId) 
	{
		String sql = "SELECT 1 FROM Authors WHERE FirstName = ? " +
				"AND LastName = ? AND Nationality = ? AND AuthorId <> ?;";
		
		try(Connection connection = DatabaseConnection.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setString(1,  author.getFirstName());
			statement.setString(2,  author.getLastName());
			statement.setString(3,  author.getNationality());
			statement.setInt(4,  author.getAuthorId());
			
			try(ResultSet result = statement.executeQuery())
			{
				return result.next();
			}
		}
		catch (SQLException e)
		{
			throw new DataAccessException("Could not check if the author already exists.", e);
		}
	}

	@Override
	public Optional<Author> findAuthorById(int authorId) 
	{
		String sql = "SELECT AuthorId, FirstName, LastName, Nationality " +
				"FROM Authors WHERE AuthorId = ?;";
		
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setInt(1, authorId);
			
			try(ResultSet result = statement.executeQuery())
			{
				if(result.next())
				{
					int id = result.getInt("AuthorId");
					String firstName = result.getString("FirstName");
					String lastName = result.getString("LastName");
					String nationality = result.getString("Nationality");
					
					Author author = new Author(firstName, lastName, nationality);
					author.setAuthorId(id);
					
					return Optional.of(author);
				}
				else
				{
					return Optional.empty();
				}
			}
		}
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not find an author by its id.", e);
		}
	}

	@Override
	public List<Author> getAllAuthors() 
	{
		List<Author> authors = new ArrayList<>();
		
		String sql = "SELECT AuthorId, FirstName, LastName, Nationality " +
				"FROM Authors;";
		
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery())
		{
			while(resultSet.next())
			{
				int id = resultSet.getInt("AuthorId");
				String firstName = resultSet.getString("FirstName");
				String lastName = resultSet.getString("LastName");
				String nationality = resultSet.getString("Nationality");
				
				Author author = new Author(firstName, lastName, nationality);
				author.setAuthorId(id);
				
				authors.add(author);
			}
			
			return authors;
		}
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not get all authors.", e);
		}
	}

	@Override
	public boolean updateAuthor(int authorId, Author updatedAuthor) 
	{
		String sql = "UPDATE Authors SET FirstName = ?, LastName = ?, " +
				"Nationality = ? WHERE AuthorId = ?;";
		
		try(Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setString(1, updatedAuthor.getFirstName());
			statement.setString(2, updatedAuthor.getLastName());
			statement.setString(3, updatedAuthor.getNationality());
			statement.setInt(4, authorId);
			
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
                    "Updating author did not change exactly one row.", null);
		}
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not update author.", e);
		}
	}

	@Override
	public boolean deleteAuthor(int authorId) 
	{
		String sql = "DELETE FROM Authors WHERE AuthorId = ?;";
		
		try (Connection connection = DatabaseConnection.getConnection();
				PreparedStatement statement = connection.prepareStatement(sql))
		{
			statement.setInt(1,  authorId);
			
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
                    "Deleting author did not delete exactly one row.", null);
		}
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not delete author.", e);
		}
	}

}
