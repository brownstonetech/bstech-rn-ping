package ca.bstech.ping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;


/**
 * Permission required: <br/>
 * <uses-permission android:name="android.permission.INTERNET"/>
 *
 */
public class PingUtil {

    private static Process process = null;

    private static Object processSync = new Object();

    private static boolean breakSignal = false;

    public static void stop() {
        synchronized(processSync) {
            if (process == null) return;
            breakSignal = true;
        }
        // waiting for grace stop
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < 5000) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // do nothing
            }
            if (process == null) return;
        }
        synchronized(processSync) {
            if (process != null) {
                // force stop
                process.destroy();
                process = null;
            }
        }
    }

    public static void ping(String domain, Map<String,String> options, Integer durationSeconds, int reportIntervalSeconds, Consumer<String> resultHandler) {
        long startTime = System.currentTimeMillis();
        Long endTime = durationSeconds == null? null: startTime + durationSeconds * 1000;
        // Loop until duration approach
        while (true) {
            // stop every report interval to retrieve statistic and summary data
            int runningDuration = reportIntervalSeconds;
            if ( endTime != null ) {
                runningDuration = (int) ((endTime - System.currentTimeMillis()) / 1000) + 1;
                if ( runningDuration > reportIntervalSeconds ) {
                    runningDuration = reportIntervalSeconds;
                }
            }
            String command = createSimplePingCommand(domain, options, runningDuration);
            ping(command, resultHandler);
            long currentTime = System.currentTimeMillis();
            if ( breakSignal || endTime != null && (currentTime >= endTime)) {
                breakSignal = false;
                break;
            }
        }
    }

    private static void ping(String command, Consumer<String> resultHandler) {
        synchronized(processSync) {
            if ( process != null ) {
                try {
                    process.destroy();
                    process = null;
                } catch (Exception e ){
                    throw new RuntimeException("Terminating running process before start failed", e);
                }
            }
        }
        try {
            synchronized(processSync) {
                if (breakSignal) return;
                process = Runtime.getRuntime().exec(command);
            }
            InputStream is = process.getInputStream();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                try {
                    String line;
                    while (null != (line = reader.readLine())) {
                        resultHandler.accept(line);
                        if (breakSignal) break;
                    }
                } finally {
                    reader.close();
                }
            } finally {
                is.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Unexpected exception while running ping command", e);
        } finally {
            synchronized(processSync) {
                if (null != process) {
                    process.destroy();
                    process = null;
                }
            }
        }
    }

    private static String createSimplePingCommand(String domain, Map<String,String> options, int durationSeconds) {
        StringBuilder sb = new StringBuilder("/system/bin/ping");
        if ( options!= null ) {
            for (Map.Entry<String, String> entry : options.entrySet()) {
                sb.append(" ").append(entry.getKey()).append(" ").append(entry.getValue());
            }
        }
        sb.append(" -w ").append(durationSeconds);
        sb.append(" ").append(domain);
        return sb.toString();
    }

}