package org.example;

import java.net.Inet4Address;
import java.net.UnknownHostException;

public class IPVerification {
    public boolean isValidIPv4(String ip) {
        if (ip == null || ip.isEmpty()) {
            return false;
        }

        try {
            Inet4Address addr = (Inet4Address) Inet4Address.getByName(ip);
            return addr.getHostAddress().equals(ip);
        } catch (UnknownHostException e) {
            return false;
        }
    }
}
