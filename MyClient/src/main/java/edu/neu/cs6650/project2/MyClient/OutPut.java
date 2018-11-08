package edu.neu.cs6650.project2.MyClient;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Timer task to handle the output csv file
 */
public class OutPut {

    int countSeconds;
    MyPrinter printer;
    int previousCount;
    FileWriter fw;
    StringBuilder sb;

    Timer timer;

    TimerTask timerTask;

    public OutPut(MyPrinter printer){
        this.printer = printer;
        this.previousCount = 0;
        sb = new StringBuilder();
        countSeconds = 1;
    }


    void outputBySecond() {
        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                String line = countSeconds + ", " + (printer.numRequest.intValue()-previousCount) + "\n";
                sb.append(line);
                countSeconds++;
                previousCount = printer.numRequest.intValue();
            }
        };
        timer.scheduleAtFixedRate(timerTask,0,1000);
    }
    void close(){
        timer.cancel();
        timerTask.cancel();
    }

    void roll() throws IOException{
        FileWriter fw = new FileWriter("test.csv");
        fw.write("Seconds, Throughput\n");
        fw.write(sb.toString());
        fw.close();
    }
}
