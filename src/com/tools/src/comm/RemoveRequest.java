package comm;

import java.io.Serializable;

public class RemoveRequest extends Request implements Serializable {

    private final String fname;
    private final int dirID;
    private final int userID;

    public RemoveRequest(String fname, int dirID, int userID) {
        this.fname = fname;
        this.dirID = dirID;
        this.userID = userID;
    }

    public String getFname() {
        return fname;
    }

    public int getDirID() {
        return dirID;
    }

    public int getUserID() {
        return userID;
    }

}
