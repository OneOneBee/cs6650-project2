package edu.neu.cs6650.project2.MyClient;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class RequestTest implements Runnable{
	private String url;
	private int userPopulation;
	private int startTimeInterval;
	private int endTimeInterval;
	private int day;
	private int numTestPerPhase;
	private int stepUpperBound;
	private Client client;
	
    private int totalRequestCount;
    private int successRequestCount;
    private List<TaskResult> taskResults;
    private List<Long> requestTimes;

	
	public RequestTest(String url, Client client, int userPopulation, int day, int startTimeInterval, int endTimeInterval, 
					int numTestPerPhase, int stepUpperBound) {
		this.url = url;
		this.client = client;
		this.userPopulation = userPopulation;
		this.startTimeInterval = startTimeInterval;
		this.endTimeInterval = endTimeInterval;
		this.day = day;
		this.numTestPerPhase = numTestPerPhase;
		this.stepUpperBound = stepUpperBound;
	}
	
	public int getTotalRequestCount() {
		
		return this.totalRequestCount;
	}
	
	public int getSuccessRequestCount() {
		
		return this.successRequestCount;
	}
	
	public List<TaskResult> getTaskResults() {
		
		return this.taskResults;
	}
	
    public List<Long> getRequestTimes() {
    	return this.requestTimes;
    }
	
	public void run() {
		
		this.taskResults = new ArrayList<TaskResult>();
		this.requestTimes = new ArrayList<Long>();
		
		WebTarget webTarget = client.target(url);

		for (int timeInterval = this.startTimeInterval; timeInterval <= this.endTimeInterval; ++timeInterval) {
			for (int i = 0; i < numTestPerPhase; ++i) {
				// post1
				DummyData data1 = new DummyData(userPopulation, day, timeInterval, stepUpperBound);
				post(webTarget, data1);
				// post2
				DummyData data2 = new DummyData(userPopulation, day, timeInterval, stepUpperBound);
				post(webTarget, data2);
				// get1
				getCurrentDay(webTarget, data1.getUserID());
				// get2
				getGivenDay(webTarget, data2.getUserID(), data2.getDay());
				// post3
				DummyData data3 = new DummyData(userPopulation, day, timeInterval, stepUpperBound);
				post(webTarget, data3);
				
			}	
		}
		
		client.close();
	}
	
	private boolean post(WebTarget webTarget, DummyData data) {
		TaskResult result = new TaskResult();
		
		long stime = System.currentTimeMillis();
		String postUrl = "/" + data.getUserID() + "/" + data.getDay() + "/" + data.getTimeInterval() + "/" + data.getStepCount();
		Response response = null;
		try {
			response = webTarget.path("/{userID}/{day}/{timeInterval}/{stepCount}")
					.resolveTemplate("userID", data.getUserID())
					.resolveTemplate("day", data.getDay())
					.resolveTemplate("timeInterval", data.getTimeInterval())
					.resolveTemplate("stepCount", data.getStepCount())
					.request().post(Entity.entity(data, MediaType.TEXT_PLAIN));
			
			long etime = System.currentTimeMillis();
			
			result.startTime = stime;
			result.endTime = etime;
			result.requestType = "POST";
			result.content = postUrl;
			
			requestTimes.add((long)etime - stime);
			
			this.totalRequestCount++;
			
//			System.out.println(response.getStatus());
			
			if (response.getStatus() == 200) {
				//System.out.println("Post success!");
				this.successRequestCount++;
				result.success = true;
				this.taskResults.add(result);
				
				return true;
			}
			
			result.success = false;
			this.taskResults.add(result);
			
		} catch (Exception e) {
			System.out.println("==========POST EXCEPTION=========");
			System.out.println(e.getMessage());
			System.out.println(e.getCause());
		} finally {
			response.close();
		}
		
		return false;
		
	}
	
	private boolean getCurrentDay(WebTarget webTarget, int userID) {
		TaskResult result = new TaskResult();

		long stime = System.currentTimeMillis();
		String getUrl = "/current/" + userID;
		
		Response response = null;
		
		try {
			webTarget.path("/current/{userID}")
			.resolveTemplate("userID", userID);
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.TEXT_PLAIN);
			response = invocationBuilder.get();
			
			long etime = System.currentTimeMillis();
			
			result.startTime = stime;
			result.endTime = etime;
			result.requestType = "GET";
			result.content = getUrl;
			
			requestTimes.add((long)etime - stime);
			
			//System.out.println(response.getStatus());
			
			this.totalRequestCount++;
			if (response.getStatus() == 200) {
				//System.out.println("Get curr day success!");
				this.successRequestCount++;
				result.success = true;
				this.taskResults.add(result);
				
				return true;
			}
			
			result.success = false;
			this.taskResults.add(result);
		} finally {
			response.close();
		}
		
		return false;

	}
	
	private boolean getGivenDay(WebTarget webTarget, int userID, int day) {
		TaskResult result = new TaskResult();
		
		Response response = null;
		
		try {
			long stime = System.currentTimeMillis();
			String getUrl = "/single/" + userID + "/" + day;
				
			webTarget.path("/single/{userID}/{day}")
					.resolveTemplate("userID", userID)
					.resolveTemplate("day", day);
			
			Invocation.Builder invocationBuilder = webTarget.request(MediaType.TEXT_PLAIN);
			response = invocationBuilder.get();
			
			long etime = System.currentTimeMillis();
			
			result.startTime = stime;
			result.endTime = etime;
			result.requestType = "GET";
			result.content = getUrl;
			
			requestTimes.add((long)etime - stime);
			
			//System.out.println(response.getStatus());
			
			this.totalRequestCount++;
			if (response.getStatus() == 200) {
				//System.out.println("Get given day success!");
				this.successRequestCount++;
				result.success = true;
				this.taskResults.add(result);
				
				return true;
			}
			
			result.success = false;
			this.taskResults.add(result);
		} finally {
			response.close();
		}
		
		return false;
	}
	
}

