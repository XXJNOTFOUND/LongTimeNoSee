package com.ixiangpro.longtimenosee;

import java.net.URLClassLoader;
import java.sql.*;

public class DatabaseManager {
    private final LongTimeNoSee plugin;
    private Connection connection;
    private String dbType;
    private URLClassLoader libLoader;

    public DatabaseManager(LongTimeNoSee plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        dbType = plugin.getConfig().getString("database.type", "sqlite");

        try {
            if (dbType.equalsIgnoreCase("mysql")) {
                connectMySQL();
            } else {
                connectSQLite();
            }
            createTable();
            plugin.getLogger().info("数据库连接成功！类型: " + dbType);
        } catch (Exception e) {
            plugin.getLogger().severe("数据库连接失败: " + e.getMessage());
        }
    }

    private void connectSQLite() throws SQLException {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/data.db";
        connection = DriverManager.getConnection(url);
    }

    private void connectMySQL() throws Exception {
        // 先检查并下载驱动
        if (!plugin.getDependencyDownloader().isDriverReady()) {
            boolean ok = plugin.getDependencyDownloader().downloadDriver();
            if (!ok) {
                plugin.getLogger().severe("无法获取 MySQL 驱动，请手动下载后放入 lib 目录。");
                return;
            }
        }

        // 用自定义类加载器加载 MySQL 驱动
        libLoader = plugin.getDependencyDownloader().createLibClassLoader();
        if (libLoader == null) {
            plugin.getLogger().severe("无法加载 MySQL 驱动类。");
            return;
        }

        // 手动注册驱动
        Class<?> driverClass = libLoader.loadClass("com.mysql.cj.jdbc.Driver");
        Driver driver = (Driver) driverClass.getDeclaredConstructor().newInstance();
        DriverManager.registerDriver(new DriverWrapper(driver));

        String host = plugin.getConfig().getString("database.mysql.host", "localhost");
        int port = plugin.getConfig().getInt("database.mysql.port", 3306);
        String database = plugin.getConfig().getString("database.mysql.database", "longtimenosee");
        String username = plugin.getConfig().getString("database.mysql.username", "root");
        String password = plugin.getConfig().getString("database.mysql.password", "");

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=false&serverTimezone=Asia/Shanghai";
        connection = DriverManager.getConnection(url, username, password);
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        String sql;
        if (dbType.equalsIgnoreCase("mysql")) {
            sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                  "uuid VARCHAR(36) PRIMARY KEY," +
                  "player_name VARCHAR(16)," +
                  "last_offline BIGINT" +
                  ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;";
        } else {
            sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                  "uuid VARCHAR(36) PRIMARY KEY," +
                  "player_name VARCHAR(16)," +
                  "last_offline BIGINT" +
                  ");";
        }
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setLastOffline(String uuid, String name, long time) {
        String sql;
        if (dbType.equalsIgnoreCase("mysql")) {
            sql = "INSERT INTO player_data (uuid, player_name, last_offline) VALUES (?, ?, ?) " +
                  "ON DUPLICATE KEY UPDATE player_name=VALUES(player_name), last_offline=VALUES(last_offline)";
        } else {
            sql = "REPLACE INTO player_data (uuid, player_name, last_offline) VALUES (?, ?, ?)";
        }
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, name);
            pstmt.setLong(3, time);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getLastOffline(String uuid) {
        String sql = "SELECT last_offline FROM player_data WHERE uuid = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("last_offline");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public long getLastOfflineByName(String name) {
        String sql = "SELECT last_offline FROM player_data WHERE player_name COLLATE NOCASE = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getLong("last_offline");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
