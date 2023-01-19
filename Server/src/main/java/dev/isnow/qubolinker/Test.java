package dev.isnow.qubolinker;

import dev.isnow.qubolinker.util.CIDRUtils;
import dev.isnow.qubolinker.util.NewIPRangeSplitter;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {
//        String asn = "185.73.243.0/24";
//        try {
//            String range = new CIDRUtils(asn).getBroadcastAddress() + " 2. " + new CIDRUtils(asn).getNetworkAddress();
//            System.out.println(range);
//            String[] ranges = {"185.73.243.0-185.73.244.255"};
//
//            List<String> splitted = NewIPRangeSplitter.splitIpRanges(Arrays.asList(ranges), 28);
//
//            List<List<String>> perClient = NewIPRangeSplitter.splitIpRangesToClients(splitted, 28);
//
//            int i = 0;
//            for(List<String> clientList : perClient) {
//                i++;
//                System.out.println("Client: " + i);
//                System.out.println(String.join(",", clientList));
//            }
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        }
        System.out.println("What IP range(s) would you like to scan?");
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
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
        boolean singleShit = false;
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
                    singleShit = true;
                } catch (UnknownHostException exception) {
                    System.out.println("INVALID CIDR");
                    System.exit(0);
                }
            }
        }

        if(!singleShit) {
            ipRangeInput.append(ipRangeInput.length() - 1);
        }
        System.out.println("[DEBUG] Converted to " + ipRangeInput);
        ArrayList<String> scanips = new ArrayList<>();
        if (!ipRangeInput.toString().contains(",")) {
            scanips.add(ipRangeInput.toString());
        } else {
            scanips.addAll(Arrays.asList(ipRangeInput.toString().split(",")));
        }

        System.out.println("qubolinker-" + scanips.get(0).split("-")[0] + "-" + scanips.get(scanips.size() - 1).split("-")[1]);
        System.out.println("qubolinker-" + scanips.get(0).split("-")[0] + "-" + scanips.get(scanips.size() - 2).split("-")[1]);
    }
}
