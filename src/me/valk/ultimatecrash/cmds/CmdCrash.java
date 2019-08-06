package me.valk.ultimatecrash.cmds;

import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import me.valk.ultimatecrash.UltimateCrash;
import me.valk.ultimatecrash.gui.GUI;
import me.valk.ultimatecrash.utils.TextModule;

public class CmdCrash implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("crash")) {
			if (!sender.hasPermission("ultimatecrash.crash") && UltimateCrash.mainConfig.getBoolean("usersRequirePermToUseCrash")) {
				sender.sendMessage(TextModule.color(UltimateCrash.messagesConfig.getString("messages.error.no_permission")));
				return true;
			}
			
			if (args.length < 1) {
				if (sender instanceof ConsoleCommandSender) {
					sender.sendMessage(TextModule.color(UltimateCrash.messagesConfig.getString("messages.error.console")));
					return true;
				}
				
				Player p = (Player) sender;
				p.openInventory(GUI.invHomePage(p));
				
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
			}
			
			if (args.length >= 1) {
				if (args[0].equalsIgnoreCase("reload")) {
					if (!sender.hasPermission("ultimatecrash.reload")) {
						sender.sendMessage(TextModule.color(UltimateCrash.messagesConfig.getString("messages.error.no_permission")));
						return true;
					}
					
					if (args.length < 2) {
						sender.sendMessage(TextModule.color(UltimateCrash.messagesConfig.getString("messages.error.specify_which_config")));
						return true;
					}
					
					switch (args[1]) {
					case "main":
						UltimateCrash.mainCM.reloadConfig();
						sender.sendMessage(TextModule.color(UltimateCrash.messagesConfig.getString("messages.command.reload_main_config")));
						break;
					case "messages":
						UltimateCrash.messagesCM.reloadConfig();
						sender.sendMessage(TextModule.color(UltimateCrash.messagesConfig.getString("messages.command.reload_messages_config")));
						break;
					case "all":
						UltimateCrash.mainCM.reloadConfig();
						UltimateCrash.messagesCM.reloadConfig();
						sender.sendMessage(TextModule.color(UltimateCrash.messagesConfig.getString("messages.command.reload_all_configs")));
						break;
					default:
						sender.sendMessage(TextModule.color(UltimateCrash.messagesConfig.getString("messages.error.invalid_config_name")));
						break;
					}
					return true;
				} else if (args[0].equalsIgnoreCase("author")) {
					sender.sendMessage(TextModule.color("&7Created by valkyrienyanko. Contact support for this plugin via discord.noventastudios.com"));
					return true;
				} else if (args[0].equalsIgnoreCase("version")) {
					sender.sendMessage(TextModule.color("&7Version: 1.0.0"));
					return true;
				}
			}
		}
		return true;
	}
}
