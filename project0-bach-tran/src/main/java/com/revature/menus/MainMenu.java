package com.revature.menus;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.log4j.Logger;

import com.revature.dao.BankAccountDAO;
import com.revature.dao.UserDAO;
import com.revature.exceptions.LoginException;
import com.revature.service.LoginService;

public class MainMenu extends Menu {
	
	private static Logger log = Logger.getLogger(MainMenu.class);
	
	public MainMenu() {
		super(new ArrayList<>(Arrays.asList("Exit Application", "Register/Login as Customer", "Login as Employee", "Login as Admin")), 
				"Please indicate whether you are a customer, employee, or admin.");
	}
	
	@Override
	public void display() {
		getState().logout();
		
		displayOptions();
		int choice = getInput();
		
		switch(choice) {
		case 1:
			getState().displayNextMenu(new CustomerRegistrationLoginMenu());
			break;
		case 2:
			try {
				new LoginService("Employee", new UserDAO(), new BankAccountDAO()).execute();
				getState().displayNextMenu(new EmployeeMainMenu());
			} catch (LoginException e) {
				log.error(e.getMessage());
				getState().displayFirstMenu();
			}
			break;
		case 3:
			try {
				new LoginService("Admin", new UserDAO(), new BankAccountDAO()).execute();
				getState().displayNextMenu(new AdminMainMenu());
			} catch (LoginException e) {
				log.error(e.getMessage());
				getState().displayFirstMenu();
			}
			break;
		}
	}
}
