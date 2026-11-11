package com.bookshelf.repository.interfaces;

import java.util.List;
import java.util.Optional;

import com.bookshelf.model.Author;

public interface IAuthorRepository 
{
	Author addAuthor(Author author);
	boolean existsDuplicate(Author author);
	boolean existsDuplicate(Author author, int excludedAuthorId);
	Optional<Author> findAuthorById(int authorId);
	List<Author> getAllAuthors();
	boolean updateAuthor(int authorId, Author updatedAuthor);
	boolean deleteAuthor(int authorId);
}
