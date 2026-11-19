package com.bookshelf.unitofwork.classes;

import com.bookshelf.unitofwork.interfaces.IUnitOfWork;
import com.bookshelf.unitofwork.interfaces.IUnitOfWorkFactory;

public class JdbcUnitOfWorkFactory implements IUnitOfWorkFactory {

	//creates and returns a new Unit of Work
	@Override
	public IUnitOfWork create() 
	{
		return new JdbcUnitOfWork();
	}

}
