package core;

import java.io.*;
import java.util.Base64;

public class FileOper {

    public static byte[] readFile(String path) throws Exception {
        File file = new File(path);
        if (!file.exists()) {
            throw new IOException();
        }
        int len = Math.toIntExact(file.length());
        byte[] byteFile = new byte[len];
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
        bis.read(byteFile, 0, len);
        return encode(byteFile);
    }

    public static void writeFile(String path, byte[] data) throws Exception {
        File file = new File(path);
        file.createNewFile();
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        byte[] realData = decode(data);
        bos.write(realData);
        bos.flush();
    }

    private static byte[] encode(byte[] data) {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] res = encoder.encode(data);
        return res;
    }

    private static byte[] decode(byte[] data) {
        Base64.Decoder decoder = Base64.getDecoder();
        byte[] res = decoder.decode(data);
        return res;
    }

}
