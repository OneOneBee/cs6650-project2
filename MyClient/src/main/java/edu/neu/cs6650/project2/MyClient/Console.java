package edu.neu.cs6650.project2.MyClient;

import java.io.IOException;

public class Console {
    public static void main(String[] args) throws InterruptedException, IOException {

    	args = new String[5];
    	
    	args[0] = "http://ec2-34-211-205-139.us-west-2.compute.amazonaws.com";
    	args[1] = "64";
    	args[2] = "100";
    	args[3] = "1";
    	args[4] = "100000";
    	
        if (args.length != 5) {
            System.out.println("Input is incorrect! Should be 5 arguments!");
            System.exit(1);
        }

    	
        int numOfThreads = Integer.parseInt(args[1]);
        int numOfIterations = Integer.parseInt(args[2]);
        int dayNum = Integer.parseInt(args[3]);
        int population = Integer.parseInt(args[4]);
        

        MyPrinter printer = new MyPrinter(numOfThreads, numOfIterations);
        printer.initClientInfo(args);

        MyClient myClient = new MyClient(
                args[0], numOfThreads, numOfIterations,dayNum,population);
        myClient.roll();
        //myClient.close();


    }
}
