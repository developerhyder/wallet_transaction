package com.ibm.parallelproj;

import java.util.InputMismatchException;
import java.util.Scanner;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.ibm.parallelproj.Dao.Transaction;
import com.ibm.parallelproj.Dao.TransactionImpl;
import com.ibm.parallelproj.exceptions.AccountNotFoundException;
import com.ibm.parallelproj.exceptions.InsufficientFundException;
import com.ibm.parallelproj.exceptions.MinimumBalanceException;
import com.ibm.parallelproj.exceptions.PassBookException;
import com.ibm.parallelproj.exceptions.WrongPinException;
import com.ibm.parallelproj.util.Account;
import com.ibm.parallelproj.util.Cons;
import com.ibm.parallelproj.util.PassBook;

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
        System.out.println( "Welcome!!!" );
        ApplicationContext context = new ClassPathXmlApplicationContext("appContext.xml");
        transactionImpl = (Transaction) context.getBean("transaction"); 
        
        sc = new Scanner(System.in);
        
        System.out.println("Enter \n 1. To create a new Account \n 2. To Use your existing Account \n 3. Exit");
        int choice=0;
        try{
        	choice = sc.nextInt();
        }catch(InputMismatchException e){
        	System.out.println("Please enter the correct input type \n Try again \n Thank u!!!");
        }
        
        if(choice == 1){
        	createAcc();
        }else if(choice == 2){
        	authenticate();
        }else if(choice == 3){
        	System.out.println("bye !!");
        	System.exit(0);
        }else{
        	System.out.println("looks like you have entered wrong choice. \n bye!!!");
        	System.exit(0);
        }
        while(true){
        	System.out.println("Enter Transaction choice\n1.Deposit\n2.withdraw\n3.Get Balance\n4.Transfer\n5.View your Transactions\n6.Exit");
        	int select=0;
        	try{
        		select= sc.nextInt();
        	}catch(InputMismatchException e){
        		System.out.println("enter integer type");
        	}
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
        		passBk();
        		break;
        	case 6:
        		System.exit(0);
        		break;
        	default:
        		System.out.println("Please select the choice");
        		break;
        	}
        }
        
    }

	private static void passBk() {
		
		try {
			if(transactionImpl.passBook(Cons.id, "getBalance", 0)==1){
				System.out.println("Your PassBook\n| TimeStamp      |   Operation     | balance");
				
				for (PassBook pbkk: Cons.pbk)
				{
					System.out.println(pbkk.getTimestamp() + "|"+ pbkk.getOperation() +"|"+ pbkk.getBalance());
				}
			}else{
				System.out.println("cannot view your passbook");
			}
		} catch (PassBookException e) {
			e.printStackTrace();
		}
	}

	private static int transfer() {
		System.out.println("Enter the amount that you want to transfer");
		double amount = 0;
		try{
			amount = sc.nextDouble();
		}catch(InputMismatchException e){
			e.printStackTrace();
			return 1;
		}
		
		System.out.println("Enter the account ID to which u want to transfer money to");
		double id2 = 0;
		try{
			id2 = sc.nextDouble();
		}catch(InputMismatchException e){
			e.printStackTrace();
			return 1;
		}
		try {
			int amnt = transactionImpl.transfer(Cons.id, amount, id2);
			if(amnt != -1)
			{
				System.out.println("Transfer Successfull \nCurrent Balance:"+amnt);
			}else{
				System.out.println("Something went wrong");
			}
			 
		} catch (InsufficientFundException e) {
			System.out.println(e.getMessage());
		}catch (AccountNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private static void getBalance() {
		
		Account acc = transactionImpl.getAccountById(Cons.id);
		System.out.println("Current Balance: "+acc.getCash());
		
	}

	private static int withdraw() {
		System.out.println("Enter the amount that you want to withdraw");
		double amount = 0;
		try{
			amount = sc.nextDouble();
		}catch(InputMismatchException e){
			e.printStackTrace();
			return 1;
		}
		try {
			int amnt = transactionImpl.withDraw(Cons.id, amount);
			if(amnt != -1)
			{
				System.out.println("With Draw Successfull \nCurrent Balance:"+amnt);
			}else{
				System.out.println("Something went wrong");
			}
		} catch (InsufficientFundException e) {
			System.out.println(e.getMessage());
		}
		return 0;
	}

	private static int deposit() {
		System.out.println("enter the amount that you want to Deposit:");
		double amount = 0;
		try{
			amount = sc.nextDouble();
		}catch(InputMismatchException e){
			e.printStackTrace();
			return 1;
		}
		if(transactionImpl.deposit(Cons.id, amount)==1) 
		{
			System.out.println("Deposit successfull");
		}else{
			System.out.println("Something went wrong during deposit");
		}
		return 0;
	}

	private static void authenticate() {
		try{
		System.out.println("Enter the ID:");
		Cons.id = sc.nextDouble();
		System.out.println("Enter the pin:");
		Cons.pin = sc.nextDouble();
		}catch(InputMismatchException e){
			System.out.println("looks like there was an exception please re do the authentication process");
			System.exit(0);
		}
		try {
			transactionImpl.authenticate(Cons.id, Cons.pin);
		} catch (AccountNotFoundException e) {
			System.out.println(e.getMessage());;
			System.exit(0);
		} catch (WrongPinException e) {
			System.out.println(e.getMessage());
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
			if(transactionImpl.createAccount(name, amount)==1)
			{
				System.out.println("Account created successfully");
				System.out.println("Credentials :"+"\nAccount Id:"+Cons.id+"\nAccount Pin: "+Cons.pin);
			}else{
				
			}
		} catch (MinimumBalanceException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			System.out.println("bye!!!");
			System.exit(0);
		}
	}
}
