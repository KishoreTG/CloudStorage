package comm;

import java.io.Serializable;

public class RmdirResponse extends Response implements Serializable {

    private final boolean result;

    public RmdirResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

}
