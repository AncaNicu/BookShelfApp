package com.bookshelf.model;
import com.bookshelf.enums.ReadingStatus;
import java.time.Year;
import java.util.Objects;

public class Book 
{
	private int bookId;
	private String title;
	private int publicationYear;
	private int totalPages;
	private ReadingStatus status;
	private Integer rating;
	
	private int authorId;
	private int genreId;
	
	public Book(String title, int publicationYear, int totalPages,
			ReadingStatus status, int authorId, int genreId, Integer rating)
	{
		validateTitle(title);
		validatePublicationYear(publicationYear);
		validateTotalPages(totalPages);
		validateStatus(status);
		validateAuthorId(authorId);
		validateGenreId(genreId);
		validateRating(rating);
		
		this.title = title;
		this.publicationYear = publicationYear;
		this.totalPages = totalPages;
		this.status = status;
		this.authorId = authorId;
		this.genreId = genreId;
		this.rating = rating;
	}
	
	public Book(String title, int publicationYear, int totalPages,
			ReadingStatus status, int authorId, int genreId)
	{
		this(title, publicationYear, totalPages, 
				status, authorId, genreId, null);
	}

	public int getBookId() 
	{
		return bookId;
	}

	public void setBookId(int bookId) 
	{
		validateBookId(bookId);
		this.bookId = bookId;
	}

	public String getTitle() 
	{
		return title;
	}

	public void setTitle(String title) 
	{
		validateTitle(title);
		this.title = title;
	}

	public int getPublicationYear() 
	{
		return publicationYear;
	}

	public void setPublicationYear(int publicationYear) 
	{
		validatePublicationYear(publicationYear);
		this.publicationYear = publicationYear;
	}

	public int getTotalPages() 
	{
		return totalPages;
	}

	public void setTotalPages(int totalPages) 
	{
		validateTotalPages(totalPages);
		this.totalPages = totalPages;
	}

	public ReadingStatus getStatus() 
	{
		return status;
	}

	public void setStatus(ReadingStatus status) 
	{
		validateStatus(status);
		this.status = status;
	}

	public Integer getRating() 
	{
		return rating;
	}

	public void setRating(Integer rating) 
	{
		validateRating(rating);
		this.rating = rating;
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

	public int getGenreId() 
	{
		return genreId;
	}

	public void setGenreId(int genreId) 
	{
		validateGenreId(genreId);
		this.genreId = genreId;
	}
	
	public boolean checkEquality(Book book)
	{
		if(book == null)
		{
			return false;
		}
		
		if(Objects.equals(this.title.trim().toLowerCase(), book.title.trim().toLowerCase()) &&
				this.publicationYear == book.publicationYear &&
				this.totalPages == book.totalPages &&
				this.authorId == book.authorId &&
				this.genreId == book.genreId)
		{
			return true;
		}
		return false;
	}
	
	private static void validateTitle(String title)
	{
		if(title == null || title.isBlank())
		{
			throw new IllegalArgumentException
			("The title of the book is required.");
		}
	}
	
	private static void validatePublicationYear(int publicationYear)
	{
		if(publicationYear > Year.now().getValue() ||
				publicationYear <= 0)
		{
			throw new IllegalArgumentException
			("The year should be valid.");
		}
	}
	
	private static void validateTotalPages(int totalPages)
	{
		if(totalPages <= 0)
		{
			throw new IllegalArgumentException
			("The number of pages of the book should be valid.");
		}
	}
	
	private static void validateStatus(ReadingStatus status)
	{
		if(status == null)
		{
			throw new IllegalArgumentException
			("The status is required.");
		}
	}
	
	private static void validateAuthorId(int authorId)
	{
		if(authorId <= 0)
		{
			throw new IllegalArgumentException
			("The id of the author should be positive.");
		}
	}
	
	private static void validateGenreId(int genreId)
	{
		if(genreId <= 0)
		{
			throw new IllegalArgumentException
			("The id of the genre should be positive.");
		}
	}
	
	private static void validateRating(Integer rating)
	{
		if(rating != null && (rating < 1 || rating > 5))
		{
			throw new IllegalArgumentException
			("The rating should be null or between 1 and 5.");
		}
	}
	
	private static void validateBookId(int bookId)
	{
		if(bookId < 0)
		{
			throw new IllegalArgumentException
			("The id must be valid.");
		}
	}
}
