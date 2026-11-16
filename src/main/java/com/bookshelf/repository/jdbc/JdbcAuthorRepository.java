package com.bookshelf.repository.jdbc;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		
		try (Connection connection = DatabaseConnection.getConnection())
		{
			PreparedStatement statement = connection.
					prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
			statement.setString(1, author.getFirstName());
			statement.setString(2, author.getLastName());
			statement.setString(3, author.getNationality());
			
			int affectedRows = statement.executeUpdate();
			
			if(affectedRows != 1)
			{
				throw new DataAccessException(
                        "Adding author did not insert exactly one row.", null);
			}
			
			try (ResultSet generatedKeys = statement.getGeneratedKeys())
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsDuplicate(Author author, int excludedAuthorId) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<Author> findAuthorById(int authorId) 
	{
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public List<Author> getAllAuthors() 
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateAuthor(int authorId, Author updatedAuthor) 
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteAuthor(int authorId) 
	{
		// TODO Auto-generated method stub
		return false;
	}

}
