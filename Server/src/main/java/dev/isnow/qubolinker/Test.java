package dev.isnow.qubolinker;

import dev.isnow.qubolinker.util.NewIPRangeSplitter;

import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String[] args) {
        String[] ranges = {"158.69.0.0-158.69.255.255"};

        List<String> splitted = NewIPRangeSplitter.splitIpRanges(Arrays.asList(ranges), 25);

        List<List<String>> perClient = NewIPRangeSplitter.splitIpRangesToClients(splitted, 25);

        int i = 0;
        for(List<String> clientList : perClient) {
            i++;
            System.out.println("Client: " + i);
            System.out.println(String.join(",", clientList));
        }
    }
}
