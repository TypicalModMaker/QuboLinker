package dev.isnow.qubolinker.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProcessOutput
        implements Runnable {

    private static Process process;

    public boolean isScanning;

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
                if(line.contains("[INFO] Received a scan request")) {
                    isScanning = true;
                }
                System.out.println(line);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isAlive() {
        return process.isAlive();
    }

    public void kill() {
        process.destroy();
    }

    private void startClient(String vpsIP, String vpsName) {
        try {
            ProcessBuilder ps = new ProcessBuilder("bash", "-c", "java -jar Client.jar " + vpsIP + " " + vpsName);
            ps.redirectErrorStream(true);
            process = ps.start();
        }
        catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
