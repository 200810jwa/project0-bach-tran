package com.revature.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.revature.exceptions.LoginException;
import com.revature.exceptions.RegistrationException;
import com.revature.model.User;

public class UserDAO {
	
	private Connection con;
	
	public void setConnection(Connection con) {
		this.con = con;
	}
	
	public List<User> getAllUsers() throws SQLException {
		PreparedStatement stmt;
		
		String sql = "SELECT id, username, password, firstname, lastname, phone, email, accounttype "
				+ "FROM users";
		
		stmt = con.prepareStatement(sql);
		
		ResultSet rs = stmt.executeQuery();
		
		List<User> list = new ArrayList<>();
		while (rs.next()) {
			int id = rs.getInt(1);
			String username = rs.getString(2);
			String password = rs.getString(3);
			String firstname = rs.getString(4);
			String lastname = rs.getString(5);
			String phone = rs.getString(6);
			String email = rs.getString(7);
			String accountType = rs.getString(8);
			
			User user = new User(id, username, password, firstname, lastname, phone, email, accountType);
			list.add(user);
		}
		
		return list;
	}
	
	public User loginUser(String username, String password) throws LoginException {
		PreparedStatement stmt;
		
		try {
			if (!checkUsernameExists(username)) {
				throw new LoginException("Username " + username + " does not exist!");
			}
			
			stmt = con.prepareStatement("SELECT * FROM Users "
					+ "WHERE username = ? AND password = ?");
			
			stmt.setString(1, username);
			stmt.setString(2, password);
			
			ResultSet rs = stmt.executeQuery();
			
			while (rs.next()) {
				int id = rs.getInt("id");
				String user = rs.getString("username");
				String pass = rs.getString("password");
				String firstName = rs.getString("firstname");
				String lastName = rs.getString("lastname");
				String phone = rs.getString("phone");
				String email = rs.getString("email");
				String accountType = rs.getString("accounttype");
				User newUser = new User(id, user, pass, firstName, lastName, phone, email, accountType);
				
				return newUser;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new LoginException("An error occurred when attempting to perform database operations during login for username " + username, e);
		} 
		return null;
	}
	
	public void registerUser(String username, String password, String firstName, String lastName, String phone, String email, String accountType) throws RegistrationException {
		PreparedStatement stmt;
		
		try {
			stmt = con.prepareStatement("INSERT INTO Users (username, password, firstname, lastname, phone, email, accounttype) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?)");
			
			stmt.setString(1, username);
			stmt.setString(2, password);
			stmt.setString(3, firstName);
			stmt.setString(4, lastName);
			stmt.setString(5, phone);
			stmt.setString(6, email);
			stmt.setString(7, accountType);
			
			if (checkUsernameExists(username)) {
				throw new RegistrationException(username + " username already exists!");
			}
			
			stmt.executeUpdate();
		} catch (SQLException e) {
			throw new RegistrationException("An error occurred when attempting to perform database operations during registration for username " + username, e);
		}
	}
	
	public boolean checkUsernameExists(String username) throws SQLException {
		PreparedStatement stmt;
		
		stmt = con.prepareStatement("SELECT username FROM Users WHERE username = ?");
		stmt.setString(1, username);
		
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next()) {
			String user = rs.getString("username");
			
			if (user != null) {
				return true;
			}
		}
		
		return false;
	}
	
}
