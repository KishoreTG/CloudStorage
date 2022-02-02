package comm;

import java.io.Serializable;

public class MkdirResponse extends Response implements Serializable {

    private final boolean result;

    public MkdirResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

}
