package org.example;

import com.jcraft.jsch.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Client {
    private Session session;
    private ChannelSftp sftpChannel;

    public Session getSession() {
        return this.session;
    }

    public ChannelSftp getSftpChannel() {
        return this.sftpChannel;
    }

    public void connect(String address, int port, String login, String password) throws JSchException {
        JSch jSch = new JSch();
        this.session = jSch.getSession(login, address, port);
        this.session.setPassword(password);
        this.session.setConfig("StrictHostKeyChecking", "no");
        this.session.connect();

        if (!this.session.isConnected()) {
            throw new JSchException("Не удалось подключиться к серверу.");
        }
        this.sftpChannel = (ChannelSftp) this.session.openChannel("sftp");
        this.sftpChannel.connect();

        if (!this.sftpChannel.isConnected()) {
            throw new JSchException("Не удалось открыть SFTP канал.");
        }
    }

    public void disconnect() {
        if (this.sftpChannel != null && this.sftpChannel.isConnected()) {
            this.sftpChannel.disconnect();
        }
        if (this.session != null && this.session.isConnected()) {
            this.session.disconnect();
        }
    }

    public String readFile(String remoteFilePath) throws SftpException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            this.sftpChannel.get(remoteFilePath, outputStream);
            return outputStream.toString();
        } catch (IOException e) {
            throw new SftpException(1, "Произошла ошибка при чтении файла: " + e.getMessage());
        }
    }

    public void writeFile(String remoteFilePath, String content) throws SftpException {
        try (InputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
            this.sftpChannel.put(inputStream, remoteFilePath);
        } catch (IOException e) {
            throw new SftpException(1, "Произошла ошибка при чтении файла: " + e.getMessage());
        }
    }
}
