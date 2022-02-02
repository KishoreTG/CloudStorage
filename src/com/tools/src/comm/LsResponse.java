package comm;

import core.ListContents;

import java.io.Serializable;

public class LsResponse extends Response implements Serializable {

    private final ListContents ls;

    public LsResponse(ListContents ls) {
        this.ls = ls;
    }

    public ListContents getLs() {
        return ls;
    }

}
