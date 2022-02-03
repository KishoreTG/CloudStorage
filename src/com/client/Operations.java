package com.client;

import comm.*;
import core.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class Operations {

    public static UserIdentifier authenticate(Handler handler) throws IOException {

        int userID;
        String username;
        do {
            Menu authMenu = new Menu(IOClient.br, IOClient.bw);

            authMenu.setTitle("Authentication Menu");
            authMenu.setChoices(Arrays.asList("Login", "Register"));

            IOClient.bw.newLine();
            IOClient.bw.flush();
            int ch = authMenu.show();

            if (ch == -1) {
                System.exit(0);
            }

            Form authForm = new Form(IOClient.br, IOClient.bw);

            authForm.setTitle("Authentication Form");
            authForm.setLabels(Arrays.asList("Username", "Password"));
            authForm.setSensitiveData(List.of(1));

            IOClient.bw.newLine();
            IOClient.bw.flush();
            List<String> inputs = authForm.fill();

            if (!(Validator.validateUsername(inputs.get(0)) && Validator.validatePassword(inputs.get(1)))) {
                IOClient.bw.write("Invalid Username/Password!");
                IOClient.bw.newLine();
                IOClient.bw.write("- Username should contain only lowercase letters and its length should be in the range [6,32].");
                IOClient.bw.newLine();
                IOClient.bw.write("- Password should contain only alphanumeric characters and its length should be atleast 8.");
                IOClient.bw.newLine();
                IOClient.bw.flush();
                continue;
            }

            AuthToken token = new AuthToken(inputs.get(0), inputs.get(1));
            Request request = null;

            assert ch == 1 || ch == 2;
            if (ch == 1) {
                request = new ValidateTokenRequest(token);
            } else {
                request = new RegisterTokenRequest(token);
            }

            handler.getOs().writeObject(request);

            Response response = null;
            try {
                response = (Response) handler.getIs().readObject();
            } catch (ClassNotFoundException ignored) {

            }
            assert response != null;

            if (ch == 1) {
                ValidateTokenResponse valResponse = (ValidateTokenResponse) response;
                userID = valResponse.getUserID();
            } else {
                RegisterTokenResponse regResponse = (RegisterTokenResponse) response;
                userID = regResponse.getUserID();
            }

            if (userID == -1) {
                if (ch == 1) {
                    IOClient.bw.write("Authentication Failed.");
                } else {
                    IOClient.bw.write("Please choose a different username.");
                }
                IOClient.bw.newLine();
                IOClient.bw.flush();
                continue;
            }

            if (ch == 1) {
                IOClient.bw.write("Login successful.");
            } else {
                IOClient.bw.write("Registration successful.");
            }
            IOClient.bw.newLine();
            IOClient.bw.flush();

            username = inputs.get(0);
            break;

        } while (true);

        return new UserIdentifier(userID, username);
    }

    public static int getDirID(Handler handler) throws IOException {

        Request request = new HomeDirRequest(handler.getUserID());

        handler.getOs().writeObject(request);

        Response response = null;
        try {
            response = (Response) handler.getIs().readObject();
        } catch (ClassNotFoundException ignored) {

        }
        assert response != null;

        HomeDirResponse dirResponse = (HomeDirResponse) response;
        return dirResponse.getDirID();
    }

    public static ListContents getContents(Handler handler) throws IOException {

        Request request = new LsRequest(handler.currentDirID, handler.getUserID());

        handler.getOs().writeObject(request);

        Response response = null;
        try {
            response = (Response) handler.getIs().readObject();
        } catch (ClassNotFoundException ignored) {

        }
        assert response != null;

        LsResponse lsResponse = (LsResponse) response;
        return lsResponse.getLs();
    }

    private static void printHelp() throws IOException {

        IOClient.bw.write(MyColors.HELP);
        IOClient.bw.write("help               -> Show the help info.");
        IOClient.bw.newLine();
        IOClient.bw.write("ls                 -> List the files and directories.");
        IOClient.bw.newLine();
        IOClient.bw.write("ls -a              -> List the files and directories with date and time.");
        IOClient.bw.newLine();
        IOClient.bw.write("mkdir NAME         -> Create a directory with the given name.");
        IOClient.bw.newLine();
        IOClient.bw.write("rmdir NAME         -> Removes the directory with the given name.");
        IOClient.bw.newLine();
        IOClient.bw.write("cd NAME            -> Goes to the directory with the given name from the current directory.");
        IOClient.bw.newLine();
        IOClient.bw.write("cd ..              -> Goes to the previous directory from the current directory.");
        IOClient.bw.newLine();
        IOClient.bw.write("upfile NAME PATH   -> Upload the file as the given name from the given path.");
        IOClient.bw.newLine();
        IOClient.bw.write("downfile NAME PATH -> Download the file with the given name to the given path.");
        IOClient.bw.newLine();
        IOClient.bw.write("rmfile NAME        -> Remove the file with the given name.");
        IOClient.bw.newLine();
        IOClient.bw.write("exit               -> Exit.");
        IOClient.bw.newLine();
        IOClient.bw.write("NAME (for files)       - should contain only lowercase letters and dots");
        IOClient.bw.newLine();
        IOClient.bw.write("NAME (for directories) - should contain only lowercase letters");
        IOClient.bw.newLine();
        IOClient.bw.write(MyColors.HELP);

        IOClient.bw.newLine();
        IOClient.bw.flush();
    }

    public static void parseCMD(String input, Handler handler) throws Exception {
        if (input.equals("ls")) {
            printContents(handler.contents, false);
        } else if (input.equals("ls -a")) {
            printContents(handler.contents, true);
        } else if (input.equals("help")) {
            printHelp();
        } else if (input.startsWith("mkdir ")) {
            mkdir(input, handler);
        } else if (input.startsWith("rmdir ")) {
            rmdir(input, handler);
        } else if (input.startsWith("cd ")) {
            cd(input, handler);
        } else if (input.startsWith("upfile ")) {
            upfile(input, handler);
        } else if (input.startsWith("downfile ")) {
            downfile(input, handler);
        } else if (input.startsWith("rmfile ")) {
            rmfile(input, handler);
        }
    }

    private static boolean upfile(String input, Handler handler) throws IOException {
        String[] parsed = input.split(" ");
        if (parsed.length != 3) {
            return false;
        }
        String fname = parsed[1];
        if (!Validator.validateFilename(fname)) {
            return false;
        }
        String path = parsed[2];
        byte[] data;
        try {
            data = FileOper.readFile(path);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Request request = new UploadRequest(data, fname, handler.currentDirID, handler.getUserID());
        handler.getOs().writeObject(request);
        Response response = null;
        try {
            response = (Response) handler.getIs().readObject();
        } catch (Exception ignored) {
        }
        assert response != null;
        UploadResponse uploadResponse = (UploadResponse) response;
        return uploadResponse.isResult();
    }

    private static boolean downfile(String input, Handler handler) throws Exception {
        String[] parsed = input.split(" ");
        if (parsed.length != 3) {
            return false;
        }
        String fname = parsed[1];
        if (!Validator.validateFilename(fname)) {
            return false;
        }
        String path = parsed[2];
        Request request = new DownloadRequest(fname, handler.currentDirID, handler.getUserID());
        handler.getOs().writeObject(request);
        Response response = null;
        try {
            response = (Response) handler.getIs().readObject();
        } catch (Exception ignored) {
        }
        assert response != null;
        DownloadResponse downloadResponse = (DownloadResponse) response;
        byte[] data = downloadResponse.getData();
        if (data == null) {
            return false;
        }
        FileOper.writeFile(path, data);
        return true;
    }

    private static boolean rmfile(String input, Handler handler) throws IOException {
        String[] parsed = input.split(" ");
        if (parsed.length != 2) {
            return false;
        }
        String fname = parsed[1];
        if (!Validator.validateFilename(fname)) {
            return false;
        }
        Request request = new RemoveRequest(fname, handler.currentDirID, handler.getUserID());
        handler.getOs().writeObject(request);
        Response response = null;
        try {
            response = (Response) handler.getIs().readObject();
        } catch (Exception ignored) {
        }
        assert response != null;
        RemoveResponse downloadResponse = (RemoveResponse) response;
        return downloadResponse.isResult();
    }

    private static boolean cd(String input, Handler handler) throws IOException {
        String[] parsed = input.split(" ");
        if (parsed.length != 2) {
            return false;
        }
        String dname = parsed[1];
        if (!(Validator.validateDirname(dname) || dname.equals(".."))) {
            return false;
        }
        if (dname.equals("..")) {
            if (handler.dirIDs.size() <= 1) {
                return false;
            }
            handler.dirIDs.removeLast();
            handler.dirs.removeLast();
            handler.currentDirID = handler.dirIDs.getLast();
        } else {
            DataGrid dirs = handler.contents.getDirs();
            int goDirID = -1;
            for (int i = 0; i < dirs.getRowCount(); i += 1) {
                if (dirs.getData(i, 1).toString().equals(dname)) {
                    goDirID = (int) dirs.getData(i, 0);
                    break;
                }
            }
            if (goDirID == -1) {
                return false;
            }
            handler.dirIDs.addLast(goDirID);
            handler.dirs.addLast(dname);
            handler.currentDirID = goDirID;
        }
        return true;
    }

    private static boolean mkdir(String input, Handler handler) throws IOException {
        String[] parsed = input.split(" ");
        if (parsed.length != 2) {
            return false;
        }
        String dname = parsed[1];
        if (!Validator.validateDirname(dname)) {
            return false;
        }
        Request request = new MkdirRequest(dname, handler.currentDirID, handler.getUserID());
        handler.getOs().writeObject(request);
        Response response = null;
        try {
            response = (Response) handler.getIs().readObject();
        } catch (Exception ignored) {
        }
        assert response != null;
        MkdirResponse mkdirResponse = (MkdirResponse) response;
        return mkdirResponse.isResult();
    }

    private static boolean rmdir(String input, Handler handler) throws IOException {
        String[] parsed = input.split(" ");
        if (parsed.length != 2) {
            return false;
        }
        String dname = parsed[1];
        if (!Validator.validateDirname(dname)) {
            return false;
        }
        Request request = new RmdirRequest(dname, handler.currentDirID, handler.getUserID());
        handler.getOs().writeObject(request);
        Response response = null;
        try {
            response = (Response) handler.getIs().readObject();
        } catch (Exception ignored) {
        }
        assert response != null;
        RmdirResponse mkdirResponse = (RmdirResponse) response;
        return mkdirResponse.isResult();
    }

    private static void printContentsFromDataGrid(DataGrid dg, boolean dateToPrint, boolean isDir) throws IOException {
        for (int i = 0; i < dg.getRowCount(); i += 1) {
            String name = (String) dg.getData(i, 1);
            Timestamp ts = (Timestamp) dg.getData(i, 2);
            LocalDateTime ldt = ts.toLocalDateTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String formattedDateTime = ldt.format(formatter);
            if (isDir) {
                IOClient.bw.write(MyColors.DIR);
            }
            if (dateToPrint) {
                IOClient.bw.write(String.format("%-20s %-15s", name, formattedDateTime));
            } else {
                IOClient.bw.write(String.format("%-20s", name));
            }
            if (isDir) {
                IOClient.bw.write(MyColors.RESET);
            }
            if (dateToPrint) {
                IOClient.bw.newLine();
            }
        }
        IOClient.bw.newLine();
        IOClient.bw.flush();
    }

    private static void printContents(ListContents contents, boolean dateToPrint) throws IOException {
        DataGrid dg_files = contents.getFiles();
        DataGrid dg_dirs = contents.getDirs();
        printContentsFromDataGrid(dg_dirs, dateToPrint, true);
        printContentsFromDataGrid(dg_files, dateToPrint, false);
    }

}
