package com.ixiangpro.longtimenosee;

import java.io.File;
import java.sql.*;

public class DatabaseManager {
    private final LongTimeNoSee plugin;
    private Connection connection;

    public DatabaseManager(LongTimeNoSee plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        try {
            File dataFolder = plugin.getDataFolder();
            if (!dataFolder.exists()) {
                dataFolder.mkdirs();
            }
            File dbFile = new File(dataFolder, "data.db");
            String url = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            connection = DriverManager.getConnection(url);
            createTable();
        } catch (SQLException e) {
            plugin.getLogger().severe("无法连接到 SQLite 数据库: " + e.getMessage());
        }
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
        String sql = "CREATE TABLE IF NOT EXISTS player_data (" +
                     "uuid VARCHAR(36) PRIMARY KEY," +
                     "player_name VARCHAR(16)," +
                     "last_offline BIGINT" +
                     ");";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 更新或插入玩家离线时间 (使用 SQLite 的 REPLACE 语法)
    public void setLastOffline(String uuid, String name, long time) {
        String sql = "REPLACE INTO player_data (uuid, player_name, last_offline) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, name);
            pstmt.setLong(3, time);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 根据 UUID 获取时间
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
        return -1; // -1 代表没有记录 (首次登录)
    }

    // 根据玩家名称获取时间（用于指令检查）
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
