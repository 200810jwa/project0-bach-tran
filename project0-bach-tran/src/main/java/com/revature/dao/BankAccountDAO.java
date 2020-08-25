package com.revature.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.revature.model.BankAccount;
import com.revature.model.User;

public class BankAccountDAO {

	private Connection con;
	
	public void setConnection(Connection con) {
		this.con = con;
	}
	
	public boolean transfer(int source, int destination, double amount) throws SQLException {
		CallableStatement stmt;
		
		String sql = "{ ? = call transfer_funds(?, ?, ?) }";
		
		stmt = con.prepareCall(sql);
		
		stmt.registerOutParameter(1, Types.BOOLEAN);
		stmt.setInt(2, source);
		stmt.setInt(3, destination);
		BigDecimal bigDecAmount = new BigDecimal(amount);
		stmt.setBigDecimal(4, bigDecAmount);
		
		stmt.execute();
		
		return stmt.getBoolean(1);
	}
	
	public boolean deposit(int BankAccountId, double amount) throws SQLException {
		CallableStatement stmt;
		
		String sql = "{ ? = call withdraw(?, ?) }";
		
		stmt = con.prepareCall(sql);
		
		stmt.registerOutParameter(1, Types.BOOLEAN);
		stmt.setInt(2, BankAccountId);
		BigDecimal bigDecAmount = new BigDecimal(-1 * amount);
		stmt.setBigDecimal(3, bigDecAmount);
		
		stmt.execute();
		
		return stmt.getBoolean(1);
	}
	
	public boolean withdraw(int BankAccountId, double amount) throws SQLException {
		CallableStatement stmt;
		
		String sql = "{ ? = call withdraw(?, ?) }";
		
		stmt = con.prepareCall(sql);
		
		stmt.registerOutParameter(1, Types.BOOLEAN);
		stmt.setInt(2, BankAccountId);
		BigDecimal bigDecAmount = new BigDecimal(amount);
		stmt.setBigDecimal(3, bigDecAmount);
		
		stmt.execute();
		
		return stmt.getBoolean(1);
	}
	
	public BankAccount getBankAccountById(int userId) {
		
		
		return null;
	}
	
	public int applyForBankAccount(int userId) throws SQLException {
		int result = 0;
		
		PreparedStatement stmt1;
		PreparedStatement stmt2;
		
		stmt1 = con.prepareStatement("INSERT INTO bankaccounts (balance) VALUES (0) RETURNING bankaccounts.id");
		
		ResultSet rs;
		int account_id = 0;
		if ((rs = stmt1.executeQuery()) != null) {
			rs.next();
			account_id = rs.getInt(1);
			result++;
		}
		
		stmt2 = con.prepareStatement("INSERT INTO user_account_join (user_id, account_id, approved, pending) VALUES (?, ?, ?, ?)");
		stmt2.setInt(1, userId);
		stmt2.setInt(2, account_id);
		stmt2.setBoolean(3, false);
		stmt2.setBoolean(4, true);
		
		result += stmt2.executeUpdate();
		
		return result;
	}
	
	public List<BankAccount> getPendingAccountsByUserId(int userId) throws SQLException {
		PreparedStatement stmt;
		List<BankAccount> list = new ArrayList<BankAccount>();
		
		stmt = con.prepareStatement("SELECT b.id, b.balance, j.approved, j.pending "
				+ "FROM bankaccounts b "
				+ "INNER JOIN user_account_join j "
				+ "ON b.id = j.account_id "
				+ "WHERE j.account_id IN "
				+ "(SELECT j.account_id "
				+ "FROM users u "
				+ "INNER JOIN user_account_join j "
				+ "ON u.id = j.user_id "
				+ "WHERE u.id = ? AND j.pending = ?) "
				+ "AND j.user_id = ?");
		
		stmt.setInt(1, userId);
		stmt.setBoolean(2, true);
		stmt.setInt(3, userId);
		
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next()) {
			int id = rs.getInt("id");
			double balance = rs.getDouble("balance");
			boolean approved = rs.getBoolean("approved");
			boolean pending = rs.getBoolean("pending");
			
			BankAccount account = new BankAccount(id, balance, approved, pending);
			list.add(account);
		}
		
