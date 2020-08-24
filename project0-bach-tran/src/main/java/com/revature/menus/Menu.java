package com.revature.menus;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import com.revature.main.StateSingleton;

public abstract class Menu {
	
	private List<String> choices;
	private String promptMessage;
	
	private StateSingleton state = StateSingleton.getInstance();
	
	private Scanner scanner = new Scanner(System.in);
	
	public Menu(List<String> choices, String promptMessage) {
		this.choices = choices;
		this.promptMessage = promptMessage;
	}
	
	public StateSingleton getState() {
		return this.state;
	}
	
	public void displayOptions() {
		String username = null;
		
		try {
			username = state.getCurrentUser().getUsername();
		} catch (NullPointerException e) { }
		
		if (username != null) {
			System.out.println("You are currently logged in as: " + username + " (" + state.getCurrentUser().getAccountType() + ")");
		}
		/* */
		System.out.println(promptMessage);
		for (int i = 0; i < choices.size(); i++) {
			System.out.println(" " + i + ".) " + choices.get(i));
		}
		
		System.out.println();
	}
	
	public int getInput() {
		int choice = -1;
		
		try {
			if (scanner.hasNextInt()) {
				choice = scanner.nextInt();
			} else {
				scanner.next();
			}
			
			if (choice < 0 || choice > (choices.size() - 1)) {
				throw new InputMismatchException();
			};
			
		} catch (InputMismatchException e) {
			System.out.println("Incorrect choice, please try again.");
			displayOptions();
			choice = getInput();
		}
		
		return choice;
	}
	
	// abstract method to be implemented to display a menu
	public abstract void display();
		
}
