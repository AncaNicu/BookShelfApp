package com.bookshelf.unitofwork.interfaces;

import com.bookshelf.repository.interfaces.IAuthorRepository;
import com.bookshelf.repository.interfaces.IBookRepository;
import com.bookshelf.repository.interfaces.IGenreRepository;

//provides repos and controls commit/rollback
public interface IUnitOfWork extends AutoCloseable
{
	//provides the author repo participating in this transaction
    IAuthorRepository authors();
    //provides the book repo participating in this transaction
    IBookRepository books();
    //provides the genre repo participating in this transaction
    IGenreRepository genres();
    //commits all the operations in this transaction
    void commit();
    //undoes all uncommitted operations
    void rollback();
    //releases all the resources used by Unit of Work
    @Override
    void close();
}
