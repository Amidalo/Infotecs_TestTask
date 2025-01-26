package org.example;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class Client {
    private Session session;

    public void connect(String address, int port, String login, String password) throws JSchException {
        JSch jSch = new JSch();
        Session session = jSch.getSession(login, address, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}
