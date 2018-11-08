package edu.neu.cs6650.project2.MyClient;

public class TaskTimestamp {
	private long startTime;
	private long endTime;
	
	public TaskTimestamp(long startTime, long endTime) {
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	public long getStart() {
		return this.startTime / 1000;
	}
	
	public long getEnd() {
		return this.endTime / 1000;
	}
}
