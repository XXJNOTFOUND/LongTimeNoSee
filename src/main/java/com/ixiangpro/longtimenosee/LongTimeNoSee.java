package com.ixiangpro.longtimenosee;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class LongTimeNoSee extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();

        // 检测 AuthMe 插件
        if (getServer().getPluginManager().getPlugin("AuthMe") != null) {
            getLogger().info("AuthMe 已检测到，启用 AuthMe 监听器...");
            getServer().getPluginManager().registerEvents(new AuthMeListener(this), this);
        } else {
            getLogger().info("未检测到 AuthMe，启用常规监听器...");
            getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("longtimenosee")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "配置已重新加载！");
                return true;
            }
        }
        return false;
    }
}
