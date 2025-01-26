package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JSONProcessing {
    private Map<String, String> addresses;
    private String filePath;

    public JSONProcessing(String filePath) throws IOException {
        addresses = new HashMap<>();
        this.filePath = filePath;
        loadAndConvertAddressesFromFile();
    }

    private void loadAndConvertAddressesFromFile() throws IOException {
        String content = readFile(filePath);
        if (content != null && !(content.isEmpty())) {
            String[] pairs = content.replaceAll("[{}\" ]", "").split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    addresses.put(keyValue[0], keyValue[1]);
                }
            }
        }
    }

    private static String readFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return content.toString();
    }

    public Map<String, String> getAddressesList() {
        return addresses;
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

    private void writeAddressesToFile() {

    }

    public void addNewPairOfDomainAddress(String domain, String ip) {
        if (addresses.containsKey(domain) || addresses.containsKey(ip)) {
            System.out.println("Такой домен или IP-адрес уже есть в файле.");
            return;
        }
        IPVerification ipVerification = new IPVerification();
        if (!(ipVerification.isValidIP(ip))) {
            System.out.println("Формат IP-адреса должен быть 4 версии.");
            return;
        }
        addresses.put(domain, ip);
        writeAddressesToFile();
    }


}
