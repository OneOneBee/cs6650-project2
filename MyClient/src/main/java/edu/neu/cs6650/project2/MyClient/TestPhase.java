	package edu.neu.cs6650.project2.MyClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class TestPhase {
	private String name;
	private String url;
	private int threadNum;
	private int startTimeInterval;
	private int endTimeInterval;
	private int day;
	private int userPopulation;
	private int stepUpperBound;
	private int numTestPerPhase;
	private int totalRequestCount;
	private int successRequestCount;
	
	private List<TaskResult> results;
	private List<Long> latencies;
	private Map<Long, Long> map;
	
	public TestPhase(String name, String url, int threadNum, int startTimeInterval, int endTimeInterval, int day, 
			int userPopulation, int stepUpperBound, int numTestPerPhase) {
		this.name = name;
		this.url = url;
		this.threadNum = threadNum;
		this.startTimeInterval = startTimeInterval;
		this.endTimeInterval = endTimeInterval;
		this.day = day;
		this.userPopulation = userPopulation;
		this.stepUpperBound = stepUpperBound;
		this.totalRequestCount = threadNum * (endTimeInterval - startTimeInterval + 1) * numTestPerPhase * 5;
		this.numTestPerPhase = numTestPerPhase;
	}

	public List<TaskResult> getResults() {
		return results;
	}
	
	public List<Long> getLatencies() {
		return latencies;
	}
	
	public Map<Long, Long> getMap() {
		return map;
	}
	
	public int getSuccessCount() {
		return successRequestCount;
	}
	
	public int getTotalCount() {
		return totalRequestCount;
	}
	
	public void run() {
		System.out.println("Running " + name + " phase : " + threadNum + " threads total " + numTestPerPhase * (endTimeInterval - startTimeInterval + 1)
				+ " times");
		
		results = new ArrayList<TaskResult>();
		latencies = new ArrayList<Long>();
		map = new TreeMap<Long, Long>();
		List<Request> requests = new ArrayList<Request>();
		
		long startTime = System.currentTimeMillis();
		
		ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
		for (int i = 0; i < threadNum; ++i) {
			Client client = ClientBuilder.newBuilder().build();
			Request request = new Request(client, url, startTimeInterval, endTimeInterval,
					userPopulation, day, stepUpperBound, numTestPerPhase);
			requests.add(request);
			executorService.submit(request);
		}
		try {
			executorService.shutdown();
			executorService.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		long endTime = System.currentTimeMillis();
		
		for (Request request : requests) {
			results.addAll(request.getResults());
			latencies.addAll(request.getLatencies());
			for (long key : request.getMap().keySet()) {
                if (!map.containsKey(key)) {
                    map.put(key, request.getMap().get(key));
                } else {
                    map.put(key, map.get(key) + request.getMap().get(key));
                }
            }

			successRequestCount += request.getSuccessCount();
		}
		
		System.out.println(String.format("%s phase complete : Time %f seconds",
                        name, (endTime - startTime) / 1000.0));
	}
	
}
