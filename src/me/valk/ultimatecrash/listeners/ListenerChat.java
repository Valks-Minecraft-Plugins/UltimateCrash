package me.valk.ultimatecrash.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import me.valk.ultimatecrash.UltimateCrash;
import me.valk.ultimatecrash.gui.GUI;
import me.valk.ultimatecrash.utils.TextModule;

public class ListenerChat implements Listener {
	@EventHandler
	private void playerChatEvent(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (ListenerGUI.playerInput.contains(p.getUniqueId())) {
			e.setCancelled(true);
			if (e.getMessage().equalsIgnoreCase("EXIT")) {
				ListenerGUI.playerInput.remove(p.getUniqueId());
				p.sendMessage(TextModule.color(UltimateCrash.messagesConfig.getString("messages.command.input_mode")));
				return;
			}
			
			int amount;
			try {
				amount = Integer.parseInt(e.getMessage());
			} catch (NumberFormatException err) {
				p.sendMessage(TextModule.color(UltimateCrash.messagesConfig.getString("messages.error.invalid_number_exit_input_mode")));
				return;
			}
			
			double playerBalance = UltimateCrash.getEconomy().getBalance(p);
			
			if (playerBalance < amount) {
				p.sendMessage(TextModule.color(UltimateCrash.messagesConfig.getString("messages.error.not_enough_money")));
				return;
			}
			
			int minBet = UltimateCrash.mainConfig.getInt("inventory.game.minBet");
			int maxBet = UltimateCrash.mainConfig.getInt("inventory.game.maxBet");
			
			if (amount < minBet || amount > maxBet) {
				String msg = UltimateCrash.messagesConfig.getString("messages.error.out_of_range");
				String r1 = msg.replaceAll("%amount%", String.valueOf(amount));
				String r2 = r1.replaceAll("%minbet%", String.valueOf(minBet));
				String r3 = r2.replaceAll("%maxbet%", String.valueOf(maxBet));
				p.sendMessage(TextModule.color(r3));
				return;
			}

			new BukkitRunnable() {
				@Override
				public void run() {
					ListenerGUI.playerInput.remove(p.getUniqueId());
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
					p.openInventory(GUI.invCrashGame(p, amount));
				}
				
			}.runTaskLater(JavaPlugin.getPlugin(UltimateCrash.class), 1);
		}
	}
}
