package com.bookshelf.database;

import java.sql.Connection;
import java.sql.SQLException;

//manages the scope in which a repo uses a connection
//shared conn => borrows the conn and doesn't close it
//no shared conn => opens a new conn and closes it when it finishes
public final class JdbcConnectionScope implements AutoCloseable
{
	private final Connection connection;
	private final boolean ownsConnection;
	
	public JdbcConnectionScope(Connection sharedConnection)
		throws SQLException
	{
		//no shared conn => open a new conn
		if(sharedConnection == null)
		{
			this.connection = DatabaseConnection.getConnection();
			this.ownsConnection = true;
		}
		//shared conn
		else
		{
			this.connection = sharedConnection;
			this.ownsConnection = false;
		}
	}
	
	public Connection getConnection()
	{
		return connection;
	}

	@Override
	public void close() throws SQLException 
	{
		//no shared conn => close the conn
		if(ownsConnection)
		{
			connection.close();
		}
	}
	
	
}
