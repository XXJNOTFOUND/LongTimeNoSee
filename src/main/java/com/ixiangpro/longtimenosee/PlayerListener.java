package com.ixiangpro.longtimenosee;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.TimeUnit;

public class PlayerListener implements Listener {
    private final LongTimeNoSee plugin;

    public PlayerListener(LongTimeNoSee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String uuid = player.getUniqueId().toString();

        long lastOffline = plugin.getDatabaseManager().getLastOffline(uuid);

        if (lastOffline == -1) {
            // 首次登录
            player.sendMessage(plugin.getConfigMessage("first-join"));
        } else {
            // 计算时间差
            long now = System.currentTimeMillis();
            long diff = now - lastOffline;

            long days = TimeUnit.MILLISECONDS.toDays(diff);
            diff -= TimeUnit.DAYS.toMillis(days);
            long hours = TimeUnit.MILLISECONDS.toHours(diff);
            diff -= TimeUnit.HOURS.toMillis(hours);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            diff -= TimeUnit.MINUTES.toMillis(minutes);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

            String msg = plugin.getConfigMessage("return-join")
                    .replace("%days%", String.valueOf(days))
                    .replace("%hours%", String.valueOf(hours))
                    .replace("%minutes%", String.valueOf(minutes))
                    .replace("%seconds%", String.valueOf(seconds));
            player.sendMessage(msg);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        // 记录离线时间
        plugin.getDatabaseManager().setLastOffline(
                player.getUniqueId().toString(),
                player.getName(),
                System.currentTimeMillis()
        );
    }
}
