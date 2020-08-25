package com.revature.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.revature.dao.BankAccountDAO;
import com.revature.dao.UserDAO;
import com.revature.exceptions.BankAccountApplyException;
import com.revature.exceptions.MoneyManagementException;
import com.revature.exceptions.RegistrationException;
import com.revature.model.User;
import com.revature.service.BankAccountApplicationsService;
import com.revature.service.AccountManagementService;
import com.revature.service.RegistrationService;

public class AdminMainMenu extends Menu {

	private static Logger log = Logger.getLogger(AdminMainMenu.class);
	private Scanner scanner = new Scanner(System.in);
	
	public AdminMainMenu() {
		super(new ArrayList<String>(Arrays.asList("Logout", "Cancel Accounts", "Manage Money of All Bank Accounts", "Create employee account", "Approve Pending Applications", "Deny Pending Applications", "View Approved Accounts", "View logged in user information")), "Welcome to the admin menu. Please select an option.");
	}

	@Override
	public void display() {
		displayOptions();
		int choice = getInput();
		
		switch (choice) {
		case 0:
			getState().displayPreviousMenu();
			break;
		case 1:
			try {
				new AccountManagementService(new BankAccountDAO(), "CancelAccount", "Admin").execute();
			} catch (MoneyManagementException e1) {
				log.error(e1.getMessage());
				System.out.println("Unable to cancel account");
			}
			getState().displayCurrentMenu();
			break;
		case 2:
			System.out.println("Please indicate whether you would like to view, deposit, withdraw, or transfer:");
			String adminManageMoneyChoice = scanner.nextLine();
			try {
				new AccountManagementService(new BankAccountDAO(), "ManageMoney_" + adminManageMoneyChoice, "Admin").execute();
			} catch (MoneyManagementException e2) {
				log.error(e2.getMessage());
				System.out.println(adminManageMoneyChoice.toUpperCase() + " failed. Please check if you have entered valid inputs and try again.");
			}
			getState().displayCurrentMenu();
			break;
		case 3:
			try {
				new RegistrationService(new UserDAO(), "Employee").execute();
			} catch (RegistrationException e) {
				log.error(e.getMessage());
				System.out.println("Unable to create new Employee account");
			}
			getState().displayCurrentMenu();
			break;
		case 4:
			try {
				new BankAccountApplicationsService(new BankAccountDAO(), "employeeViewApplications_approve").execute();
			} catch (BankAccountApplyException e) {
				log.error(e.getMessage());
				System.out.println("Unable to process pending applications");
			}
			getState().displayCurrentMenu();
			break;
		case 5:
			try {
				new BankAccountApplicationsService(new BankAccountDAO(), "employeeViewApplications_deny").execute();
			} catch (BankAccountApplyException e) {
				log.error(e.getMessage());
				System.out.println("Unable to process pending applications");
			}
			getState().displayCurrentMenu();
			break;
		case 6:
			try {
				new BankAccountApplicationsService(new BankAccountDAO(), "employeeViewApproved").execute();
			} catch (BankAccountApplyException e) {
				log.error(e.getMessage());
				System.out.println("Unable to view approved accounts");
			}
			getState().displayCurrentMenu();
			break;
		case 7:
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
