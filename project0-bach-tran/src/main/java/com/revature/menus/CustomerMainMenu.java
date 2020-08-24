package com.revature.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.revature.dao.BankAccountDAO;
import com.revature.exceptions.BankAccountApplyException;
import com.revature.exceptions.MoneyManagementException;
import com.revature.model.BankAccount;
import com.revature.model.User;
import com.revature.service.BankAccountApplicationsService;
import com.revature.service.AccountManagementService;

public class CustomerMainMenu extends Menu {

	private static Logger log = Logger.getLogger(CustomerMainMenu.class);
	private Scanner scanner = new Scanner(System.in);
	
	public CustomerMainMenu() {
		super(new ArrayList<>(Arrays.asList("Logout", "Manage your bank accounts", "Apply for new bank account", 
				"Apply for joint ownership of existing account", "View previous applications", "View user information")),
				"Welcome to the customer services menu");
	}

	@Override
	public void display() {
		displayOptions();
		int choice = getInput();

		switch (choice) {
		case 0:
			getState().displayFirstMenu();
			break;
		case 1:
			System.out.println("Please indicate whether you would like to view, deposit, withdraw, or transfer:");
			String customerManageMoneyChoice = scanner.nextLine();
			try {
				new AccountManagementService(new BankAccountDAO(), "ManageMoney_" + customerManageMoneyChoice, "Customer").execute();
			} catch (MoneyManagementException e2) {
				log.error(e2.getMessage());
				System.out.println(customerManageMoneyChoice + " failed");
			}
			getState().displayCurrentMenu();
			break;
		case 2:
			try {
				new BankAccountApplicationsService(new BankAccountDAO(), "apply").execute();
			} catch (BankAccountApplyException e) {
				log.error(e.getMessage());
				System.out.println("Unable to apply for account");
			}
			getState().displayCurrentMenu();
			break;
		case 3:
			try {
				new BankAccountApplicationsService(new BankAccountDAO(), "applyJoint").execute();
			} catch (BankAccountApplyException e1) {
				log.error(e1.getMessage());
				System.out.println("Unable to apply for joint account");
			}
			getState().displayCurrentMenu();
			break;
		case 4:
			try {
				new BankAccountApplicationsService(new BankAccountDAO(), "customerViewApplications").execute();
			} catch (BankAccountApplyException e) {
				log.error(e.getMessage());
				System.out.println("Unable to retrieve bank account applications");
			}
			getState().displayCurrentMenu();
			break;
		case 5:
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
