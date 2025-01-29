package org.example;

import com.jcraft.jsch.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Client {
    private Session session;
    private ChannelSftp sftpChannel;

    public void connect(String address, int port, String login, String password) throws JSchException {
        JSch jSch = new JSch();
        session = jSch.getSession(login, address, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        if (!session.isConnected()) {
            throw new JSchException("Не удалось подключиться к серверу.");
        }
        sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();

        if (!sftpChannel.isConnected()) {
            throw new JSchException("Не удалось открыть SFTP канал.");
        }
    }

    public void disconnect() {
        if (sftpChannel != null && sftpChannel.isConnected()) {
            sftpChannel.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    public String readFile(String remoteFilePath) throws SftpException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            sftpChannel.get(remoteFilePath, outputStream);
            return outputStream.toString();
        } catch (IOException e) {
            throw new SftpException(1, "Произошла ошибка при чтении файла: " + e.getMessage());
        }
    }

    public void writeFile(String remoteFilePath, String content) throws SftpException {
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
            sftpChannel.put(inputStream, remoteFilePath);
        } catch (IOException e) {
            throw new SftpException(1, "Произошла ошибка при чтении файла: " + e.getMessage());
        }
    }
}
