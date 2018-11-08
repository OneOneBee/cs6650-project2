package edu.neu.cs6650.project2.MyClient;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

public class MyClient{
    private final int NUM_OF_THREADS;
    private final int NUM_OF_ITERATIONS;
    private final int POPULATION;
    private final int DAY_NUM;

    private final MyPrinter printer;
    private OutPut outPut;
    private Client client;
    private String url;
    private  int numIterations;
    //int[] timeInterval;

    public MyClient(String ipV4,int numOfThreads, int numOfIterations, int dayNum, int population) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        url = ipV4 + ":8080" + "/simple-service-webapp-updated/webapi/myresource/";
        NUM_OF_THREADS = numOfThreads;
        NUM_OF_ITERATIONS = numOfIterations;
        POPULATION = population;
        DAY_NUM = dayNum;

        printer = new MyPrinter(numOfThreads, numOfIterations);
        outPut = new OutPut(printer);
    }

    void threadRunner(String phaseName, double percentage) throws InterruptedException {
        long startTimeInMills = System.currentTimeMillis();
        String currentTime = getCurrentTime();
        System.out.println(phaseName + " phase start. Time: " + currentTime);
        int numOfThreads = (int) (NUM_OF_THREADS * percentage);
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
        //Client client = ClientBuilder.newClient();

        int[] timeInterval = timeIntervalForPhase(phaseName);
        numIterations = (timeInterval[1]-timeInterval[0]+1)*NUM_OF_ITERATIONS;
        List<ClientTask> tasks = new ArrayList<>();
        for (int i = 0; i < numOfThreads; i++) {
            tasks.add(new ClientTask(client,url,numIterations,DAY_NUM,POPULATION,timeInterval,printer));
        }
        System.out.println(phaseName + " phase all threads running....");
        executorService.invokeAll(tasks);

        executorService.shutdown();

        //client.close();

        currentTime = getCurrentTime();
        long endTimeInMills = System.currentTimeMillis();
        long timeElapsedInSeconds = (endTimeInMills - startTimeInMills)
                / 1000;
        System.out.println(phaseName + " phase complete, " +
                "Time: " + currentTime + ". " + timeElapsedInSeconds + " seconds.");

    }
    private int[] timeIntervalForPhase(String phase){
        if(phase.equals(MyPrinter.WARM_UP)) return  new int[]{0,2};
        else if(phase.equals(MyPrinter.LOADING)) return  new int[]{3,7};
        else if(phase.equals(MyPrinter.PEAK)) return new int[]{8,18};
        else return  new int[]{19,23};
    }



    String getCurrentTime() {
        DateTimeFormatter dtf =
                DateTimeFormatter.ofPattern("hh:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return dtf.format(now);
    }


//    public void close() {
//        client.close();
//    }

    public void roll() throws InterruptedException, IOException {
        long startTimeInMills = System.currentTimeMillis();
        String currentTime = getCurrentTime();
        System.out.println("Client starting.. Time: " + currentTime);

        //start timer output
        outPut.outputBySecond();

        threadRunner(MyPrinter.WARM_UP, 0.1);
        threadRunner(MyPrinter.LOADING, 0.5);
        threadRunner(MyPrinter.PEAK, 1);
        threadRunner(MyPrinter.COOLDOWN, 0.25);

        //close timer
        outPut.close();
        outPut.roll();
        //client.close();

        System.out.println("==============================");
        long endTimeInMills = System.currentTimeMillis();
        long timeElapsedInSeconds = (endTimeInMills - startTimeInMills)
                / 1000;
        printer.printResult(timeElapsedInSeconds);
    }

//    public static void main(String[] args) {
//        int numOfThreads = Integer.parseInt(args[1]);
//        int numOfIterations = Integer.parseInt(args[2]);
//        int dayNum = Integer.parseInt(args[3]);
//        int population = Integer.parseInt(args[4]);
//        MyClient myClient = new MyClient(args[0], numOfThreads, numOfIterations,dayNum,population);
//        long currentTime = System.currentTimeMillis();
//        for (int i = 0;i < 2;i++) {
//            Response response = myClient.postUserData(4,1,6,500);
//            if(myClient.isSuccessful(response)) {
//                System.out.println("Post " + i + " is successful");
//            }else {
//                System.out.println("Post " + i + " is failed");
//            }
//            response = myClient.getSingleUserData(1,1);
//            if(myClient.isSuccessful(response)) {
//                System.out.println("Get Single  " + i + " is successful");
//            }else {
//                System.out.println("Get Single" + i + " is failed");
//            }
//
//            response = myClient.getCurrentUserData(1);
//            if(myClient.isSuccessful(response)) {
//                System.out.println("Get current " + i + " is successful");
//            }else {
//                System.out.println("Get current " + i + " is failed\n");
//            }
//        }
//        System.out.println(System.currentTimeMillis() - currentTime);
//    }


}
