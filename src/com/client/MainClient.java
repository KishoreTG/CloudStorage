package com.client;

import java.io.IOException;
import java.net.Socket;

public class MainClient {

    public static void main(String[] args) throws IOException {

        IOClient.bw.write("Enter the IP Address  : ");
        IOClient.bw.flush();
        String ipAddress = IOClient.br.readLine();

        IOClient.bw.write("Enter the Port Number : ");
        IOClient.bw.flush();
        String portNum = IOClient.br.readLine();

        Socket s = null;
        try {
            s = new Socket(ipAddress, Integer.parseInt(portNum));
        } catch (Exception ex) {
            IOClient.bw.write("Couldn't connect to the Server.");
            IOClient.bw.newLine();
            IOClient.bw.flush();
            System.exit(0);
        }

        new Thread(new Handler(s)).start();

    }

}
