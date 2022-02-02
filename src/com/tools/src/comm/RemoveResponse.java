package comm;

import java.io.Serializable;

public class RemoveResponse extends Response implements Serializable {

    private final boolean result;

    public RemoveResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

}
