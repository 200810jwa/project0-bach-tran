package com.revature.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.junit.Before;
import org.junit.Test;

import com.revature.dao.UserDAO;
import com.revature.exceptions.LoginException;
import com.revature.exceptions.RegistrationException;
import com.revature.model.User;

public class RegistrationServiceTest {

	RegistrationService service;
	UserDAO dao;

	@Before
	public void state() {
		dao = mock(UserDAO.class);
		service = new RegistrationService(dao, "");
	}

	@Test
	public void testVerifyCorrectPassword_correct() {
		String password = "testingPassword";
		String confirmPassword = new String("testingPassword");
		assertTrue(service.verifyCorrectPassword(password, confirmPassword));
	}

	@Test
	public void testIncorrectVerifyCorrectPassword_incorrect() {
		String password = "testingPassword";
		String confirmPassword = "testingpassword";
		assertFalse(service.verifyCorrectPassword(password, confirmPassword));
	}

	@Test
	public void testHashPassword() throws NoSuchAlgorithmException, RegistrationException {
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
	public void testCapitalizeText() {
		String expected = "Bill";
		String actual = service.capitalizeText("bIlL");
		assertEquals(expected, actual);

		String expected2 = "O'reilly";
		String actual2 = service.capitalizeText("o'ReiLLy");
		assertEquals(expected2, actual2);
	}
	
	@Test(expected=RegistrationException.class)
	public void testBlankInputs() throws RegistrationException {
		String username = "bob_smith";
		String password = "";
		String firstname = "Bob";
		String lastname = "Smith";
		String phone = "";
		String email="bob_smith@outlook.com";
		
		service.checkBlankInputs(username, password, firstname, lastname, phone, email);;
	}

	@Test
	public void testRegister() throws LoginException, RegistrationException{
		User user = null;
		
		when(dao.loginUser("bob_smith", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5")).thenReturn(new User(1, "bob_smith", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5",
				"Bob", "Smith", "512-826-3246", "bob_smith@outlook.com", "Customer"));
		
		user = service.register("bob_smith", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5",
					"Bob", "Smith", "512-826-3246", "bob_smith@outlook.com", "Customer");
	
		assertEquals(1, user.getId());
		assertEquals("Bob", user.getFirstName());
		assertEquals("Smith", user.getLastName());
		assertEquals("512-826-3246", user.getPhone());
		assertEquals("bob_smith", user.getUsername());
		assertEquals("Customer", user.getAccountType());
		assertEquals("5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5", user.getPassword());
		assertEquals("bob_smith@outlook.com", user.getEmail());
	}
	
	@Test(expected=RegistrationException.class)
	public void testRegister_twice_sameUsername() throws RegistrationException {
		doNothing().doThrow(new RegistrationException()).when(dao).registerUser(eq("bob_smith"), anyString(), anyString(), anyString(), anyString(), anyString(), anyString());
		
		service.register("bob_smith", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5",
				"Bobby", "Smith", "512-826-3247", "bob_smith@outlook.com", "Customer");
		service.register("bob_smith", "5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5",
				"Bob", "Smith", "512-826-3248", "bob_smith@outlook.com", "Customer");
	}
	
	@Test
	public void testVerifyPhoneNumber_correct() {
		assertTrue(service.verifyPhoneNumber("5128263246"));
		assertTrue(service.verifyPhoneNumber("512.826-3246"));
		assertTrue(service.verifyPhoneNumber("512-826-3246"));
		assertTrue(service.verifyPhoneNumber("512 826 3246"));
		assertTrue(service.verifyPhoneNumber("512.826.3246"));
	}
	
	@Test
	public void testVerifyPhoneNumber_incorrect() {
		assertFalse(service.verifyPhoneNumber("15128262486"));
		assertFalse(service.verifyPhoneNumber("5123-623-4343"));
		assertFalse(service.verifyPhoneNumber("512  826  3246"));
		assertFalse(service.verifyPhoneNumber("512 8263 246"));
	}
	
	@Test
	public void testVerifyEmail_correct() {
		assertTrue(service.verifyEmail("d@df.com"));
		assertTrue(service.verifyEmail("bob_smith@outlook.com"));
		assertTrue(service.verifyEmail("jane_doe@email.net"));
		assertTrue(service.verifyEmail("john@test"));
	}
	
	@Test
	public void testVerifyEmail_incorrect() {
		assertFalse(service.verifyEmail("john_doe@"));
		assertFalse(service.verifyEmail("@outlook.com"));
		assertFalse(service.verifyEmail("john_doe.com"));
		assertFalse(service.verifyEmail("john_doe@"));
		assertFalse(service.verifyEmail("john_doe@.com"));
	}
}
