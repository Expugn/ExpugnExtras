package io.github.expugn.expugnextras;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import io.github.expugn.expugnextras.expugn.ExpugnCommand;
import io.github.expugn.expugnextras.expugn.ListTitles.ListTitles;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

/**
 * <b>ExpugnExtras</b>
 * 
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 * @version 2.1
 */
public class ExpugnExtras extends JavaPlugin 
{
	public static Economy econ = null;
	public static Permission permission = null;
	private io.github.expugn.expugnextras.Configs.MainConfig config;
	
	private boolean expugnEnabled = false;

	//-----------------------------------------------------------------------
	/**
	 * Runs whenever the plugin starts up.
	 */
	@Override
	public void onEnable() 
	{
		config = new io.github.expugn.expugnextras.Configs.MainConfig(this);
		expugnEnabled = config.getBoolean("expugn");
		
		setupCommands();
	}

	//-----------------------------------------------------------------------
	/**
	 * Sets up ExpugnExtra commands.
	 */
	private void setupCommands()
	{
		if (expugnEnabled)
		{	
			getCommand("expugn").setExecutor(new ExpugnCommand(this));
			getCommand("titles").setExecutor(new ListTitles(this));
		}
		else
			getCommand("expugn").setExecutor(new DisabledCommand());

		setupEconomy();
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
}
