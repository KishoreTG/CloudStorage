package core;

import java.io.Serializable;

public class ListContents implements Serializable {

    private final DataGrid files;
    private final DataGrid dirs;

    public ListContents(DataGrid files, DataGrid dirs) {
        this.files = files;
        this.dirs = dirs;
    }

    public DataGrid getFiles() {
        return files;
    }

    public DataGrid getDirs() {
        return dirs;
    }

}
