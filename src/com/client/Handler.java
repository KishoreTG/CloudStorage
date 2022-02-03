package com.client;

import core.ListContents;
import core.UserIdentifier;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;


public class Handler implements Runnable {

    public final String connLoc;
    public final Socket s;
    public final ObjectOutputStream os;
    public final ObjectInputStream is;
    public final int userID;
    public final String username;
    public final Deque<String> dirs;
    public final Deque<Integer> dirIDs;
    public final int TIME_BREAK = 6000;
    public int currentDirID;
    public ListContents contents;

    public Handler(Socket s) throws IOException {
        this.s = s;
        this.os = new ObjectOutputStream(s.getOutputStream());
        this.is = new ObjectInputStream(s.getInputStream());
        UserIdentifier userIdentifier = Operations.authenticate(this);
        this.userID = userIdentifier.getUserID();
        this.username = userIdentifier.getUsername();
        this.currentDirID = Operations.getDirID(this);
        this.contents = Operations.getContents(this);
        dirs = new ArrayDeque<>();
        dirIDs = new ArrayDeque<>();
        dirs.addLast("");
        dirIDs.addLast(currentDirID);
        this.connLoc = findConnLoc();
    }

    private String findConnLoc() {
        return String.format(MyColors.ID + "%s@%s" + MyColors.RESET, username, s.getRemoteSocketAddress());
    }

    public Socket getS() {
        return s;
    }

    public ObjectOutputStream getOs() {
        return os;
    }

    public ObjectInputStream getIs() {
        return is;
    }

    public int getUserID() {
        return userID;
    }

    private String getCurrPath() {
        StringBuilder sb = new StringBuilder();
        sb.append(MyColors.PATH);
        for (String each : dirs) {
            sb.append(each).append("/");
        }
        sb.append(MyColors.RESET);
        return sb.toString();
    }

    private void separator() throws IOException {
        IOClient.bw.newLine();
        IOClient.bw.write("-------------------------------------------------");
        IOClient.bw.newLine();
        IOClient.bw.newLine();
        IOClient.bw.flush();
    }

    private String prompt() throws IOException {
        IOClient.bw.write(connLoc + "-" + getCurrPath() + "$ ");
        IOClient.bw.flush();
        String input = IOClient.br.readLine();
        return input;
    }

    private void initialize() throws Exception {

        IOClient.bw.write("Connecting to the Cloud Storage Machine...");
        IOClient.bw.newLine();
        IOClient.bw.flush();
        Thread.sleep(TIME_BREAK);

//        ConsoleOperations.clearConsole();
    }

    private void finish() throws Exception {

        IOClient.bw.write("Disconnecting from the Cloud Storage Machine...");
        IOClient.bw.newLine();
        IOClient.bw.flush();
        Thread.sleep(TIME_BREAK);

//        ConsoleOperations.clearConsole();
    }

    private void debug() throws IOException {
        IOClient.bw.write(currentDirID);
        IOClient.bw.write(String.valueOf(dirs));
        IOClient.bw.write(String.valueOf(dirIDs));
        contents.getDirs().printDataFormatted();
        IOClient.bw.newLine();
    }

    private void printError(String message) throws IOException {
        IOClient.bw.write(MyColors.ERROR);
        IOClient.bw.newLine();
        IOClient.bw.write("Error Occurred : " + message);
        IOClient.bw.newLine();
        IOClient.bw.write(MyColors.ERROR);

        IOClient.bw.flush();
    }

    @Override
    public void run() {

        try {

            initialize();

            separator();
            while (true) {
                this.contents = Operations.getContents(this);
//                debug();
                String input = prompt();
                if (input.equals("exit")) {
                    break;
                } else {
                    try {
                        Operations.parseCMD(input, this);
                    }
                    catch (Exception e) {
                        try {
                            printError(e.getMessage());
                        } catch (IOException ew) {
                            ew.printStackTrace();
                        }
                    }
                }
            }
            separator();

            finish();
        } catch (Exception ex) {
            try {
                printError(ex.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
