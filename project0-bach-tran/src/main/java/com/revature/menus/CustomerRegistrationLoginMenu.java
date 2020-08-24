package com.revature.menus;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.revature.dao.BankAccountDAO;
import com.revature.dao.UserDAO;
import com.revature.exceptions.LoginException;
import com.revature.exceptions.RegistrationException;
import com.revature.service.LoginService;
import com.revature.service.RegistrationService;

public class CustomerRegistrationLoginMenu extends Menu {
	
	private static Logger log = Logger.getLogger(CustomerRegistrationLoginMenu.class);
	
	public CustomerRegistrationLoginMenu() {
		super(new ArrayList<>(Arrays.asList("Back", "Login to Account", "Register new account")),
				"Please specify your choice below.");
	}
	
	@Override
	public void display() {
		// Ensure that accounts are not logged in at this menu
		getState().logout();
		
		displayOptions();
		int choice = getInput();
		
		switch (choice) {
		case 0:
			getState().displayPreviousMenu();
			break;
		case 1:
			try {
				new LoginService("Customer", new UserDAO(), new BankAccountDAO()).execute();
				getState().displayNextMenu(new CustomerMainMenu());
				break;
			} catch (LoginException e1) {
				log.error(e1.getMessage());
				System.out.println("Please check if your username and password are correct. You will be returned to the main menu.");
				getState().displayFirstMenu();
				break;
			}
		case 2:
			try {
				new RegistrationService(new UserDAO(), "Customer").execute();
				getState().displayNextMenu(new CustomerMainMenu());
				break;
			} catch (RegistrationException e) {
				log.error(e.getMessage());
				System.out.println("Please try registering again");
				getState().displayFirstMenu();
				break;
			}
			
		}
	}

}
