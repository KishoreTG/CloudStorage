package comm;

import java.io.Serializable;

public class UploadRequest extends Request implements Serializable {

    private final byte[] data;
    private final String fname;
    private final int parentDir;
    private final int userID;

    public UploadRequest(byte[] data, String fname, int parentDir, int userID) {
        this.data = data;
        this.fname = fname;
        this.parentDir = parentDir;
        this.userID = userID;
    }

    public byte[] getData() {
        return data;
    }

    public String getFname() {
        return fname;
    }

    public int getParentDir() {
        return parentDir;
    }

    public int getUserID() {
        return userID;
    }

}
