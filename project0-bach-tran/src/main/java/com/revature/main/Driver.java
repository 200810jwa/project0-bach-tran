package com.revature.main;

import com.revature.menus.MainMenu;

public class Driver {
	
	public static void main(String[] args) {
		
		StateSingleton state = StateSingleton.getInstance();
		
		System.out.println("Welcome to the Banking App!");
		
		state.displayNextMenu(new MainMenu());
		
		System.out.println("Goodbye!");
	}
}
