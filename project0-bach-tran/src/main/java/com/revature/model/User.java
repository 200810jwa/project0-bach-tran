package com.revature.model;

import java.util.Objects;

public class User {
	
	private int id;
	private String username;
	private String password;
	private String firstName;
	private String lastName;
	private String phone;
	private String email;
	private String userType;
	
	public User(String username, String password, String firstName, String lastName, String phone, String email, String accountType) {
		super();
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
		
		if (accountType.equals("Customer") || accountType.equals("Employee") || accountType.equals("Admin")) {
			this.userType = accountType;
		} else {
			throw new RuntimeException(accountType + " could not be set for User instance");
		}
	}
	
	public User(int id, String username, String password, String firstName, String lastName, String phone, String email, String accountType) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
		
		if (accountType.equals("Customer") || accountType.equals("Employee") || accountType.equals("Admin")) {
			this.userType = accountType;
		} else {
			throw new RuntimeException(accountType + " could not be set for User instance");
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAccountType() {
		return userType;
	}

	public void setAccountType(String accountType) {
		this.userType = accountType;
	}

	

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", phone=" + phone + ", email=" + email + ", userType=" + userType + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(userType, email, firstName, id, lastName, password, phone, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof User)) {
			return false;
		}
		User other = (User) obj;
		return Objects.equals(userType, other.userType) && Objects.equals(email, other.email)
				&& Objects.equals(firstName, other.firstName) && id == other.id
				&& Objects.equals(lastName, other.lastName) && Objects.equals(password, other.password)
				&& Objects.equals(phone, other.phone) && Objects.equals(username, other.username);
	}
}
