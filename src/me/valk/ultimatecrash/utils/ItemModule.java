package me.valk.ultimatecrash.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class ItemModule {
	public static ItemStack item(String name, String lore, Material material) {
		ItemStack item = new ItemStack(material);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.WHITE + TextModule.color(name));
		im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		List<String> list = new ArrayList<String>();
		for (String element : lore.split("\n")) {
			list.add(TextModule.color(element));
		}
		im.setLore(list);
		item.setItemMeta(im);
		return item;
	}
	
	public static ItemStack itemEnchanted(String name, String lore, Material material) {
		ItemStack item = new ItemStack(material);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(ChatColor.WHITE + TextModule.color(name));
		im.addItemFlags(ItemFlag.values());
		List<String> list = new ArrayList<String>();
		for (String element : lore.split("\n")) {
			list.add(TextModule.color(element));
		}
		im.setLore(list);
		im.addEnchant(Enchantment.LUCK, 1, false);
		item.setItemMeta(im);
		return item;
	}
}
