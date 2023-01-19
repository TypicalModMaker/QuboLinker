package dev.isnow.qubolinker;

import com.github.simplenet.packet.Packet;
import dev.isnow.qubolinker.client.ClientListener;
import dev.isnow.qubolinker.client.impl.QuboClient;
import dev.isnow.qubolinker.util.*;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
public class QuboServer {
    private final ClientListener clientListener;
    private final File scanningFile;
    private static QuboServer instance;
    private final File outputFolder;

    private long startTime;
    public QuboServer() {
        instance = this;
        System.out.println("  /$$$$$$  /$$   /$$ /$$$$$$$   /$$$$$$        /$$       /$$$$$$ /$$   /$$ /$$   /$$ /$$$$$$$$ /$$$$$$$ ");
        System.out.println(" /$$__  $$| $$  | $$| $$__  $$ /$$__  $$      | $$      |_  $$_/| $$$ | $$| $$  /$$/| $$_____/| $$__  $$");
        System.out.println("| $$  \\ $$| $$  | $$| $$  \\ $$| $$  \\ $$      | $$        | $$  | $$$$| $$| $$ /$$/ | $$      | $$  \\ $$");
        System.out.println("| $$  | $$| $$  | $$| $$$$$$$ | $$  | $$      | $$        | $$  | $$ $$ $$| $$$$$/  | $$$$$   | $$$$$$$/");
        System.out.println("| $$  | $$| $$  | $$| $$__  $$| $$  | $$      | $$        | $$  | $$  $$$$| $$  $$  | $$__/   | $$__  $$");
        System.out.println("| $$/$$ $$| $$  | $$| $$  \\ $$| $$  | $$      | $$        | $$  | $$\\  $$$| $$\\  $$ | $$      | $$  \\ $$");
        System.out.println("|  $$$$$$/|  $$$$$$/| $$$$$$$/|  $$$$$$/      | $$$$$$$$ /$$$$$$| $$ \\  $$| $$ \\  $$| $$$$$$$$| $$  | $$");
        System.out.println(" \\____ $$$ \\______/ |_______/  \\______/       |________/|______/|__/  \\__/|__/  \\__/|________/|__/  |__/");
        System.out.println("      \\__/                                                                                              ");
        System.out.println("A Private project made with " + Color.ANSI_RED + "❤" + Color.ANSI_RESET + " by Isnow");
        System.out.println("Creating outputs directory.");

        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        outputFolder = new File(s, "outputs/");

        if(!outputFolder.exists()) {
            boolean done = outputFolder.mkdir();
            if(done) {
                System.out.println("Finished creating output directory.");
            }
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));

        System.out.println("Is there a scan in-progress?");
        String inProgress = "NO";
        try {
            inProgress = reader.readLine();
        } catch (IOException e) {
            System.out.println("INVALID RESPONSE");
            System.exit(0);
        }

