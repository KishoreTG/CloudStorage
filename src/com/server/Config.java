package com.server;

public class Config {

    public static final int PORT_NUMBER = 34574;
    public static final String DB_URL = String.format("jdbc:postgresql:%s?user=%s&password=%s", Database.NAME, Database.USERNAME, Database.PASSWORD);

    private static final String HOME_PATH = System.getProperty("user.home");
    public static final String LOG_FILENAME = "vm_database.log";
    public static final String CLOUD_DIRNAME = "vm_storage";
    public static final String LOG_FILEPATH = HOME_PATH + "/" + LOG_FILENAME;
    public static final String CLOUD_DIR = HOME_PATH + "/" + CLOUD_DIRNAME;

    private static final class Database {
        public static final String NAME = "virtualmachine";
        public static final String USERNAME = "vmbox";
        public static final String PASSWORD = "Grz72DkHS9qWCtQT";
    }

}
