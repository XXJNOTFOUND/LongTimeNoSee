package com.ixiangpro.longtimenosee;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public class LTNSPlaceholder extends PlaceholderExpansion {

    private final LongTimeNoSee plugin;

    public LTNSPlaceholder(LongTimeNoSee plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "longtimenosee";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "iXiangPro";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true; // 插件重载时不注销
    }

    @Override
    @Nullable
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        String uuid = player.getUniqueId().toString();
        long lastOffline = plugin.getDatabaseManager().getLastOffline(uuid);

        if (lastOffline == -1) {
            // 没有记录
            switch (params.toLowerCase()) {
                case "first_join":
                    return "true";
                case "last_offline_formatted":
                    return "§7从未离线";
                default:
                    return "";
            }
        }

        long now = System.currentTimeMillis();
        long diff = now - lastOffline;

        long days = TimeUnit.MILLISECONDS.toDays(diff);
        diff -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(diff);
        diff -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
        diff -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(diff);

        switch (params.toLowerCase()) {
            case "days":
                return String.valueOf(days);
            case "hours":
                return String.valueOf(hours);
            case "minutes":
                return String.valueOf(minutes);
            case "seconds":
                return String.valueOf(seconds);
            case "total_seconds":
                return String.valueOf((now - lastOffline) / 1000);
            case "last_offline_formatted":
                return days + "天" + hours + "小时" + minutes + "分" + seconds + "秒前";
            case "last_offline_timestamp":
                return String.valueOf(lastOffline);
            case "first_join":
                return "false";
            default:
                return "";
        }
    }
}
