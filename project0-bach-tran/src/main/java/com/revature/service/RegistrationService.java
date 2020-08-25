package com.revature.service;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.revature.dao.ConnectionUtility;
import com.revature.dao.UserDAO;
import com.revature.exceptions.LoginException;
import com.revature.exceptions.RegistrationException;
import com.revature.main.StateSingleton;
import com.revature.model.User;

public class RegistrationService implements Service {

	private UserDAO dao;
	private String accountType;
	
	private static Logger log = Logger.getLogger(RegistrationService.class);

	private StateSingleton state = StateSingleton.getInstance();
	private Scanner scanner = new Scanner(System.in);

	public RegistrationService(UserDAO dao, String accountType) {
		super();
		this.dao = dao;
		this.accountType = accountType;
	}

	// UI Method
	@Override
	public void execute() throws RegistrationException {
		System.out.println(this.accountType + " Registration:");
		System.out.println("=============");

		System.out.println("Please enter your desired username below:");
		String username = scanner.nextLine();

		System.out.println("Please enter your desired password:");
		String password = scanner.nextLine();
		System.out.println("Please confirm your desired password:");
		String confirmPassword = scanner.nextLine();

		while (!verifyCorrectPassword(password, confirmPassword)) {
			System.out.println("Passwords did not match. Please reenter registration details again.");
			System.out.println("Please enter your desired password:");
			password = scanner.nextLine();
			System.out.println("Please confirm your desired password:");
			confirmPassword = scanner.nextLine();
		}

		String hashedPassword = null;
		try {
			hashedPassword = hashPassword(password, MessageDigest.getInstance("SHA-256"));
		} catch (NoSuchAlgorithmException e) {
			throw new RegistrationException("Supplied algorithm for password hashing does not exist", e);
		}

		// Prompt for first name, last name
		System.out.println("Please enter your first name:");
		String firstname = scanner.nextLine();
		System.out.println("Please enter your last name:");
		String lastname = scanner.nextLine();

		// Phone number w/ input checking
		System.out.println("Please enter your phone number:");
		String phone = scanner.nextLine();
		while (!verifyPhoneNumber(phone)) {
			System.out.println("Incorrect phone number format. Phone number must be in the format xxx xxx xxxx "
					+ "(with no separations or with -, . or blanks in between");
			System.out.println("Please enter your phone number:");
			phone = scanner.nextLine();
		}
		
		// Email
		System.out.println("Please enter your email:");
		String email = scanner.nextLine();
		while(!verifyEmail(email)) {
			System.out.println("Email format incorrect. Please enter a valid email");
			email = scanner.nextLine();
		}
		
		// Check for blank inputs, throw exception
		checkBlankInputs(username, password, firstname, lastname, phone, email);

		// Registration here
		User registeredUser = register(username, hashedPassword, capitalizeText(firstname), capitalizeText(lastname),
				phone, email, accountType);
		log.info("New user " + username + " of type " + this.accountType + " registered successfully");

		if (this.accountType.equals("Customer")) {
			state.setCurrentUser(registeredUser);
		}
	}
	
	// Service Methods
	public boolean verifyPhoneNumber(String phone) {
		Pattern pattern = Pattern.compile("^(\\d{3}[- .]?){2}\\d{4}$");
	    Matcher matcher = pattern.matcher(phone);
	    return matcher.matches();
	}
	
	public boolean verifyEmail(String email) {
		Pattern pattern = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9-]+.[a-zA-Z0-9.-]+$");
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	public void checkBlankInputs(String username, String password, String firstname, String lastname, String phone, String email) throws RegistrationException {
		if (username.equals("") || password.equals("") || firstname.equals("") || lastname.equals("") || phone.equals("") || email.equals("")) {
			throw new RegistrationException("Blank fields are not allowed. Blank registration fields were entered.");
		}
	}

	public String capitalizeText(String string) {
		string = string.toLowerCase();
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	public boolean verifyCorrectPassword(String password, String confirmPassword) {
		return (password.equals(confirmPassword));
	}

	// https://www.codejava.net/coding/how-to-calculate-md5-and-sha-hash-values-in-java
	// Using a different library, Apache Commons Binary for encoding hex string
	public String hashPassword(String password, MessageDigest digest) throws RegistrationException {
		try {
			// MessageDigest digest = MessageDigest.getInstance(algorithm);
			byte[] hashedBytes = digest.digest(password.getBytes("UTF-8"));
			return Hex.encodeHexString(hashedBytes);
		} catch (UnsupportedEncodingException e) {
			throw new RegistrationException("Could not generate hash from password input", e);
		}
	}

	public User register(String username, String password, String firstName, String lastName, String phone, String email, String accountType) 
			throws RegistrationException {
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		dao.registerUser(username, password, firstName, lastName, phone, email, accountType);

		User registeredUser = null;
		try {
			registeredUser = dao.loginUser(username, password);
		} catch (LoginException e) {
			// TODO Auto-generated catch block
			throw new RegistrationException("Automatic login after registration failed", e);
		}
		
		state.setApprovedAccountsUser(new ArrayList<>());

		return registeredUser;
	}
}
