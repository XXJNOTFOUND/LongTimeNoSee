package com.ixiangpro.longtimenosee;

import org.bukkit.plugin.java.JavaPlugin;

import fr.xephi.authme.api.v3.AuthMeApi;

public class LongTimeNoSee extends JavaPlugin {
    private AuthMeApi authMeApi;
    private boolean authMeEnabled = false;

    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        // 保存默认配置
        saveDefaultConfig();
        
        // 初始化数据库
        databaseManager = new DatabaseManager(this);
        databaseManager.initializeDatabase();

        // 检测AuthMe插件
        if (getServer().getPluginManager().getPlugin("AuthMe") != null) {
            authMeApi = AuthMeApi.getInstance();
            authMeEnabled = true;
            getLogger().info("检测到AuthMe插件，已启用集成支持");
        }

        getServer().getPluginManager().registerEvents(new PlayerEventListener(this, databaseManager, authMeEnabled), this);
        getLogger().info("LongTimeNoSee 插件已启用！");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeDatabase();
        }
        getLogger().info("LongTimeNoSee 插件已禁用！");
    }

    public AuthMeApi getAuthMeApi() {
        return authMeApi;
    }

    public boolean isAuthMeEnabled() {
        return authMeEnabled;
    }

}
