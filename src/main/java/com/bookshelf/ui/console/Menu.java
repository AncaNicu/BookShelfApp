package com.bookshelf.ui.console;

import java.util.Scanner;

public abstract class Menu 
{
	protected final Scanner scanner;
	
	public Menu(Scanner scanner)
	{
		this.scanner = scanner;
	}
	
	public abstract void start();
	protected abstract void displayMenu();
	protected int handleIntInput()
	{
		int option = -2;
		String input = scanner.nextLine().trim();
		
		try 
		{
			option = Integer.parseInt(input);
		} 
		catch (NumberFormatException e) {}
		
		return option;
	}
	
	protected int readPositiveInteger(String prompt)
	{
		System.out.print(prompt);
		
		boolean validId = false;
		int id = handleIntInput();
		
		while(!validId)
		{			
			if(id <= 0)
			{
				System.out.println("Please enter a positive integer id.");
				System.out.print(prompt);
				id = handleIntInput();
			}
			else
			{
				validId = true;
			}
		}
		
		return id;
	}
}
