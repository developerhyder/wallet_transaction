package com.ibm.parallelproj.util;

public class Account {

	String name;
	double id, pin, cash;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getId() {
		return id;
	}
	public void setId(double id) {
		this.id = id;
	}
	public double getPin() {
		return pin;
	}
	public void setPin(double pin) {
		this.pin = pin;
	}
	public double getCash() {
		return cash;
	}
	public void setCash(double cash) {
		this.cash = cash;
	}
	
	@Override
	public String toString() {
		return "Account [name=" + name + ", id=" + id + ", pin=" + pin + ", cash=" + cash + "]";
	}
	
}
