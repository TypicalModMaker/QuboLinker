package dev.isnow.qubolinker.client.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.SocketAddress;

@Data
@RequiredArgsConstructor
public class QuboClient {

    private final com.github.simplenet.Client client;
    private final SocketAddress ip;
    private final String name;
    private String currentScanningIpRange;
}
