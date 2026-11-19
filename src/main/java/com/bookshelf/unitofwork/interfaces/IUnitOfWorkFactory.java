package com.bookshelf.unitofwork.interfaces;

//a factory to create a new Unit of Work
public interface IUnitOfWorkFactory
{
    IUnitOfWork create();
}