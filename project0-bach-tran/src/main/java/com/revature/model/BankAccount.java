package com.revature.model;

import java.util.Objects;

public class BankAccount {
	
	private int id;
	private double balance;
	private boolean approved;
	private boolean pending;
	
	public BankAccount(double balance, boolean approved, boolean pending) {
		super();
		this.balance = balance;
		this.approved = approved;
		this.pending = pending;
	}
	
	public BankAccount(int id, double balance, boolean approved, boolean pending) {
		super();
		this.id = id;
		this.balance = balance;
		this.approved = approved;
		this.pending = pending;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}
	
	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isPending() {
		return pending;
	}

	public void setPending(boolean pending) {
		this.pending = pending;
	}

	@Override
	public int hashCode() {
		return Objects.hash(approved, balance, id, pending);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof BankAccount)) {
			return false;
		}
		BankAccount other = (BankAccount) obj;
		return approved == other.approved && Double.doubleToLongBits(balance) == Double.doubleToLongBits(other.balance)
				&& id == other.id && pending == other.pending;
	}

	@Override
	public String toString() {
		return "BankAccount [id=" + id + ", balance=" + balance + ", approved=" + approved + ", pending=" + pending
				+ "]";
	}
}
