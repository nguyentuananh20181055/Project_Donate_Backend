package config;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class DatabaseConfig {
    private static final String CONFIG_JSON_PATH = "./conf/dbConfig.json";
    private static DatabaseConfig inst;
    private static final Object lock = new Object();

    public String dbHost;
    public int dbPort;
    public String dbName;
    public String tableName;
    public String userName;
    public String dbPass;
    public String fieldsFormat;

    public DatabaseConfig() {

    }

    public static DatabaseConfig getInstance() {
        DatabaseConfig result = inst;
        if (result == null) {
            synchronized (lock) {
                result = inst;
                if (result == null) {
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new FileReader(CONFIG_JSON_PATH));
                        inst = result = new Gson().fromJson(br, DatabaseConfig.class);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }
}
