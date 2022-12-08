package dev.isnow.qubolinker.server;

import com.github.simplenet.Client;
import com.github.simplenet.packet.Packet;
import dev.isnow.qubolinker.QuboClient;
import dev.isnow.qubolinker.process.ProcessOutput;
import lombok.Getter;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter
public class ServerThread {
    private Client client;

    private final String masterIP;

    public ServerThread(String ip, int port) {
        masterIP = ip;
        Thread t = new Thread(() -> {
            client = new Client();
            client.onConnect(() -> {
              System.out.println("[INFO] Connected to the server!");
              if(!QuboClient.getInstance().getQueuedHits().isEmpty()) {
                  sendHit(Objects.requireNonNull(QuboClient.getInstance().getQueuedHits().element()));
              }
              Packet.builder().putByte(0).putString(QuboClient.getInstance().getClientName()).queueAndFlush(client);
              client.readByteAlways(opcode -> {
                  switch (opcode) {
                      case 1:
                          client.readString(message -> {
                              if (message == null) {
                                  return;
                              }
                              String[] split = message.split("\\|");
                              String ipStart = split[0];
                              String ipEnd = split[1];
                              String threads = split[2];
                              String timeout = split[3];
                              String portrange = split[4];
                              if (QuboClient.getInstance().getProcessOutput() != null) {
                                  QuboClient.getInstance().getProcessOutput().stopProcess();
                              }
                              // Prevent infinite client instance creation when stopped when using auto connect + turning off main server before saying "YES"
                              if(QuboClient.getInstance().isAlreadyRecievedScanReqBefore()) {
                                  QuboClient.getInstance().setReconnect(false);
                                  client.close();
                                  System.exit(0);
                                  return;
                              }

                              System.out.println("[INFO] Received a scan request, " + ipStart + "-" + ipEnd + " Ports: " + portrange + " Threads: " + threads + " Timeout: " + timeout);
                              QuboClient.getInstance().setProcessOutput(new ProcessOutput(ipStart, ipEnd, portrange, threads, timeout));
                              Thread t2 = new Thread(() -> QuboClient.getInstance().getProcessOutput().run());
                              t2.start();
                              QuboClient.getInstance().setAlreadyRecievedScanReqBefore(true);
                          });
                          break;
                      case 2:
                          client.readString(message -> {
                              if (message == null) {
                                  return;
                              }
                              if (!message.equals("QUBOLINKER-1337")) {
                                  return;
                              }
                              if (QuboClient.getInstance().getProcessOutput() != null) {
                                  QuboClient.getInstance().getProcessOutput().sendStatus();
                              }
                          });
                          break;
                      case 3:
                          client.readString(message -> {
                              if (message == null) {
                                  return;
                              }
                              if(!message.equals("QUBOLINKER-1337")) {
                                  return;
                              }
                              System.out.println("[INFO] Received a STOP request.");
                              if (QuboClient.getInstance().getProcessOutput() != null) {
                                  QuboClient.getInstance().getProcessOutput().stopProcess();
                              }
                              QuboClient.getInstance().setReconnect(false);
                              client.close();
                              System.exit(0);
                          });
                          break;
                  }
                });


                client.postDisconnect(() -> {
                    System.out.println("[INFO] Lost Connection to the server...");
                    if (QuboClient.getInstance().isReconnect())  {
                        QuboClient.getInstance().setServerThread(new ServerThread(ip ,port));
                        client.close();
                    }
                });
            });

            client.connect(ip, port, 5, TimeUnit.SECONDS, () -> {
                if (QuboClient.getInstance().isReconnect())   {
                    QuboClient.getInstance().setServerThread(new ServerThread(ip ,port));
                    client.close();
                }
            });
        });
        t.start();
    }

    public void sendStatus(final String line) {
        Packet.builder().putByte(1).putString(line).queueAndFlush(client);
    }

    public void sendHit(final String line) {
        if(line.contains("Ochrona DDoS:") || line.contains("Craftserve.pl - wydajny hosting Minecraft!Testuj za darmo przez 24h!") || line.contains("Serwer jest wylaczony") || line.contains("start.falix.cc") || line.contains("start.Falix.cc") || line.contains("Powered by FalixNodes.net") || line.contains("Ochrona DDoS") || line.contains("Blad pobierania statusu. Polacz sie bezposrednio") || line.contains("Please refer to our documentation at docs.tcpshield.com")) {
            return;
        }
        Thread t = new Thread(() -> {
            HttpClient httpclient = HttpClients.createDefault();
            try {
                URI address = new URI("http", null, masterIP, 1338, "/hit", null, null);
                HttpPost httppost = new HttpPost(address);
                httppost.setHeader("key", "QUBOLINKER-1337");
                httppost.setHeader("hit", line);
                httpclient.execute(httppost);
                QuboClient.getInstance().getQueuedHits().remove();
                if(QuboClient.getInstance().getQueuedHits().size() > 0 && QuboClient.getInstance().getQueuedHits().element() == null) {
                    sendHit(QuboClient.getInstance().getQueuedHits().element() );
                } else if(QuboClient.getInstance().getProcessOutput().isFinishing()) {
                    System.exit(0);
                }
            } catch (URISyntaxException | IOException e) {
                System.out.println("[INFO] Failed to send hit to the Master Server! [1]");
                if(!QuboClient.getInstance().getQueuedHits().contains(line)) {
                    QuboClient.getInstance().getQueuedHits().add(line);
                }
            } catch (NoSuchElementException ignored) {}
        });
        t.start();
    }
}
