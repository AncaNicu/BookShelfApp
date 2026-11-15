package com.bookshelf.ui.console;

import java.util.Scanner;

public abstract class SecondaryMenu extends Menu
{
	private static final int BACK_OPTION = 6;
	
	public SecondaryMenu(Scanner scanner) 
	{
		super(scanner);
	}
	
	protected abstract void handleAddOption();
	protected abstract void handleViewAllOption();
	protected abstract void handleFindByIdOption();
	protected abstract void handleUpdateOption();
	protected abstract void handleDeleteOption();
	
	@Override
	public void start() 
	{
		int option = -1;
		
		while(option != BACK_OPTION)
		{
			displayMenu();
			option = handleIntInput();
			
			switch(option)
			{
			case 1:
				handleAddOption();
				break;
			case 2:
				handleViewAllOption();
				break;
			case 3:
				handleFindByIdOption();
				break;
			case 4:
				handleUpdateOption();
				break;
			case 5:
				handleDeleteOption();
				break;
			case 6:
				break;
			default:
				System.out.println("Please enter a valid option.");
				break;
			}
		}
	}
	
}
