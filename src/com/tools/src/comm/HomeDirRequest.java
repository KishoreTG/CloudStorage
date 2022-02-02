package comm;

import java.io.Serializable;

public class HomeDirRequest extends Request implements Serializable {

    private final int userID;

    public HomeDirRequest(int userID) {
        this.userID = userID;
    }

    public int getUserID() {
        return userID;
    }

}
