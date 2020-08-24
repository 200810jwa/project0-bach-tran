package com.revature.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import com.revature.dao.BankAccountDAO;
import com.revature.dao.UserDAO;
import com.revature.exceptions.LoginException;
import com.revature.exceptions.RegistrationException;
import com.revature.main.StateSingleton;
import com.revature.model.BankAccount;
import com.revature.model.User;

public class LoginServiceTest {

	LoginService service;
	StateSingleton state;
	UserDAO userDao;
	BankAccountDAO accountDao;
	
	@Before
	public void setUp() {
		userDao = mock(UserDAO.class);
		accountDao = mock(BankAccountDAO.class);
	}
	
	@Test
	public void testHashPassword() throws NoSuchAlgorithmException, RegistrationException {
		service = new LoginService("Customer", userDao, accountDao);
		String expected = "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5";
		
		// Reverse from hex string expected output to byte array produced by MessageDigest (which is being mocked)
		String str = "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5";
	    byte[] val = new byte[str.length() / 2];
	    for (int i = 0; i < val.length; i++) {
	    	int index = i * 2;
	    	int j = Integer.parseInt(str.substring(index, index + 2), 16);
	        val[i] = (byte) j;
	    }
		
		MessageDigest digest = mock(MessageDigest.class);
		
		when(digest.digest(any(byte[].class))).thenReturn(val); // return hashed bytes
		
		String actual = service.hashPassword("12345", digest);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testLoginCustomer() throws LoginException {
		service = new LoginService("Customer", userDao, accountDao);
		when(userDao.loginUser("bob_smith", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5")).thenReturn(new User(1, "bob_smith", 
				"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", "Bob", "Smith", "512-826-3246", "bob_smith@outlook.com", "Customer"));
		
		User user = service.login("bob_smith", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5");
		
		assertEquals("bob_smith", user.getUsername());
		assertEquals(1, user.getId());
		assertEquals("5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", user.getPassword());
		assertEquals("Customer", user.getAccountType());
		assertEquals("512-826-3246", user.getPhone());
		assertEquals("bob_smith@outlook.com", user.getEmail());
		assertEquals("Bob", user.getFirstName());
		assertEquals("Smith", user.getLastName());
	}
	
	@Test(expected=LoginException.class)
	public void testLoginCustomer_wrongUsername() throws LoginException {
		service = new LoginService("Customer", userDao, accountDao);
		when(userDao.loginUser("bob_smith", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5")).thenReturn(new User(1, "bob_smith", 
				"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", "Bob", "Smith", "512-826-3246", "bob_smith@outlook.com", "Customer"));
		
		User user = service.login("bob_smithdfdfdf", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5");
		
		assertEquals("bob_smith", user.getUsername());
		assertEquals(1, user.getId());
		assertEquals("5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", user.getPassword());
		assertEquals("Customer", user.getAccountType());
		assertEquals("512-826-3246", user.getPhone());
		assertEquals("Bob", user.getFirstName());
		assertEquals("Smith", user.getLastName());
	}
	
	@Test(expected=LoginException.class)
	public void testLoginCustomer_wrongPassword() throws LoginException {
		service = new LoginService("Customer", userDao, accountDao);
		when(userDao.loginUser("bob_smith", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5")).thenReturn(new User(1, "bob_smith", 
				"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", "Bob", "Smith", "512-826-3246", "bob_smith@outlook.com", "Customer"));
		
		User user = service.login("bob_smith", "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");
		
		assertEquals("bob_smith", user.getUsername());
		assertEquals(1, user.getId());
		assertEquals("5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", user.getPassword());
		assertEquals("Customer", user.getAccountType());
		assertEquals("512-826-3246", user.getPhone());
		assertEquals("Bob", user.getFirstName());
		assertEquals("Smith", user.getLastName());
		assertEquals("bob_smith@outlook.com", user.getEmail());
	}
	
	@Test(expected=LoginException.class)
	public void testLoginCustomer_wrongUsername_wrongPassword() throws LoginException {
		service = new LoginService("Customer", userDao, accountDao);
		when(userDao.loginUser("bob_smith", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5")).thenReturn(new User(1, "bob_smith", 
				"5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", "Bob", "Smith", "512-826-3246", "bob_smith@outlook.com", "Customer"));
		
		User user = service.login("bob_smithsdf", "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92");
		
		assertEquals("bob_smith", user.getUsername());
		assertEquals(1, user.getId());
		assertEquals("5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", user.getPassword());
		assertEquals("Customer", user.getAccountType());
		assertEquals("512-826-3246", user.getPhone());
		assertEquals("Bob", user.getFirstName());
		assertEquals("Smith", user.getLastName());
		assertEquals("bob_smith@outlook.com", user.getEmail());
	}
	
	@Test
	public void testCustomerLoginCheckAuthorization() {
		service = new LoginService("Customer", userDao, accountDao);
		
		assertTrue(service.checkAuthorization("Customer"));
		assertTrue(service.checkAuthorization("Employee"));
		assertTrue(service.checkAuthorization("Admin"));
	}
	
	@Test
	public void testEmployeeLoginCheckAuthorization() {
		service = new LoginService("Employee", userDao, accountDao);
		
		assertFalse(service.checkAuthorization("Customer"));
		assertTrue(service.checkAuthorization("Employee"));
		assertTrue(service.checkAuthorization("Admin"));
	}
	
	@Test
	public void testAdminLoginCheckAuthorization() {
		service = new LoginService("Admin", userDao, accountDao);
		
		assertFalse(service.checkAuthorization("Customer"));
		assertFalse(service.checkAuthorization("Employee"));
		assertTrue(service.checkAuthorization("Admin"));
	}
	
	@Test
	public void testGetApprovedAccountsUser() throws SQLException, LoginException {
		service = new LoginService("Customer", userDao, accountDao);
		
		when(accountDao.getApprovedAccountsByUserId(1)).thenReturn(Arrays.asList(new BankAccount(6, 0, true, false)));
		
		assertArrayEquals(Arrays.asList(new BankAccount(6, 0, true, false)).toArray(), service.getApprovedAccountsUser(1).toArray());
	}
}
