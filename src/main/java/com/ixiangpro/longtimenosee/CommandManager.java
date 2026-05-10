package com.ixiangpro.longtimenosee;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

public class CommandManager implements CommandExecutor {
    private final LongTimeNoSee plugin;

    public CommandManager(LongTimeNoSee plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getConfigMessage("check-usage"));
            return true;
        }

        // 重载指令
        if (args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("ltns.admin")) {
                sender.sendMessage(plugin.getConfigMessage("no-permission"));
                return true;
            }

            // 重载配置并重新连接数据库
            plugin.reloadConfig();
            plugin.getDatabaseManager().disconnect();
            plugin.getDatabaseManager().connect();

            sender.sendMessage(plugin.getConfigMessage("reloaded"));
            return true;
        }

        // 查询指令
        if (args[0].equalsIgnoreCase("check")) {
            if (!sender.hasPermission("ltns.check")) {
                sender.sendMessage(plugin.getConfigMessage("no-permission"));
                return true;
            }
            if (args.length < 2) {
                sender.sendMessage(plugin.getConfigMessage("check-usage"));
                return true;
            }

            String targetName = args[1];
            long lastOffline = plugin.getDatabaseManager().getLastOfflineByName(targetName);

            if (lastOffline == -1) {
                sender.sendMessage(plugin.getConfigMessage("player-not-found"));
            } else {
                long now = System.currentTimeMillis();
                long diff = now - lastOffline;

                long days = TimeUnit.MILLISECONDS.toDays(diff);
                diff -= TimeUnit.DAYS.toMillis(days);
                long hours = TimeUnit.MILLISECONDS.toHours(diff);
                diff -= TimeUnit.HOURS.toMillis(hours);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                diff -= TimeUnit.MINUTES.toMillis(minutes);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

                String timeStr = String.format("%d天 %d小时 %d分钟 %d秒", days, hours, minutes, seconds);
                String msg = plugin.getConfigMessage("check-result")
                        .replace("%player%", targetName)
                        .replace("%time%", timeStr);
                sender.sendMessage(msg);
            }
            return true;
        }

        sender.sendMessage("§c未知参数，用法: /ltns <reload|check>");
        return true;
    }
}
