package edu.neu.cs6650.project2.MyClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

public class Phase{
    private String phaseName;
    private String url;
    private int threadCount;
    private int numTestPerPhase;
    private int startTimeInterval;
    private int endTimeInterval;
    private int dayNum;
    private int userPopulation;
    private int stepUpperBound;
    
    private long startTime;
    private long endTime;
    
    private int totalRequestCount;
    private int successRequestCount;
    private List<TaskResult> results;
    private List<Long> requestTimes;
    
    
    public Phase(String phaseName, String url, int threadCount, int numTestPerPhase, 
    		int startTimeInterval, int endTimeInterval, int dayNum, int userPopulation, int stepUpperBound)
    {
        this.phaseName = phaseName;
        this.url = url;
        this.threadCount = threadCount;
        this.numTestPerPhase = numTestPerPhase;
        this.startTimeInterval = startTimeInterval;
        this.endTimeInterval = endTimeInterval;
        this.dayNum = dayNum;
        this.userPopulation = userPopulation;
        this.stepUpperBound = stepUpperBound;
    }

    public int getTotalRequestCount() {
     
    	return this.totalRequestCount;
    }

    public int getSuccessRequestCount() {
    	
    	return this.successRequestCount;
    }

    public List<TaskResult> getTaskResult() {
    	return this.results;
    }
    
    public List<Long> getRequestTimes() {
    	return this.requestTimes;
    }

    public void run() throws InterruptedException {
        System.out.println(
                String.format(
                        "%s phase : %d threads each performs POST/GET request for %d times",
                        phaseName, threadCount, numTestPerPhase * (endTimeInterval - startTimeInterval + 1)));
        
        results = new ArrayList<TaskResult>();
        requestTimes = new ArrayList<Long>();
        startTime = System.currentTimeMillis();
        
    	ExecutorService threadPool = Executors.newFixedThreadPool(threadCount);
    	List<RequestTest> tests = new ArrayList<>();
    	for (int i = 0; i < threadCount; ++i) {
    		Client client = ClientBuilder.newClient();
    		RequestTest requestTest = new RequestTest(url, client, userPopulation, dayNum, this.startTimeInterval, 
    										this.endTimeInterval, numTestPerPhase, stepUpperBound);
    		tests.add(requestTest);
    		threadPool.submit(requestTest);
    	}
    										
    	threadPool.shutdown();
    	
    	try {
    		threadPool.awaitTermination(1, TimeUnit.DAYS);
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}

        endTime = System.currentTimeMillis();
        
        for (RequestTest requestTest : tests) {
            results.addAll(requestTest.getTaskResults());
            requestTimes.addAll(requestTest.getRequestTimes());
            this.totalRequestCount += requestTest.getTotalRequestCount();
            this.successRequestCount += requestTest.getSuccessRequestCount();
        }
        
        System.out.println(
                String.format(
                        "%s phase complete : Time %f seconds",
                        phaseName, (endTime - startTime) / 1000.0));

    }
}
