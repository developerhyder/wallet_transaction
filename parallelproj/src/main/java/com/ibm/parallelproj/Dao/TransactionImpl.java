package com.ibm.parallelproj.Dao;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.ibm.parallelproj.App;
import com.ibm.parallelproj.exceptions.AccountNotFoundException;
import com.ibm.parallelproj.exceptions.InsufficientFundException;
import com.ibm.parallelproj.exceptions.MinimumBalanceException;
import com.ibm.parallelproj.exceptions.PassBookException;
import com.ibm.parallelproj.exceptions.WrongPinException;
import com.ibm.parallelproj.util.Account;
import com.ibm.parallelproj.util.Cons;
import com.ibm.parallelproj.util.PassBook;
import com.ibm.parallelproj.util.Utils;


@Component("transaction")
public class TransactionImpl implements Transaction{
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public int createAccount(String name, double amount) throws MinimumBalanceException {
		if (amount <= 1000){
			throw new MinimumBalanceException("please make sure that you deposit more than 1000rs");
		}
		double pin = Utils.generatePin();
		//did not do the incremental approach to the account id generation
		double id = Utils.generateId();
		
		String sql = "INSERT INTO acc values(?,?,?,?)" ;
				
		if(jdbcTemplate.update(sql, id, name, amount, pin)==1)
		{
			try {
				passBook(id, "createaccount", amount);
			} catch (PassBookException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Cons.id = id;
			Cons.pin = pin;
			return 1;
			
		}
		else{
			System.exit(0);
		}
		
		
		return 0;
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
	public int deposit(double id, double amount) {
		
		Account account = getAccountById(id);
		System.out.println("ID : "+id);
		double total = account.getCash() + amount;
		String sql = "update acc set balance = ? where id = ?";
		
		if(jdbcTemplate.update(sql, total, id)==1)
		{
			try {
				passBook(id, "deposited "+ Double.toString(amount), total);
			} catch (PassBookException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 1;
			//System.out.println("added money \n current Balance: "+ total);
		}else{
			//System.out.println("something went wrong");
			return 0;
		}
	}
	
	
	public int withDraw(double id, double amount) throws InsufficientFundException {
		Account account = getAccountById(id);
		double total = account.getCash() - amount;
		if(total < 1000){
			throw new InsufficientFundException("minimum balance of 1000rs is a must");
		}
		
		String sql = "update acc set balance = ? where id = ?";
		
		if(jdbcTemplate.update(sql, total, id)==1)
		{
			try {
				passBook(id, "withdrawal of "+ Double.toString(amount), total);
			} catch (PassBookException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//System.out.println("current Balance: "+ total);
			return (int)total;
		}else{
			//System.out.println("something went wrong");
			return -1;
		}
		
	}
	public int transfer(double id, double amount, double id2) throws InsufficientFundException, AccountNotFoundException {
		Account account = getAccountById(id);
		double total = account.getCash() - amount;
		if(total < 1000){
			throw new InsufficientFundException("minsimum balance of 1000rs is a must you cannot transfer if u have less balance");
		}
		
		String sql = "update acc set balance = ? where id = ?";
		Account account1 = getAccountById(id2);
		
		if(account1 == null){
			throw new AccountNotFoundException("The account for which that you are trying to transfer is not available");
		}
		if(jdbcTemplate.update(sql, total, id)==1)
		{
			//System.out.println("current Balance: "+ total);
			deposit(id2, amount);
			try {
				passBook(id, "Transfered "+ Double.toString(amount), total);
			} catch (PassBookException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return (int) total;
		}else{
			//System.out.println("something went wrong");
			return -1;
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

	public int passBook(double id, String operation, double balance) throws PassBookException{
		String idd = "pb"+Integer.toString((int)id);
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		
		if(operation.equals("getBalance")){
			//returns the checkbook
			 System.out.println("TimeStamp | Operation | Balance");
			 Cons.pbk = (ArrayList<PassBook>)getAllInfo(idd);
			 
			 
			return 1;
		}else if(operation.equals("createaccount")){
			String sql = "create table "+idd+" (timestamp varchar(25), operation varchar(50), balance numeric(10))" ;
			
			if(jdbcTemplate.update(sql)!=1)
			{
//				System.out.println("passbook creation was successfull");
			}else{
//				System.out.println("passbook creation went wrong");
				//throw exceptions here
				throw new PassBookException("something terrible happened during account creation");
			}
			
			String sql1 = "INSERT INTO "+idd+" values(? , ?, ?)";
			
			if(jdbcTemplate.update(sql1, ts.toString(),operation, balance)==1)
			{
				//System.out.println("updated passbook");
			}else{
				throw new PassBookException("something terrible happened during account updation");
				//throw exceptions here
			}
		}else{
			String sql1 = "INSERT INTO "+idd+" values(? , ?, ?)";
			
			if(jdbcTemplate.update(sql1,  ts.toString(),operation, balance)==1)
			{
				
			}else{
				throw new PassBookException("failed to update passbook");
			}
		}
		return 0;
	}
	
	public List<PassBook> getAllInfo(String id) {
		 String sql = "SELECT * FROM "+id;
		 BigDecimal bd;

	        List<Map<String, Object>> list = jdbcTemplate
	                .queryForList(sql);

	        List<PassBook> pbList = new ArrayList<PassBook>();

	        for (Map<String, Object> map : list)
	        {
	            PassBook passBook = new PassBook();
	            passBook.setOperation((String) map.get("operation"));
	            passBook.setTimestamp((String) map.get("timestamp"));
	            bd =(BigDecimal) map.get("balance");
	            passBook.setBalance(bd.doubleValue());
	            pbList.add(passBook);
	        }
	        return pbList;
	}
}
