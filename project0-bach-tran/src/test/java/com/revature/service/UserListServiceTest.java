package com.revature.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Test;

import com.revature.dao.UserDAO;
import com.revature.model.User;

public class UserListServiceTest {

	private UserDAO dao = mock(UserDAO.class);
	private UserListService service;
	
	@Test
	public void testGetAllUsers() throws SQLException {
		service = new UserListService(dao, "viewUsers");
		when(dao.getAllUsers()).thenReturn(Arrays.asList(new User(1, "bach_tran", "123", "Bach", "Tran", "512-826-2486", "bach_tran@outlook.com", "Customer")));
		
		assertEquals(Arrays.asList(new User(1, "bach_tran", "123", "Bach", "Tran", "512-826-2486", "bach_tran@outlook.com", "Customer")), service.getAllUsers());
	}

}
