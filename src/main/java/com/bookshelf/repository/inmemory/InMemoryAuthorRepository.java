package com.bookshelf.repository.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.bookshelf.model.Author;
import com.bookshelf.repository.interfaces.IAuthorRepository;

public class InMemoryAuthorRepository implements IAuthorRepository 
{
	private final Map<Integer, Author> authors;
	private int nextId;
	
	public InMemoryAuthorRepository()
	{
		this.authors = new HashMap<>();
		this.nextId = 1;
	}

	@Override
	public Author addAuthor(Author author) 
	{	
		author.setAuthorId(this.nextId);
		this.authors.put(this.nextId, author);
		this.nextId++;
		
		return author;
	}

	@Override
	public Optional<Author> findAuthorById(int authorId) 
	{
		return Optional.ofNullable(this.authors.get(authorId));
	}

	@Override
	public List<Author> getAllAuthors() 
	{
		return new ArrayList<>(this.authors.values());
	}

	@Override
	public boolean updateAuthor(int authorId, Author updatedAuthor) 
	{	
		if(!this.authors.containsKey(authorId))
		{
			return false;
		}
		
		updatedAuthor.setAuthorId(authorId);
		this.authors.put(authorId, updatedAuthor);
		
		return true;
	}

	@Override
	public boolean deleteAuthor(int authorId) 
	{		
		return this.authors.remove(authorId) != null;
	}

	//checks if an author already exists
	//for add operation
	@Override
	public boolean existsDuplicate(Author author) 
	{
		for(Author a : authors.values())
		{
			if(a.checkEquality(author))
			{
				return true;
			}
		}
		
		return false;
	}

	//checks if an author already exists
	//for update operation
	@Override
	public boolean existsDuplicate(Author author, int excludedAuthorId) 
	{
		for(Author a : authors.values())
		{
			if(a.checkEquality(author) && a.getAuthorId()!= excludedAuthorId)
			{
				return true;
			}
		}
		
		return false;
	}

}
