package com.ixiangpro.longtimenosee.utils;

import net.md_5.bungee.api.ChatColor;

public class MessageUtils {

    public static String parseColors(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
