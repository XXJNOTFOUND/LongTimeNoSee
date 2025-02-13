package com.ixiangpro.longtimenosee;

import com.ixiangpro.longtimenosee.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PlayerEventListener implements Listener {
    private final LongTimeNoSee plugin;
    private final DatabaseManager databaseManager;
    private final boolean authMeEnabled;

    public PlayerEventListener(LongTimeNoSee plugin, DatabaseManager databaseManager, boolean authMeEnabled) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.authMeEnabled = authMeEnabled;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        int delaySeconds = plugin.getConfig().getInt("message-delay", 5);

        // 创建延迟任务
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            // 如果启用AuthMe且玩家未登录则跳过
            if (authMeEnabled && !plugin.authMeApi.isAuthenticated(player)) {
                return;
            }

            boolean enableFirstLoginMessage = plugin.getConfig().getBoolean("enable-first-login-message", true);
            boolean enableRejoinMessage = plugin.getConfig().getBoolean("enable-rejoin-message", true);

            // 首次登录
            String firstLoginTime = databaseManager.getFirstLoginTime(playerName);
            if (firstLoginTime == null) {
                if (enableFirstLoginMessage) {
                    String message = plugin.getConfig().getString("first-login-message", "&a欢迎回来，{player}！这是你的首次登录！");
                    message = message.replace("{player}", playerName);
                    player.sendMessage(MessageUtils.parseColors(message));
                }
                databaseManager.saveFirstLoginTime(playerName, LocalDateTime.now());
                return;
            }

            // 再次登录
            LocalDateTime lastLogoutTime = databaseManager.getLastLogoutDateTime(playerName);
            if (lastLogoutTime != null && enableRejoinMessage) {
                LocalDateTime now = LocalDateTime.now();
                java.time.Duration duration = java.time.Duration.between(lastLogoutTime, now);
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
        }, delaySeconds * 20L); // 添加闭合括号和延迟参数
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        databaseManager.saveLogoutTime(player.getName(), LocalDateTime.now());
    }
}
