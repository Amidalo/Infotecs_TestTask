package org.example;

import java.net.Inet4Address;

public class IPVerification {
    public boolean isValidIP(String ip) {
        try {
            Inet4Address addr = (Inet4Address) Inet4Address.getByName(ip);
            return addr.getHostAddress().equals(ip);
        } catch (Exception e) {
            return false;
        }
    }
}
