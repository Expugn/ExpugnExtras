package io.github.expugn.expugnextras;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.expugn.commands.ExpugnCommand;
import io.github.expugn.commands.FreeCommand;
import io.github.expugn.commands.MarriageCommand;
import io.github.expugn.listeners.PlayerInteractListener;
import net.milkbowl.vault.economy.Economy;

/**
 * <b>ExpugnExtras</b>
 * 
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 * @version 1.0
 */
public class ExpugnExtras extends JavaPlugin 
{
	public static Economy econ = null;

	//-----------------------------------------------------------------------
	/**
	 * onEnable: Runs when the plugin starts up.
	 * <p><ul>
	 * <li> Sets up ExpugnCommand {@link ExpugnCommand}.
	 * <li> Sets up FreeCommand {@link FreeCommand}.
	 * <li> Sets up MarriageCommand {@link MarriageCommand}.
	 * <li> Links to a method named 'setupEconomy' on this class: {@link #setupEconomy}.
	 * </ul><p>
	 */
	@Override
	public void onEnable() 
	{
		getCommand("expugn").setExecutor(new ExpugnCommand(this));
		getLogger().info("'/expugn' command enabled.");

		getCommand("expugnfree").setExecutor(new FreeCommand(this));
		getLogger().info("'/expugnfree' command enabled.");

		if (!setupEconomy()) 
			getLogger().info("'/marriage' command failed to be enabled.");
		else 
		{
			getCommand("marriage").setExecutor(new MarriageCommand(this));
			Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
			
			getLogger().info("'/marriage' command enabled.");
		}
		this.saveDefaultConfig();
		this.reloadConfig();
	}

	//-----------------------------------------------------------------------
	/**
	 * setupEconomy: Sets up Vault's economy system.
	 * 
	 * @return 
	 * 		<li> true: economy setup is successful
	 * 		<li> false: economy setup has failed
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
	
	public FileConfiguration readConfig(String file)
	{
		File ymlFile = new File(getDataFolder() + "/" + file + ".yml");
		return YamlConfiguration.loadConfiguration(ymlFile);
	}
}
