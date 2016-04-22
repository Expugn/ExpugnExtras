package io.github.expugn.expugnextras;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.expugn.expugnextras.Marriage.MarriageCommand;
import io.github.expugn.expugnextras.Marriage.PlayerInteractListener;
import io.github.expugn.expugnextras.expugn.ExpugnCommand;
import net.milkbowl.vault.economy.Economy;

/**
 * <b>ExpugnExtras</b>
 * 
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 * @version 1.3
 */
public class ExpugnExtras extends JavaPlugin 
{
	public static Economy econ = null;
	private io.github.expugn.expugnextras.Configs.MainConfig config;
	
	private boolean expugnEnabled = false;
	private boolean marriageEnabled = false;

	//-----------------------------------------------------------------------
	/**
	 * Runs whenever the plugin starts up.
	 */
	@Override
	public void onEnable() 
	{
		config = new io.github.expugn.expugnextras.Configs.MainConfig(this);
		
		expugnEnabled = config.getBoolean("expugn");
		marriageEnabled = config.getBoolean("marriage");
		
		setupCommands();
	}

	//-----------------------------------------------------------------------
	/**
	 * Sets up ExpugnExtra commands.
	 */
	private void setupCommands()
	{
		if (expugnEnabled)
			getCommand("expugn").setExecutor(new ExpugnCommand(this));

		if (marriageEnabled)
		{
			if (!setupEconomy()) 
				getLogger().info("'/marriage' command failed to be enabled.");
			else 
			{
				getCommand("marriage").setExecutor(new MarriageCommand(this));
				Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
			}	
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Sets up Vault's economy system.
	 * 
	 * @return  true if economy setup is successful, else false.
	 */
	private boolean setupEconomy() 
	{
		if (getServer().getPluginManager().getPlugin("Vault") == null)
			return false;
		
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		
		if (rsp == null)
			return false;
		
		econ = rsp.getProvider();
		return econ != null;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns a read-only file data.
	 * 
	 * @param file  File name.
	 * @return  File data from data folder.
	 */
	public FileConfiguration readConfig(String file)
	{
		File ymlFile = new File(getDataFolder() + "/" + file + ".yml");
		return YamlConfiguration.loadConfiguration(ymlFile);
	}
}
