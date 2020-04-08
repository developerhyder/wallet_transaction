package com.ibm.parallelproj.Dao;

import org.springframework.jdbc.core.JdbcTemplate;

import com.ibm.parallelproj.App;
import com.ibm.parallelproj.exceptions.AccountNotFoundException;
import com.ibm.parallelproj.exceptions.InsufficientFundException;
import com.ibm.parallelproj.exceptions.MinimumBalanceException;
import com.ibm.parallelproj.exceptions.WrongPinException;
import com.ibm.parallelproj.util.Account;
import com.ibm.parallelproj.util.Cons;
import com.ibm.parallelproj.util.Utils;

public class TransactionImpl implements Transaction{

	JdbcTemplate jdbcTemplate;
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public void createAccount(String name, double amount) throws MinimumBalanceException {
		if (amount <= 1000){
			throw new MinimumBalanceException("please make sure that you deposit more than 1000rs");
		}
		double pin = Utils.generatePin();
		//did not do the incremental approach to the account id generation
		double id = Utils.generateId();
		
		String sql = "INSERT INTO acc values("+id+",'"+name+"',"+amount+","+pin+")" ;
				
		if(jdbcTemplate.update(sql)==1)
		{
			System.out.println("Your account has been created please note down your credentials");
			System.out.println("Account Id: "+ id +" Account Pin: "+ pin);
			//assigning id and pin so that i can use it later on
			Cons.id = id;
			Cons.pin = pin;
		}
		else{
			System.out.println("something terrible happened please try again later!!!");
			System.exit(0);
		}
	}
	public void authenticate(double id, double pin) throws AccountNotFoundException, WrongPinException {
		Account account = getAccountById(id);
		if(account != null){
			
			if (account.getPin() != pin){
				throw new WrongPinException("you have entered wrong pin");
			}else{
				Cons.id = id;
				Cons.pin = pin;
			}
		}else{
			throw new AccountNotFoundException("the account that you are searching is not available");
		}
	}
	public void deposit(double id, double amount) {
		
		Account account = getAccountById(id);
		System.out.println("ID : "+id);
		double total = account.getCash() + amount;
		String sql = "update acc set balance = ? where id = ?";
		
		if(jdbcTemplate.update(sql, total, id)==1)
		{
			System.out.println("added money \n current Balance: "+ total);
		}else{
			System.out.println("something went wrong");
		}
		
	}
	
	
	public void withDraw(double id, double amount) throws InsufficientFundException {
		Account account = getAccountById(id);
		double total = account.getCash() - amount;
		if(total < 1000){
			throw new InsufficientFundException("minimum balance of 1000rs is a must");
		}
		
		String sql = "update acc set balance = ? where id = ?";
		
		if(jdbcTemplate.update(sql, total, id)==1)
		{
			System.out.println("current Balance: "+ total);
		}else{
			System.out.println("something went wrong");
		}
	}
	public void transfer(double id, double amount) throws InsufficientFundException {
		Account account = getAccountById(id);
		double total = account.getCash() - amount;
		if(total < 1000){
			throw new InsufficientFundException("minimum balance of 1000rs is a must you cannot transfer if u have less balance");
		}
		
		String sql = "update acc set balance = ? where id = ?";
		
		if(jdbcTemplate.update(sql, total, id)==1)
		{
			System.out.println("current Balance: "+ total);
		}else{
			System.out.println("something went wrong");
		}
	}
	public Account getAccountById(double id) {
		String sql = "select * from acc where id = ?";
		try{
		return jdbcTemplate.queryForObject(sql, new Object[] {id}, new org.springframework.jdbc.core.RowMapper<Account>() {
			public Account mapRow(
					java.sql.ResultSet resultSet, int rowNumber) throws java.sql.SQLException {
				Account account = 
						new Account() ;
				account.setId(resultSet.getDouble("id"));
				account.setName(resultSet.getString("name"));
				account.setCash(resultSet.getDouble("balance"));
				account.setPin(resultSet.getDouble("pin"));
				return account;
			}
		});
		}catch(Exception e){
			return null;
		}
	}

	

}
