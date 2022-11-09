package dev.isnow.qubolinker;

import dev.isnow.qubolinker.server.ServerListener;

public class QuboStartServer {


    public QuboStartServer(String vpsName) {
        new ServerListener(1337, vpsName);
    }
}
