package dev.isnow.qubolinker.server;

import com.github.simplenet.Server;

import java.io.IOException;
import java.net.SocketAddress;

public class ServerListener {
    private Server server;

    public ServerListener(int port, String vpsName) {
        Thread t = new Thread(() -> {
            server = new Server();
            server.onConnect(client -> {
                SocketAddress address = null;
                try {
                    address = client.getChannel().getRemoteAddress();
                } catch (IOException e) {
                    System.out.println("Failed to receive socketadddress from current client!");
                }
                System.out.println("[DEBUG] Client " + address + " has connected!");

                SocketAddress finalAddress1 = address;

                client.readStringAlways(message -> {
                    try {
                        if(message.equals("QUBOLINKER-AUTHSTRING-01")) {
                            System.out.println("[DEBUG] Starting Client.jar");
                            Thread t2 = new Thread(() -> {
                                ProcessBuilder ps = new ProcessBuilder("bash", "-c", "java -jar Client.jar " + finalAddress1.toString().split(":")[0].replace("/", "") + " " + vpsName);
                                try {
                                    ps.start();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            t2.start();
                            client.close();
                        } else {
                            System.out.println("[INFO] Received a weird message from " + finalAddress1 + " message: " + message);
                        }
                    } catch (Exception ignored) {
                        System.out.println("Failed to split the ip!");
                    }

                });

                // Prevent infinite client instance creation when stopped when using auto connect + turning off main server before saying "YES"
                client.postDisconnect(() -> {
                    System.out.println("[DEBUG] " + finalAddress1 + " Disconnected!");
                });
            });
            server.bind("0.0.0.0", port);
        });
        t.start();
    }
}
