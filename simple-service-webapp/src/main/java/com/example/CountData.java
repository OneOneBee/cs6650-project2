package com.example;

public class CountData {
	private int userId;
	private int day;
	private int stepCount;
	private int timeInterval;
	
	public CountData(int userId, int day, int timeInterval, int stepCount) {
		this.userId = userId;
		this.day = day;
		this.stepCount = stepCount;
		this.timeInterval = timeInterval;
	}
	
	public int getUserId() {
		return this.userId;
	}
	
	public int getDay() {
		return this.day;
	}
	
	public int getCount() {
		return this.stepCount;
	}
	
	public int getInterval() {
		return this.timeInterval;
	}
}
