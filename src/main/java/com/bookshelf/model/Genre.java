package com.bookshelf.model;

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
	
	private static void validateName(String name) 
	{
		if(name == null || name.isBlank())
		{
			throw new IllegalArgumentException
			("The name of the genre is required.");
		}
	}
}
