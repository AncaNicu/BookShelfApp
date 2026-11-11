package com.bookshelf.exception;

@SuppressWarnings("serial")
public class DuplicateBookException extends RuntimeException
{
	public DuplicateBookException(String message)
	{
		super(message);
	}
}
