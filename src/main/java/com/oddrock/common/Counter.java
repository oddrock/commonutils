package com.oddrock.common;

public class Counter {
	private int count;
	public void incre() {
		this.count++;
	}
	public void incre(int count) {
		this.count+=count;
	}
	public void decre() {
		this.count--;
	}
	public void decre(int count) {
		this.count-=count;
	}
	public int get() {
		return this.count;
	}
}
