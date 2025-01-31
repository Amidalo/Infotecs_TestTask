package org.example;

import com.jcraft.jsch.SftpException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONProcessing {
    private Map<String, String> addresses;
    private Client client;
    private String remoteFilePath;

    public JSONProcessing(Client client, String remoteFilePath) throws IOException, SftpException {
        this.client = client;
        this.addresses = new HashMap<>();
        this.remoteFilePath = remoteFilePath;
        loadAndConvertAddressesFromFile();
    }

    private void loadAndConvertAddressesFromFile() throws IOException, SftpException {
        String content = client.readFile(remoteFilePath);

        if (content != null && !content.isEmpty()) {
            content = content.trim();

            if (content.startsWith("{") && content.contains("\"addresses\":")) {
                int startIndex = content.indexOf("[");
                int endIndex = content.lastIndexOf("]");

                if (startIndex != -1 && endIndex != -1) {
                    String arrayContent = content.substring(startIndex + 1, endIndex).trim();
                    String[] objects = arrayContent.split("\\},\\s*\\{");
                    for (String obj : objects) {
                        obj = obj.replace("{", "").replace("}", "").trim();
                        String[] pairs = obj.split(",");
                        String domain = null;
                        String ip = null;
                        for (String pair : pairs) {
                            String[] keyValue = pair.split(":");

                            if (keyValue.length == 2) {
                                String key = keyValue[0].replaceAll("\"", "").trim();
                                String value = keyValue[1].replaceAll("\"", "").trim();

                                if (key.equals("domain")) {
                                    domain = value;
                                } else if (key.equals("ip")) {
                                    ip = value;
                                }
                            }
                        }

                        if (domain != null && ip != null) {
                            addresses.put(domain, ip);
                        }
                    }
                } else {
                    System.out.println("Файл не содержит корректный массив addresses.");
                }
            } else {
                System.out.println("Файл не является корректным JSON с ключом addresses.");
            }
        } else {
            System.out.println("Файл пуст или не может быть прочитан.");
        }
    }

    public void getAddressesList() {
        List<Map.Entry<String, String>> sortedEntries = new ArrayList<>(addresses.entrySet());
        sortedEntries.sort(Map.Entry.comparingByKey());
        System.out.println("Загруженные адреса:");
        for (Map.Entry<String, String> entry : sortedEntries) {
            System.out.println(entry.getKey() + " - " + entry.getValue());
        }
    }

    public String getIPByDomain(String domain) {
        return addresses.getOrDefault(domain, "Такого домена в файле нет.");
    }

    public String getDomainByIP(String ip) {
        for (Map.Entry<String, String> entry : addresses.entrySet()) {
            if (entry.getValue().equals(ip)) {
                return entry.getKey();
            }
        }
        return "Такого IP-адреса в файле нет.";
    }

    private void writeAddressesToFile() throws IOException, SftpException {
        StringBuilder content = new StringBuilder("{\n  \"addresses\": [\n");

        int size = addresses.size();
        int count = 0;

        for (Map.Entry<String, String> entry : addresses.entrySet()) {
            content.append("    {\n")
                    .append("      \"domain\": \"").append(entry.getKey()).append("\",\n")
                    .append("      \"ip\": \"").append(entry.getValue()).append("\"\n")
                    .append("    }");

            if (count < size - 1) {
                content.append(",");
            }
            content.append("\n");
            count++;
        }

        content.append("  ]\n}");
        client.writeFile(remoteFilePath, content.toString());
    }

    public void addNewPairOfDomainAddress(String domain, String ip) throws IOException, SftpException {
        if (addresses.containsKey(domain)) {
            System.out.println("Такой домен уже есть в файле.");
            return;
        }

        if (addresses.containsValue(ip)) {
            System.out.println("Такой IP-адрес уже есть в файле.");
            return;
        }
        IPVerification ipVerification = new IPVerification();

        if (!(ipVerification.isValidIPv4(ip))) {
            System.out.println("Формат IP-адреса должен быть 4 версии.");
            return;
        }
        addresses.put(domain, ip);
        writeAddressesToFile();
        System.out.println("Пара добавлена.");
    }

    public void removePairOfDomainAddressByDomain(String domain) throws IOException, SftpException {
        if (addresses.containsKey(domain)) {
            String IPToRemove = getIPByDomain(domain);
            addresses.remove(domain);
            writeAddressesToFile();
            System.out.println("Удален домен: " + domain + " с IP: " + IPToRemove);
        } else {
            System.out.println("Такого домена нет в файле.");
        }
    }

    public void removePairOfDomainAddressByIP(String ip) throws IOException, SftpException {
        String domainToRemove = getDomainByIP(ip);
        if (!domainToRemove.equals("Такого IP-адреса в файле нет.")) {
            addresses.remove(domainToRemove);
            writeAddressesToFile();
            System.out.println("Удален домен: " + domainToRemove + " с IP: " + ip);
        } else {
            System.out.println("Такого IP-адреса нет в файле.");
        }
    }
}
