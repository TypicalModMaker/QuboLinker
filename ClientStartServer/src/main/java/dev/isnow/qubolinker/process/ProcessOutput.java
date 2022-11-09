package dev.isnow.qubolinker.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessOutput
        implements Runnable {

    private static Process process;

    public ProcessOutput(String vpsIP, String vpsName) {
        startClient(vpsIP, vpsName);
    }

    @Override
    public void run() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        while (process.isAlive()) {
            try {
                String line = bufferedReader.readLine();
                if (line == null) continue;
                System.out.println(line);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void startClient(String vpsIP, String vpsName) {
        try {
            ProcessBuilder ps = new ProcessBuilder("bash", "-c", "java -jar Client-1.0-SNAPSHOT.jar " + vpsIP + " " + vpsName);
            ps.redirectErrorStream(true);
            process = ps.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
