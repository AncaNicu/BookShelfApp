package com.bookshelf.model;
import java.util.Objects;

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
		validateNationality(nationality);
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
		validateAuthorId(authorId);
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
		validateNationality(nationality);
		this.nationality = nationality;
	}
	
	public boolean checkEquality(Author author)
	{
		if(author == null)
		{
			return false;
		}
		
		if(Objects.equals(this.firstName, author.firstName) &&
				Objects.equals(this.lastName, author.lastName) &&
				Objects.equals(this.nationality, author.nationality))
		{
			return true;
		}
		return false;
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
	
	private static void validateAuthorId(int authorId)
	{
		if(authorId < 0)
		{
			throw new IllegalArgumentException
			("The id must be valid.");
		}
	}
	
	private static void validateNationality(String nationality)
	{
	    if(nationality == null || nationality.isBlank())
	    {
	        throw new IllegalArgumentException
	        ("The author's nationality is required.");
	    }
	}
}
