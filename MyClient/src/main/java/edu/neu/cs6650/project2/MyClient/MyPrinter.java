package edu.neu.cs6650.project2.MyClient;

import javax.imageio.IIOException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MyPrinter {
    private final int NUM_OF_THREADS;
    private final int NUM_OF_ITERATIONS;
    private final List<Long> latencyList;

     AtomicInteger numRequest = new AtomicInteger();
     AtomicInteger numResponse = new AtomicInteger();

    public static final String WARM_UP = "Warmup";
    public static final String LOADING = "Loading";
    public static final String PEAK = "Peak";
    public static final String COOLDOWN = "Cooldown";

    public MyPrinter(int numOfThreads, int numOfIterations) {
        NUM_OF_THREADS = numOfThreads;
        NUM_OF_ITERATIONS = numOfIterations;
        latencyList = Collections.synchronizedList(new ArrayList<Long>());
    }

    public void initClientInfo(String[] args) {
        System.out.println("----------------Results-----------");
        System.out.println("Number of threads: " + args[1]);
        System.out.println("Number of iterations: " + args[2]);
        System.out.println("dayNum: " + args[3]);
        System.out.println("Population: " + args[4]);
        System.out.println("-----------------------------------");
    }

    public synchronized void numOfRequestsSent(int amount) {
        numRequest.addAndGet(amount);
    }

    public synchronized void numOfSuccessfulRequests(int amount) {
        numResponse.addAndGet(amount);
    }

    public void appendLatency(long timeElapsed) {
        latencyList.add(timeElapsed);
    }


    public void printResult(long totalTimeToFinishInSeconds) {
        System.out.println("Total number of requests sent: " + numRequest.intValue());
        System.out.println("Total number of successful requests: "
                + numResponse.intValue());
        System.out.println("Test Wall Time: " + totalTimeToFinishInSeconds + "s.");

        System.out.println("--------------------------------");

        System.out.println("Overall throughput : " + numRequest.get() / totalTimeToFinishInSeconds);

        System.out.println("--------------------------------");
        printLatencyResult();
    }

    private void printLatencyResult() {
        Collections.sort(latencyList);
        int latencyListSize = latencyList.size();
        if (latencyListSize == 0) {
            System.out.println("No latency data available.");
            return;
        }

        long sumOfLatency = 0;
        for (long latency : latencyList) {
            sumOfLatency += latency;
        }
        long meanLatency = sumOfLatency / latencyList.size();
        System.out.println("The mean value of latency is: " + meanLatency + "ms.");

        long medianLatency = 0;
        if (latencyListSize % 2 == 0) {
            long leftLatency = latencyList.get(latencyListSize / 2 - 1);
            long rightLatency = latencyList.get(latencyListSize / 2);
            medianLatency = (leftLatency + rightLatency) / 2;
        } else {
            medianLatency = latencyList.get(latencyListSize / 2);
        }
        System.out.println("The median value of latency is: "
                + medianLatency + "ms.");

        printPercentileLatency(95, latencyListSize);
        printPercentileLatency(99, latencyListSize);
    }

    private void printPercentileLatency(int percentile, int latencyListSize) {
        long percentileLatency =
                latencyList.get(latencyListSize * percentile / 100);
        System.out.println("The " + percentile + "th percentile latency is: " +
                percentileLatency + "ms.");
    }


}
