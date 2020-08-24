package com.revature.main;

import com.revature.menus.MainMenu;

public class Driver {
	
	public static void main(String[] args) {
		
		StateSingleton state = StateSingleton.getInstance();
		
		state.displayNextMenu(new MainMenu());
		
		System.out.println("Goodbye!");
	}
}
