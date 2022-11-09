package dev.isnow.qubolinker.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class IPRange {
    private InetAddress start;
    private InetAddress end;

    public InetAddress getStart() {
        return start;
    }

    public void setStart(InetAddress start) {
        this.start = start;
    }

    public InetAddress getEnd() {
        return end;
    }

    public void setEnd(InetAddress end) {
        this.end = end;
    }

    public static List<IPRange> split(InetAddress ipStart, InetAddress ipEnd, int rangeCount) {
        List<IPRange> result = new ArrayList<>();
        int start = ByteBuffer.wrap(ipStart.getAddress()).getInt();
        int end = ByteBuffer.wrap(ipEnd.getAddress()).getInt();

        int rangeSize = (end - start + 1) / rangeCount;
        int remains = (end - start + 1) % rangeCount;

        while (start < end) {
            int rangeEnd = Math.min(start + rangeSize - 1, end);
            if (remains > 0) {
                rangeEnd++;
                remains--;
            }

            IPRange ipRange = new IPRange();
            try {
                ipRange.setStart(InetAddress.getByAddress(ByteBuffer.allocate(4).putInt(start).array()));
                ipRange.setEnd(InetAddress.getByAddress(ByteBuffer.allocate(4).putInt(rangeEnd).array()));
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
            result.add(ipRange);
            start = rangeEnd + 1;
        }
        return result;
    }

    public static String formatted(IPRange ipRange) {
        return ipRange.getStart() + "-" + ipRange.getEnd();
    }
}