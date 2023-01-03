package dev.isnow.qubolinker.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

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
                System.out.println(line); // Does not work, I have no idea why, if you know how to fix it please make a pull request
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void startClient(String vpsIP, String vpsName) {
        Thread t2 = new Thread(() -> {
            ProcessBuilder ps = new ProcessBuilder("bash", "-c", "java -jar Client.jar " + vpsIP + " " + vpsName);
            ps.redirectErrorStream(true);
            try {
                process = ps.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        t2.start();
    }
}
