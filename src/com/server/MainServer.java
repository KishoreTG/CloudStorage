package com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainServer {

    public static void main(String[] args) {

        ServerSocket ss = null;
        try {
            ss = new ServerSocket(Config.PORT_NUMBER);
        } catch (IOException e) {
            System.out.println("Couldn't configure the server.");
            e.printStackTrace();
            System.exit(0);
        }

        try {
            Database.initialize();
        } catch (Exception e) {
            System.out.println("Couldn't connect to the Database.");
            e.printStackTrace();
            System.exit(0);
        }
        System.out.println("Server is ready!");

        while (true) {
            try {
                Socket s = ss.accept();
                new Thread(new Handler(s)).start();
            } catch (IOException e) {
                System.out.println("Missed connection from one of the Clients.");
            }
        }

    }

}