        if(inProgress.equalsIgnoreCase("yes")) {
            scanningFile = new File(outputFolder, "qubolinker-recovered.txt");
            System.out.println("Starting ClientListener...");
            clientListener = new ClientListener(1340);
            startTime = System.currentTimeMillis();
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    if (line.equals("status")) {
                        System.out.println("[Info] Requesting status...");
                        clientListener.getClients().values().forEach(quboClient -> Packet.builder().putByte(2).putString("QUBOLINKER-1337").queueAndFlush(quboClient.getClient()));
                    } else if (line.equals("stop")) {
                        System.out.println("[Info] Stopping all scans...");
                        clientListener.getClients().values().forEach(quboClient -> Packet.builder().putByte(3).putString("QUBOLINKER-1337").queueAndFlush(quboClient.getClient()));
                    }
                }
            } catch (IOException ignored) {}
        } else {
            System.out.println("What IP range(s) would you like to scan?");

            StringBuilder ipRangeInput = new StringBuilder("1.1.1.1");
            try {
                ipRangeInput = new StringBuilder(reader.readLine());
            } catch (IOException e) {
                System.out.println("Invalid IpRange");
                System.exit(0);
            }

            if (ipRangeInput.toString().contains(" ")) {
                System.out.println("IPRange cant contain any spaces!");
                System.exit(0);
            }
            if (ipRangeInput.toString().contains("/")) {
                if (ipRangeInput.toString().contains(",")) {
                    List<String> split = new ArrayList<>(Arrays.asList(ipRangeInput.toString().split(",")));
                    ipRangeInput = new StringBuilder();
                    for(String cidr : split) {
                        try {
                            CIDRUtils converted = new CIDRUtils(cidr);
                            String convertedSting = converted.getNetworkAddress() + "-" + converted.getBroadcastAddress();
                            ipRangeInput.append(convertedSting).append(",");
                        } catch (UnknownHostException e) {
                            System.out.println("INVALID CIDR");
                            System.exit(0);
                        }
                    }
                } else {
                    try {
                        CIDRUtils converted = new CIDRUtils(ipRangeInput.toString());
                        ipRangeInput = new StringBuilder(converted.getNetworkAddress() + "-" + converted.getBroadcastAddress());
                    } catch (UnknownHostException exception) {
                        System.out.println("INVALID CIDR");
                        System.exit(0);
                    }
                }
            }
            ipRangeInput.setLength(ipRangeInput.length() - 1);

            ArrayList<String> scanips = new ArrayList<>();
            if (!ipRangeInput.toString().contains(",")) {
                scanips.add(ipRangeInput.toString());
            } else {
                scanips.addAll(Arrays.asList(ipRangeInput.toString().split(",")));
            }

            scanningFile = new File(outputFolder, "qubolinker-" + scanips.get(0) + "-" + scanips.get(scanips.size() - 1) + ".txt");
            try {
                scanningFile.delete();
                scanningFile.createNewFile();
            } catch (IOException e) {
                System.out.println("Couldn't create new scanning file.");
                System.exit(0);
            }
            FileUtil.writeSplash(scanningFile);

            System.out.println("What port range would you like to use?");
            String portrange = "1-65535";

            try {
                portrange = reader.readLine();
            } catch (IOException e) {
                System.out.println("Invalid port range");
                System.exit(0);
            }
            if (portrange.equals("")) {
                System.out.println("PortRange cant be empty");
                System.exit(0);
            }
            if (portrange.contains(" ")) {
                System.out.println("PortRange cant contain any spaces!");
                System.exit(0);
            }

            System.out.println("How many threads would you like to use? [PER CLIENT]");
            String threads = "10250";

            try {
                threads = reader.readLine();
            } catch (IOException e) {
                System.out.println("Invalid threads amount");
                System.exit(0);
            }
            if (threads.equals("")) {
                System.out.println("Threads amount cannot be empty!");
                System.exit(0);
            }
            System.out.println("What timeout would you like to use? [PER CLIENT]");
            String timeout = "1000";

            try {
                timeout = reader.readLine();
            } catch (IOException e) {
                System.out.println("Invalid timeout amount");
                System.exit(0);
            }
            if (timeout.equals("")) {
                System.out.println("Timeout amount cannot be empty!");
                System.exit(0);
            }
            System.out.println("Do you wish to autoConnect VPS'es?");

            String ready = "NO";
            try {
                ready = reader.readLine();
            } catch (IOException e) {
                System.exit(0);
            }
            ArrayList<InetAddress> ips = null;
            if (ready.equalsIgnoreCase("yes")) {
                System.out.println("Reading vpses.txt...");
                ips = FileUtil.getVPSList();
            }

            System.out.println("Starting ClientListener...");
            clientListener = new ClientListener(1337);
            System.out.println("Waiting for clients to connect...");

            if (ips != null) {
                System.out.println("[DEBUG] Connecting to the vpses...");
                int count = 1;
                String masterIP = IPUtils.getIP();
                for (InetAddress ip : ips) {
                    int finalCount = count;
                    ProcessBuilder ps = new ProcessBuilder("ssh", "-o", "StrictHostKeyChecking=no", "root@" + ip.getHostAddress(), "-i", "/root/.ssh/id_rsa", "-t", "screen", "-d", "-m", "java", "-jar", "Client.jar", masterIP, "VPS" + String.valueOf(finalCount));
                    try {
                        ps.start();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
//                    Thread clientThread = new Thread(() -> {
////                        com.github.simplenet.Client client = new com.github.simplenet.Client();
////                        client.onConnect(() -> {
////                            try {
////                                Thread.sleep(10);
////                            } catch (InterruptedException ignored) {}
////                            Packet.builder().putString("QUBOLINKER-AUTHSTRING-01").queueAndFlush(client);
////
////                            client.postDisconnect(() -> Thread.currentThread().interrupt());
////                        });
////
////                        client.connect(ip.getHostAddress(), 1337);
//                        ProcessBuilder ps = new ProcessBuilder("ssh", "-o", "StrictHostKeyChecking=no", "root@" + ip.getHostAddress(), "-i", "/root/.ssh/id_rsa", "-t", "screen", "-d", "-m", "java", "-jar", "Client.jar", masterIP, "VPS" + String.valueOf(finalCount));
//                        try {
//                            ps.start();
//                        } catch (IOException e) {
//                            throw new RuntimeException(e);
//                        }
//                    });
//                    clientThread.start();
                    count++;
                }

            }

            String finalPortrange = portrange;
            String finalThreads = threads;
            String finalTimeout = timeout;

            Thread scanThread = new Thread(() -> {
                try {
                    Thread.sleep(400);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                int clients = getClientListener().getClients().size();
                System.out.println("Splitting the IP Range to " + getClientListener().getClients().size());
                try {
                    if (clients == 0) {
                        System.out.println("No clients connected!");
                        System.exit(0);
                    }
                    List<String> splitted = NewIPRangeSplitter.splitIpRanges(scanips, clients);
                    List<List<String>> perClient = NewIPRangeSplitter.splitIpRangesToClients(splitted, clients);

                    int index = 0;
                    for (String key : clientListener.getClients().keySet()) {
                        QuboClient client = clientListener.getClients().get(key);
                        List<String> list = perClient.get(index);
                        client.setCurrentScanningIpRanges(list);
                        System.out.println("Broadcasting " + String.join(",", list) + " to " + client.getName());
                        clientListener.sendIPRanges(client.getClient(), list, finalPortrange, finalThreads, finalTimeout);
                        ++index;
                    }

                    startTime = System.currentTimeMillis();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.equals("status")) {
                            System.out.println("[Info] Requesting status...");
                            clientListener.getClients().values().forEach(quboClient -> Packet.builder().putByte(2).putString("QUBOLINKER-1337").queueAndFlush(quboClient.getClient()));
                        } else if (line.equals("stop")) {
                            System.out.println("[Info] Stopping all scans...");
                            clientListener.getClients().values().forEach(quboClient -> Packet.builder().putByte(3).putString("QUBOLINKER-1337").queueAndFlush(quboClient.getClient()));
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Failed to split the IP Range.");
                    System.exit(0);
                }
            });
            scanThread.start();

        }
    }

    public static QuboServer getInstance() {
        return instance;
    }
}
