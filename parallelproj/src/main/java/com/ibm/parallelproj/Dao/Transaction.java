package com.ibm.parallelproj.Dao;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ibm.parallelproj.exceptions.AccountNotFoundException;
import com.ibm.parallelproj.exceptions.InsufficientFundException;
import com.ibm.parallelproj.exceptions.MinimumBalanceException;
import com.ibm.parallelproj.exceptions.PassBookException;
import com.ibm.parallelproj.exceptions.WrongPinException;
import com.ibm.parallelproj.util.Account;

public interface Transaction {
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate);
	public int createAccount(String name, double amount)throws MinimumBalanceException;
	public void authenticate(double id, double pin)throws AccountNotFoundException, WrongPinException;
	public int deposit(double id, double amount);
	public int withDraw(double id, double amount)throws InsufficientFundException;
	public int transfer(double id, double amount, double id2)throws InsufficientFundException, AccountNotFoundException;
	public Account getAccountById(double id);
	public int passBook(double id, String operation,double balance) throws PassBookException;
	
}
