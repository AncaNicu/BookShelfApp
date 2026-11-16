package com.bookshelf.database;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnection
{
    private static final Path CONFIG_PATH =
            Path.of("config", "db.properties");

    private DatabaseConnection()
    {
        //Utility class: prevent instantiation.
    }

    public static Connection getConnection() throws SQLException
    {
        Properties properties = new Properties();

        try (InputStream input = Files.newInputStream(CONFIG_PATH))
        {
            properties.load(input);
        }
        catch (IOException e)
        {
            throw new IllegalStateException(
                    "Could not load database configuration.", e);
        }

        String url = properties.getProperty("db.url");

        if (url == null || url.isBlank())
        {
            throw new IllegalStateException(
                    "Database URL is missing from the configuration file.");
        }

        return DriverManager.getConnection(url);
    }
}

