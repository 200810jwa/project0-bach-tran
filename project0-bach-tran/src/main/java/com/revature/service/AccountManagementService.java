package com.revature.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.revature.dao.BankAccountDAO;
import com.revature.dao.ConnectionUtility;
import com.revature.exceptions.LoginException;
import com.revature.exceptions.MoneyManagementException;
import com.revature.main.StateSingleton;
import com.revature.model.BankAccount;

public class AccountManagementService implements Service {

	private static Logger log = Logger.getLogger(AccountManagementService.class);
	private StateSingleton state = StateSingleton.getInstance();
	private Scanner scanner = new Scanner(System.in);
	private BankAccountDAO dao;
	private String executeType;
	private String loginType;
	
	public AccountManagementService(BankAccountDAO bankAccountDAO, String executeType, String loginType) {
		this.dao = bankAccountDAO;
		this.executeType = executeType;
		this.loginType = loginType;
	}

	@Override
	public void execute() throws MoneyManagementException {
		List<BankAccount> listApprovedAccountsUser = getApprovedAccountsUser(state.getCurrentUser().getId(), loginType);
		state.setApprovedAccountsUser(listApprovedAccountsUser);
		
		if (executeType.startsWith("ManageMoney")) {
			String operationType;
			if (executeType.endsWith("deposit")) {
				operationType = "deposit";
			} else if (executeType.endsWith("withdraw")) {
				operationType = "withdraw";
			} else if (executeType.endsWith("transfer")) {
				operationType = "transfer";
			} else if (executeType.endsWith("view")) {
				operationType = "view";
			} else {
				throw new MoneyManagementException("Incorrect operation type specified. Type needs to be 'view', 'deposit', 'withdraw', or 'transfer'");
			}
			
			List<BankAccount> approvedAccounts = state.getApprovedAccountsUser();
			int accountCounter = 0;
			System.out.println("====APPROVED ACCOUNTS====");
			for (BankAccount b : approvedAccounts) {
				accountCounter++;
				System.out.print(accountCounter + ".) ");
				System.out.println("Account ID: " + b.getId());
				System.out.println("Balance: " + b.getBalance());
				System.out.println();
			}
			
			if (operationType.equals("view")) {
				System.out.println("Type back to go back");
				while(true) if(scanner.nextLine().equals("back")) break;
			}
			
			
			while(true) {
				if (operationType.equals("view")) break;
				System.out.println("Please select an account for this " + operationType + " operation: (or type back to go back)");
				int choice = -1;
				try {
					if (scanner.hasNextInt()) {
						choice = scanner.nextInt();
					}
					
					if (scanner.nextLine().equals("back")) {
						break;
					}
					
					if (choice < 1 || choice > approvedAccounts.size()) {
						throw new InputMismatchException();
					} else {
						BankAccount selectedAccount = approvedAccounts.get(choice - 1);
						int accountID = selectedAccount.getId();
						if (operationType.equals("deposit")) {
							System.out.println("Please specify the amount you would like to deposit:");
							double depositAmount = scanner.nextDouble();
							if (deposit(accountID, depositAmount)) {
								log.info(state.getCurrentUser().getAccountType() + " " + state.getCurrentUser().getUsername() + " successfully deposited " +
										depositAmount + " to accountID " + accountID);
								break;
							} else {
								throw new MoneyManagementException(state.getCurrentUser().getUsername() + 
										" failed to deposit " + depositAmount + " to accountID " + accountID);
							}
						} else if (operationType.equals("withdraw")) {
							System.out.println("Please specify the amount you would to withdraw:");
							double withdrawAmount = scanner.nextDouble();
							if (withdraw(accountID, withdrawAmount)) {
								log.info(state.getCurrentUser().getAccountType() + " " + state.getCurrentUser().getUsername() + " successfully withdrew " +
										withdrawAmount + " from accountID " + accountID);
								break;
							} else {
								throw new MoneyManagementException(state.getCurrentUser().getUsername() +
										" failed to withdraw " + withdrawAmount + " from accountID " + accountID);
							}
						} else if (operationType.equals("transfer")) {
							System.out.println("Please specify the amount you would like to transfer:");
							double transferAmount = scanner.nextDouble();
							System.out.println("Please specify the accountID you would like to transfer to:");
							int targetAccountID = scanner.nextInt();
							if (transfer(accountID, targetAccountID, transferAmount)) {
								log.info(state.getCurrentUser().getAccountType() + " " + state.getCurrentUser().getUsername() + " successfully transferred " +
										transferAmount + " from accountID " + accountID + " to accountID " + targetAccountID);
								break;
							} else {
								throw new MoneyManagementException(state.getCurrentUser().getUsername() + " failed to transfer " 
										+ transferAmount + " from accountID " + accountID + " to accountID " + targetAccountID);
							}
						}
					}
				} catch (InputMismatchException e) {
					System.out.println("Incorrect accountID or input, please try again");
				} catch (SQLException e) {
					throw new MoneyManagementException("A database interaction issue occurred while trying to deposit, withdraw, or transfer", e);
				}
			}
			
		}
	}
	
	// Testable methods
	public boolean deposit(int accountId, double amount) throws SQLException {
		if (amount <= 0) {
			return false;
		}
		
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		return dao.deposit(accountId, amount);
	}
	
	public boolean withdraw(int accountId, double amount) throws SQLException {
		if (amount <= 0) {
			return false;
		}
		
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		return dao.withdraw(accountId, amount);
	}
	
	public boolean transfer(int accountId, int targetAccountId, double amount) throws SQLException {
		if (amount <= 0) {
			return false;
		}
		
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		return dao.transfer(accountId, targetAccountId, amount);
	}
	
	public List<BankAccount> getApprovedAccountsUser(int userId, String loginType) throws MoneyManagementException {
		List<BankAccount> listApprovedAccountsUser;
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		
		try {
			if (state.getCurrentUser().getAccountType().equals("Admin") && loginType.equals("Admin")) {
				listApprovedAccountsUser = dao.getAllApprovedAccounts();
			} else {
				listApprovedAccountsUser = dao.getApprovedAccountsByUserId(userId);
			}
		} catch (SQLException e) {
			throw new MoneyManagementException("Unable to retrieve accounts", e);
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				System.out.println("Unable to close connection!");
				e.printStackTrace();
			}
		}
		
		return listApprovedAccountsUser;
	}
}
