package com.oddrock.common;

public class ConcurrentCounter {
	private volatile int count;
	public synchronized void incre() {
		this.count++;
	}
	public synchronized void incre(int count) {
		this.count+=count;
	}
	public synchronized void decre() {
		this.count--;
	}
	public synchronized void decre(int count) {
		this.count-=count;
	}
	public synchronized int get() {
		return this.count;
	}
}
