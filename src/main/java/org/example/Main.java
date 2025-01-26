package org.example;

import com.jcraft.jsch.JSchException;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Введите адрес сервера: ");
        String inputAddress = sc.nextLine();

        System.out.print("Введите порт: ");
        int inputPort = sc.nextInt();
        sc.nextLine();

        System.out.print("Введите логин: ");
        String inputLogin = sc.nextLine();

        System.out.print("Введите пароль: ");
        String inputPassword = sc.nextLine();

        try {
            Client client = new Client();
            client.connect(inputAddress, inputPort, inputLogin, inputPassword);
            JSONProcessing jsonProcessing = new JSONProcessing("/home/vboxuser/Документы/addresses.txt");

            boolean exit = false;

            do {
                System.out.println("Меню возможных действий:\n" +
                        "1. Получить список пар 'домен - адрес' из файла;\n" +
                        "2. Получить IP-адрес по доменному имени;\n" +
                        "3. Получить доменное имя по IP-адресу;\n" +
                        "4. Добавить новую пару 'домен - адрес' в файл;\n" +
                        "5. Удалить пару 'домен - адрес' по доменному имени;\n" +
                        "6. Удалить пару 'домен - адрес' по IP-адресу;\n" +
                        "7. Выход.\n");

                System.out.print("Ваш выбор: ");
                int userInput = sc.nextInt();
                sc.nextLine();

                switch (userInput) {
                    case 1:
                        System.out.println();
                        break;
                    case 2:
                        System.out.println();
                        break;
                    case 3:
                        System.out.println();
                        break;
                    case 4:
                        System.out.println();
                        break;
                    case 5:
                        System.out.println();
                        break;
                    case 6:
                        System.out.println();
                        break;
                    case 7:
                        System.out.println("Вы решили завершить работу.");
                        exit = true;
                        break;
                    default:
                        System.out.println();
                        break;
                }
            } while (!exit);
        } catch (JSchException | IOException e) {
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }
}