package com.revature.service;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.AdditionalMatchers.*;


import java.sql.SQLException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.revature.dao.BankAccountDAO;
import com.revature.exceptions.MoneyManagementException;
import com.revature.model.BankAccount;

public class AccountManagementServiceTest {

	AccountManagementService service;
	BankAccountDAO bankAccountDAO = mock(BankAccountDAO.class);
	
	@Test
	public void testGetApprovedAccountsUser_Customer() throws SQLException, MoneyManagementException {
		service = new AccountManagementService(bankAccountDAO, "ManageMoney_deposit", "Customer");
		
		when(bankAccountDAO.getApprovedAccountsByUserId(1)).thenReturn(Arrays.asList(new BankAccount(1, 3500, true, false),
				new BankAccount(2, 4500, true, false)));
				
		assertEquals(Arrays.asList(new BankAccount(1, 3500, true, false),
				new BankAccount(2, 4500, true, false)), service.getApprovedAccountsUser(1, "Customer"));
	}
	
	@Test
	public void testGetApprovedAcountsUser_Admin() throws MoneyManagementException, SQLException {
		service = new AccountManagementService(bankAccountDAO, "ManageMoney_deposit", "Admin");
		
		when(bankAccountDAO.getAllApprovedAccounts()).thenReturn(Arrays.asList(new BankAccount(1, 3500, true, false),
				new BankAccount(2, 4500, true, false), new BankAccount(3, 0, true, false), new BankAccount(4, 0, true, false)));
				
		assertEquals(Arrays.asList(new BankAccount(1, 3500, true, false),
				new BankAccount(2, 4500, true, false), new BankAccount(3, 0, true, false), 
				new BankAccount(4, 0, true, false)), service.getApprovedAccountsUser(2, "Admin"));
	}
	
	@Test
	public void testDeposit() throws SQLException {
		service = new AccountManagementService(bankAccountDAO, "ManageMoney_deposit", "Customer");
		
		when(bankAccountDAO.deposit(anyInt(), anyInt())).thenReturn(true);
		
		assertTrue(service.deposit(1, 1000));
		assertFalse(service.deposit(1, 0));
	}
	
	@Test
	public void testWithdraw() throws SQLException {
		service = new AccountManagementService(bankAccountDAO, "ManageMoney_withdraw", "Customer");
		
		when(bankAccountDAO.withdraw(eq(1), gt(3500.0))).thenReturn(false);
		when(bankAccountDAO.withdraw(eq(1), leq(3500.0))).thenReturn(true);
		
		assertFalse(service.withdraw(1, 3501));
		assertFalse(service.withdraw(1, -1000));
		assertTrue(service.withdraw(1, 3500));
	}	
	
	@Test
	public void testTransfer() throws SQLException {
		service = new AccountManagementService(bankAccountDAO, "ManageMoney_transfer", "Customer");
		
		when(bankAccountDAO.transfer(eq(1), eq(2), gt(3500.0))).thenReturn(false);
		assertFalse(service.transfer(1, 2, 3501));
		
		when(bankAccountDAO.transfer(eq(1), eq(2), leq(3500.0))).thenReturn(true);
		assertTrue(service.transfer(1, 2, 3500));
		
		assertFalse(service.transfer(1, 2, -100));
		
		assertFalse(service.transfer(1, 100, 1000));
	}

	@Test
	public void testGetCancelableAccounts() throws SQLException, MoneyManagementException {
		service = new AccountManagementService(bankAccountDAO, "CancelAccount", "Admin");
		
		when(bankAccountDAO.getEmptyApprovedAccounts()).thenReturn(Arrays.asList(new BankAccount(3, 0, true, false), 
				new BankAccount(4, 0, true, false)));
		
		assertEquals(Arrays.asList(new BankAccount(3, 0, true, false), new BankAccount(4, 0, true, false)), 
				service.getCancelableAccounts());
	}
	
	@Test
	public void testCancelAccount() throws SQLException, MoneyManagementException {
		service = new AccountManagementService(bankAccountDAO, "CancelAccount", "Admin");
		
		when(bankAccountDAO.deleteAccount(4)).thenReturn(1);
		when(bankAccountDAO.deleteAccount(100)).thenReturn(0);
		
		assertTrue(service.cancelAccount(4));
		assertFalse(service.cancelAccount(100));
	}
}