		return list;
	}
	
	public List<BankAccount> getDeniedAccountsByUserId(int userId) throws SQLException {
		PreparedStatement stmt;
		List<BankAccount> list = new ArrayList<BankAccount>();
		
		stmt = con.prepareStatement("SELECT b.id, b.balance, j.approved, j.pending "
				+ "FROM bankaccounts b "
				+ "INNER JOIN user_account_join j "
				+ "ON b.id = j.account_id "
				+ "WHERE j.account_id IN "
				+ "(SELECT j.account_id "
				+ "FROM users u "
				+ "INNER JOIN user_account_join j "
				+ "ON u.id = j.user_id "
				+ "WHERE u.id = ? AND j.pending = ? AND j.approved = ?) "
				+ "AND j.user_id = ?");
		
		stmt.setInt(1, userId);
		stmt.setBoolean(2, false);
		stmt.setBoolean(3, false);
		stmt.setInt(4, userId);
		
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next()) {
			int id = rs.getInt("id");
			double balance = rs.getDouble("balance");
			boolean approved = rs.getBoolean("approved");
			boolean pending = rs.getBoolean("pending");
			
			BankAccount account = new BankAccount(id, balance, approved, pending);
			list.add(account);
		}
		
		return list;
	}
	
	public List<BankAccount> getApprovedAccountsByUserId(int userId) throws SQLException {
		PreparedStatement stmt;
		List<BankAccount> list = new ArrayList<>();
		
		stmt = con.prepareStatement("SELECT b.id, b.balance, j.approved, j.pending "
				+ "FROM bankaccounts b "
				+ "INNER JOIN user_account_join j "
				+ "ON b.id = j.account_id "
				+ "WHERE j.account_id IN "
				+ "(SELECT j.account_id "
				+ "FROM users u "
				+ "INNER JOIN user_account_join j "
				+ "ON u.id = j.user_id "
				+ "WHERE u.id = ? AND j.pending = ? AND j.approved = ?) "
				+ "AND j.user_id = ?");
		
		stmt.setInt(1, userId);
		stmt.setBoolean(2, false);
		stmt.setBoolean(3, true);
		stmt.setInt(4, userId);
		
		ResultSet rs = stmt.executeQuery();
		
		while (rs.next()) {
			int id = rs.getInt("id");
			double balance = rs.getDouble("balance");
			boolean approved = rs.getBoolean("approved");
			boolean pending = rs.getBoolean("pending");
			
			BankAccount account = new BankAccount(id, balance, approved, pending);
			list.add(account);
		}
		
		return list;
	}
	
	public List<Integer> getAccountIdListMultipleApprovedOrPending() throws SQLException {
		PreparedStatement stmt;
		List<Integer> list = new ArrayList<>();
		
		stmt = con.prepareStatement("SELECT account_id, COUNT(user_id) "
				+ "FROM user_account_join "
				+ "WHERE approved = true OR pending = true "
				+ "GROUP BY account_id "
				+ "HAVING COUNT(user_id) > 1");
		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			int id = rs.getInt("account_id");
			list.add(id);
		}
		
		return list;
	}
	
	public List<Integer> getAccountIdListAllApprovedJoint() throws SQLException {
		PreparedStatement stmt;
		List<Integer> list = new ArrayList<>();
		
		stmt = con.prepareStatement("SELECT account_id, COUNT(user_id) "
				+ "FROM user_account_join "
				+ "WHERE approved = true AND pending = false "
				+ "GROUP BY account_id "
				+ "HAVING COUNT(user_id) > 1");
		
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			int id = rs.getInt("account_id");
			list.add(id);
		}
		
		return list;
	}
	
	public List<Pair<User, BankAccount>> getAllApprovedUserBankAccountPairs() throws SQLException {
		List<Pair<User, BankAccount>> list = new ArrayList<>();
		
		PreparedStatement stmt;
		
		String sql = "SELECT c.id, c.username, c.password, c.firstname, c.lastname, c.phone, c.email, c.accounttype, " + 
				"b.id, b.balance, c.approved, c.pending FROM " + 
				"(SELECT * " + 
				"FROM users u " + 
				"LEFT JOIN user_account_join j " + 
				"ON u.id = j.user_id " + 
				"WHERE approved = true AND pending = false) c " + 
				"LEFT JOIN bankaccounts b " + 
				"ON c.account_id = b.id "
				+ "ORDER BY b.id, c.id";
		
		stmt = con.prepareStatement(sql);
		
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()) {
			int userId = rs.getInt(1);
			String username = rs.getString(2);
			String password = rs.getString(3);
			String firstName = rs.getString(4);
			String lastName = rs.getString(5);
			String phone = rs.getString(6);
			String email = rs.getString(7);
			String accountType = rs.getString(8);
			int accountId = rs.getInt(9);
			double balance = rs.getDouble(10);
			boolean approved = rs.getBoolean(11);
			boolean pending = rs.getBoolean(12);
			
			Pair<User, BankAccount> pair = 
					new MutablePair<>(new User(userId, username, password, firstName, lastName, phone, email, accountType), new BankAccount(accountId, balance, approved, pending));
			list.add(pair);
		}
		
		return list;
	}
	
	public List<Pair<User, BankAccount>> getAllPending() throws SQLException {
		List<Pair<User, BankAccount>> list = new ArrayList<>();
		
		PreparedStatement stmt;
		
		String sql = "SELECT c.id, c.username, c.password, c.firstname, c.lastname, c.phone, c.email, c.accounttype, " + 
				"b.id, b.balance, c.approved, c.pending FROM " + 
				"(SELECT * " + 
				"FROM users u " + 
				"LEFT JOIN user_account_join j " + 
				"ON u.id = j.user_id " + 
				"WHERE approved = false AND pending = true) c " + 
				"LEFT JOIN bankaccounts b " + 
				"ON c.account_id = b.id";
		
		stmt = con.prepareStatement(sql);
		
		ResultSet rs = stmt.executeQuery();
		
		while(rs.next()) {
			int userId = rs.getInt(1);
			String username = rs.getString(2);
			String password = rs.getString(3);
			String firstName = rs.getString(4);
			String lastName = rs.getString(5);
			String phone = rs.getString(6);
			String email = rs.getString(7);
			String accountType = rs.getString(8);
			int accountId = rs.getInt(9);
			double balance = rs.getDouble(10);
			boolean approved = rs.getBoolean(11);
			boolean pending = rs.getBoolean(12);
			
			Pair<User, BankAccount> pair = 
					new MutablePair<>(new User(userId, username, password, firstName, lastName, phone, email, accountType), new BankAccount(accountId, balance, approved, pending));
			list.add(pair);
		}
		
		return list;
	}
		
	public List<BankAccount> getAllApprovedAccounts() throws SQLException {
		PreparedStatement stmt;
		
		String sql = "SELECT DISTINCT " + 
				"b.id, b.balance, c.approved, c.pending FROM " + 
				"(SELECT * FROM users u " + 
				"LEFT JOIN user_account_join j " + 
				"ON u.id = j.user_id " + 
				"WHERE approved = true AND pending = false) c " + 
				"LEFT JOIN bankaccounts b " + 
				"ON c.account_id = b.id";
		
		stmt = con.prepareStatement(sql);
		
		ResultSet rs = stmt.executeQuery();
		List<BankAccount> list = new ArrayList<>();
		while (rs.next()) {
			int accountId = rs.getInt(1);
			double balance = rs.getDouble(2);
			boolean approved = rs.getBoolean(3);
			boolean pending = rs.getBoolean(4);
			
			BankAccount account = new BankAccount(accountId, balance, approved, pending);
			list.add(account);
		}
		
		return list;
	}
	
	public List<Integer> getAllApprovedAccountsId() throws SQLException {
		PreparedStatement stmt;
		
		String sql = "SELECT DISTINCT " + 
				"b.id FROM " + 
				"(SELECT * FROM users u " + 
				"LEFT JOIN user_account_join j " + 
				"ON u.id = j.user_id " + 
				"WHERE approved = true AND pending = false) c " + 
				"LEFT JOIN bankaccounts b " + 
				"ON c.account_id = b.id";
		
		stmt = con.prepareStatement(sql);
		
		ResultSet rs = stmt.executeQuery();
		List<Integer> list = new ArrayList<>();
		while (rs.next()) {
			int accountId = rs.getInt(1);
			
			list.add(accountId);
		}
		
		return list;
	}

	public int approveAccount(int userId, int accountId) throws SQLException {
		PreparedStatement stmt;
		
		String sql = "UPDATE user_account_join SET "
				+ "approved = true, "
				+ "pending = false "
				+ "WHERE user_id = ? AND account_id = ?";
		
		stmt = con.prepareStatement(sql);
		stmt.setInt(1, userId);
		stmt.setInt(2, accountId);
		
		int result = stmt.executeUpdate();
		
		return result;
	}

	public int denyAccount(int userId, int accountId) throws SQLException {
		PreparedStatement stmt;
		
		String sql = "UPDATE user_account_join SET "
				+ "approved = false, "
				+ "pending = false "
				+ "WHERE user_id = ? AND account_id = ?";
		
		stmt = con.prepareStatement(sql);
		stmt.setInt(1, userId);
		stmt.setInt(2, accountId);
		
		int result = stmt.executeUpdate();
		
		return result;
	}

	public boolean checkExistingJointApplication(int userId, int accountId) throws SQLException {
		PreparedStatement stmt;
		
		String sql = "SELECT * FROM user_account_join "
				+ "WHERE user_id = ? AND account_id = ?";
		
		stmt = con.prepareStatement(sql);
		
		stmt.setInt(1, userId);
		stmt.setInt(2, accountId);
		
		ResultSet rs = stmt.executeQuery();
		
		int count = 0;
		while(rs.next()) {
			count++;
		}
		
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	public int applyforJointBankAccount(int userId, int accountId) throws SQLException {
		PreparedStatement stmt;
		
		String sql = "INSERT INTO user_account_join (user_id, account_id, approved, pending) "
				+ "VALUES (?, ?, false, true)";
		
		stmt = con.prepareStatement(sql);
		
		stmt.setInt(1, userId);
		stmt.setInt(2, accountId);
		
		
		return stmt.executeUpdate();
	}

	public boolean checkExistingAccount(int accountId) throws SQLException {
		PreparedStatement stmt;
		
		String sql = "SELECT * FROM user_account_join "
				+ "WHERE account_id = ?";
		
		stmt = con.prepareStatement(sql);
		
		stmt.setInt(1, accountId);
		
		ResultSet rs = stmt.executeQuery();
		
		int count = 0;
		while(rs.next()) {
			count++;
		}
		
		if (count > 0) {
			return true;
		} else {
			return false;
		}
	}

	public List<BankAccount> getEmptyApprovedAccounts() throws SQLException {
		PreparedStatement stmt;
		
		String sql = "SELECT DISTINCT " + 
				"b.id, b.balance, c.approved, c.pending FROM " + 
				"(SELECT * FROM users u " + 
				"LEFT JOIN user_account_join j " + 
				"ON u.id = j.user_id " + 
				"WHERE approved = true AND pending = false) c " + 
				"LEFT JOIN bankaccounts b " + 
				"ON c.account_id = b.id "
				+ "WHERE b.balance = 0";
		
		stmt = con.prepareCall(sql);
		
		ResultSet rs = stmt.executeQuery();
		ArrayList<BankAccount> list = new ArrayList<>();
		while (rs.next()) {
			int accountId = rs.getInt(1);
			double balance = rs.getDouble(2);
			boolean approved = rs.getBoolean(3);
			boolean pending = rs.getBoolean(4);
			
			BankAccount account = new BankAccount(accountId, balance, approved, pending);
			list.add(account);
		}
		
		return list;
	}

	public int deleteAccount(int accountId) throws SQLException {
		PreparedStatement stmt;

		String sql = "DELETE FROM bankaccounts "
				+ "WHERE id = ?";
		
		stmt = con.prepareStatement(sql);
		stmt.setInt(1, accountId);
		
		return stmt.executeUpdate();
	}
	
}
