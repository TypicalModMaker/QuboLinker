package dev.isnow.qubolinker.process;

import dev.isnow.qubolinker.QuboClient;
import lombok.Getter;

import java.io.*;
import java.util.Objects;

public class ProcessOutput
        implements Runnable {

    private static Process process;

    private OutputStream outputStream;

    @Getter
    private boolean finishing;
    public ProcessOutput(String ipStart, String ipEnd, String portrange, String threads, String timeout) {
        startQubo(ipStart, ipEnd, portrange, threads, timeout);
    }

    @Override
    public void run() {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (process.isAlive()) {
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
                else if(line.contains("Scan time:")) {
                    finishing = true;
                    if(!QuboClient.getInstance().getQueuedHits().isEmpty() && QuboClient.getInstance().getQueuedHits().element() == null) {
                        QuboClient.getInstance().getServerThread().sendHit(Objects.requireNonNull(QuboClient.getInstance().getQueuedHits().element()));
                    } else {
                        System.exit(0);
                    }
                }
            } catch (Exception ignored) {}
        }
    }

    private void startQubo(String ipStart, String ipEnd, String portrange, String threads, String timeout) {
        try {
            final ProcessBuilder ps = new ProcessBuilder("bash", "-c", "java -Dfile.encoding=UTF-8 -jar qubo.jar -c 1 -noping -range " + ipStart + "-" + ipEnd + " -ti " + timeout + " -th " + threads + " -ports " + portrange);
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
