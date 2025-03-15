package com.ixiangpro.longtimenosee.utils

import net.md_5.bungee.api.ChatColor

object MessageUtils {
    fun String.parseColors(): String {
        return ChatColor.translateAlternateColorCodes('&', this)
    }
}
