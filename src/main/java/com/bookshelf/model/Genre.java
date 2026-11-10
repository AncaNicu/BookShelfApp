package com.bookshelf.model;
import java.util.Objects;

public class Genre 
{
	private int genreId;
	private String name;
	
	public Genre(String name)
	{
		validateName(name);
		this.name = name;
	}

	public int getGenreId() 
	{
		return genreId;
	}

	public void setGenreId(int genreId) 
	{
		validateGenreId(genreId);
		this.genreId = genreId;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		validateName(name);	
		this.name = name;
	}
	
	public boolean checkEquality(Genre genre)
	{
		if(genre == null)
		{
			return false;
		}
		
		if(Objects.equals(this.name, genre.name))
		{
			return true;
		}
		return false;
	}
	
	private static void validateName(String name) 
	{
		if(name == null || name.isBlank())
		{
			throw new IllegalArgumentException
			("The name of the genre is required.");
		}
	}
	
	private static void validateGenreId(int genreId)
	{
		if(genreId < 0)
		{
			throw new IllegalArgumentException
			("The id must be valid.");
		}
	}
}
