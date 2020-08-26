package com.revature.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.revature.dao.BankAccountDAO;
import com.revature.dao.ConnectionUtility;
import com.revature.exceptions.AccountManagementException;
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

	// UI Method
	@Override
	public void execute() throws AccountManagementException {
		List<BankAccount> listApprovedAccountsUser = getApprovedAccountsUser(state.getCurrentUser().getId(), loginType);
		state.setApprovedAccountsUser(listApprovedAccountsUser);
		
		if (executeType.equals("CancelAccount")) {
			List<BankAccount> cancelableAccounts = getCancelableAccounts();
			
			int accountCounter = 0;
			System.out.println("====CANCELABLE ACCOUNTS====");
			for (BankAccount b : cancelableAccounts) {
				accountCounter++;
				System.out.print(accountCounter + ".) ");
				System.out.println("Account ID: " + b.getId());
				System.out.println("Balance: " + b.getBalance());
				System.out.println();
			}
			
			while(true) {
				System.out.println("Please select an account to cancel: (or type back to go back)");
				int choice = -1;
				try {
					if (scanner.hasNextInt()) {
						choice = scanner.nextInt();
					}
					
					if (scanner.nextLine().equals("back")) {
						break;
					}
					
					if (choice < 1 || choice > cancelableAccounts.size()) {
						throw new InputMismatchException();
					} else {
						int cancelableAccountId = cancelableAccounts.get(choice - 1).getId();
						cancelAccount(cancelableAccountId);
						log.info(state.getCurrentUser().getAccountType() + " " + 
						state.getCurrentUser().getUsername() + " successfully canceled account " + cancelableAccountId);
					}
				} catch (InputMismatchException e) {
					System.out.println("Invalid input, please try again.");
				}
			}
		} else if (executeType.startsWith("ManageMoney")) {
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
				throw new AccountManagementException("Incorrect operation type specified. Type needs to be 'view', 'deposit', 'withdraw', or 'transfer'");
			}
			
			List<BankAccount> approvedAccounts = state.getApprovedAccountsUser();
			int accountCounter = 0;
			List<Integer> jointAccountIDs;
			try {
				jointAccountIDs = getJointIDs();
			} catch (SQLException e1) {
				throw new AccountManagementException("Unable to retrieve joint account IDs from DB");
			}
			System.out.println("====APPROVED ACCOUNTS====");
			for (BankAccount b : approvedAccounts) {
				accountCounter++;
				System.out.print(accountCounter + ".) ");
				System.out.println("Account ID: " + b.getId());
				System.out.println("Balance: " + b.getBalance());
				if(jointAccountIDs.contains(b.getId())) {
					System.out.println("JOINT ACCOUNT");
				}
				System.out.println();
			}
			
			if (approvedAccounts.size() == 0) {
				System.out.println("No Accounts Available");
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
							} else {
								throw new AccountManagementException(state.getCurrentUser().getUsername() + 
										" failed to deposit " + depositAmount + " to accountID " + accountID);
							}
						} else if (operationType.equals("withdraw")) {
							System.out.println("Please specify the amount you would to withdraw:");
							double withdrawAmount = scanner.nextDouble();
							if (withdraw(accountID, withdrawAmount)) {
								log.info(state.getCurrentUser().getAccountType() + " " + state.getCurrentUser().getUsername() + " successfully withdrew " +
										withdrawAmount + " from accountID " + accountID);
							} else {
								throw new AccountManagementException(state.getCurrentUser().getUsername() +
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
							} else {
								throw new AccountManagementException(state.getCurrentUser().getUsername() + " failed to transfer " 
										+ transferAmount + " from accountID " + accountID + " to accountID " + targetAccountID);
							}
						}
					}
				} catch (InputMismatchException e) {
					System.out.println("Incorrect accountID or input, please try again");
				} catch (SQLException e) {
					throw new AccountManagementException("A database interaction issue occurred while trying to deposit, withdraw, or transfer", e);
				}
			}
			
		}
	}
	
	public List<Integer> getJointIDs() throws SQLException {
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		
		List<Integer> list = dao.getAccountIdListAllApprovedJoint();
		
		return list;
	}

	// Service Methods
	public boolean cancelAccount(int accountId) throws AccountManagementException {
		int result = 0;
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		
		try {
			result = dao.deleteAccount(accountId);
		} catch (SQLException e) {
			throw new AccountManagementException("Unable to delete accountID " + accountId, e);
		}
		
		if (result == 1) {
			return true;
		} else {
			return false;
		}
	}

	public List<BankAccount> getCancelableAccounts() throws AccountManagementException {
		List<BankAccount> list;
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		
		try {
			list = dao.getEmptyApprovedAccounts();
		} catch (SQLException e) {
			throw new AccountManagementException("Unable to retrieve cancelable accounts", e);
		} finally {
			try {
				con.close();
			} catch (SQLException e) {
				System.out.println("Unable to close connection!");
				e.printStackTrace();
			}
		}
		
		return list;
	}

	public boolean deposit(int accountId, double amount) throws SQLException {
		if (amount <= 0 ) {
			System.out.println("Please enter a positive amount");
			return false;
		}
		
		if (!(dao.getAllApprovedAccountsId().contains(accountId))) {
			return false;
		}
		
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		return dao.deposit(accountId, amount);
	}
	
	public boolean withdraw(int accountId, double amount) throws SQLException {
		if (amount <= 0) { 
			System.out.println("Please enter a positive amount");
			return false;
		}
		
		if (!(dao.getAllApprovedAccountsId().contains(accountId))) {
			System.out.println(accountId + " is not an approved account");
			return false;
		}
		
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		return dao.withdraw(accountId, amount);
	}
	
	public boolean transfer(int accountId, int targetAccountId, double amount) throws SQLException {
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		
		if (amount <= 0) {
			System.out.println("Please enter a positive amount");
			return false;
		}
		
		if ((!(dao.getAllApprovedAccountsId().contains(targetAccountId)) || accountId == targetAccountId)) {
			return false;
		}
		
		return dao.transfer(accountId, targetAccountId, amount);
	}
	
	public List<BankAccount> getApprovedAccountsUser(int userId, String loginType) throws AccountManagementException {
		List<BankAccount> listApprovedAccountsUser;
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		
		try {
			if (loginType.equals("Admin")) {
				listApprovedAccountsUser = dao.getAllApprovedAccounts();
			} else {
				listApprovedAccountsUser = dao.getApprovedAccountsByUserId(userId);
			}
		} catch (SQLException e) {
			throw new AccountManagementException("Unable to retrieve accounts", e);
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
