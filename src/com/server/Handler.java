package com.server;

import comm.*;
import core.AuthToken;
import core.ListContents;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Handler implements Runnable {

    public final Socket s;
    public final ObjectOutputStream os;
    public final ObjectInputStream is;

    public Handler(Socket s) throws IOException {
        this.s = s;
        this.os = new ObjectOutputStream(s.getOutputStream());
        this.is = new ObjectInputStream(s.getInputStream());
    }

    @Override
    public void run() {

        while (true) {

            try {
                Request request = getRequest();
                Response response = findResponse(request);
                sendResponse(response);
            } catch (IOException e) {
                break;
            }

        }

    }

    private Request getRequest() throws IOException {
        Request request = null;
        try {
            request = (Request) is.readObject();
        } catch (ClassNotFoundException ignored) {

        }
        return request;
    }

    private void sendResponse(Response response) throws IOException {
        os.writeObject(response);
    }

    private Response findResponse(Request request) {

        try {
            ValidateTokenRequest validateTokenRequest = (ValidateTokenRequest) request;
            AuthToken token = validateTokenRequest.getToken();
            int userID = Database.validateToken(token);
            return new ValidateTokenResponse(userID);
        } catch (ClassCastException ignored) {

        }

        try {
            RegisterTokenRequest registerTokenRequest = (RegisterTokenRequest) request;
            AuthToken token = registerTokenRequest.getToken();
            int userID = Database.registerToken(token);
            return new RegisterTokenResponse(userID);
        } catch (ClassCastException ignored) {

        }

        try {
            HomeDirRequest homeDirRequest = (HomeDirRequest) request;
            int userID = homeDirRequest.getUserID();
            int dirID = Database.getHomeDir(userID);
            return new HomeDirResponse(dirID);
        } catch (ClassCastException ignored) {

        }

        try {
            LsRequest lsRequest = (LsRequest) request;
            int userID = lsRequest.getUserID();
            int parentID = lsRequest.getParentDir();
            ListContents contents = Database.ls(parentID, userID);
            return new LsResponse(contents);
        } catch (ClassCastException ignored) {

        }

        try {
            MkdirRequest mkdirRequest = (MkdirRequest) request;
            String dname = mkdirRequest.getDirname();
            int userID = mkdirRequest.getUserID();
            int parentID = mkdirRequest.getParentDir();
            boolean result = Database.mkdir(dname, parentID, userID);
            return new MkdirResponse(result);
        } catch (ClassCastException ignored) {

        }

        try {
            RmdirRequest rmdirRequest = (RmdirRequest) request;
            String dname = rmdirRequest.getDirname();
            int userID = rmdirRequest.getUserID();
            int parentID = rmdirRequest.getParentDir();
            boolean result = Database.rmdir(dname, parentID, userID);
            return new RmdirResponse(result);
        } catch (ClassCastException ignored) {

        }

        try {
            UploadRequest uploadRequest = (UploadRequest) request;
            byte[] data = uploadRequest.getData();
            String fname = uploadRequest.getFname();
            int parentDir = uploadRequest.getParentDir();
            int userID = uploadRequest.getUserID();
            boolean result = Database.upfile(data, fname, parentDir, userID);
            return new UploadResponse(result);
        } catch (ClassCastException ignored) {

        }

        try {
            DownloadRequest downloadRequest = (DownloadRequest) request;
            String fname = downloadRequest.getFname();
            int parentDir = downloadRequest.getDirID();
            int userID = downloadRequest.getUserID();
            byte[] result = Database.downfile(fname, parentDir, userID);
            return new DownloadResponse(result);
        } catch (ClassCastException ignored) {

        }

        try {
            RemoveRequest removeRequest = (RemoveRequest) request;
            String fname = removeRequest.getFname();
            int parentDir = removeRequest.getDirID();
            int userID = removeRequest.getUserID();
            boolean res = Database.rmfile(fname, parentDir, userID);
            return new RemoveResponse(res);
        } catch (ClassCastException ignored) {

        }

        return null;
    }

}
