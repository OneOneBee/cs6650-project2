package edu.neu.cs6650.project2.MyClient;

import java.util.Random;

public class DummyData {
	private int userID;
	private int stepCount;
	private int day;
	private int timeInterval;
	
	public DummyData(int userPopulation, int day, int timeInterval, int stepUpperBound) {
		Random random = new Random();
		this.userID = random.nextInt(userPopulation) + 1;
		this.day = day;
		this.timeInterval = timeInterval;
		this.stepCount = random.nextInt(stepUpperBound + 1);
	}
	
	public int getUserID() {
		return this.userID;
	}
	
	public int getStepCount() {
		return this.stepCount;
	}
	
	public int getDay() {
		return this.day;
	}
	
	public int getTimeInterval() {
		return this.timeInterval;
	}
}
