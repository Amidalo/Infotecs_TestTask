package org.example;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            String inputAddress = enterAddress(sc);
            int inputPort = enterPort(sc);
            sc.nextLine();
            String inputLogin = enterLogin(sc);
            String inputPassword = enterPassword(sc);
            String remoteFilePath = enterRemoteFilePath(sc);

            Client client = new Client();
            client.connect(inputAddress, inputPort, inputLogin, inputPassword);
            JSONProcessing jsonProcessing = new JSONProcessing(client, remoteFilePath);

            processUserInput(sc, jsonProcessing, client);

        } catch (JSchException | IOException | SftpException e) {
            System.err.println("Ошибка: " + e.getMessage());
            System.exit(2);
        }
    }

    private static void processUserInput(Scanner sc, JSONProcessing jsonProcessing, Client client) throws IOException,
            SftpException {
        boolean exit = false;
        while (!exit) {
            printMenu();
            int userInput = doChoice(sc);

            String ip;
            String domain;

            switch (userInput) {
                case 1:
                    jsonProcessing.getAddressesList();
                    break;
                case 2:
                    System.out.print("Введите доменное имя: ");
                    domain = sc.nextLine();
                    ip = jsonProcessing.getIPByDomain(domain);
                    System.out.println("IP-адрес для домена " + domain + ": " + ip);
                    break;
                case 3:
                    System.out.print("Введите IP-адрес: ");
                    ip = sc.nextLine();
                    domain = jsonProcessing.getDomainByIP(ip);
                    System.out.println("Доменное имя для IP-адреса " + ip + ": " + domain);
                    break;
                case 4:
                    System.out.print("Введите доменное имя: ");
                    domain = sc.nextLine();
                    System.out.print("Введите IP-адрес: ");
                    ip = sc.nextLine();
                    jsonProcessing.addNewPairOfDomainAddress(domain, ip);
                    break;
                case 5:
                    System.out.print("Введите доменное имя для удаления: ");
                    domain = sc.nextLine();
                    jsonProcessing.removePairOfDomainAddressByDomain(domain);
                    break;
                case 6:
                    System.out.print("Введите IP-адрес для удаления: ");
                    ip = sc.nextLine();
                    jsonProcessing.removePairOfDomainAddressByIP(ip);
                    break;
                case 7:
                    exit = true;
                    System.out.println("Вы решили завершить работу.");
                    client.disconnect();
                    break;
            }
        }
    }

    private static int doChoice(Scanner sc) {
        while (true) {
            try {
                System.out.print("Ваш выбор: ");
                int choice = Integer.parseInt(sc.nextLine());
                if (choice >= 1 && choice <= 7) {
                    return choice;
                } else {
                    System.out.println("Некорректный выбор. Пожалуйста, введите число от 1 до 7.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Некорректный ввод. Пожалуйста, введите число.");
            }
        }
    }

    private static String enterRemoteFilePath(Scanner sc) {
        System.out.print("Введите путь к файлу (например, /home/vboxuser/myaddresses.txt): ");
        return sc.nextLine();
    }

    private static String enterPassword(Scanner sc) {
        System.out.print("Введите пароль: ");
        return sc.nextLine();
    }

    private static String enterLogin(Scanner sc) {
        System.out.print("Введите логин: ");
        return sc.nextLine();
    }

    private static int enterPort(Scanner sc) {
        while (true) {
            System.out.print("Введите порт: ");

            if (sc.hasNextInt()) {
                return sc.nextInt();
            } else {
                System.out.println("Некорректный порт.");
                sc.next();
            }
        }
    }

    private static String enterAddress(Scanner sc)  {
        IPVerification IPVerification = new IPVerification();

        while (true) {
            System.out.print("Введите адрес сервера: ");
            String address = sc.nextLine();

            if (IPVerification.isValidIPv4(address)) {
                return address;
            } else {
                System.out.println("Формат IP-адреса должен быть 4 версии.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\nМеню возможных действий:\n" +
                "1. Получить список пар 'домен - адрес' из файла;\n" +
                "2. Получить IP-адрес по доменному имени;\n" +
                "3. Получить доменное имя по IP-адресу;\n" +
                "4. Добавить новую пару 'домен - адрес' в файл;\n" +
                "5. Удалить пару 'домен - адрес' по доменному имени;\n" +
                "6. Удалить пару 'домен - адрес' по IP-адресу;\n" +
                "7. Выход.\n");
    }
}