package comm;

import java.io.Serializable;

public class ValidateTokenResponse extends Response implements Serializable {

    private final int userID;

    public ValidateTokenResponse(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

}
