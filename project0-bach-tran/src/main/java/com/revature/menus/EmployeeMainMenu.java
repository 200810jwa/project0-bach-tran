package com.revature.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.revature.dao.BankAccountDAO;
import com.revature.exceptions.BankAccountApplyException;
import com.revature.model.User;
import com.revature.service.BankAccountApplicationsService;

public class EmployeeMainMenu extends Menu {

	private static Logger log = Logger.getLogger(EmployeeMainMenu.class);
	private Scanner scanner = new Scanner(System.in);
	
	public EmployeeMainMenu() {
		super(new ArrayList<>(Arrays.asList("Logout", "Approve Pending Applications", "Deny Pending Applications", "View Approved Accounts", "View logged in user information")), "Welcome to the employee main menu. Please select an option.");
	}

	@Override
	public void display() {
		// TODO Auto-generated method stub
		displayOptions();
		int choice = getInput();
		
		switch (choice) {
		case 0:
			getState().displayPreviousMenu();
			break;
		case 1:
			try {
				new BankAccountApplicationsService(new BankAccountDAO(), "employeeViewApplications_approve").execute();
			} catch (BankAccountApplyException e) {
				log.error(e.getMessage());
				System.out.println("Unable to process pending applications");
			}
			getState().displayCurrentMenu();
			break;
		case 2:
			try {
				new BankAccountApplicationsService(new BankAccountDAO(), "employeeViewApplications_deny").execute();
			} catch (BankAccountApplyException e) {
				log.error(e.getMessage());
				System.out.println("Unable to process pending applications");
			}
			getState().displayCurrentMenu();
			break;
		case 3:
			try {
				new BankAccountApplicationsService(new BankAccountDAO(), "employeeViewApproved").execute();
			} catch (BankAccountApplyException e) {
				log.error(e.getMessage());
				System.out.println("Unable to view approved accounts");
			}
			getState().displayCurrentMenu();
			break;
		case 4:
			User current = getState().getCurrentUser();
			System.out.println("========USER INFORMATION========");
			System.out.println("UserID: " + current.getId());
			System.out.println("Username: " + current.getUsername());
			System.out.println("Name: " + current.getFirstName() + " " + current.getLastName());
			System.out.println("Email: " + current.getEmail());
			System.out.println("Phone: " + current.getPhone());
			System.out.println();
			System.out.println("Type 'back' to go back");
			System.out.println("================================");
			while(true) if(scanner.nextLine().equals("back")) break;
			getState().displayCurrentMenu();
			break;
		}
	}
}
