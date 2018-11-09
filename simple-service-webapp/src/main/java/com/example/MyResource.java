package com.example;

//import java.sql.Connection;
import java.util.List;

import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {
	
	StepCounterDao stepCounterDao = StepCounterDao.getInstance();
	
    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
	@Path("/get")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
//    	try {
//    		CountData data = new CountData(1,1,0,3000);
//    		String rst = stepCounterDao.insert(data);
//    		
//    		return rst;
//    	} catch (Exception e) {
//    		System.out.println(e.getMessage());
//    	}
    	
    	
        return "Got it !";
    }
    
    
    @Path("{userID}/{day}/{timeInterval}/{stepCount}")
    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String postData(@PathParam("userID") int userID, @PathParam("day") int day, 
    		@PathParam("timeInterval") int timeInterval, @PathParam("stepCount") int stepCount) {
    	try {
			CountData data = new CountData(userID, day, timeInterval, stepCount);
			String rst = stepCounterDao.insert(data);
			
			return rst;
		} catch (Exception e) {
			return e.getMessage();
		}
    	
//    	return "Post unsuccessfully";
    }
    
    @Path("current/{userID}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public int getCurrentDayData(@PathParam("userID") int userID) {
    	try {
    		int count = stepCounterDao.getCurrent(userID);
    		
    		return count;
    		
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	return -1;
    }
    
    @Path("single/{userID}/{day}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public int getGivenDayData(@PathParam("userID") int userID, @PathParam("day") int day) {
    	try {
    		int count = stepCounterDao.getDay(userID, day);
    		
    		return count;
    		
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	return -1;
    }
    
    @Path("range/{userID}/{startDay}/{numDays}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getGivenDayData(@PathParam("userID") int userID, @PathParam("startDay") int day, 
    		@PathParam("numDays") int numDays) {
    	try {
    		List<Integer> counts = stepCounterDao.getRange(userID, day, numDays);
    		
    		StringBuilder sb = new StringBuilder();
    		for (Integer count : counts) {
    			sb.append(count);
    			sb.append(", ");
    		}
    		
    		sb.delete(sb.length() - 2, sb.length() - 1);
    		
    		return sb.toString();
    		
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	return "No data available!";
    }
    
}
