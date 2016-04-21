package io.github.expugn.expugnextras;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.expugn.expugnextras.commands.ConsoleCommand;
import io.github.expugn.expugnextras.commands.ExpugnCommand;
import io.github.expugn.expugnextras.commands.FreeCommand;
import io.github.expugn.expugnextras.commands.GollemCommand;
import io.github.expugn.expugnextras.commands.MarriageCommand;
import io.github.expugn.expugnextras.listeners.PlayerInteractListener;
import net.milkbowl.vault.economy.Economy;

/**
 * <b>ExpugnExtras</b>
 * 
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 * @version 1.2
 */
public class ExpugnExtras extends JavaPlugin 
{
	public static Economy econ = null;

	//-----------------------------------------------------------------------
	/**
	 * {@code onEnable}: Runs when the plugin starts up.
	 * <ul>
	 * <li> Links to a method named 'setupCommands' on this class: {@link #setupCommands}.
	 * </ul>
	 */
	@Override
	public void onEnable() 
	{
		setupCommands();
		
		this.saveDefaultConfig();
		this.reloadConfig();
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code setupCommands}: Setup ExpugnExtra commands.
	 * <ul>
	 * <li> Sets up ExpugnCommand {@link ExpugnCommand}.
	 * <li> Sets up FreeCommand {@link FreeCommand}.
	 * <li> Sets up MarriageCommand {@link MarriageCommand}.
	 * <li> Sets up GollemCommand {@link GollemCommand}.
	 * <li> Links to a method named 'setupEconomy' on this class: {@link #setupEconomy}.
	 * </ul>
	 */
	private void setupCommands()
	{
		getCommand("expugn").setExecutor(new ExpugnCommand(this));
		getCommand("expugnfree").setExecutor(new FreeCommand(this));
		getCommand("expugnconsole").setExecutor(new ConsoleCommand(this));
		
		if (!setupEconomy()) 
			getLogger().info("'/marriage' command failed to be enabled.");
		else 
		{
			getCommand("marriage").setExecutor(new MarriageCommand(this));
			Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
		}		
		
		getCommand("gollem").setExecutor(new GollemCommand(this));
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code setupEconomy}: Sets up Vault's economy system.
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
	
	//-----------------------------------------------------------------------
	/**
	 * {@code readConfig}: Returns a read-only file data.
	 * 
	 * @param file  File name
	 * @return  File data from data folder
	 */
	public FileConfiguration readConfig(String file)
	{
		File ymlFile = new File(getDataFolder() + "/" + file + ".yml");
		return YamlConfiguration.loadConfiguration(ymlFile);
	}
}
