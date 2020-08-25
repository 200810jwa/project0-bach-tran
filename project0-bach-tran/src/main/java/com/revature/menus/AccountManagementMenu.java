package com.revature.menus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.revature.dao.BankAccountDAO;
import com.revature.exceptions.AccountManagementException;
import com.revature.service.AccountManagementService;

public class AccountManagementMenu extends Menu {

	private static Logger log = Logger.getLogger(AdminMainMenu.class);
	@SuppressWarnings("unused")
	private Scanner scanner = new Scanner(System.in);
	private String loginType;
	
	public AccountManagementMenu(String loginType) {
		super(new ArrayList<String>(Arrays.asList("Back", "Deposit", "Withdraw", "Transfer", "View")), "Please select an option.");
		this.loginType = loginType;
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
				new AccountManagementService(new BankAccountDAO(), "ManageMoney_deposit", this.loginType).execute();
			} catch (AccountManagementException e2) {
				log.error(e2.getMessage());
				System.out.println("Deposit failed. Please check if you have entered a deposit amount greater than 0 and try again.");
			}
			getState().displayCurrentMenu();
			break;
		case 2:
			try {
				new AccountManagementService(new BankAccountDAO(), "ManageMoney_withdraw", this.loginType).execute();
			} catch (AccountManagementException e2) {
				log.error(e2.getMessage());
				System.out.println("Withdraw failed. Please check if you have sufficient funds or entered a withdraw amount greater than 0 and try again.");
			}
			getState().displayCurrentMenu();
			break;
		case 3:
			try {
				new AccountManagementService(new BankAccountDAO(), "ManageMoney_transfer", this.loginType).execute();
			} catch (AccountManagementException e2) {
				log.error(e2.getMessage());
				System.out.println("Transfer failed. Please check if you have sufficient funds, entered the correct account ID to transfer to, or entered a transfer amount greater than 0 and try again.");
			}
			getState().displayCurrentMenu();
			break;
		case 4:
			try {
				new AccountManagementService(new BankAccountDAO(), "ManageMoney_view", this.loginType).execute();
			} catch (AccountManagementException e2) {
				log.error(e2.getMessage());
				System.out.println("Unable to view bank accounts");
			}
			getState().displayCurrentMenu();
			break;
		}

	}

}
