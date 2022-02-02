package comm;

import java.io.Serializable;

public class HomeDirResponse extends Response implements Serializable {

    private final int dirID;

    public HomeDirResponse(int dirID) {
        this.dirID = dirID;
    }

    public int getDirID() {
        return dirID;
    }

}
