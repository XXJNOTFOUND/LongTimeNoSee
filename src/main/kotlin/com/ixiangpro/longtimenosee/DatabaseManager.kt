package com.ixiangpro.longtimenosee

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatabaseManager(private val plugin: LongTimeNoSee) {

    private var connection: Connection? = null
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    fun initializeDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:${plugin.dataFolder}/playerdata.db")
            val createTableQuery = """
                CREATE TABLE IF NOT EXISTS players (
                    player_name TEXT PRIMARY KEY,
                    player_firstlogin TEXT,
                    player_lastoffline TEXT
                )
            """.trimIndent()
            connection?.createStatement()?.use { it.execute(createTableQuery) }
        } catch (e: SQLException) {
            plugin.logger.severe("数据库初始化失败：${e.message}")
        }
    }

    fun saveFirstLoginTime(playerName: String, time: LocalDateTime) {
        ensureConnection()
        val query = "INSERT OR IGNORE INTO players (player_name, player_firstlogin) VALUES (?, ?)"
        connection?.prepareStatement(query)?.use { stmt ->
            stmt.setString(1, playerName)
            stmt.setString(2, time.format(dateFormatter))
            stmt.executeUpdate()
        } ?: plugin.logger.severe("保存首次登录时间失败：数据库连接为空")
    }

    fun saveLogoutTime(playerName: String, time: LocalDateTime) {
        ensureConnection()
        val query = "UPDATE players SET player_lastoffline = ? WHERE player_name = ?"
        connection?.prepareStatement(query)?.use { stmt ->
            stmt.setString(1, time.format(dateFormatter))
            stmt.setString(2, playerName)
            stmt.executeUpdate()
        } ?: plugin.logger.severe("保存下线时间失败：数据库连接为空")
    }

    fun getFirstLoginTime(playerName: String): String? {
        ensureConnection()
        val query = "SELECT player_firstlogin FROM players WHERE player_name = ?"
        return connection?.prepareStatement(query)?.use { stmt ->
            stmt.setString(1, playerName)
            stmt.executeQuery().use { rs ->
                if (rs.next()) rs.getString("player_firstlogin") else null
            }
        } ?: run {
            plugin.logger.severe("查询首次登录时间失败：数据库连接为空")
            null
        }
    }

    fun getLastLogoutDateTime(playerName: String): LocalDateTime? {
        ensureConnection()
        val query = "SELECT player_lastoffline FROM players WHERE player_name = ?"
        return connection?.prepareStatement(query)?.use { stmt ->
            stmt.setString(1, playerName)
            stmt.executeQuery().use { rs ->
                if (rs.next()) {
                    val lastLogoutTime = rs.getString("player_lastoffline")
                    LocalDateTime.parse(lastLogoutTime, dateFormatter)
                } else null
            }
        } ?: run {
            plugin.logger.severe("查询下线时间失败：数据库连接为空")
            null
        }
    }

    fun closeDatabase() {
        try {
            connection?.takeIf { !it.isClosed }?.close()
        } catch (e: SQLException) {
            plugin.logger.severe("关闭数据库失败：${e.message}")
        }
    }

    private fun isConnectionClosed(): Boolean {
        return try {
            connection?.isClosed ?: true
        } catch (e: SQLException) {
            plugin.logger.severe("检查数据库连接状态失败：${e.message}")
            true
        }
    }

    private fun ensureConnection() {
        if (isConnectionClosed()) {
            initializeDatabase()
        }
    }
}
