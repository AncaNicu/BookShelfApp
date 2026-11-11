package com.bookshelf.exception;

@SuppressWarnings("serial")
public class DuplicateGenreException extends RuntimeException
{
	public DuplicateGenreException(String message)
	{
		super(message);
	}
}
