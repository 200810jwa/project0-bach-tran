package com.revature.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

import com.revature.dao.BankAccountDAO;
import com.revature.dao.ConnectionUtility;
import com.revature.exceptions.BankAccountApplyException;
import com.revature.main.StateSingleton;
import com.revature.model.BankAccount;
import com.revature.model.User;

public class BankAccountApplicationsService implements Service {
	
	private static Logger log = Logger.getLogger(BankAccountApplicationsService.class);
	private StateSingleton state = StateSingleton.getInstance();
	private Scanner scanner = new Scanner(System.in);
	private BankAccountDAO dao;
	private String executeType;
	
	public BankAccountApplicationsService(BankAccountDAO dao, String executeType) {
		this.dao = dao;
		this.executeType = executeType;
	}	

	@Override
	public void execute() throws BankAccountApplyException {
		if (executeType.equals("apply")) {
			if (hasPendingApplication(state.getCurrentUser().getId())) {
				throw new BankAccountApplyException("User " + state.getCurrentUser().getUsername() + " tried to apply, but "
						+ "already has a bank account application pending");
			}
			
			System.out.println("Please type YES (capitalized) if you would like to submit an application for a bank account, otherwise "
					+ "type anything else to exit.");
			if (scanner.nextLine().equals("YES")) {
				if (!submitApplication(state.getCurrentUser().getId())) {
					throw new BankAccountApplyException("User " + state.getCurrentUser().getUsername() + " attempted to submit "
							+ "an application, but was unable to successfully to do so");
				} else {
					log.info("User " + state.getCurrentUser().getUsername() + " has applied for a bank account");
					System.out.println();
					System.out.println("Successfully applied for a bank account. Please wait while a bank associate reviews your application.");
					System.out.println();
					System.out.println("=====================================================================================================");
					System.out.println();
				}
			}
		} else if (executeType.equals("customerViewApplications")) {
			List<String> accountStrings = null;
			try {
				accountStrings = describeApprovedPendingDeniedAccounts(state.getCurrentUser().getId());
			} catch (SQLException e1) {
				throw new BankAccountApplyException("Unable to retrieve application history from the DB", e1);
			}
			
			System.out.println("======APPLICATION VIEWER======");
			if (accountStrings.size() != 0) {
				for (String e : accountStrings) {
					System.out.println(e);
				}
			} else {
				System.out.println("No applications found in the system");
			}
			System.out.println();
			System.out.println("Type 'back' to go back");
			System.out.println("================================");
			System.out.println();
			while(true) if(scanner.nextLine().equals("back")) break;
		} else if (executeType.startsWith("employeeViewApplications")) {
			try {
				dao.setConnection(ConnectionUtility.getConnection());
				List<Pair<User, BankAccount>> pendingUserAccountPairs = dao.getAllPending();
				List<Integer> accountIdMultipleApprovedOrPending = dao.getAccountIdListMultipleApprovedOrPending();
				int applicationCounter = 0;
				System.out.println("=====PENDING APPLICATIONS=====");
				if (pendingUserAccountPairs.size() == 0) {
					System.out.println("There are no pending applications");
				}
				for (Pair<User, BankAccount> pair : pendingUserAccountPairs) {
					applicationCounter++;
					System.out.print(applicationCounter + ".) ");
					System.out.println(pair.getLeft());
					System.out.println(pair.getRight());
					if (accountIdMultipleApprovedOrPending.contains(pair.getRight().getId())) {
						System.out.println("JOINT APPLICATION");
					}
					System.out.println();
				}
				System.out.println("Please select an application to perform actions on, or type back to go back:");
				while (true) {
					int choice = -1;
					try {			
						if (scanner.hasNextInt()) {
							choice = scanner.nextInt();
						} 
						
						if (scanner.nextLine().equals("back")) {
							break;
						}
						
						// Change this section
						if (choice < 1 || choice > pendingUserAccountPairs.size()) {
							throw new InputMismatchException();
						} else {
							Pair<User, BankAccount> pair = pendingUserAccountPairs.get(choice - 1);
							User user = pair.getLeft();
							BankAccount account = pair.getRight();
							if (executeType.endsWith("_approve")) {
								if (processApplication(user.getId(), account.getId(), "approve")) {
									log.info(state.getCurrentUser().getAccountType() + " " + state.getCurrentUser().getUsername()
											+ " successfully approved account ID " + account.getId() + " for user " + user.getUsername());
									break;
								} else {
									throw new BankAccountApplyException(state.getCurrentUser().getAccountType() + " " + state.getCurrentUser().getUsername()
											+ " unable to approve account ID " + account.getId() + " for user " + user.getUsername());
								}
							} else if (executeType.endsWith("_deny")) {
								if (processApplication(user.getId(), account.getId(), "deny")) {
									log.info(state.getCurrentUser().getAccountType() + " " + state.getCurrentUser().getUsername()
											+ " successfully denied account ID " + account.getId() + " for user " + user.getUsername());
									break;
								} else {
									throw new BankAccountApplyException(state.getCurrentUser().getAccountType() + " " + state.getCurrentUser().getUsername()
											+ " unable to deny account ID " + account.getId() + " for user " + user.getUsername());
								}
							}
						}
						
					} catch (InputMismatchException e) {
						System.out.println("Incorrect accountID or input, please try again");
					}
				}
			} catch (SQLException e2) {
				throw new BankAccountApplyException("A database error has occurred while attempting to approve pending applications", e2);
			}
		} else if (executeType.equals("employeeViewApproved")) {
			try {
				dao.setConnection(ConnectionUtility.getConnection());
				List<Pair<User, BankAccount>> pendingUserAccountPairs = dao.getAllApproved();
				for (Pair<User, BankAccount> pair : pendingUserAccountPairs) {
					System.out.println(pair.getLeft());
					System.out.println(pair.getRight());
					System.out.println();
				}
				System.out.println("Please type back to go back.");
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (true) {
				int choice = -1;
				try {			
					if (scanner.hasNextInt()) {
						choice = scanner.nextInt();
					} 
					
					if (scanner.nextLine().equals("back")) {
						break;
					}
					
					// Change this section
					if ((choice < 0 || choice > 5)) {
						throw new InputMismatchException();
					}
					
				} catch (InputMismatchException e) {
					System.out.println("Incorrect accountID or input, please try again");
				}
				
			}
		} else if (executeType.equals("applyJoint")) {
			if (hasPendingApplication(state.getCurrentUser().getId())) {
				throw new BankAccountApplyException("User " + state.getCurrentUser().getUsername() + " tried to apply, but "
						+ "already has a bank account application pending");
			}
			
			System.out.println("Please enter the accountID you would like to apply for joint access:");
			while (true) {
				int choice = -1;
				try {			
					if (scanner.hasNextInt()) {
						choice = scanner.nextInt();
					} 
					
					if (scanner.nextLine().equals("back")) {
						break;
					}
					
					// Change this section
					if ((choice < 0)) {
						throw new InputMismatchException();
					} else {
						if (applyJoint(state.getCurrentUser().getId(), choice)) {
							log.info(state.getCurrentUser().getUsername() + " successfully applied for a joint account with existing accountID " + choice);
							break;
						} else {
							throw new BankAccountApplyException("An issue occurred with applying for a joint account");
						}
					}
					
				} catch (InputMismatchException e) {
					System.out.println("Invalid input, please try again");
				}
			}
		}
	}
	
	public boolean applyJoint(int userId, int accountId) throws BankAccountApplyException {
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		int result = 0;
		try {
			if (dao.checkExistingJointApplication(userId, accountId) || !dao.checkExistingAccount(accountId)) {
				throw new BankAccountApplyException("A previous pending or denied joint application already exists, or the accountID supplied "
						+ "does not exist");
			} else {
				result = dao.applyforJointBankAccount(userId, accountId);
			}
		} catch (SQLException e) {
			throw new BankAccountApplyException("An error occurred with retrieving or inserting records during joint application", e);
		}
		
		if (result == 1) {
			return true;
		} else {
			return false;
		}
	}

	public boolean processApplication(int userId, int accountId, String type) throws SQLException {
		int result = 0;
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		
		if (type.equals("approve")) {
			result = dao.approveAccount(userId, accountId);
		} else if (type.equals("deny")) {
			result = dao.denyAccount(userId, accountId);
		}
		
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}

	public boolean submitApplication(int userId) throws BankAccountApplyException {
		int result = 0;
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		try {
			con.setAutoCommit(false);
			result = dao.applyForBankAccount(userId);
			if (result == 2) {
				con.commit();
				con.setAutoCommit(true);
				return true;
			} else {
				con.rollback();
				con.setAutoCommit(true);
				return false;
			}
		} catch (SQLException e) {
			throw new BankAccountApplyException("An error occurred while attempting to insert application into DB", e);
		}
	}
	
	public boolean hasPendingApplication(int userId) throws BankAccountApplyException {
		List<BankAccount> list;
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		try {
			list = dao.getPendingAccountsByUserId(userId);
			if (list.size() > 0) {
				return true;
			}
		} catch (SQLException e) {
			throw new BankAccountApplyException("An issue occurred while retrieving pending accounts from DB for user id " + userId, e);
		}
		
		return false;
	}

	public List<String> describeApprovedPendingDeniedAccounts(int userId) throws SQLException {
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		
		List<BankAccount> pendingAccounts = dao.getPendingAccountsByUserId(userId); // Pending
		List<BankAccount> deniedAccounts = dao.getDeniedAccountsByUserId(userId); // Denied
		List<BankAccount> approvedAccounts = dao.getApprovedAccountsByUserId(userId); // Approved
		List<Integer> accountIdMultipleApprovedOrPending = dao.getAccountIdListMultipleApprovedOrPending(); // All approved and/or pending
		List<Integer> accountIdMultipleAllApproved = dao.getAccountIdListAllApproved(); // All approved, no pending
		
		List<String> stringList = new ArrayList<>();
		
		for (BankAccount a : approvedAccounts) {
			if (accountIdMultipleAllApproved.contains(a.getId())) {
				stringList.add("Bank Account ID " + a.getId() + ": Approved (joint)");
			} else if (accountIdMultipleApprovedOrPending.contains(a.getId())) {
				stringList.add("Bank Account ID " + a.getId() + ": Approved (pending joint)");
			} else {
				stringList.add("Bank Account ID " + a.getId() + ": Approved");
			}
		}
		
		for (BankAccount a : pendingAccounts) {
			if (accountIdMultipleApprovedOrPending.contains(a.getId())) {
				stringList.add("Bank Account ID " + a.getId() + ": Pending (joint)");
			} else {
				stringList.add("Bank Account ID " + a.getId() + ": Pending");
			}
		}
		
		for (BankAccount a : deniedAccounts) {
			stringList.add("Bank Account ID " + a.getId() + ": Denied");
		}
		
		return stringList;
	}
	
}
