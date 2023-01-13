package dev.isnow.qubolinker.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class NewIPRangeSplitter {

    public static List<List<String>> splitIpRangesToClients(List<String> splitRanges, int numClients) {
        List<List<String>> rangesPerClient = new ArrayList<>();
        for (int i = 0; i < numClients; i++) {
            rangesPerClient.add(new ArrayList<String>());
        }
        for (int i = 0; i < splitRanges.size(); i++) {
            rangesPerClient.get(i % numClients).add(splitRanges.get(i));
        }
        return rangesPerClient;
    }

    public static List<String> splitIpRanges(List<String> ranges, int numClients) {
        List<String> splitRanges = new ArrayList<>();
        for (String range : ranges) {
            String[] rangeParts = range.split("-");
            String startIp = rangeParts[0];
            String endIp = rangeParts[1];

            // Convert the IP addresses to long integers for easier calculations
            long start = ipToLong(startIp);
            long end = ipToLong(endIp);

            // Calculate the size of each sub-range
            long rangeSize = end - start + 1;
            long subRangeSize = rangeSize / numClients;

            // Split the range into sub-ranges and add them to the splitRanges list
            for (int i = 0; i < numClients; i++) {
                long subRangeStart = start + i * subRangeSize;
                long subRangeEnd = subRangeStart + subRangeSize - 1;
                // Handle the last sub-range separately in case the range can't be evenly split
                if (i == numClients - 1) {
                    subRangeEnd = end;
                }
                splitRanges.add(longToIp(subRangeStart) + "-" + longToIp(subRangeEnd));
            }
        }
        return splitRanges;
    }

    // Helper method to convert an IP address to a long integer
    private static long ipToLong(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < parts.length; i++) {
            int power = 3 - i;
            result += (Integer.parseInt(parts[i]) % 256 * Math.pow(256, power));
        }
        return result;
    }

    // Helper method to convert a long integer to an IP address
    private static String longToIp(long i) {
        return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
    }

}