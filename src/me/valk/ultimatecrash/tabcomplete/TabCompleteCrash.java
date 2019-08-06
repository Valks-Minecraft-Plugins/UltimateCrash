package me.valk.ultimatecrash.tabcomplete;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TabCompleteCrash implements TabCompleter {
	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("crash") && args.length == 1) {
			List<String> list = new ArrayList<String>();
			list.add("reload");
			list.add("author");
			list.add("version");
			return list;
		}
		
		if (command.getName().equalsIgnoreCase("crash") && args.length == 2 && args[0].equalsIgnoreCase("reload")) {
			List<String> list = new ArrayList<String>();
			list.add("main");
			list.add("messages");
			list.add("all");
			return list;
		}
		return null;
	}

}
