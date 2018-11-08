package edu.neu.cs6650.project2.MyClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class Request implements Runnable {
	private Client client;
	private String url;
	private int startTimeInterval;
	private int endTimeInterval;
	private int userPopulation;
	private int day;
	private int stepUpperBound;
	private int numTestPerPhase;
	
	private int successRequestCount;
	
	private List<TaskResult> results;
	private List<Long> latencies;
	private Map<Long, Long> map;
	
	public Request (Client client, String url, int startTimeInterval, int endTimeInterval, 
			int userPopulation, int day, int stepUpperBound, int numTestPerPhase) {
		this.client = client;
		this.url = url;
		this.startTimeInterval = startTimeInterval;
		this.endTimeInterval = endTimeInterval;
		this.userPopulation = userPopulation;
		this.day = day;
		this.stepUpperBound = stepUpperBound;
		this.numTestPerPhase = numTestPerPhase;
		this.successRequestCount = 0;
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

	public void run() {
		results = new ArrayList<TaskResult>();
		latencies = new ArrayList<Long>();
		map = new TreeMap<Long, Long>();
		WebTarget webTarget = client.target(url);
		
		for (int time = startTimeInterval; time <= endTimeInterval; ++time) {
			for (int i = 0; i < numTestPerPhase; ++i) {
				// post1
				DummyData data1 = new DummyData(userPopulation, day, time, stepUpperBound);
				post(webTarget, data1);
				// post2
				DummyData data2 = new DummyData(userPopulation, day, time, stepUpperBound);
				post(webTarget, data2);
				// get1
				getCurrentDay(webTarget, data1.getUserID());
				// get2
				getGivenDay(webTarget, data2.getUserID(), data2.getDay());
				// post3
				DummyData data3 = new DummyData(userPopulation, day, time, stepUpperBound);
				post(webTarget, data3);
//				
//				for (int j = 0; j < 5; ++j) {
//					test(webTarget);
//				}
			}
		}
	}
	
	public void test(WebTarget webTarget) {
		Response response = null;
		TaskResult result = new TaskResult();
		
		try {
			long stime = System.currentTimeMillis();
			response = webTarget.path("/get")
					.request().get();
			
			long etime = System.currentTimeMillis();
			
			result.startTime = stime / 1000;
			result.endTime = etime / 1000;
			result.latency = etime - stime;
			result.requestType = "GET";
			System.out.println(result.latency);
			if (!map.containsKey(result.startTime)) {
				map.put(result.startTime, (long)1);
			} else {
				map.put(result.startTime, map.get(result.startTime) + 1);
			}
			
			if (response.getStatus() == 200) {
				this.successRequestCount++;
				result.success = true;
			}
			
			results.add(result);
			latencies.add(etime - stime);
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		} finally {
			response.close();
		}
		
		
	}
	
	public void post(WebTarget webTarget, DummyData data) {
		Response response = null;
		TaskResult result = new TaskResult();
		
		try {
			long stime = System.currentTimeMillis();
			response = webTarget.path("/{userID}/{day}/{timeInterval}/{stepCount}")
					.resolveTemplate("userID", data.getUserID())
					.resolveTemplate("day", data.getDay())
					.resolveTemplate("timeInterval", data.getTimeInterval())
					.resolveTemplate("stepCount", data.getStepCount())
					.request(MediaType.TEXT_PLAIN)
					.post(Entity.entity(data, MediaType.TEXT_PLAIN));
			
			long etime = System.currentTimeMillis();
			
			result.startTime = stime / 1000;
			result.endTime = etime / 1000;
			result.latency = etime - stime;
			result.requestType = "POST";
			
//			System.out.println(result.latency);
			
			if (!map.containsKey(result.startTime)) {
				map.put(result.startTime, (long)1);
			} else {
				map.put(result.startTime, map.get(result.startTime) + 1);
			}
			
			if (response.getStatus() == 200) {
				this.successRequestCount++;
				result.success = true;
			}
			
			results.add(result);
			latencies.add(etime - stime);
			
		} catch (Exception e) {
//			System.out.println("Post step count request");
			System.out.println("/" + data.getUserID() + "/" + data.getDay() + "/" + data.getTimeInterval() + "/" + data.getStepCount() + "\n"
					+ e.getMessage() + "\n"
					+ e.getCause());
		} finally {
			response.close();
		}
	}
	
	public void getCurrentDay(WebTarget webTarget, int userID) {
		Response response = null;
		TaskResult result = new TaskResult();
		
		try {
			long stime = System.currentTimeMillis();
			
			response = webTarget.path("/current/{userID}")
				.resolveTemplate("userID", userID)
				.request(MediaType.TEXT_PLAIN)
				.get();
			
			long etime = System.currentTimeMillis();
			
			result.startTime = stime / 1000;
			result.endTime = etime / 1000;
			result.latency = etime - stime;
			result.requestType = "GET";
			
//			System.out.println(result.latency);
			
			if (!map.containsKey(result.startTime)) {
				map.put(result.startTime, (long)1);
			} else {
				map.put(result.startTime, map.get(result.startTime) + 1);
			}
			
			if (response.getStatus() == 200) {
				this.successRequestCount++;
				result.success = true;
			}
			
			results.add(result);
			latencies.add(etime - stime);
			
		} catch (Exception e) {
			System.out.println("GET current day request");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		} finally {
			response.close();
		}

	}
	
	public void getGivenDay(WebTarget webTarget, int userID, int day) {
		Response response = null;
		TaskResult result = new TaskResult();
		
		try {
			long stime = System.currentTimeMillis();
			
			response = webTarget.path("/single/{userID}/{day}")
				.resolveTemplate("userID", userID)
				.resolveTemplate("day", day)
				.request(MediaType.TEXT_PLAIN)
				.get();
			
			long etime = System.currentTimeMillis();
			
			result.startTime = stime / 1000;
			result.endTime = etime / 1000;
			result.latency = etime - stime;
			result.requestType = "GET";
			
//			System.out.println(result.latency);
			
			if (!map.containsKey(result.startTime)) {
				map.put(result.startTime, (long)1);
			} else {
				map.put(result.startTime, map.get(result.startTime) + 1);
			}
			
			if (response.getStatus() == 200) {
				this.successRequestCount++;
				result.success = true;
			}
			
			results.add(result);
			latencies.add(etime - stime);
			
		} catch (Exception e) {
			System.out.println("GET given day request");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		} finally {
			response.close();
		}
		
	}
}
