# project0-bach-tran

# Project 0: Banking App

## Description
Leveraging Java 8 and following Test Driven Development, create an application that simulates simple banking transactions

## Requirements

*	Build the application using Java 8
*	All interaction with the user should be done through the console using the `Scanner` class
*	Customers of the bank should be able to register with a username and password, and apply to open an account
*	Once the account is open, customers should be able to withdraw, deposit, and transfer funds between accounts
    * All basic validation should be done, such as trying to input negative amounts, overdrawing from accounts etc
*	Employees of the bank should be able to view all of their customer's information
    * This includes:
    * Account information
    * Account balances
    * Personal information
*	Employees should be able to approve/deny open applications for accounts
*	Bank Admins should be able to view *and edit* all accounts
    * This includes:
    * Approving/denying accounts
    * Withdrawing, depositing, transferring from all accounts
    * Cancelling accounts
*	80-90% test coverage for the *service layer* is expected using JUnit
    * You should be using TDD
*	Logging should be accomplished using Log4J
    * All transactions should be logged
* Create a SQL script that will create a user in an SQL database and a table schema for storing your bank users and account information
* Have your bank application connect to your SQL database using JDBC and store all information that way
* You should use the DAO design pattern for data connectivity

## Stretch Goals
* Customers can apply for joint accounts
* Customers can own multiple accounts
* Your database can leverage at least 1 stored procedure
* Have separate Checking/Savings Accounts
  * Interest can accumulate
* Try running your application outside of the IDE as a jar
* Look into password hashing
