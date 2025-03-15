package com.ixiangpro.longtimenosee

import org.bukkit.plugin.java.JavaPlugin

class LongTimeNoSee : JavaPlugin() {

    private lateinit var databaseManager: DatabaseManager

    override fun onEnable() {
        saveDefaultConfig()
        databaseManager = DatabaseManager(this)
        databaseManager.initializeDatabase()

        server.pluginManager.registerEvents(PlayerEventListener(this, databaseManager), this)
        logger.info("LongTimeNoSee 插件已启用！")
    }

    override fun onDisable() {
        if (::databaseManager.isInitialized) {
            databaseManager.closeDatabase()
        }
        logger.info("LongTimeNoSee 插件已禁用！")
    }
}
