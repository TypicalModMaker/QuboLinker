package dev.isnow.qubolinker;


import dev.isnow.qubolinker.process.ProcessOutput;
import dev.isnow.qubolinker.server.ServerThread;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
public class QuboClient {
    private static QuboClient instance;

    private String clientName = "VPS-UNDEFINED";
    @Setter
    private ServerThread serverThread;

    @Setter
    private ProcessOutput processOutput;

    @Setter
    private boolean reconnect = true;

    @Setter
    private boolean alreadyRecievedScanReqBefore;

    private final Queue<String> queuedHits = new ConcurrentLinkedQueue<>();
    public QuboClient(String[] args) {
        instance = this;
        if(args.length == 0) {
            System.out.println("What should be this vps be called? [must be unique]");
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(System.in));

            try {
                clientName = reader.readLine();
            } catch (IOException e) {
                System.out.println("Invalid Name");
                System.exit(0);
            }

            System.out.println("What's the master server ip?");

            String masterIPInput = "1.1.1.1";
            try {
                masterIPInput = reader.readLine();
            } catch (IOException e) {
                System.out.println("Invalid IP");
                System.exit(0);
            }
            serverThread = new ServerThread(masterIPInput, 1337);
        } else {
            clientName = args[1];
            serverThread = new ServerThread(args[0], 1337);
        }

    }

    public static QuboClient getInstance() {
        return instance;
    }
}