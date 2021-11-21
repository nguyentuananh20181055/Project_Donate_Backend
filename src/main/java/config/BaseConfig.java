package config;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class BaseConfig {
    private static final String CONFIG_JSON_PATH = "./conf/base.json";
    private static BaseConfig inst;
    private static final Object lock = new Object();

    public int port;

    private BaseConfig() {

    }

    public static BaseConfig getInstance() {
        BaseConfig result = inst;
        if (result == null) {
            synchronized (lock) {
                result = inst;
                if (result == null) {
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new FileReader(CONFIG_JSON_PATH));
                        inst = result = new Gson().fromJson(br, BaseConfig.class);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return result;
    }
}
