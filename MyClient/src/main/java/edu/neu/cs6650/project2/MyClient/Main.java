package edu.neu.cs6650.project2.MyClient;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main {
	public static void main(String[] args) {
//        String serverAddress = "ec2-34-211-205-139.us-west-2.compute.amazonaws.com";
//        String serverAddress = "ec2-34-212-240-117.us-west-2.compute.amazonaws.com";
//        String serverAddress = "LoadBalancer-1770157446.us-west-2.elb.amazonaws.com";
		String serverAddress = "localhost";
		String port = "8080";
        //String path = "simple-service-webapp-updated/webapi/myresource";
        String path = "project2_server_connpool/webapi/myresource";
//		String path = "ec2local/webapi/myresource";
        int maxThread = 64;
        int dayNum = 1;
        int userPopulation = 100000;
        int numTestPerPhase = 100;
        int stepUpperBound = 5000;
        //Map<String, Integer> exception = new HashMap<>();
        
        String url = "http://" + serverAddress + ":" + port + "/" + path;
        
        System.out.println("Start testing against : " + url);
        System.out.println("=============================================================================================");

        long startTime = System.currentTimeMillis();

        TestPhase warmup = new TestPhase("Warmup", url, (int) (maxThread * 0.1), 0, 2, dayNum, userPopulation, stepUpperBound, numTestPerPhase);
        warmup.run();

        TestPhase loading = new TestPhase("Loading", url, (int) (maxThread * 0.5), 3, 7, dayNum, userPopulation, stepUpperBound, numTestPerPhase);
        loading.run();

        TestPhase peak = new TestPhase("Peak", url, maxThread, 8, 18, dayNum, userPopulation, stepUpperBound, numTestPerPhase);
        peak.run();

        TestPhase cooldown = new TestPhase("Cooldown", url, (int) (maxThread * 0.25), 19, 23, dayNum, userPopulation, stepUpperBound, numTestPerPhase);
        cooldown.run();

        long endTime = System.currentTimeMillis();
        
        System.out.println("Total wall time : " + (endTime - startTime) / 1000);
        
        
        TestPhase[] phases = new TestPhase[4];
        phases[0] = warmup;
        phases[1] = loading;
        phases[2] = peak;
        phases[3] = cooldown;
        
        statistic(phases, startTime, endTime);
	}
	
	public static void statistic(TestPhase[] phases, long startTime, long endTime) {
		List<TaskResult> results = new ArrayList<TaskResult>();
		List<Long> latencies = new ArrayList<Long>();
		Map<Long, Long> map = new TreeMap<Long, Long>();
		int successRequestCount = 0;
		int totalRequestCount = 0;
		
		for (TestPhase phase : phases) {
			results.addAll(phase.getResults());
			latencies.addAll(phase.getLatencies());
			successRequestCount += phase.getSuccessCount();
			totalRequestCount += phase.getTotalCount();
			for (long key : phase.getMap().keySet()) {
                if (!map.containsKey(key)) {
                    map.put(key, phase.getMap().get(key));
                } else {
                    map.put(key, map.get(key) + phase.getMap().get(key));
                }
	        }
		}
		
		writeToCSV(results);
		
		writeMap(map);
		
		Collections.sort(latencies);
		
	    System.out.println("===========================================================");
	    System.out.println(String.format("Total number of requests sent : %d", totalRequestCount));
	    System.out.println(String.format("Total number of successful response : %d", successRequestCount));
	    System.out.println(String.format("Total Wall Time : %f seconds", (endTime - startTime) / 1000.0));
	    System.out.println(String.format("Overall throughput is : %f requests / second", (totalRequestCount * 1000.0 / (endTime - startTime))));
	    System.out.println(String.format("Mean latency is : %f seconds", CalcMeanLatency(latencies) / 1000.0));
	    System.out.println(String.format("Median latency is : %f seconds", GetMedian(latencies) / 1000.0));
	    System.out.println(String.format("95-th percentile latency is : %f seconds", GetPercentile(latencies, 0.95f) / 1000.0));
	    System.out.println(String.format("99-th percentile latency is : %f seconds", GetPercentile(latencies, 0.99f) / 1000.0));
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
	
	public static void writeToCSV(List<TaskResult> results) {
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
				fileWriter.append(String.valueOf(result.latency));
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
	
	public static void writeMap(Map<Long, Long> map) {
		FileWriter fileWriter = null;
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("_yyyy_MM_dd_HH_mm_ss");
			fileWriter = new FileWriter("map" + sdf.format(new Date()) + ".csv");
			fileWriter.append("timestamp,reqNum\n");
			
			for (Long time : map.keySet()) {
				fileWriter.append(String.valueOf(time));
				fileWriter.append(",");
				fileWriter.append(String.valueOf(map.get(time)));
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
}
