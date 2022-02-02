package comm;

import java.io.Serializable;

public class LsRequest extends Request implements Serializable {

    private final int parentDir;
    private final int userID;

    public LsRequest(int parentDir, int userID) {
        this.parentDir = parentDir;
        this.userID = userID;
    }

    public int getParentDir() {
        return parentDir;
    }

    public int getUserID() {
        return userID;
    }

}
