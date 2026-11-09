package com.bookshelf.model;

public class Author 
{
	private int authorId;
	private String firstName;
	private String lastName;
	private String nationality;
	
	public Author(String firstName, String lastName, String nationality)
	{
		validateFirstName(firstName);
		validateLastName(lastName);
		this.firstName = firstName;
		this.lastName = lastName;
		this.nationality = nationality;
	}

	public int getAuthorId() 
	{
		return authorId;
	}

	public void setAuthorId(int authorId) 
	{
		this.authorId = authorId;
	}

	public String getFirstName() 
	{
		return firstName;
	}

	public void setFirstName(String firstName) 
	{
		validateFirstName(firstName);
		this.firstName = firstName;
	}

	public String getLastName() 
	{
		return lastName;
	}

	public void setLastName(String lastName) 
	{
		validateLastName(lastName);
		this.lastName = lastName;
	}

	public String getNationality() 
	{
		return nationality;
	}

	public void setNationality(String nationality) 
	{
		this.nationality = nationality;
	}
	
	private static void validateFirstName(String firstName)
	{
		if(firstName == null || firstName.isBlank())
		{
			throw new IllegalArgumentException
			("The author's first name is required.");
		}
	}
	
	private static void validateLastName(String lastName)
	{
		if(lastName == null || lastName.isBlank())
		{
			throw new IllegalArgumentException
			("The author's last name is required.");
		}
	}
	
}
