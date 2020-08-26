package com.revature.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.revature.dao.ConnectionUtility;
import com.revature.dao.UserDAO;
import com.revature.exceptions.UserListException;
import com.revature.model.User;

public class UserListService implements Service {

	private UserDAO dao;
	private String operationType;
	private Scanner scanner = new Scanner(System.in);
	
	public UserListService(UserDAO dao, String operationType) {
		this.dao = dao;
		this.operationType = operationType;
	}
	
	@Override
	public void execute() throws UserListException {
		if (operationType.equals("viewUsers")) {
			List<User> userList;
			System.out.println("=====USER LIST======");
			try {
				userList = getAllUsers();
			} catch (SQLException e) {
				throw new UserListException("Unable to retrieve users", e);
			}
			for (int i = 0; i < userList.size(); i++) {
				System.out.println((i+1) + "). " + userList.get(i));
			}
			
			System.out.println("====================");
			System.out.println("Type back to go back");
			while (true) if(scanner.nextLine().equals("back")) break;
		}
	}
	
	public List<User> getAllUsers() throws SQLException {
		Connection con = ConnectionUtility.getConnection();
		dao.setConnection(con);
		
		List<User> list = dao.getAllUsers();
		
		return list;
	}

}
