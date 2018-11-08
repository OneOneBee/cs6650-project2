package edu.neu.cs6650.project2.MyClient;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

public class ClientTask implements Callable<MyPrinter> {

    //private final int NUM_OF_ITERATIONS;
    private final int POPULATION;
    private final int DAY_NUM;

    private final MyPrinter printer;
    private final Client client;// = ClientBuilder.newClient();
    private String url;
    private  int numIterations;
    int[] timeInterval;

    public ClientTask(Client client, String url,int numOfIterations, int dayNum, int population, int[] timeInterval, MyPrinter printer){
        this.client = client;
        this.url = url;
        this.numIterations = numOfIterations;
        POPULATION = population;
        DAY_NUM = dayNum;
        this.printer = printer;
        this.timeInterval = timeInterval;
    }

    @Override
    public MyPrinter call() {
        for (int j = 0; j < numIterations; j++) {
            if (!handleHttpRequests(timeInterval)) {
                break;
            }
        }
        //client.close();
        return printer;
    }


    /**
     * Deal with the request send and response.
     */
    private boolean handleHttpRequests(int[] timeInterval) {
        //generate 3 random data
        int userId1 = ThreadLocalRandom.current().nextInt(POPULATION);
        int userId2 = ThreadLocalRandom.current().nextInt(POPULATION);
        int userId3 = ThreadLocalRandom.current().nextInt(POPULATION);

        int timeInterval1 = ThreadLocalRandom.current().nextInt(timeInterval[1] - timeInterval[0]+1) + timeInterval[0];
        int timeInterval2 = ThreadLocalRandom.current().nextInt(timeInterval[1] - timeInterval[0]+1) + timeInterval[0];
        int timeInterval3 = ThreadLocalRandom.current().nextInt(timeInterval[1] - timeInterval[0]+1) + timeInterval[0];
        int stepCount1 = ThreadLocalRandom.current().nextInt(5000);
        int stepCount2 = ThreadLocalRandom.current().nextInt(5000);
        int stepCount3 = ThreadLocalRandom.current().nextInt(5000);

        try {
            long startTime = System.currentTimeMillis();
            Response response = postUserData(userId1,DAY_NUM,timeInterval1,stepCount1);
            countResponse(startTime,response);

            startTime = System.currentTimeMillis();
            response = postUserData(userId2,DAY_NUM,timeInterval2,stepCount2);
            countResponse(startTime,response);

            startTime = System.currentTimeMillis();
            response = getCurrentUserData(userId1);
            countResponse(startTime,response);

            startTime = System.currentTimeMillis();
            response = getSingleUserData(userId1, DAY_NUM);
            countResponse(startTime,response);

            startTime = System.currentTimeMillis();
            response = postUserData(userId3,DAY_NUM,timeInterval3,stepCount3);
            countResponse(startTime,response);

        } catch (Exception e) {
           // System.out.println("Exception in Post1: " + e.getClass().getSimpleName());
            System.out.println(e.getMessage() + "\n" + e.getCause());
            return false;
        }

        printer.numOfRequestsSent(5);
        return true;
    }

    private void countResponse(long startTime,Response response){
        if (isSuccessful(response)) {
            printer.appendLatency(
                    System.currentTimeMillis() - startTime);
            printer.numOfSuccessfulRequests(1);
            //System.out.println(printer.numResponse.intValue());
        }
        response.close();
    }


    /**
     * Return true if the reponse status is 200 and false otherwise
     *
     * @param response The response to be validated against
     * @return true if the reponse status is 200 and false otherwise
     */
    protected boolean isSuccessful(Response response) {
        if (response == null) {
            return false;
        }
        //System.out.println(" Get Status is " + response.getStatus());
        return response.getStatus() == 200;
    }

    public Response getSingleUserData(int userId, int day) throws ClientErrorException {
        //WebTarget webTarget = client.target(url).path("myresource");
        return client.target(url).path("single/" + userId + "/" + day).request(TEXT_PLAIN).get();
    }

    public Response getCurrentUserData(int userId) throws ClientErrorException {
        return client.target(url).path("current/" + userId).request(TEXT_PLAIN).get();
    }

    public Response postUserData(int userId, int day, int time,int stepCount) throws ClientErrorException {
        //webTarget.path(MessageFormat.format("load/resortID={0}&dayNum={1}&timestamp={2}&skierID={3}&liftID={4}", new Object[]{resortID, dayNum, timestamp, skierID, liftID})));
        return client.target(url).path(userId + "/" + day + "/" + time + "/" + stepCount).request(MediaType.TEXT_PLAIN).post(Entity.text(""));
    }

    public void close() {
        client.close();
    }
}
