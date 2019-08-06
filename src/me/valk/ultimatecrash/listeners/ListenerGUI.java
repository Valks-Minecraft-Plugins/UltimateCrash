package me.valk.ultimatecrash.listeners;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import me.valk.ultimatecrash.UltimateCrash;
import me.valk.ultimatecrash.gui.GUI;
import me.valk.ultimatecrash.utils.TextModule;

public class ListenerGUI implements Listener {
	public static List<UUID> playerInput = new ArrayList<UUID>();
	
	@EventHandler
	private void invClickEvent(InventoryClickEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase(UltimateCrash.mainConfig.getString("inventory.home.title"))) {
			Player p = (Player) e.getWhoClicked();
			e.setCancelled(true);
			int playItemSlot = UltimateCrash.mainConfig.getInt("inventory.home.playitem.slot");
			if (e.getSlot() == playItemSlot) {
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
				// Stop animations from home page.
				if (GUI.ids.containsKey(p.getUniqueId())) {
					Bukkit.getScheduler().cancelTask(GUI.ids.get(p.getUniqueId()));
					GUI.ids.remove(p.getUniqueId());
				}
				
				// Close inventory and await input from player.
				playerInput.add(p.getUniqueId());
				p.closeInventory();
				
				int minBet = UltimateCrash.mainConfig.getInt("inventory.game.minBet");
				int maxBet = UltimateCrash.mainConfig.getInt("inventory.game.maxBet");
				
				String msg = UltimateCrash.messagesConfig.getString("messages.command.amount_crash");
				String r1 = msg.replaceAll("%minbet%", String.valueOf(minBet));
				String r2 = r1.replaceAll("%maxbet%", String.valueOf(maxBet));
				
				p.sendMessage(TextModule.color(r2));
			}
		}
		
		if (e.getView().getTitle().equalsIgnoreCase(UltimateCrash.mainConfig.getString("inventory.game.title"))) {
			Player p = (Player) e.getWhoClicked();
			e.setCancelled(true);
			int playItemSlot = UltimateCrash.mainConfig.getInt("inventory.game.playitem.slot");
			int stopItemSlot = UltimateCrash.mainConfig.getInt("inventory.game.stopitem.slot");
			if (e.getSlot() == playItemSlot) {
				GUI.crashStarted.replace(p.getUniqueId(), true);
			}
			if (e.getSlot() == stopItemSlot && GUI.crashStarted.get(p.getUniqueId())) {
				GUI.crashStopped.replace(p.getUniqueId(), true);
			}
		}
	}
	
	/*
	 * Stops the animations when the player exits the inventory.
	 */
	@EventHandler
	private void invCloseEvent(InventoryCloseEvent e) {
		if (e.getView().getTitle().equalsIgnoreCase(UltimateCrash.mainConfig.getString("inventory.home.title"))) {
			Player p = (Player) e.getPlayer();
			
			if (GUI.ids.containsKey(p.getUniqueId())) {
				Bukkit.getScheduler().cancelTask(GUI.ids.get(p.getUniqueId()));
				GUI.ids.remove(p.getUniqueId());
			}
		}
		
		if (e.getView().getTitle().equalsIgnoreCase(UltimateCrash.mainConfig.getString("inventory.game.title"))) {
			Player p = (Player) e.getPlayer();
			
			GUI.crashStopped.replace(p.getUniqueId(), true);
			
			/*if (GUI.gameIDs.containsKey(p.getUniqueId())){
				Bukkit.getScheduler().cancelTask(GUI.gameIDs.get(p.getUniqueId()));
				GUI.gameIDs.remove(p.getUniqueId());
				GUI.crashStopped.remove(p.getUniqueId());
				GUI.crashStarted.remove(p.getUniqueId());
			}*/
		}
	}
	
	/*
	 * Stops the animations if the player leaves the server while the animated inventory is open.
	 */
	@EventHandler
	private void playerLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		
		if (GUI.ids.containsKey(p.getUniqueId())) {
			Bukkit.getScheduler().cancelTask(GUI.ids.get(p.getUniqueId()));
			GUI.ids.remove(p.getUniqueId());
		}
		
		if (GUI.gameIDs.containsKey(p.getUniqueId())){
			Bukkit.getScheduler().cancelTask(GUI.gameIDs.get(p.getUniqueId()));
			GUI.gameIDs.remove(p.getUniqueId());
			GUI.crashStopped.remove(p.getUniqueId());
			GUI.crashStarted.remove(p.getUniqueId());
		}
	}
}
