package io.github.expugn.expugnextras;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.expugn.commands.ExpugnCommand;
import io.github.expugn.commands.FreeCommand;
import io.github.expugn.commands.MarriageCommand;
import io.github.expugn.listeners.PlayerInteractListener;
import net.milkbowl.vault.economy.Economy;

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
	public static Economy econ = null;
	
	@Override
	public void onEnable()
	{
		getCommand("expugn").setExecutor(new ExpugnCommand(this));
		getLogger().info("'/expugn' command enabled.");
		
		getCommand("expugnfree").setExecutor(new FreeCommand(this));
		getLogger().info("'/expugnfree' command enabled.");
		
		if (!setupEconomy())
		{
			getLogger().info("'/marriage' command disabled.");
			getLogger().info("Vault dependency is not found.");
		}
		else
		{
			getCommand("marriage").setExecutor(new MarriageCommand(this));
			getLogger().info("'/marriage' command enabled.");
			Bukkit.getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
		}
		this.saveDefaultConfig();
		this.reloadConfig();
	}

	private boolean setupEconomy()
	{
		if (getServer().getPluginManager().getPlugin("Vault") == null)
		{
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) 
		{
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
	}
}
