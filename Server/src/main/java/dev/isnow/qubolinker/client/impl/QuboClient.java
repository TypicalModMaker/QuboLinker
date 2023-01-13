package dev.isnow.qubolinker.client.impl;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.SocketAddress;
import java.util.List;

@Data
@RequiredArgsConstructor
public class QuboClient {

    private final com.github.simplenet.Client client;
    private final SocketAddress ip;
    private final String name;
    private List<String> currentScanningIpRanges;

    private float lastKeepaliveTime = System.currentTimeMillis();

}
