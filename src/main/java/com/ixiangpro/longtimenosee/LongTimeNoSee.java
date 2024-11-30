package com.ixiangpro.longtimenosee;

import org.bukkit.plugin.java.JavaPlugin;

public class LongTimeNoSee extends JavaPlugin {

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        databaseManager = new DatabaseManager(this);
        databaseManager.initializeDatabase();

        // 注册 PlaceholderAPI 扩展
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new LTNCExpansion(this).register();
            getLogger().info("PlaceholderAPI 扩展已注册！");
        } else {
            getLogger().warning("未检测到 PlaceholderAPI，扩展功能不可用！");
        }

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

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
