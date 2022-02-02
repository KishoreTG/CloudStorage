package comm;

import java.io.Serializable;

public class UploadResponse extends Response implements Serializable {

    private final boolean result;

    public UploadResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

}
