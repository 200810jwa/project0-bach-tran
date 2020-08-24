package com.revature.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.postgresql.Driver;

public class ConnectionUtility {
	public static final String URL = "jdbc:postgresql://training-ohio.clwebyd8kmnl.us-east-2.rds.amazonaws.com/T200810";
	public static final String USER = "root";
	public static final String PASS = "password";
	
	private ConnectionUtility() {
		super();
	}

	public static Connection getConnection() {
	    try {
	        DriverManager.registerDriver(new Driver());
	        return DriverManager.getConnection(URL, USER, PASS);
	    } catch (SQLException ex) {
	        throw new RuntimeException("Error connecting to the database", ex);
	    }
	}
	
//	public static void main(String[] args) {
//		Connection con = ConnectionUtility.getConnection();
//		PreparedStatement stmt;
//		
//		List<User> userList = new ArrayList<>();
//		try {
//			stmt = con.prepareStatement("SELECT * FROM \"user\" "
//					+ "WHERE username = 'bach_tran' AND password = 'CF80CD8AED482D5D1527D7DC72FCEFF84E6326592848447D2DC0B0E87DFC9A90'");
//			ResultSet rs = stmt.executeQuery();
//			while (rs.next()) {
//				int id = rs.getInt("id");
//				String username = rs.getString("username");
//				String password = rs.getString("password");
//				String firstName = rs.getString("firstname");
//				String lastName = rs.getString("lastname");
//				String phone = rs.getString("phone");
//				String accountType = rs.getString("accounttype");
//				User newUser = new User(username, password, firstName, lastName, phone, accountType);
//			
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
