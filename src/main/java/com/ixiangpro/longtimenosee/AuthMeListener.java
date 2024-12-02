package com.ixiangpro.longtimenosee;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

public class AuthMeListener implements Listener {
    private final LongTimeNoSee plugin;

    public AuthMeListener(LongTimeNoSee plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAuthMeLogin(LoginEvent event) {
        Player player = event.getPlayer();
        int delay = plugin.getConfig().getInt("message_delay_seconds", 10); // 从配置读取延迟时间
        String message = plugin.getConfig().getString("welcome_message", "欢迎回来！很高兴再次见到您！");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            player.sendMessage(message); // 从配置读取欢迎消息
        }, delay * 20L); // 将秒数转换为 Tick
    }
}
