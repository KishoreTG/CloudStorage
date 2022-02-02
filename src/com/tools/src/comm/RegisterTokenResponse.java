package comm;

import java.io.Serializable;

public class RegisterTokenResponse extends Response implements Serializable {

    private final int userID;

    public RegisterTokenResponse(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

}
