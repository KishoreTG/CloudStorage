package core;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Hasher {

    public static String hash(String input) {

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException ignored) {

        }
        assert md != null;

        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger num = new BigInteger(1, messageDigest);

        StringBuilder hashed = new StringBuilder(num.toString(16));
        while (hashed.length() < 32) {
            hashed.insert(0, "0");
        }

        return hashed.toString().toUpperCase();
    }

}
