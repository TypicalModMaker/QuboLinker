package dev.isnow.qubolinker.process;

import dev.isnow.qubolinker.QuboClient;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Queue;

public class ProcessOutput
        implements Runnable {

    @Getter
    public Process process;

    private OutputStream outputStream;

    @Getter
    private boolean finishing;

    private final Queue<String> ranges;
    private final String portrange, threads, timeout;

    public ProcessOutput(String rangesString, String portrange, String threads, String timeout) {
        ranges = new LinkedList<>(Arrays.asList(rangesString.split(",")));
        this.portrange = portrange;
        this.threads = threads;
        this.timeout = timeout;

        String range = ranges.poll();
        if(range != null) {
            startQubo(range);
        }
    }

    public ProcessOutput(Queue<String> rangesQueue, String portrange, String threads, String timeout) {
        ranges = rangesQueue;
        this.portrange = portrange;
        this.threads = threads;
        this.timeout = timeout;

        String range = ranges.poll();
        if(range != null) {
            System.out.println("range is not null, scanning " + range);
            startQubo(range);
        } else {
            System.out.println("range is null? nigga???");
        }
    }

    @Override
    public void run() {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (process != null && process.isAlive()) {
            try {
                String line = bufferedReader.readLine();
                if (line == null) continue;
                System.out.println(line);
                if(line.startsWith("(") && line.contains(":")) {
                    QuboClient.getInstance().getServerThread().sendHit(line);
                }
                else if(line.startsWith("[") && line.endsWith(")") && line.contains("Current ip:")) {
                    QuboClient.getInstance().getServerThread().sendStatus(line);
                }

                if(line.contains("Scan terminated")) {
                    if(ranges.isEmpty()) {
                        finishing = true;
                        if(!QuboClient.getInstance().getQueuedHits().isEmpty() && QuboClient.getInstance().getQueuedHits().element() == null) {
                            QuboClient.getInstance().getServerThread().sendHit(Objects.requireNonNull(QuboClient.getInstance().getQueuedHits().element()));
                        } else {
                            process.destroy();
                            System.exit(0);
                        }
                    }
                }
            } catch (Exception ignored) {}
        }
        if(!finishing) {
            QuboClient.getInstance().setProcessOutput(new ProcessOutput(ranges, portrange, threads, timeout));
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Thread t2 = new Thread(() -> QuboClient.getInstance().getProcessOutput().run());
            t2.start();
        }
    }

    private void startQubo(String range) {
        try {
            final ProcessBuilder ps = new ProcessBuilder("bash", "-c", "java -Dfile.encoding=UTF-8 -jar qubo.jar -c 1 -noping -range " + range + " -ti " + timeout + " -th " + threads + " -ports " + portrange);
            ps.redirectErrorStream(true);
            process = ps.start();
            outputStream = process.getOutputStream();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void sendStatus() {
        System.out.println("[INFO] Received a STATUS request.");
        final PrintStream printStream = new PrintStream(outputStream);
        try {
            printStream.println("status");
        } finally {
            printStream.flush();
        }
    }
    public void stopProcess() {
        process.destroy();
    }
}
