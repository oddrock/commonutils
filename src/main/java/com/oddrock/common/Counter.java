package com.oddrock.common;

public class Counter {
	private int counter;
	public synchronized void incre() {
		counter++;
	}
	public synchronized void decre() {
		counter--;
	}
	public synchronized int get() {
		return counter;
	}
}
