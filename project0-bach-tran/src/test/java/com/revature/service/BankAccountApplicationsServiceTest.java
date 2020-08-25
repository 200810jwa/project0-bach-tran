package com.revature.service;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.revature.dao.BankAccountDAO;
import com.revature.exceptions.BankAccountApplyException;
import com.revature.model.BankAccount;

public class BankAccountApplicationsServiceTest {

	BankAccountDAO dao = mock(BankAccountDAO.class);
	BankAccountApplicationsService service;

	@Test
	public void testHasPendingApplication() throws SQLException, BankAccountApplyException {
		service = new BankAccountApplicationsService(dao, "");
		when(dao.getPendingAccountsByUserId(1)).thenReturn(new ArrayList<BankAccount>(Arrays.asList(new BankAccount(1, 0, false, true))));
		
		assertTrue(service.hasPendingApplication(1));
	}
	
	@Test
	public void testSubmitApplication() throws SQLException, BankAccountApplyException {
		service = new BankAccountApplicationsService(dao, "");
		when(dao.applyForBankAccount(1)).thenReturn(2);
		
		assertTrue(service.submitApplication(1));
	}
	
	@Test
	public void testDescribePendingAndDeniedAccounts_approved_joint() throws SQLException {
		service = new BankAccountApplicationsService(dao, "");
		
		when(dao.getApprovedAccountsByUserId(1)).thenReturn(Arrays.asList(new BankAccount(6, 0, true, false)));
		when(dao.getPendingAccountsByUserId(1)).thenReturn(Arrays.asList(new BankAccount(4, 0, false, true)));
		when(dao.getDeniedAccountsByUserId(1)).thenReturn(Arrays.asList(new BankAccount(1, 0, false, false)));
		when(dao.getAccountIdListMultipleApprovedOrPending()).thenReturn(Arrays.asList(6));
		when(dao.getAccountIdListAllApprovedJoint()).thenReturn(Arrays.asList(6));
		
		List<String> expected = Arrays.asList("Bank Account ID 6: Approved (joint)", "Bank Account ID 4: Pending", "Bank Account ID 1: Denied");
		assertArrayEquals(expected.toArray(), service.describeApprovedPendingDeniedAccounts(1).toArray());
	}
	
	@Test
	public void testDescribePendingAndDeniedAccounts_pending_joint() throws SQLException {
		service = new BankAccountApplicationsService(dao, "");
		
		when(dao.getApprovedAccountsByUserId(1)).thenReturn(Arrays.asList(new BankAccount(6, 0, true, false)));
		when(dao.getPendingAccountsByUserId(1)).thenReturn(Arrays.asList(new BankAccount(4, 0, false, true)));
		when(dao.getDeniedAccountsByUserId(1)).thenReturn(Arrays.asList(new BankAccount(1, 0, false, false)));
		when(dao.getAccountIdListMultipleApprovedOrPending()).thenReturn(Arrays.asList(6));
		when(dao.getAccountIdListAllApprovedJoint()).thenReturn(Arrays.asList());
		
		List<String> expected = Arrays.asList("Bank Account ID 6: Approved (pending joint)", "Bank Account ID 4: Pending", "Bank Account ID 1: Denied");
		assertArrayEquals(expected.toArray(), service.describeApprovedPendingDeniedAccounts(1).toArray());
	}

	@Test
	public void testProcessApplication() throws SQLException {
		service = new BankAccountApplicationsService(dao, "employeeViewApplications_approve");
		
		when(dao.approveAccount(1, 1)).thenReturn(1);
		when(dao.denyAccount(1, 1)).thenReturn(1);
		
		assertTrue(service.processApplication(1, 1, "approve"));
		assertTrue(service.processApplication(1, 1, "deny"));
		assertFalse(service.processApplication(1, 1, "asdfsdf"));
		assertFalse(service.processApplication(0, 0, "approve"));
	}
	
	@Test
	public void testApplyJoint_noPreviousExisting() throws BankAccountApplyException, SQLException {
		service = new BankAccountApplicationsService(dao, "applyJoint");
		
		when(dao.checkExistingJointApplication(1, 6)).thenReturn(false);
		when(dao.checkExistingAccount(6)).thenReturn(true);
		when(dao.applyforJointBankAccount(1, 6)).thenReturn(1);
		
		assertTrue(service.applyJoint(1, 6));
	}
	
	@Test(expected=BankAccountApplyException.class)
	public void testApplyJoint_PreviousExisting() throws BankAccountApplyException, SQLException {
		service = new BankAccountApplicationsService(dao, "applyJoint");
		
		when(dao.checkExistingJointApplication(1, 6)).thenReturn(true);
		when(dao.checkExistingAccount(6)).thenReturn(true);
		when(dao.applyforJointBankAccount(1, 6)).thenReturn(1);
		
		assertFalse(service.applyJoint(1, 6));
	}
	
	@Test
	public void testApplyJoint_noRecordInserted() throws BankAccountApplyException, SQLException {
		service = new BankAccountApplicationsService(dao, "applyJoint");
		
		when(dao.checkExistingJointApplication(1, 6)).thenReturn(false);
		when(dao.checkExistingAccount(6)).thenReturn(true);
		when(dao.applyforJointBankAccount(1, 6)).thenReturn(0);
		
		assertFalse(service.applyJoint(1, 6));
	}
}
