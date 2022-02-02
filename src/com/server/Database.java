package com.server;

import core.AuthToken;
import core.DataGrid;
import core.ListContents;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Database {


    private static Connection connection;
    private static BufferedWriter logFile;


    private static String getCurrentTimestamp() {

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        return now.format(pattern);
    }

    private static void writeLog(String name, SQLException exception, Boolean isUser) {

        String timestamp = getCurrentTimestamp();
        int errCode = exception.getErrorCode();
        String sqlState = exception.getSQLState();
        String errMsg = exception.getMessage();

        try {
            logFile.newLine();
            logFile.write("-" + "-".repeat(timestamp.length()) + "-");
            logFile.newLine();
            logFile.write(" " + timestamp + " ");
            logFile.newLine();
            logFile.write("-" + "-".repeat(timestamp.length()) + "-");
            logFile.newLine();
            if (isUser != null) {
                if (isUser) {
                    logFile.write("      Username : " + name);
                } else {
                    logFile.write("File/Directory : " + name);
                }
            }
            logFile.newLine();
            logFile.write("    Error Code : " + errCode);
            logFile.newLine();
            logFile.write("     SQL State : " + sqlState);
            logFile.newLine();
            logFile.write(" Error Message : " + errMsg);
            logFile.newLine();
            logFile.write("-" + "-".repeat(timestamp.length()) + "-");
            logFile.newLine();

            logFile.flush();
        } catch (IOException ex) {
            System.out.println("Couldn't write to the log file.");
        }

    }


    public static void initialize() throws Exception {

        File file = new File(Config.LOG_FILEPATH);
        file.createNewFile();
        logFile = new BufferedWriter(new FileWriter(file, true));

        File dir = new File(Config.CLOUD_DIR);
        dir.mkdir();

        connection = DriverManager.getConnection(Config.DB_URL);
        connection.setAutoCommit(false);

    }

    public static int validateToken(AuthToken token) {

        int userID = -1;
        String username = token.getUsername();
        String password = token.getPassword();

        try {

            String q = "SELECT id FROM credential WHERE username = ? AND PASSWORD = ?";
            PreparedStatement ps = connection.prepareStatement(q);

            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                userID = rs.getInt(1);
            }

            connection.commit();
        } catch (SQLException transaction) {
            writeLog(username, transaction, true);
            try {
                connection.rollback();
                userID = -1;
            } catch (SQLException rollback) {
                writeLog(username, rollback, true);
            }
        }

        return userID;
    }

    public static int registerToken(AuthToken token) {

        int userID = -1;
        String username = token.getUsername();
        String password = token.getPassword();

        try {

            String q = "SELECT id FROM credential WHERE username = ?";
            PreparedStatement ps = connection.prepareStatement(q);

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {

                q = "INSERT INTO credential (username, password) VALUES (?, ?) RETURNING id";
                ps = connection.prepareStatement(q);

                ps.setString(1, username);
                ps.setString(2, password);

                rs = ps.executeQuery();

                rs.next();
                userID = rs.getInt(1);
                assert userID != -1;

                mkdir("", null, userID);
            }

            connection.commit();
        } catch (SQLException transaction) {
            writeLog(username, transaction, true);
            try {
                connection.rollback();
                userID = -1;
            } catch (SQLException rollback) {
                writeLog(username, rollback, true);
            }
        }

        return userID;
    }

    public static boolean mkdir(String dirname, Integer parentDir, int userID) {

        boolean result = false;

        try {

            String q;
            PreparedStatement ps;
            ResultSet rs;

            if (parentDir == null) {

                q = "INSERT INTO directory (dname) VALUES (?) RETURNING id";
                ps = connection.prepareStatement(q);

                ps.setString(1, dirname);

                rs = ps.executeQuery();

                rs.next();
                int dirID = rs.getInt(1);

                q = "INSERT INTO dir_owner (d_id, c_id) VALUES (?, ?)";
                ps = connection.prepareStatement(q);

                ps.setInt(1, dirID);
                ps.setInt(2, userID);

                ps.executeUpdate();

                result = true;

            } else {

                q = "SELECT directory.id FROM directory, dir_loc, dir_owner WHERE " +
                        "dir_owner.d_id = directory.id AND " +
                        "dir_loc.d_id = directory.id AND " +
                        "parent_dir = ? AND " +
                        "c_id = ? AND " +
                        "dname = ?";
                ps = connection.prepareStatement(q);
                ps.setInt(1, parentDir);
                ps.setInt(2, userID);
                ps.setString(3, dirname);

                rs = ps.executeQuery();

                if (!rs.next()) {

                    q = "INSERT INTO directory (dname) VALUES (?) RETURNING id";
                    ps = connection.prepareStatement(q);

                    ps.setString(1, dirname);

                    rs = ps.executeQuery();

                    rs.next();
                    int dirID = rs.getInt(1);

                    q = "INSERT INTO dir_loc (d_id, parent_dir) VALUES (?, ?)";
                    ps = connection.prepareStatement(q);

                    ps.setInt(1, dirID);
                    ps.setInt(2, parentDir);

                    ps.executeUpdate();

                    q = "INSERT INTO dir_owner (d_id, c_id) VALUES (?, ?)";
                    ps = connection.prepareStatement(q);

                    ps.setInt(1, dirID);
                    ps.setInt(2, userID);

                    ps.executeUpdate();

                    result = true;
                }

            }

            connection.commit();
        } catch (SQLException transaction) {
            writeLog(dirname, transaction, false);
            try {
                connection.rollback();
                result = false;
            } catch (SQLException rollback) {
                writeLog(dirname, rollback, false);
            }
        }

        return result;
    }

    private static boolean rmfile(int fileID) {

        boolean result = false;

        try {
            String q;
            PreparedStatement ps;

            q = "DELETE FROM file WHERE id = ?";
            ps = connection.prepareStatement(q);

            ps.setInt(1, fileID);

            ps.executeUpdate();

            connection.commit();
            result = removeFile(fileID);
        } catch (SQLException transaction) {
            writeLog(String.valueOf(fileID), transaction, false);
            try {
                connection.rollback();
                result = false;
            } catch (SQLException rollback) {
                writeLog(String.valueOf(fileID), rollback, false);
            }
        }

        return result;
    }

    private static boolean rmdir(int dirID) {

        boolean result = true;

        try {

            String q;
            PreparedStatement ps;
            ResultSet rs;

            q = "SELECT f_id FROM file_loc WHERE " +
                    "parent_dir = ?";
            ps = connection.prepareStatement(q);
            ps.setInt(1, dirID);

            rs = ps.executeQuery();

            while (rs.next()) {
                int fileID = rs.getInt(1);
                result &= rmfile(fileID);
            }

            q = "SELECT d_id FROM dir_loc WHERE " +
                    "parent_dir = ?";
            ps = connection.prepareStatement(q);
            ps.setInt(1, dirID);

            rs = ps.executeQuery();

            while (rs.next()) {
                int childID = rs.getInt(1);
                result &= rmdir(childID);
            }

            q = "DELETE FROM directory WHERE id = ?";
            ps = connection.prepareStatement(q);

            ps.setInt(1, dirID);

            ps.executeUpdate();

            connection.commit();
        } catch (SQLException transaction) {
            writeLog(String.valueOf(dirID), transaction, false);
            try {
                connection.rollback();
                result = false;
            } catch (SQLException rollback) {
                writeLog(String.valueOf(dirID), rollback, false);
            }
        }

        return result;
    }

    public static boolean rmdir(String dirname, int parentDir, int userID) {

        boolean result = false;

        try {

            String q;
            PreparedStatement ps;

            q = "SELECT directory.id FROM directory, dir_loc, dir_owner WHERE " +
                    "dir_owner.d_id = directory.id AND " +
                    "dir_loc.d_id = directory.id AND " +
                    "parent_dir = ? AND " +
                    "c_id = ? AND " +
                    "dname = ?";
            ps = connection.prepareStatement(q);
            ps.setInt(1, parentDir);
            ps.setInt(2, userID);
            ps.setString(3, dirname);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                int dirID = rs.getInt(1);
                result = rmdir(dirID);
            }

            connection.commit();
        } catch (SQLException transaction) {
            writeLog(dirname, transaction, false);
            try {
                connection.rollback();
                result = false;
            } catch (SQLException rollback) {
                writeLog(dirname, rollback, false);
            }
        }

        return result;
    }

    public static ListContents ls(int parentDir, int userID) {

        DataGrid dg_file = null;
        DataGrid dg_dir = null;

        try {

            String q;
            PreparedStatement ps;

            q = "SELECT directory.id, dname, created_at FROM directory, dir_loc, dir_owner WHERE " +
                    "dir_owner.d_id = directory.id AND " +
                    "dir_loc.d_id = directory.id AND " +
                    "parent_dir = ? AND " +
                    "c_id = ?";
            ps = connection.prepareStatement(q);
            ps.setInt(1, parentDir);
            ps.setInt(2, userID);

            ResultSet rs_dir = ps.executeQuery();
            dg_dir = new DataGrid(rs_dir);

            q = "SELECT file.id, fname, created_at FROM file, file_loc, file_owner WHERE " +
                    "file_owner.f_id = file.id AND " +
                    "file_loc.f_id = file.id AND " +
                    "parent_dir = ? AND " +
                    "c_id = ?";
            ps = connection.prepareStatement(q);
            ps.setInt(1, parentDir);
            ps.setInt(2, userID);

            ResultSet rs_file = ps.executeQuery();
            dg_file = new DataGrid(rs_file);

            connection.commit();
        } catch (SQLException transaction) {
            writeLog("", transaction, null);
            try {
                connection.rollback();
            } catch (SQLException rollback) {
                writeLog("", rollback, null);
            }
        }

        return new ListContents(dg_file, dg_dir);
    }

    public static int getHomeDir(int userID) {

        int dirID = 0;
        try {

            String q;
            PreparedStatement ps;

            q = "SELECT directory.id FROM directory, dir_owner WHERE " +
                    "dir_owner.d_id = directory.id AND " +
                    "dname = ? AND " +
                    "c_id = ?";
            ps = connection.prepareStatement(q);
            ps.setString(1, "");
            ps.setInt(2, userID);

            ResultSet rs_dir = ps.executeQuery();
            rs_dir.next();

            dirID = rs_dir.getInt(1);

            connection.commit();
        } catch (SQLException transaction) {
            writeLog("", transaction, null);
            try {
                connection.rollback();
            } catch (SQLException rollback) {
                writeLog("", rollback, null);
            }
        }

        return dirID;
    }

    public static boolean upfile(byte[] data, String fname, int parentDir, int userID) {

        boolean result = false;

        try {
            String q;
            PreparedStatement ps;
            ResultSet rs;

            q = "SELECT file.id FROM file, file_loc, file_owner WHERE " +
                    "file_owner.f_id = file.id AND " +
                    "file_loc.f_id = file.id AND " +
                    "parent_dir = ? AND " +
                    "c_id = ? AND " +
                    "fname = ?";
            ps = connection.prepareStatement(q);
            ps.setInt(1, parentDir);
            ps.setInt(2, userID);
            ps.setString(3, fname);

            rs = ps.executeQuery();

            if (!rs.next()) {

                q = "INSERT INTO file (fname) VALUES (?) RETURNING id";
                ps = connection.prepareStatement(q);

                ps.setString(1, fname);

                rs = ps.executeQuery();

                rs.next();
                int fileID = rs.getInt(1);

                q = "INSERT INTO file_loc (f_id, parent_dir) VALUES (?, ?)";
                ps = connection.prepareStatement(q);

                ps.setInt(1, fileID);
                ps.setInt(2, parentDir);

                ps.executeUpdate();

                q = "INSERT INTO file_owner (f_id, c_id) VALUES (?, ?)";
                ps = connection.prepareStatement(q);

                ps.setInt(1, fileID);
                ps.setInt(2, userID);

                ps.executeUpdate();

                try {
                    saveFile(data, fileID);
                } catch (Exception e) {
                    throw new SQLException();
                }

                result = true;
            }

            connection.commit();
        } catch (SQLException transaction) {
            writeLog(fname, transaction, false);
            try {
                connection.rollback();
                result = false;
            } catch (SQLException rollback) {
                writeLog(fname, rollback, false);
            }
        }

        return result;
    }

    private static void saveFile(byte[] data, int fileID) throws Exception {

        OutputStream fos = new FileOutputStream(Config.CLOUD_DIR + "/" + fileID + ".bin");
        BufferedOutputStream bos = new BufferedOutputStream(fos);

        bos.write(data);

        bos.flush();
        bos.close();
    }

    private static byte[] readFile(int fileID) throws Exception {
        InputStream fis = new FileInputStream(Config.CLOUD_DIR + "/" + fileID + ".bin");
        BufferedInputStream bos = new BufferedInputStream(fis);

        byte[] res = fis.readAllBytes();

        bos.close();

        return res;
    }

    private static boolean removeFile(int fileID) {
        File file = new File(Config.CLOUD_DIR + "/" + fileID + ".bin");
        return file.delete();
    }

    public static byte[] downfile(String fname, int parentDir, int userID) {

        byte[] result = null;

        try {
            String q;
            PreparedStatement ps;
            ResultSet rs;

            q = "SELECT file.id FROM file, file_loc, file_owner WHERE " +
                    "file_owner.f_id = file.id AND " +
                    "file_loc.f_id = file.id AND " +
                    "parent_dir = ? AND " +
                    "c_id = ? AND " +
                    "fname = ?";
            ps = connection.prepareStatement(q);
            ps.setInt(1, parentDir);
            ps.setInt(2, userID);
            ps.setString(3, fname);

            rs = ps.executeQuery();

            if (rs.next()) {

                int fileID = rs.getInt(1);

                try {
                    result = readFile(fileID);
                } catch (Exception e) {
                    throw new SQLException();
                }
            }

            connection.commit();
        } catch (SQLException transaction) {
            writeLog(fname, transaction, false);
            try {
                connection.rollback();
            } catch (SQLException rollback) {
                writeLog(fname, rollback, false);
            }
        }

        return result;
    }

    public static boolean rmfile(String fname, int parentDir, int userID) {

        boolean result = false;

        try {
            String q;
            PreparedStatement ps;
            ResultSet rs;

            q = "SELECT file.id FROM file, file_loc, file_owner WHERE " +
                    "file_owner.f_id = file.id AND " +
                    "file_loc.f_id = file.id AND " +
                    "parent_dir = ? AND " +
                    "c_id = ? AND " +
                    "fname = ?";
            ps = connection.prepareStatement(q);
            ps.setInt(1, parentDir);
            ps.setInt(2, userID);
            ps.setString(3, fname);

            rs = ps.executeQuery();

            if (rs.next()) {

                result = true;

                int fileID = rs.getInt(1);
                result &= rmfile(fileID);
            }

            connection.commit();
        } catch (SQLException transaction) {
            writeLog(fname, transaction, false);
            try {
                connection.rollback();
            } catch (SQLException rollback) {
                writeLog(fname, rollback, false);
            }
        }

        return result;
    }

}
