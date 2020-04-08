package com.ibm.parallelproj;

import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibm.parallelproj.Dao.Transaction;
import com.ibm.parallelproj.Dao.TransactionImpl;
import com.ibm.parallelproj.exceptions.AccountNotFoundException;
import com.ibm.parallelproj.exceptions.InsufficientFundException;
import com.ibm.parallelproj.exceptions.MinimumBalanceException;
import com.ibm.parallelproj.exceptions.WrongPinException;
import com.ibm.parallelproj.util.Account;
import com.ibm.parallelproj.util.Cons;

/**
 * Hello world!
 *
 */
public class App 
{
	static Transaction transactionImpl;
	static Scanner sc;
	
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        ApplicationContext context = new ClassPathXmlApplicationContext("appContext.xml");
        transactionImpl = (Transaction) context.getBean("transaction"); 
        sc = new Scanner(System.in);
        
        System.out.println("Enter \n 1. To create a new Account \n 2. To Use your existing Account \n 3. Exit");
        
        int choice = sc.nextInt();
        
        if(choice == 1){
        	createAcc();
        }else if(choice == 2){
        	authenticate();
        }else if(choice == 3){
        	System.out.println("bye !!");
        	System.exit(0);
        }else{
        	System.out.println("looks like you have entered wrong choice. Sometimes life can be all about right choices \n bye!!!");
        	System.exit(0);
        }
        while(true){
        	System.out.println("Enter Transaction choice\n1.Deposit\n2.withdraw\n3.Get Balance\n4.Transfer\n5.Exit");
        	int select = sc.nextInt();
        	switch(select){
        	case 1:
        		deposit();
        		break;
        	case 2:
        		withdraw();
        		break;
        	case 3:
        		getBalance();
        		break;
        	case 4:
        		transfer();
        		break;
        	case 5:
        		System.exit(0);
        	}
        	
        }
        
    }

	private static void transfer() {
		System.out.println("Enter the amount that you want to transfer");
		double amount = sc.nextDouble();
		try {
			transactionImpl.transfer(Cons.id, amount);
		} catch (InsufficientFundException e) {
			e.printStackTrace();
		}
	}

	private static void getBalance() {
		
		Account acc = transactionImpl.getAccountById(Cons.id);
		System.out.println("Current Balance: "+acc.getCash());
	}

	private static void withdraw() {
		System.out.println("Enter the amount that you want to withdraw");
		double amount = sc.nextDouble();
		try {
			transactionImpl.withDraw(Cons.id, amount);
		} catch (InsufficientFundException e) {
			e.printStackTrace();
		}
	}

	private static void deposit() {
		System.out.println("enter the amount that you want to Deposit:");
		double amount = sc.nextDouble();
		transactionImpl.deposit(Cons.id, amount);
	}

	private static void authenticate() {
		
		System.out.println("Enter the ID:");
		Cons.id = sc.nextDouble();
		
		System.out.println("Enter the pin:");
		
		Cons.pin = sc.nextDouble();
		
		try {
			transactionImpl.authenticate(Cons.id, Cons.pin);
		} catch (AccountNotFoundException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (WrongPinException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		
	}

	private static void createAcc() {
		String name;
		double amount;
		
		System.out.println("Enter your Name: ");
		
		sc.nextLine();
		name = sc.nextLine();
		System.out.println("Enter the amount that you want to deposit initially");
		amount = sc.nextDouble();
		try {
			transactionImpl.createAccount(name, amount);
		} catch (MinimumBalanceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("bye!!!");
			System.exit(0);
		}
	}
}
