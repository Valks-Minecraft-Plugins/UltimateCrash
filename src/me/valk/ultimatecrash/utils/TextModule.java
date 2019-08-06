package me.valk.ultimatecrash.utils;

import org.bukkit.ChatColor;

public class TextModule {
	public static String color(String message) {
		return ChatColor.translateAlternateColorCodes('&', message);
	}
}
