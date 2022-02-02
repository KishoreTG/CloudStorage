package core;

import java.util.regex.Pattern;

public class Validator {

    private static final Pattern username = Pattern.compile("^[a-z]{6,32}$");
    private static final Pattern password = Pattern.compile("^[a-zA-Z0-9]{8,}$");
    private static final Pattern filename = Pattern.compile("^[a-z.]{1,50}$");
    private static final Pattern dirname = Pattern.compile("^[a-z]{1,50}$");

    public static boolean validateUsername(String input) {
        return username.matcher(input).matches();
    }

    public static boolean validatePassword(String input) {
        return password.matcher(input).matches();
    }

    public static boolean validateFilename(String input) {
        return filename.matcher(input).matches();
    }

    public static boolean validateDirname(String input) {
        return dirname.matcher(input).matches();
    }

}
