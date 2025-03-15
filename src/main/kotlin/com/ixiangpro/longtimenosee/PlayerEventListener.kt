package com.ixiangpro.longtimenosee

import com.ixiangpro.longtimenosee.utils.MessageUtils
import com.ixiangpro.longtimenosee.utils.MessageUtils.parseColors
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.scheduler.BukkitRunnable
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PlayerEventListener(
    private val plugin: LongTimeNoSee,
    private val databaseManager: DatabaseManager
) : Listener {

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerName = player.name

        val enableFirstLoginMessage = plugin.config.getBoolean("enable-first-login-message", true)
        val enableRejoinMessage = plugin.config.getBoolean("enable-rejoin-message", true)

        // 首次登录
        val firstLoginTime = databaseManager.getFirstLoginTime(playerName)
        if (firstLoginTime == null) {
            if (enableFirstLoginMessage) {
                val message = plugin.config.getString("first-login-message", "&a欢迎回来，{player}！这是你的首次登录！")
                val finalMessage = message?.replace("{player}", playerName) ?: ""
                val delay = plugin.config.getInt("delay-seconds", 3)
                
                object : BukkitRunnable() {
                    override fun run() {
                        player.sendMessage(finalMessage.parseColors())
                    }
                }.runTaskLater(plugin, delay * 20L)
            }
            databaseManager.saveFirstLoginTime(playerName, LocalDateTime.now())
            return
        }

        // 再次登录
        val lastLogoutTime = databaseManager.getLastLogoutDateTime(playerName)
        if (lastLogoutTime != null && enableRejoinMessage) {
            val now = LocalDateTime.now()
            val duration = Duration.between(lastLogoutTime, now)
            val days = duration.toDays()
            val hours = duration.toHoursPart()
            val minutes = duration.toMinutesPart()
            val seconds = duration.toSecondsPart()

            val rejoinMessage = plugin.config.getString("rejoin-message",
                "&b欢迎回来，{player}！你上次在线是 {last_online}，距今 {days} 天 {hours} 小时 {minutes} 分钟 {seconds} 秒。")
                ?.replace("{player}", playerName)
                ?.replace("{last_online}", lastLogoutTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                ?.replace("{days}", days.toString())
                ?.replace("{hours}", hours.toString())
                ?.replace("{minutes}", minutes.toString())
                ?.replace("{seconds}", seconds.toString())
                ?: ""
            
            val delay = plugin.config.getInt("delay-seconds", 3)
            object : BukkitRunnable() {
                override fun run() {
                    player.sendMessage(rejoinMessage.parseColors())
                }
            }.runTaskLater(plugin, delay * 20L)
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        databaseManager.saveLogoutTime(event.player.name, LocalDateTime.now())
    }
}
