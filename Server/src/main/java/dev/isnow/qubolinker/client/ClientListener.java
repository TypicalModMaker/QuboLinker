package dev.isnow.qubolinker.client;

import com.github.simplenet.Client;
import com.github.simplenet.Server;
import com.github.simplenet.packet.Packet;
import dev.isnow.qubolinker.QuboServer;
import dev.isnow.qubolinker.client.impl.QuboClient;
import dev.isnow.qubolinker.express.ApiController;
import dev.isnow.qubolinker.util.FileUtil;
import lombok.Getter;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ClientListener {

    private final ConcurrentHashMap<String, QuboClient> clients = new ConcurrentHashMap<>();

    private Server server;


    public ClientListener(int port) {
        Thread t = new Thread(() -> {
            new ApiController();
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
                client.postDisconnect(() -> {
                    clients.values().stream().filter(quboClient1 -> quboClient1.getIp() == finalAddress1).findFirst().ifPresent(quboClient1 -> clients.remove(quboClient1.getName()));
                        System.out.println("[INFO] " + finalAddress1 + " disconnected! Clients Left: " + clients.size());
                    if(QuboServer.getInstance().getClientListener().getClients().size() == 0) {
                        System.out.println("[INFO] All clients finished scanning, output saved to " + QuboServer.getInstance().getScanningFile().getAbsolutePath());
                        System.out.println("[INFO] Took " + ((System.currentTimeMillis() - QuboServer.getInstance().getStartTime() ) / 1000.0) + " seconds to complete every scan!");
                        client.close();
                        server.close();
                        System.exit(0);
                    }
                });
                client.readByteAlways(opcode -> {
                    switch (opcode) {
                        case 0:
                            client.readString(message -> {
                                if (QuboServer.getInstance().getClientListener().getClients().get(message) != null) {
                                    System.out.println("[INFO] Client with a name " + message + "already exist in the database.");
                                    return;
                                }
                                QuboServer.getInstance().getClientListener().getClients().put(message, new QuboClient(client, finalAddress1, message));
                                System.out.println("[INFO] Registered a client with a name " + message);
                            });
                            break;
                        case 1:
                            Optional<QuboClient> clientQubo = clients.values().stream().filter(quboClient1 -> quboClient1.getIp() == finalAddress1).findFirst();
                            if(clientQubo.isEmpty()) {
                                break;
                            }
                            client.readString(message -> {
                                if(message == null) {
                                    return;
                                }
                                try {
                                    String ip = message.split("ip: ")[1].split(" -")[0];
                                    String percentage = message.split(" - \\(")[1].split("\\)")[0];
                                    System.out.println("[STATUS] " + clientQubo.get().getName() + " - Current IP: " + ip + " Percentage: " + percentage);
                                } catch (Exception ignored) {};
                            });
                            break;
                        default:
                            System.out.println("[INFO] " + "Received a weird message from a client " + finalAddress1 + ", byte:" + opcode + ", trying to parse string!");
                            try {
                                client.readString(message -> {
                                    System.out.println(message);
                                    FileUtil.writeOutput(message, QuboServer.getInstance().getScanningFile());
                                });
                            } catch (Exception ignored) {}
                            break;
                    }
                });
            });
            server.bind("0.0.0.0", port);
        });
        t.start();
    }


    public void sendIPRange(Client client, String ipStart, String ipEnd, String portrange, String threads, String timeout) {
        Packet.builder().putByte(1).putString(ipStart + "|" + ipEnd + "|" + threads + "|" + timeout + "|" + portrange).queueAndFlush(client);
    }
}
