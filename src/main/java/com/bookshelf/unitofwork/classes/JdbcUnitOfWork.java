package com.bookshelf.unitofwork.classes;

import java.sql.Connection;
import java.sql.SQLException;

import com.bookshelf.database.DatabaseConnection;
import com.bookshelf.exception.DataAccessException;
import com.bookshelf.repository.interfaces.IAuthorRepository;
import com.bookshelf.repository.interfaces.IBookRepository;
import com.bookshelf.repository.interfaces.IGenreRepository;
import com.bookshelf.repository.jdbc.JdbcAuthorRepository;
import com.bookshelf.repository.jdbc.JdbcBookRepository;
import com.bookshelf.repository.jdbc.JdbcGenreRepository;
import com.bookshelf.unitofwork.interfaces.IUnitOfWork;

public class JdbcUnitOfWork implements IUnitOfWork
{
	//the connection shared by the repos
    private final Connection connection;

    //the repos that will use the connection
    private final IAuthorRepository authorRepository;
    private final IBookRepository bookRepository;
    private final IGenreRepository genreRepository;

    //tracks the state of the transaction 
    //committed or rolled back
    private boolean completed;

    public JdbcUnitOfWork()
    {
    	Connection openedConnection = null;
    	
    	try
    	{
    		//opens a db connection
    		openedConnection = DatabaseConnection.getConnection();
    		//and sets it to not autocommit
    		//so that we can decide when to commit the changes
    		openedConnection.setAutoCommit(false);
    		
    		//sets the shared connection
    		this.connection = openedConnection;
    		
    		//initializes the repos with the shared connection
    		this.authorRepository = new JdbcAuthorRepository(connection);
    		this.bookRepository = new JdbcBookRepository(connection);
    		this.genreRepository = new JdbcGenreRepository(connection);
    	}
    	catch(SQLException e)
    	{
    		if(openedConnection != null)
    		{
    			try
    			{
    				openedConnection.close();
    			}
    			catch(SQLException closeException)
    			{
    				e.addSuppressed(closeException);
    			}
    		}
    		
            throw new DataAccessException(
                    "Could not initialize the database transaction.", e);
    	}
    }

    //returns the repo for authors
	@Override
	public IAuthorRepository authors() 
	{
		return authorRepository;
	}

	//returns the repo for books
	@Override
	public IBookRepository books() 
	{
		return bookRepository;
	}

	//returns the repo for genres
	@Override
	public IGenreRepository genres() 
	{
		return genreRepository;
	}

	@Override
	public void commit() 
	{
		if(completed)
		{
	        throw new IllegalStateException(
	                "The transaction has already been completed.");
		}
		
		try
		{
			//try to commit the changes
			connection.commit();
			completed = true;
		}
		catch(SQLException e)
		{
			try
			{
				//rollback the changes
				//if an error occurs during the transaction
				connection.rollback();
				completed = true;
			}
	        catch(SQLException rollbackException)
	        {
	        	//rollback fails => preserve the 2nd exception
	            e.addSuppressed(rollbackException);
	        }
			
	        throw new DataAccessException(
	                "Could not commit the database transaction.", e);
		}
	}

	@Override
	public void rollback() 
	{
		if(completed)
		{
			return;
		}
		
		try
		{
			connection.rollback();
			completed = true;
		}
	    catch (SQLException e)
	    {
	        throw new DataAccessException(
	                "Could not roll back the database transaction.", e);
	    }
	}

	//closes the transaction
	@Override
	public void close() 
	{
		SQLException failure = null;
		
		if(!completed)
		{
			try
			{
				connection.rollback();
				completed = true;
			}
			catch(SQLException e)
			{
				failure = e;
			}
		}
		
		try
		{
			connection.close();
		}
		catch(SQLException e)
		{
			if(failure == null)
			{
				failure = e;
			}
			else
			{
				failure.addSuppressed(e);
			}
		}
		
		if(failure != null)
		{
	        throw new DataAccessException(
	                "Could not properly close the database transaction.",
	                failure);
		}
	}
}