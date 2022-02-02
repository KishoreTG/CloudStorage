package comm;

import java.io.Serializable;

public class DownloadResponse extends Response implements Serializable {

    private final byte[] data;

    public DownloadResponse(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

}
