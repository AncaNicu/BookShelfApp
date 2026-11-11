package com.bookshelf.exception;

@SuppressWarnings("serial")
public class DuplicateAuthorException extends RuntimeException
{
	public DuplicateAuthorException(String message)
	{
		super(message);
	}
}
