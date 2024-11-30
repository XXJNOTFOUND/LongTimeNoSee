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
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
                String message = plugin.getConfig().getString("first-login-message", "&a欢迎回来，{player}！这是你的首次登录！");
                message = message.replace("{player}", playerName);
                player.sendMessage(MessageUtils.parseColors(message));
            }
            databaseManager.saveFirstLoginTime(playerName, LocalDateTime.now());
            return;
        }

        // 再次登录
        String lastLogoutTime = databaseManager.getLastLogoutTime(playerName);
        if (lastLogoutTime != null && enableRejoinMessage) {
            String rejoinMessage = plugin.getConfig().getString("rejoin-message", "&b欢迎回来，{player}！你上次在线是 {last_online}");
            rejoinMessage = rejoinMessage.replace("{player}", playerName).replace("{last_online}", lastLogoutTime);
            player.sendMessage(MessageUtils.parseColors(rejoinMessage));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        databaseManager.saveLogoutTime(player.getName(), LocalDateTime.now());
    }
}
