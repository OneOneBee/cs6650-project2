package edu.neu.cs6650.project2.MyClient;

import java.io.FileWriter;
import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ClientRequest {
	public static void main(String[] args) throws InterruptedException {
        String serverAddress = "ec2-34-211-205-139.us-west-2.compute.amazonaws.com";
        String port = "8080";
        String path = "simple-service-webapp-updated/webapi/myresource";

        int maxThread = 32;
        int dayNum = 1;
        int userPopulation = 100000;
        int numTestPerPhase = 100;
        int stepUpperBound = 5000;
        //Map<String, Integer> exception = new HashMap<>();
        
        String url = "http://" + serverAddress + ":" + port + "/" + path;
        
        System.out.println("Start testing against : " + url);
        System.out.println("=============================================================================================");

        long startTime = System.currentTimeMillis();

        Phase warmup = new Phase("Warmup", url, (int) (maxThread * 0.1), numTestPerPhase, 0, 2, dayNum, userPopulation, stepUpperBound);
        warmup.run();

        Phase loading = new Phase("Loading", url, (int) (maxThread * 0.5), numTestPerPhase, 3, 7, dayNum, userPopulation, stepUpperBound);
        loading.run();

        Phase peak = new Phase("Peak", url, maxThread, numTestPerPhase, 8, 18, dayNum, userPopulation, stepUpperBound);
        peak.run();

        Phase cooldown = new Phase("Cooldown", url, (int) (maxThread * 0.25), numTestPerPhase, 19, 23, dayNum, userPopulation, stepUpperBound);
        cooldown.run();

        long endTime = System.currentTimeMillis();
        
        int totalRequestCount
        = warmup.getTotalRequestCount() +
          loading.getTotalRequestCount() +
          peak.getTotalRequestCount() +
          cooldown.getTotalRequestCount();

	    int totalSuccessRequestCount
	        = warmup.getSuccessRequestCount() +
	          loading.getSuccessRequestCount() +
	          peak.getSuccessRequestCount() +
	          cooldown.getSuccessRequestCount();
	
	    List<TaskResult> taskResults = new ArrayList<TaskResult>();
	    List<Long> requestTimes = new ArrayList<Long>();
	    
	    taskResults.addAll(warmup.getTaskResult());
	    taskResults.addAll(loading.getTaskResult());
	    taskResults.addAll(peak.getTaskResult());
	    taskResults.addAll(cooldown.getTaskResult());
	    
	    requestTimes.addAll(warmup.getRequestTimes());
	    requestTimes.addAll(loading.getRequestTimes());
	    requestTimes.addAll(peak.getRequestTimes());
	    requestTimes.addAll(cooldown.getRequestTimes());
	    
	    writeToCSV(taskResults);
	    
	    // Sort the request times for calculating statistics.
	    Collections.sort(requestTimes);
	
	    System.out.println("===========================================================");
	    System.out.println(String.format("Total number of requests sent : %d", totalRequestCount));
	    System.out.println(String.format("Total number of successful response : %d", totalSuccessRequestCount));
	    System.out.println(String.format("Total Wall Time : %f seconds", (endTime - startTime) / 1000.0));
	    System.out.println(String.format("Overall throughput is : %f requests / second", (totalRequestCount * 1000.0 / (endTime - startTime))));
	    System.out.println(String.format("Mean latency is : %f seconds", CalcMeanLatency(requestTimes) / 1000.0));
	    System.out.println(String.format("Median latency is : %f seconds", GetMedian(requestTimes) / 1000.0));
	    System.out.println(String.format("95-th percentile latency is : %f seconds", GetPercentile(requestTimes, 0.95f) / 1000.0));
	    System.out.println(String.format("99-th percentile latency is : %f seconds", GetPercentile(requestTimes, 0.99f) / 1000.0));
	}
	
	// write to CSV file
	private static void writeToCSV(List<TaskResult> results) {
		
		FileWriter fileWriter = null;
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
			fileWriter = new FileWriter("requestTimes" + sdf.format(new Date()) + ".csv");
			fileWriter.append("StartTime,EndTime,Latency,Success,RequestType,Content\n");
			
			for (TaskResult result : results) {
				fileWriter.append(String.valueOf(result.startTime));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(result.endTime));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(result.endTime - result.startTime));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(result.success));
				fileWriter.append(",");
				fileWriter.append(result.requestType);
				fileWriter.append(",");
				fileWriter.append(result.content);
				fileWriter.append("\n");
			}
			
			System.out.println("CSV file was created successfully!");
			
		} catch (Exception e) {
			System.out.println("Error when writing CSV file");
			e.printStackTrace();
		} finally {
			try {
				fileWriter.flush();
				fileWriter.close();
			} catch (IOException e) {
				System.out.println("Error when flushing/closing fileWriter!");
				e.printStackTrace();
			}
		}
	}
	
    // Calculate mean latencies
    private static float CalcMeanLatency(List<Long> latencies)
    {
        Long total = 0L;
        Long count = 0L;
        for (Long latency : latencies) {
            if (latency >= 0) {
                total += latency;
                count++;
            }
        }

        if (count == 0) return 0.0f;
        else return 1.0f * total / count;
    }

    // Assuming latencies are sorted
    private static float GetMedian(List<Long> latencies)
    {
        int mid = latencies.size() / 2;

        if (latencies.size() % 2 == 0) {
            return (latencies.get(mid) + latencies.get(mid-1)) / 2.0f;
        } else {
            return latencies.get(mid);
        }
    }

    // Assume latencies are sorted
    private static long GetPercentile(List<Long> latencies, float percentile)
    {
        int index = (int) (latencies.size() * percentile);
        return latencies.get(index);
    }
}
