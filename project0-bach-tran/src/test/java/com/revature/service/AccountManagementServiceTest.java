package com.revature.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.dao.BankAccountDAO;
import com.revature.model.BankAccount;

public class AccountManagementServiceTest {

	AccountManagementService service;
	BankAccountDAO bankAccountDAO = mock(BankAccountDAO.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		service = new AccountManagementService(new BankAccount(2, 1100, true, false), bankAccountDAO);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		
	}

}
