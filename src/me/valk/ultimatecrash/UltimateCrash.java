package me.valk.ultimatecrash;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.valk.ultimatecrash.cmds.CmdCrash;
import me.valk.ultimatecrash.configs.ConfigItem;
import me.valk.ultimatecrash.configs.ConfigManager;
import me.valk.ultimatecrash.listeners.ListenerChat;
import me.valk.ultimatecrash.listeners.ListenerGUI;
import me.valk.ultimatecrash.tabcomplete.TabCompleteCrash;
import me.valk.ultimatecrash.utils.ItemModule;
import net.milkbowl.vault.economy.Economy;

public class UltimateCrash extends JavaPlugin {
	public static Economy economy = null;
	
	public static ConfigManager mainCM;
	public static YamlConfiguration mainConfig;
	
	public static ConfigManager messagesCM;
	public static YamlConfiguration messagesConfig;
	
	public static File pluginFolder;
	
	@Override
	public void onEnable() {
		pluginFolder = getDataFolder();
		
		mainCM = new ConfigManager("config");
		mainConfig = mainCM.getConfig();
		
		messagesCM = new ConfigManager("messages");
		messagesConfig = messagesCM.getConfig();
		
		registerListeners(getServer().getPluginManager());
		registerCommands();
		initMainConfig();
		initMessagesConfig();
		setupEconomy();
	}
	
	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }
	
	public static Economy getEconomy() {
        return economy;
    }
	
	private void initMessagesConfig() {
		defaultSet(messagesConfig, "messages.error.console", "Only a in-game players can execute this.");
		defaultSet(messagesConfig, "messages.error.no_permission", "&cYou lack the permissions to do that. Contact an administrator if you feel this is an error.");
		defaultSet(messagesConfig, "messages.error.invalid_number_exit_input_mode", "&cPlease enter a valid number! Type EXIT to leave this mode.");
		defaultSet(messagesConfig, "messages.error.invalid_config_name", "&cInvalid config name. Valid configs: [messages | main | all]");
		defaultSet(messagesConfig, "messages.error.specify_which_config", "&cSpecify a config to reload. [messages | main | all]");
		defaultSet(messagesConfig, "messages.error.not_enough_money", "&cYou do not have enough money to do that! Type EXIT to leave this mode.");
		defaultSet(messagesConfig, "messages.error.out_of_range", "&cThe amount (%amount%) you entered is out of range! Minbet: %minbet%. Maxbet: %maxbet%. Type EXIT to leave this mode.");
		
		defaultSet(messagesConfig, "messages.command.input_mode", "&7Exited input mode.");
		defaultSet(messagesConfig, "messages.command.amount_crash", "&7Enter an amount! &8(&7Min: &a$%minbet% &7Max: &a%maxbet%&8)");
		defaultSet(messagesConfig, "messages.command.reload_main_config", "&2Sucessfully reloaded main config.");
		defaultSet(messagesConfig, "messages.command.reload_messages_config", "&2Sucessfully reloaded messages config.");
		defaultSet(messagesConfig, "messages.command.reload_all_configs", "&2Sucessfully reloaded all configs.");
		
		defaultSet(messagesConfig, "messages.message.reward", "&2You got &a%amount% &2back with a %multiplier%x multiplier!");
		defaultSet(messagesConfig, "messages.message.lost", "&cYou crashed at %multiplier%x and lost %amount%!");
		
		defaultSet(messagesConfig, "messages.gui.displayitem.lore", "&a$&7: &a%amount% &7x%multiplier%");
		
		messagesCM.saveConfig();
	}
	
	private void initMainConfig() {
		ConfigItem configItem = new ConfigItem(mainCM);
		
		defaultSet(mainConfig, "usersRequirePermToUseCrash", false);
		
		// GUI Home
		defaultSet(mainConfig, "inventory.home.title", "Crash Home");
		defaultSet(mainConfig, "inventory.home.rows", 5);
		if (!mainConfig.isSet("inventory.home.playitem")) {
			configItem.set("inventory.home.playitem", ItemModule.item("&aPlay Crash", "&7Welcome to &cCrash&7!\n\n&eClick here to get started!", Material.NETHER_STAR));
		}
		defaultSet(mainConfig, "inventory.home.playitem.slot", 22);
		defaultSet(mainConfig, "inventory.home.animation.delay", 5);
		
		if (!mainConfig.isSet("inventory.home.animation.items")) {
			List<String> list = new ArrayList<String>();
			mainConfig.set("inventory.home.animation.items", list);
			
			configItem.set("inventory.home.animation.items.1", ItemModule.itemEnchanted("", "", Material.BLACK_STAINED_GLASS_PANE));
			configItem.set("inventory.home.animation.items.2", ItemModule.item("", "", Material.GREEN_STAINED_GLASS_PANE));
			configItem.set("inventory.home.animation.items.3", ItemModule.item("", "", Material.LIME_STAINED_GLASS_PANE));
			configItem.set("inventory.home.animation.items.4", ItemModule.item("", "", Material.PURPLE_STAINED_GLASS_PANE));
			configItem.set("inventory.home.animation.items.5", ItemModule.item("", "", Material.WHITE_STAINED_GLASS_PANE));
		}
		
		// GUI Game
		defaultSet(mainConfig, "inventory.game.title", "Crash Game");
		defaultSet(mainConfig, "inventory.game.rows", 5);
		if (!mainConfig.isSet("inventory.game.playitem")) {
			configItem.set("inventory.game.playitem", ItemModule.item("&fPlay", "&7Press to Play", Material.LIME_STAINED_GLASS_PANE));
		}
		defaultSet(mainConfig, "inventory.game.playitem.slot", 21);
		if (!mainConfig.isSet("inventory.game.stopitem")) {
			configItem.set("inventory.game.stopitem", ItemModule.item("&fStop", "&7Press to Stop", Material.BLACK_STAINED_GLASS_PANE));
		}
		defaultSet(mainConfig, "inventory.game.stopitem.slot", 23);
		if (!mainConfig.isSet("inventory.game.amountitem")) {
			configItem.set("inventory.game.amountitem", ItemModule.item("&2$", "", Material.GOLD_INGOT));
		}
		defaultSet(mainConfig, "inventory.game.amountitem.slot", 22);
		defaultSet(mainConfig, "inventory.game.gameSpeed", 2);
		defaultSet(mainConfig, "inventory.game.baseMultiplier", 1.00);
		defaultSet(mainConfig, "inventory.game.perAddAmount", 0.01);
		defaultSet(mainConfig, "inventory.game.startingCrashChance", 0.0001);
		defaultSet(mainConfig, "inventory.game.crashChanceAddPerFrame", 0.0003);
		defaultSet(mainConfig, "inventory.game.minBet", 1);
		defaultSet(mainConfig, "inventory.game.maxBet", 100);
		mainCM.saveConfig();
	}
	
	private void defaultSet(YamlConfiguration config, String path, Object value) {
		if (!config.isSet(path)) {
			config.set(path, value);
		}
	}
	
	private void registerListeners(PluginManager pm) {
		pm.registerEvents(new ListenerGUI(), this);
		pm.registerEvents(new ListenerChat(), this);
	}
	
	private void registerCommands() {
		getCommand("crash").setExecutor(new CmdCrash());
		getCommand("crash").setTabCompleter(new TabCompleteCrash());
	}
}
