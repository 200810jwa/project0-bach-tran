package com.revature.main;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import com.revature.menus.Menu;
import com.revature.model.BankAccount;
import com.revature.model.User;

public class StateSingleton {

	private static StateSingleton instance = null;
	
	private StateSingleton() {
		super();
	}
	
	public static StateSingleton getInstance() {
		if (instance == null) {
			instance = new StateSingleton();
		}
		
		return instance;
	}
	
	// Non singleton functionality below
	
	private Deque<Menu> stack = new ArrayDeque<Menu>();
	private User currentUser;
	private List<BankAccount> approvedAccountsUser;
	
	public void setApprovedAccountsUser(List<BankAccount> approvedAccountsUser) {
		if (approvedAccountsUser != null) {
			this.approvedAccountsUser = approvedAccountsUser;
		} else {
			this.approvedAccountsUser = new ArrayList<>();
		}
	}
	
	public List<BankAccount> getApprovedAccountsUser() {
		return this.approvedAccountsUser;
	}
	
	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	
	public User getCurrentUser() {
		return this.currentUser;
	}
	
	public void logout() {
		this.currentUser = null;
		this.approvedAccountsUser = null;
	}
	
	public void displayFirstMenu() {
		Menu firstMenu = stack.getFirst();
		stack.clear();
		stack.addFirst(firstMenu);
		stack.peekFirst().display();
	}
	
	public void displayCurrentMenu() {
		stack.peekLast().display();
	}
	
	public void displayNextMenu(Menu menu) {
		stack.addLast(menu);
		menu.display();
	}
	
	public void displayPreviousMenu() {
		stack.removeLast();
		stack.peekLast().display();
	}

	@Override
	public String toString() {
		return "StateSingleton [currentMenu=" + stack.peekLast().getClass().getName() + ", currentUser=" + currentUser.getUsername() + "]";
	}
}
