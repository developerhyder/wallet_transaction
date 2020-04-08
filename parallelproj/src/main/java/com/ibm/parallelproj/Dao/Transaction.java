package com.ibm.parallelproj.Dao;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ibm.parallelproj.exceptions.AccountNotFoundException;
import com.ibm.parallelproj.exceptions.InsufficientFundException;
import com.ibm.parallelproj.exceptions.MinimumBalanceException;
import com.ibm.parallelproj.exceptions.WrongPinException;
import com.ibm.parallelproj.util.Account;

public interface Transaction {

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate);
	public void createAccount(String name, double amount)throws MinimumBalanceException;
	public void authenticate(double id, double pin)throws AccountNotFoundException, WrongPinException;
	public void deposit(double id, double amount);
	public void withDraw(double id, double amount)throws InsufficientFundException;
	public void transfer(double id, double amount)throws InsufficientFundException;
	public Account getAccountById(double id);
	
}