//package edu.neu.cs6650.project2.MyClient;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.client.Invocation;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//
//public class RequestTest implements Runnable{
//	private String url;
//	private int userPopulation;
////	private int timeInterval;
//	private int day;
//	private int numTestPerPhase;
//	private int stepUpperBound;
//	private int startTimeInterval;
//	private int endTimeInterval;
//	
//    private int totalRequestCount;
//    private int successRequestCount;
//    private List<TaskResult> results;
//    private List<Long> requestTimes;
//	
//	public RequestTest(String url, int userPopulation, int day, int startTimeInterval, int endTimeInterval, int numTestPerPhase, int stepUpperBound) {
//		this.url = url;
//		this.userPopulation = userPopulation;
//		this.startTimeInterval = startTimeInterval;
//		this.endTimeInterval = endTimeInterval;
//		this.day = day;
//		this.numTestPerPhase = numTestPerPhase;
//		this.stepUpperBound = stepUpperBound;
//	}
//	
//	public int getTotalRequestCount() {
//		
//		return this.totalRequestCount;
//	}
//	
//	public int getSuccessRequestCount() {
//		
//		return this.successRequestCount;
//	}
//	
//	public List<TaskResult> getTaskResults() {
//		
//		return this.results;
//	}
//	
//	public List<Long> getRequestTimes() {
//		return this.requestTimes;
//	}
//	
//	public void run() {
//		
//		this.results = new ArrayList<TaskResult>();
//		this.requestTimes = new ArrayList<Long>();
//        //String threadId = new Long(Thread.currentThread().getId()).toString();
//
//       
////        WebTarget webTarget = client.target(url);
////        Invocation.Builder getCaller = webTarget.request();
////        Invocation.Builder postCaller = webTarget.request();
//		
//		for (int timeInterval = this.startTimeInterval; timeInterval <= this.endTimeInterval; ++timeInterval) {
//			for (int i = 0; i < numTestPerPhase; ++i) {
//				// post1
//				DummyData data1 = new DummyData(userPopulation, day, timeInterval, stepUpperBound);
//				post(data1);
//				// post2
//				DummyData data2 = new DummyData(userPopulation, day, timeInterval, stepUpperBound);
//				post(data2);
//				// get1
//				getCurrentDay(data1.getUserID());
//				// get2
//				getGivenDay(data2.getUserID(), data2.getDay());
//				// post3
//				DummyData data3 = new DummyData(userPopulation, day, timeInterval, stepUpperBound);
//				post(data3);
//				
//				this.totalRequestCount += 5;
//			}	
//		}
//	}
//	
//	private boolean post(DummyData data) {
//		TaskResult result = new TaskResult();
//		result.requestType = "POST";
//		Client client = ClientBuilder.newClient();
//		
//		long stime = System.currentTimeMillis();
//		result.startTime = stime;
//		
//		String postUrl = url + "/" + data.getUserID() + "/" + data.getDay() + "/" + data.getTimeInterval() + "/" + data.getStepCount();
//		result.content = "/" + data.getUserID() + "/" + data.getDay() + "/" + data.getTimeInterval() + "/" + data.getStepCount();
//		try {
//			
//			WebTarget webTarget = client.target(postUrl);		
//			Invocation.Builder postCaller = webTarget.request();
//			postCaller.post(Entity.entity(data, MediaType.TEXT_PLAIN));
//			
//			long etime = System.currentTimeMillis();
//			result.endTime = etime;
//				
//			this.successRequestCount++;
//			result.success = true;
//			//System.out.println("Post success!");
//			results.add(result);
//			return true;
//		} catch (Exception e) {
//			result.success = false;
//			results.add(result);
//			System.out.println("Post error : " + e.getMessage());
//			return false;
//		}
//		
//	}
//	
//	private boolean getCurrentDay(int userID) {
//		TaskResult result = new TaskResult();
//		result.requestType = "GET";
//		Client client = ClientBuilder.newClient();
//		
//		long stime = System.currentTimeMillis();
//		result.startTime = stime;
//		
//		String getUrl = url + "/current/" + userID;
//		result.content = "/current/" + userID;
//		
//		try {
//			WebTarget webTarget = client.target(getUrl);
//			Invocation.Builder getCaller = webTarget.request();
//			getCaller.get(Integer.class);
//			
//			long etime = System.currentTimeMillis();
//			result.endTime = etime;
//			
//			this.successRequestCount++;
//			result.success = true;
//			
//			//System.out.println("Get curr day success!");
//			results.add(result);
//			return true;
//		} catch (Exception e) {
//			result.success = false;
//			results.add(result);
//			System.out.println("Get curr day error : " + e.getMessage());
//			return false;
//		}
//	}
//	
//	private boolean getGivenDay(int userID, int day) {
//		TaskResult result = new TaskResult();
//		result.requestType = "GET";
//		Client client = ClientBuilder.newClient();
//		
//		long stime = System.currentTimeMillis();
//		result.startTime = stime;
//		
//		String getUrl = url + "/single/" + userID + "/" + day;
//		result.content = "/single/" + userID + "/" + day;
//		
//		try {
//			
//			WebTarget webTarget = client.target(getUrl);
//			Invocation.Builder getCaller = webTarget.request();
//			getCaller.get(Integer.class);
//			
//			long etime = System.currentTimeMillis();
//			result.endTime = etime;
//
//			this.successRequestCount++;
//			result.success = true;
//			//System.out.println("Get given day success!");
//			results.add(result);
//			return true;
//		} catch (Exception e) {
//			result.success = false;
//			results.add(result);
//			System.out.println("Get given day error : " + e.getMessage());
//			return false;
//		}
//	}
//	
//}
//
