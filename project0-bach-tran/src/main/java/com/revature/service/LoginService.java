package com.revature.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.revature.dao.BankAccountDAO;
import com.revature.dao.ConnectionUtility;
import com.revature.dao.UserDAO;
import com.revature.exceptions.HashGenerationException;
import com.revature.exceptions.LoginException;
import com.revature.main.StateSingleton;
import com.revature.model.BankAccount;
import com.revature.model.User;

public class LoginService implements Service {
	
	private static Logger log = Logger.getLogger(LoginService.class);
	private StateSingleton state = StateSingleton.getInstance();
	private Scanner scanner = new Scanner(System.in);
	private String loginType;
	private UserDAO userDao;
	private BankAccountDAO accDao;

	public LoginService(String loginType, UserDAO dao, BankAccountDAO accDao) {
		super();
		this.loginType = loginType;
		this.userDao = dao;
		this.accDao = accDao;
	}
	
	@Override
	public void execute() throws LoginException {
		System.out.println(this.loginType + " Login:");
		System.out.println("=============");
		
		System.out.println("Please enter your username:");
		String username = scanner.nextLine();
		
		System.out.println("Please enter your password:");
		String password = scanner.nextLine();
		
		String hashedPassword;
		try {
			hashedPassword = hashPassword(password, MessageDigest.getInstance("SHA-256"));
		} catch (NoSuchAlgorithmException e) {
			throw new LoginException("Supplied algorithm for password hashing does not exist", e);
		}
		
		// Grab user object from database, check if user is authorized for LoginService instance
		// Set SingletonState state instance current account
		User user = login(username, hashedPassword);

		if(!checkAuthorization(user.getAccountType())) throw new LoginException(username + " is not authorized for loginType " + this.loginType); // throws LoginException
		log.info(username + " logged in successfully");
		
		// Set current user 
		state.setCurrentUser(user);
		
		// Set current bank accounts available
		List<BankAccount> listApprovedAccountsUser = getApprovedAccountsUser(user.getId());
		state.setApprovedAccountsUser(listApprovedAccountsUser);
	}
	
	public boolean checkAuthorization(String userAccountType) {
		// Code here
		if (this.loginType.equals("Customer")) {
			if (userAccountType.equals("Customer") || userAccountType.equals("Employee") || userAccountType.equals("Admin"))
				return true;
		} else if (this.loginType.equals("Employee")) {
			if (userAccountType.equals("Employee") || userAccountType.equals("Admin"))
				return true;
		} else if (this.loginType.equals("Admin")) {
			if (userAccountType.equals("Admin")) 
				return true;
		}
		
		return false;
	}
	
	public List<BankAccount> getApprovedAccountsUser(int userId) throws LoginException {
		List<BankAccount> listApprovedAccountsUser;
		Connection con = ConnectionUtility.getConnection();
		accDao.setConnection(con);
		
		try {
			listApprovedAccountsUser = accDao.getApprovedAccountsByUserId(userId);
		} catch (SQLException e) {
			throw new LoginException("Unable to retrieve approved accounts after login", e);
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
	
	public User login(String username, String password) throws LoginException {
		Connection con = ConnectionUtility.getConnection();
		this.userDao.setConnection(con);
		
		User loginUser = userDao.loginUser(username, password);
		if (loginUser == null) {
			throw new LoginException("Login attempt for user " + username + " failed.");
		}
		
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println("Unable to close connection!");
			e.printStackTrace();
		}
		
		return loginUser;
	}
	
	// https://www.codejava.net/coding/how-to-calculate-md5-and-sha-hash-values-in-java
	// Using a different library, Apache Commons Binary for encoding hex string
	public String hashPassword(String password, MessageDigest digest) {
		try {
	        byte[] hashedBytes = digest.digest(password.getBytes("UTF-8"));
	        
	        return Hex.encodeHexString(hashedBytes);
	    } catch (UnsupportedEncodingException ex) {
	        throw new HashGenerationException("Could not generate hash from String");
	    }
	}
	
}
