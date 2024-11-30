package com.ixiangpro.longtimenosee;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class LTNCExpansion extends PlaceholderExpansion {

    private final LongTimeNoSee plugin;

    public LTNCExpansion(LongTimeNoSee plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "ltnc";
    }

    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null) return "";

        DatabaseManager db = plugin.getDatabaseManager();

        switch (params.toLowerCase()) {
            case "last_login_time":
                return db.getLastLogoutTime(player.getName());
            case "first_login_time":
                return db.getFirstLoginTime(player.getName());
            default:
                return null;
        }
    }
}
