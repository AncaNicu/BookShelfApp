package com.bookshelf.repository.jdbc;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import java.util.Objects;

import com.bookshelf.database.JdbcConnectionScope;
import com.bookshelf.exception.DataAccessException;
import com.bookshelf.model.Genre;
import com.bookshelf.repository.interfaces.IGenreRepository;

public class JdbcGenreRepository implements IGenreRepository 
{
	private final Connection sharedConnection;
	
	//constructor for a repo that manages its own conn
	public JdbcGenreRepository()
	{
		this.sharedConnection = null;
	}
	
	//constructor for a repo that uses a shared conn
	//used by Unit of Work
	public JdbcGenreRepository(Connection sharedConnection)
	{
		this.sharedConnection = Objects.requireNonNull(sharedConnection);
	}
	
	@Override
	public Genre addGenre(Genre genre) 
	{
		String sql = "INSERT INTO Genres (Name) VALUES (?);";
		
		try(JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
			PreparedStatement statement = scope.getConnection().
					prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
		{
			
			
			statement.setString(1, genre.getName());
			
			int affectedRows = statement.executeUpdate();
			
			if(affectedRows != 1)
			{
				throw new DataAccessException(
                        "Adding genre did not insert exactly one row.", null);
			}
			
			try(ResultSet generatedKeys = statement.getGeneratedKeys())
			{
				if(generatedKeys.next())
				{
					int generatedId = generatedKeys.getInt(1);
					genre.setGenreId(generatedId);
				}
				else
				{
                    throw new DataAccessException(
                            "Adding genre failed. No generated ID was returned.",
                            null);
				}
			}
			
			return genre;
		} 
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not add genre.", e);
		}
	}

	@Override
	public boolean existsDuplicate(Genre genre) 
	{
		String sql = "SELECT 1 FROM Genres WHERE Name = ?;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
			PreparedStatement statement = scope.getConnection().
					prepareStatement(sql))
		{	
			statement.setString(1, genre.getName());
			
			try(ResultSet result = statement.executeQuery())
			{
				return result.next();
			}		
		}
		catch (SQLException e)
		{
			throw new DataAccessException("Could not check if the genre already exists.", e);
		}
	}

	@Override
	public boolean existsDuplicate(Genre genre, int excludedGenreId) 
	{
		String sql = "SELECT 1 FROM Genres WHERE Name = ? AND GenreId <> ?;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql))
		{
			statement.setString(1, genre.getName());
			statement.setInt(2, excludedGenreId);
			
			try(ResultSet result = statement.executeQuery())
			{
				return result.next();
			}
		}
		catch (SQLException e)
		{
			throw new DataAccessException("Could not check if the genre already exists.", e);
		}
	}

	@Override
	public Optional<Genre> findGenreById(int genreId) 
	{
		String sql = "SELECT GenreId, Name FROM Genres WHERE GenreId = ?;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql))
		{			
			statement.setInt(1, genreId);
			
			try(ResultSet resultSet = statement.executeQuery())
			{
				if(resultSet.next())
				{
					int id = resultSet.getInt("GenreId");
					String name = resultSet.getString("Name");
					
					Genre genre = new Genre(name);
					genre.setGenreId(id);
					
					return Optional.of(genre);
				}
				else
				{
					return Optional.empty();
				}
			}
		} 
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not find a genre by its id.", e);
		}
	}

	@Override
	public List<Genre> getAllGenres() 
	{
		List<Genre> genres = new ArrayList<>();
		
		String sql = "SELECT GenreId, Name FROM Genres;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql);
				ResultSet resultSet = statement.executeQuery())
		{
			while(resultSet.next())
			{
				int genreId = resultSet.getInt("GenreId");
				String name = resultSet.getString("Name");
				
				Genre genre = new Genre(name);
				genre.setGenreId(genreId);
				
				genres.add(genre);
			}
			
			return genres;
		} 
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not get all genres.", e);
		}
	}

	@Override
	public boolean updateGenre(int genreId, Genre updatedGenre) 
	{
		String sql = "UPDATE Genres SET Name = ? WHERE GenreId = ?;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql))
		{
			statement.setString(1, updatedGenre.getName());
			statement.setInt(2, genreId);
			
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
                    "Updating genre did not change exactly one row.", null);
		} 
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not update genre.", e);
		}
	}

	@Override
	public boolean deleteGenre(int genreId) 
	{
		String sql = "DELETE FROM Genres WHERE GenreId = ?;";
		
		try (JdbcConnectionScope scope = new JdbcConnectionScope(sharedConnection);
				PreparedStatement statement = scope.getConnection().
						prepareStatement(sql))
		{
			statement.setInt(1, genreId);
			
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
                    "Deleting genre did not delete exactly one row.", null);
		} 
		catch (SQLException e) 
		{
			throw new DataAccessException("Could not delete genre.", e);
		}
	}

}
