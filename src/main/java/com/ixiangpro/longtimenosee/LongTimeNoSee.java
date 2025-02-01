package com.ixiangpro.longtimenosee;

import org.bukkit.plugin.java.JavaPlugin;

public class LongTimeNoSee extends JavaPlugin {

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        databaseManager = new DatabaseManager(this);
        databaseManager.initializeDatabase();

        getServer().getPluginManager().registerEvents(new PlayerEventListener(this, databaseManager), this);
        getLogger().info("LongTimeNoSee 插件已启用！");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeDatabase();
        }
        getLogger().info("LongTimeNoSee 插件已禁用！");
    }

}