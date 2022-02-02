package comm;

import java.io.Serializable;

public class RmdirRequest extends Request implements Serializable {

    private final String dirname;
    private final int parentDir;
    private final int userID;

    public RmdirRequest(String dirname, int parentDir, int userID) {
        this.dirname = dirname;
        this.parentDir = parentDir;
        this.userID = userID;
    }

    public String getDirname() {
        return dirname;
    }

    public int getParentDir() {
        return parentDir;
    }

    public int getUserID() {
        return userID;
    }

}
