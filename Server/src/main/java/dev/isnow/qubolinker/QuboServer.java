package dev.isnow.qubolinker;

import com.github.simplenet.packet.Packet;
import dev.isnow.qubolinker.client.ClientListener;
import dev.isnow.qubolinker.client.impl.QuboClient;
import dev.isnow.qubolinker.util.CIDRUtils;
import dev.isnow.qubolinker.util.Color;
import dev.isnow.qubolinker.util.FileUtil;
import dev.isnow.qubolinker.util.IPRange;
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
import java.util.List;

@Getter
public class QuboServer {
    private final ClientListener clientListener;
    private final File scanningFile;
    private static QuboServer instance;

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
        System.out.println("Spooky, if you leak this ill chop your nuts off.");
        System.out.println("Creating outputs directory.");
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        File outputFolder = new File(s, "outputs/");
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
            System.out.println("What IP range would you like to scan?");

            String ipRangeInput = "1.1.1.1";
            try {
                ipRangeInput = reader.readLine();
                ipRangeInput = ipRangeInput.replaceAll(",", ".");
            } catch (IOException e) {
                System.out.println("Invalid IpRange");
                System.exit(0);
            }

            if (ipRangeInput.contains(" ")) {
                System.out.println("IPRange cant contain any spaces!");
                System.exit(0);
            }
            if (ipRangeInput.contains("/")) {
                try {
                    CIDRUtils converted = new CIDRUtils(ipRangeInput);
                    ipRangeInput = converted.getNetworkAddress() + "-" + converted.getBroadcastAddress();
                } catch (UnknownHostException exception) {
                    System.out.println("INVALID CIDR");
                    System.exit(0);
                }
            }
            String start = "158.69.0.0", end = "158.69.255.255";
            try {
                start = ipRangeInput.split("-")[0];
                end = ipRangeInput.split("-")[1];
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Invalid IpRange");
                System.exit(0);
            }
            scanningFile = new File(outputFolder, "qubolinker-" + start + "-" + end + ".txt");
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
            System.out.println("What timeout would you like to use? [PER CLIENT]");
            String timeout = "1000";

            try {
                timeout = reader.readLine();
            } catch (IOException e) {
                System.out.println("Invalid timeout amount");
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


            if (ips != null) {
                System.out.println("[DEBUG] Connecting to the vpses...");
                for (InetAddress ip : ips) {
                    Thread clientThread = new Thread(() -> {
                        com.github.simplenet.Client client = new com.github.simplenet.Client();
                        client.onConnect(() -> {
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException ignored) {
                            }
                            Packet.builder().putString("QUBOLINKER-AUTHSTRING-01").queueAndFlush(client);

                            client.postDisconnect(() -> Thread.currentThread().interrupt());
                        });

                        client.connect(ip.getHostAddress(), 1337);
                    });
                    clientThread.start();
                }
            }
            System.out.println("Starting ClientListener...");
            clientListener = new ClientListener(1337);
            System.out.println("Waiting for clients to connect... Type YES when you are ready to scan");

            ready = "NO";
            try {
                ready = reader.readLine();
            } catch (IOException e) {
                System.exit(0);
            }

            if (!ready.equalsIgnoreCase("YES")) {
                System.out.println("Imagine not being ready");
                System.exit(0);
            } else {
                int clients = getClientListener().getClients().size();
                System.out.println("Splitting the IP Range to " + getClientListener().getClients().size());

                try {
                    List<IPRange> ipRangeList = IPRange.split(InetAddress.getByName(start), InetAddress.getByName(end), clients);
                    if (clients == 0) {
                        System.out.println("No clients connected!");
                        System.exit(0);
                    }
                    for (QuboClient quboClient : getClientListener().getClients().values()) {
                        IPRange ipRange = ipRangeList.get(0);
                        String formattedIpRange = IPRange.formatted(ipRange);
                        quboClient.setCurrentScanningIpRange(formattedIpRange);
                        System.out.println("Broadcasting " + formattedIpRange + " to " + quboClient.getName());
                        clientListener.sendIPRange(quboClient.getClient(), ipRange.getStart().getHostAddress(), ipRange.getEnd().getHostAddress(), portrange, threads, timeout);
                        ipRangeList.remove(0);
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
                } catch (UnknownHostException e) {
                    System.out.println("Failed to split the IP Range.");
                    System.exit(0);
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static QuboServer getInstance() {
        return instance;
    }
}
