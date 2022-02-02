package core;

import java.io.Serializable;

public class AuthToken implements Serializable {

    private final String username;
    private final String password;

    public AuthToken(String username, String password) {
        this.username = username;
        this.password = MD5Hasher.hash(password);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

}
