package com.ixiangpro.longtimenosee;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatabaseManager {

    private final LongTimeNoSee plugin;
    private Connection connection;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public DatabaseManager(LongTimeNoSee plugin) {
        this.plugin = plugin;
    }

    public void initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/playerdata.db");
            String createTableQuery = """
                CREATE TABLE IF NOT EXISTS players (
                    player_name TEXT PRIMARY KEY,
                    player_firstlogin TEXT,
                    player_lastoffline TEXT
                )
            """;
            try (Statement statement = connection.createStatement()) {
                statement.execute(createTableQuery);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("数据库初始化失败：" + e.getMessage());
        }
    }

    public void saveFirstLoginTime(String playerName, LocalDateTime time) {
        if (connection == null || isConnectionClosed()) {
            initializeDatabase(); // 自动重连
        }
        String query = "INSERT OR IGNORE INTO players (player_name, player_firstlogin) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerName);
            statement.setString(2, time.format(DATE_FORMATTER));
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("保存首次登录时间失败：" + e.getMessage());
        }
    }

    public void saveLogoutTime(String playerName, LocalDateTime time) {
        if (connection == null || isConnectionClosed()) {
            initializeDatabase(); // 自动重连
        }
        String query = "UPDATE players SET player_lastoffline = ? WHERE player_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, time.format(DATE_FORMATTER));
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("保存下线时间失败：" + e.getMessage());
        }
    }

    public String getFirstLoginTime(String playerName) {
        if (connection == null || isConnectionClosed()) {
            initializeDatabase(); // 自动重连
        }
        String query = "SELECT player_firstlogin FROM players WHERE player_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("player_firstlogin");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("查询首次登录时间失败：" + e.getMessage());
        }
        return null;
    }

    public LocalDateTime getLastLogoutDateTime(String playerName) {
        if (connection == null || isConnectionClosed()) {
            initializeDatabase(); // 自动重连
        }
        String query = "SELECT player_lastoffline FROM players WHERE player_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String lastLogoutTime = resultSet.getString("player_lastoffline");
                return LocalDateTime.parse(lastLogoutTime, DATE_FORMATTER);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("查询下线时间失败：" + e.getMessage());
        }
        return null;
    }

    public void closeDatabase() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("关闭数据库失败：" + e.getMessage());
        }
    }

    private boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            plugin.getLogger().severe("检查数据库连接状态失败：" + e.getMessage());
            return true;
        }
    }
}