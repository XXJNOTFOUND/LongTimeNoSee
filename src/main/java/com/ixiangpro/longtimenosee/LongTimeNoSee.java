package com.ixiangpro.longtimenosee;

import org.bukkit.plugin.java.JavaPlugin;

public class LongTimeNoSee extends JavaPlugin {

    private DatabaseManager databaseManager;
    private DependencyDownloader dependencyDownloader;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // 初始化依赖下载器
        dependencyDownloader = new DependencyDownloader(this);

        // 初始化并连接数据库
        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        // 注册事件监听器
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // 注册指令
        if (getCommand("ltns") != null) {
            getCommand("ltns").setExecutor(new CommandManager(this));
        }

        getLogger().info("LongTimeNoSee 插件已启用！作者: iXiangPro");
    }

    @Override
    public void onDisable() {
        // 断开数据库连接
        if (databaseManager != null) {
            databaseManager.disconnect();
        }
        getLogger().info("LongTimeNoSee 插件已卸载！");
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public DependencyDownloader getDependencyDownloader() {
        return dependencyDownloader;
    }

    /** 从 config.yml 读取消息，自动拼接前缀 */
    public String getConfigMessage(String path) {
        String prefix = getConfig().getString("messages.prefix", "§7[§bLongTimeNoSee§7] ");
        String message = getConfig().getString("messages." + path, "");
        return prefix + message;
    }
}
