package io.github.expugn.expugnextras;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * ExpugnExtras - Main Class
 * 
 * @author Expugn
 * https://github.com/Expugn
 * 
 * @version 1.0
 */
public class ExpugnExtras extends JavaPlugin
{
	@Override
	public void onEnable()
	{
		getCommand("expugn").setExecutor(new ExpugnCommand(this));
		getCommand("marriage").setExecutor(new MarriageCommand(this));
		this.saveDefaultConfig();
	}
	
	@Override
	public void onDisable()
	{
		PluginDescriptionFile pdfFile = this.getDescription();
		getLogger().info(pdfFile.getName() + " is disabled.");
		getLogger().info("[WARNING]: Some features in-game may not work correctly!");
		this.saveConfig();
	}
}
