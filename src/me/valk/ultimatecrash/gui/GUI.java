package me.valk.ultimatecrash.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import me.valk.ultimatecrash.UltimateCrash;
import me.valk.ultimatecrash.configs.ConfigItem;
import me.valk.ultimatecrash.utils.TextModule;

public class GUI {
	public static Map<UUID, Integer> ids = new HashMap<UUID, Integer>();
	public static Map<UUID, Integer> gameIDs = new HashMap<UUID, Integer>();

	public static Map<UUID, Boolean> crashStarted = new HashMap<UUID, Boolean>();
	public static Map<UUID, Boolean> crashStopped = new HashMap<UUID, Boolean>();

	private static int gameID = 0;

	private static final int GAME_SPEED = UltimateCrash.mainConfig.getInt("inventory.game.gameSpeed");

	public static Inventory invHomePage(Player p) {
		Inventory inv = Bukkit.createInventory(p, UltimateCrash.mainConfig.getInt("inventory.home.rows") * 9,
				UltimateCrash.mainConfig.getString("inventory.home.title"));
		ConfigItem item = new ConfigItem(UltimateCrash.mainCM);
		inv.setItem(UltimateCrash.mainConfig.getInt("inventory.home.playitem.slot"),
				item.get("inventory.home.playitem"));
		animatedBorder(inv, p);
		return inv;
	}

	private static void stopGameAnim(Player p) {
		Bukkit.getScheduler().cancelTask(GUI.gameIDs.get(p.getUniqueId()));
		gameIDs.remove(p.getUniqueId());
		crashStarted.remove(p.getUniqueId());
		crashStopped.remove(p.getUniqueId());
	}

	public static Inventory invCrashGame(Player p, int initialAmount) {
		Inventory inv = Bukkit.createInventory(p, UltimateCrash.mainConfig.getInt("inventory.game.rows") * 9,
				UltimateCrash.mainConfig.getString("inventory.game.title"));
		ConfigItem configItem = new ConfigItem(UltimateCrash.mainCM);
		ItemStack amountItem = configItem.get("inventory.game.amountitem");

		crashStarted.put(p.getUniqueId(), false);
		crashStopped.put(p.getUniqueId(), false);

		gameID = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(UltimateCrash.class),
				new Runnable() {
					double baseMultiplier = UltimateCrash.mainConfig.getDouble("inventory.game.baseMultiplier");
					final double PER_ADD_AMOUNT = UltimateCrash.mainConfig.getDouble("inventory.game.perAddAmount");
					final double CRASH_CHANCE = UltimateCrash.mainConfig
							.getDouble("inventory.game.startingCrashChance");
					double crashAdder = 0;
					final double CRASH_ADD_AMOUNT = UltimateCrash.mainConfig
							.getDouble("inventory.game.crashChanceAddPerFrame");

					@Override
					public void run() {
						int money = (int) (initialAmount * baseMultiplier);
						if (crashStopped.get(p.getUniqueId())) {
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BANJO, 1.0f, 1.0f);

							String msg = UltimateCrash.messagesConfig.getString("messages.message.reward");
							String r1 = msg.replaceAll("%amount%", String.valueOf(money - initialAmount));
							String r2 = r1.replaceAll("%multiplier%",
									String.valueOf((Math.round(baseMultiplier * 100.0)) / 100.0));

							p.sendMessage(TextModule.color(r2));
							UltimateCrash.getEconomy().depositPlayer(p, money - initialAmount);
							p.closeInventory();
							
							stopGameAnim(p);
							return;
						}

						if (crashStarted.get(p.getUniqueId())) {
							p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BIT, 1.0f, 1.0f);

							baseMultiplier += PER_ADD_AMOUNT;
							crashAdder += CRASH_ADD_AMOUNT;
							if (Math.random() < CRASH_CHANCE + crashAdder) {
								p.closeInventory();
								stopGameAnim(p);
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 1.0f);

								String msg = UltimateCrash.messagesConfig.getString("messages.message.lost");
								String r1 = msg.replaceAll("%amount%", String.valueOf(initialAmount));
								String r2 = r1.replaceAll("%multiplier%",
										String.valueOf((Math.round(baseMultiplier * 100.0)) / 100.0));

								p.sendMessage(TextModule.color(r2));
								UltimateCrash.getEconomy().withdrawPlayer(p, initialAmount);
							}
						}

						ItemStack item = amountItem;
						ItemMeta im = item.getItemMeta();
						List<String> lore = new ArrayList<String>();

						double multiplier = Math.round(baseMultiplier * 100.0) / 100.0;

						String msg = UltimateCrash.messagesConfig.getString("messages.gui.displayitem.lore");
						String r1 = msg.replaceAll("%amount%", String.valueOf(money));
						String r2 = r1.replaceAll("%multiplier%", String.valueOf(multiplier));

						// lore.add(TextModule.color("&a$&7: &a" + money + " &7x" + multiplier));
						lore.add(TextModule.color(r2));
						im.setLore(lore);
						item.setItemMeta(im);

						inv.setItem(UltimateCrash.mainConfig.getInt("inventory.game.amountitem.slot"), item);
					}
				}, 0, GAME_SPEED);

		gameIDs.put(p.getUniqueId(), gameID);

		inv.setItem(UltimateCrash.mainConfig.getInt("inventory.game.playitem.slot"),
				configItem.get("inventory.game.playitem"));
		inv.setItem(UltimateCrash.mainConfig.getInt("inventory.game.stopitem.slot"),
				configItem.get("inventory.game.stopitem"));
		return inv;
	}

	private static void animatedBorder(Inventory inv, Player p) {
		int animationDelay = UltimateCrash.mainConfig.getInt("inventory.home.animation.delay");

		ConfigurationSection configSection = UltimateCrash.mainConfig
				.getConfigurationSection("inventory.home.animation.items");

		List<ItemStack> glassPanes = new ArrayList<ItemStack>();

		for (String element : configSection.getKeys(false)) {
			ConfigItem configItem = new ConfigItem(UltimateCrash.mainCM);
			ItemStack item = configItem.get("inventory.home.animation.items." + element);
			glassPanes.add(item);
		}

		int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(UltimateCrash.class),
				new Runnable() {
					int i = 0;

					@Override
					public void run() {
						inv.setItem(0, glassPanes.get(i));
						inv.setItem(1, glassPanes.get(i));
						inv.setItem(2, glassPanes.get(i));
						inv.setItem(3, glassPanes.get(i));
						inv.setItem(4, glassPanes.get(i));
						inv.setItem(5, glassPanes.get(i));
						inv.setItem(6, glassPanes.get(i));
						inv.setItem(7, glassPanes.get(i));
						inv.setItem(8, glassPanes.get(i));
						inv.setItem(9, glassPanes.get(i));
						inv.setItem(17, glassPanes.get(i));
						inv.setItem(18, glassPanes.get(i));
						inv.setItem(26, glassPanes.get(i));
						inv.setItem(27, glassPanes.get(i));
						inv.setItem(35, glassPanes.get(i));
						inv.setItem(36, glassPanes.get(i));
						inv.setItem(37, glassPanes.get(i));
						inv.setItem(38, glassPanes.get(i));
						inv.setItem(39, glassPanes.get(i));
						inv.setItem(40, glassPanes.get(i));
						inv.setItem(41, glassPanes.get(i));
						inv.setItem(42, glassPanes.get(i));
						inv.setItem(43, glassPanes.get(i));
						inv.setItem(44, glassPanes.get(i));
						i = (i + 1) % glassPanes.size();
					}
				}, animationDelay, animationDelay);

		ids.put(p.getUniqueId(), id);
	}
}
