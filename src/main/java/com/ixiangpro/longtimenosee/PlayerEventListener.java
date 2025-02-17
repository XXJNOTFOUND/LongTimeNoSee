package com.ixiangpro.longtimenosee;

import com.ixiangpro.longtimenosee.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.Bukkit;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PlayerEventListener implements Listener {

    private final LongTimeNoSee plugin;
    private final DatabaseManager databaseManager;

    public PlayerEventListener(LongTimeNoSee plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        boolean enableFirstLoginMessage = plugin.getConfig().getBoolean("enable-first-login-message", true);
        boolean enableRejoinMessage = plugin.getConfig().getBoolean("enable-rejoin-message", true);

        // 首次登录
        String firstLoginTime = databaseManager.getFirstLoginTime(playerName);
        if (firstLoginTime == null) {
            if (enableFirstLoginMessage) {
                final String message = plugin.getConfig().getString("first-login-message", "&a欢迎回来，{player}！这是你的首次登录！");
                final String finalMessage = message.replace("{player}", playerName);
                int delay = plugin.getConfig().getInt("delay-seconds", 3);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.sendMessage(MessageUtils.parseColors(finalMessage));
                    }
                }, delay * 20L); // 将秒转换为ticks（1秒=20 ticks）
            }
            databaseManager.saveFirstLoginTime(playerName, LocalDateTime.now());
            return;
        }

        // 再次登录
        LocalDateTime lastLogoutTime = databaseManager.getLastLogoutDateTime(playerName);
        if (lastLogoutTime != null && enableRejoinMessage) {
            LocalDateTime now = LocalDateTime.now();
            java.time.Duration duration = java.time.Duration.between(lastLogoutTime, now); // 优化时间差计算
            long days = duration.toDays();
            long hours = duration.toHoursPart();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();

            String rejoinMessage = plugin.getConfig().getString("rejoin-message",
                    "&b欢迎回来，{player}！你上次在线是 {last_online}，距今 {days} 天 {hours} 小时 {minutes} 分钟 {seconds} 秒。");
            rejoinMessage = rejoinMessage
                    .replace("{player}", playerName)
                    .replace("{last_online}", lastLogoutTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .replace("{days}", String.valueOf(days))
                    .replace("{hours}", String.valueOf(hours))
                    .replace("{minutes}", String.valueOf(minutes))
                    .replace("{seconds}", String.valueOf(seconds));

            player.sendMessage(MessageUtils.parseColors(rejoinMessage));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        databaseManager.saveLogoutTime(player.getName(), LocalDateTime.now());
    }
}
