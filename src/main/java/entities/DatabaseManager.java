package entities;

import config.DatabaseConfig;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class DatabaseManager {
    private static DatabaseManager instance; // Sketelon instance
    private final DatabaseConfig config;
    private Connection connection;

    private DatabaseManager() {
        super();

        config = DatabaseConfig.getInstance();
        openConnection();
       // closeConnection();
    }

    public static DatabaseManager getInstance() {
        if (instance == null)
            instance = new DatabaseManager();
        return instance;
    }

    private boolean execQuery(String query, String func) {
        try {
            return connection.prepareStatement(query).execute();
        } catch (Exception e) {
            if (func != null) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public void openConnection() {
        try {
            if (connection != null)
                connection.close();
            connection = null;

            String url =
                    "jdbc:mysql://" + config.dbHost + ":" + config.dbPort + "/" + config.dbName +
                            "?useUnicode=true&characterEncoding=UTF-8";

            connection = DriverManager.getConnection(url, config.userName, config.dbPass);
            if (connection != null) {
                if (!checkExistTable()) {
                    createTable(config.fieldsFormat);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (connection == null) {
            try {
                String url =
                        "jdbc:mysql://" + config.dbHost + ":" + config.dbPort + "/" +
                                "?useUnicode=true&characterEncoding=UTF-8";

                connection = DriverManager.getConnection(url, config.userName, config.dbPass);
                createDatabase();
                grantPermission(config.dbHost, config.dbName, config.userName, config.dbPass);
                createTable(config.fieldsFormat);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void closeConnection() {
        try {
            if (connection != null)
                connection.close();
            connection = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createDatabase() {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE DATABASE IF NOT EXISTS ").append(config.dbName).append(" DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci");

        String query = builder.toString();
        execQuery(query, "createDatabase");
    }

    public boolean checkExistTable() {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ").append(config.dbName).append(".").append(config.tableName);
        String query = builder.toString();
        return execQuery(query, "checkExistTable");
    }

    public void createTable(String fieldsFormat) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ").append(config.dbName).append(".").append(config.tableName).append("(").append(fieldsFormat).append(") DEFAULT CHARACTER SET utf8");

        String query = builder.toString();
        execQuery(query, "createTable");
    }

    public void insertTable(DonateInfo infor) {
        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ").append(config.tableName).append("(name, money, comment, methodDonate) VALUES( \"").append(infor.name).append("\" ,").append(infor.money).append(",\"").append(infor.comment).append("\",\"").append(infor.methodDonate).append("\")");

        String query = builder.toString();
        execQuery(query, "insertTable");
    }

    public List<DonateInfo> getHighestDonate(String startDate, String endDate, int num) {
        List<DonateInfo> result = new LinkedList<>();
        String query = "SELECT name, SUM(money) as money  FROM "+ config.tableName+" WHERE createdOn >= " +"\'"+ startDate + "\' AND createdOn <= \'"+endDate+"\' group by name ORDER BY SUM(money) DESC LIMIT " + num;
        ResultSet rs = null;
        try {
            rs = connection.createStatement().executeQuery(query);
            int count = 0;
            while(rs.next()) {
                DonateInfo objDonate = new DonateInfo();
                objDonate.name = rs.getString("name");
                objDonate.money = (rs.getInt("money"));
                objDonate.comment = "";
                objDonate.methodDonate = "";
                result.add(objDonate);
                count++;
                if(count >= num) {
                    break;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public List<DonateInfo> getListDonate(String startDate, String endDate) {
        List<DonateInfo> result = new LinkedList<>();

        String query = "SELECT name, money, comment, methodDonate, createdOn  FROM "+ config.tableName+" WHERE createdOn >= " +"\'"+ startDate + "\' AND createdOn <= \'"+endDate+"\'";
        ResultSet rs = null;
        try {
            rs = connection.createStatement().executeQuery(query);
            while(rs.next()) {
                DonateInfo objDonate = new DonateInfo();
                objDonate.name = rs.getString("name");
                objDonate.money = (rs.getInt("money"));
                objDonate.comment = rs.getString("comment");
                objDonate.methodDonate = rs.getString("methodDonate");
                objDonate.timeStamp = rs.getTimestamp("createdOn").getTime();
                result.add(objDonate);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public long getTotalDonate(String startDate, String endDate) {
        String query = "SELECT SUM(money)  FROM "+ config.tableName +" WHERE createdOn >= " +"\'"+ startDate + "\' AND createdOn <= \'"+endDate+"\'";
        ResultSet rs = null;
        try {
            rs = connection.createStatement().executeQuery(query);
            while(rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return 0;
    }

    public void grantPermission(String host, String dbName, String userName, String password) {
        StringBuilder builder = new StringBuilder();
        builder.append("GRANT ALL ON ").append(dbName).append(".* TO '").append(userName).append("'@'").append(host).append("' IDENTIFIED BY '").append(password).append("'");

        String query = builder.toString();
        execQuery(query, "grantPermission");
    }
}
