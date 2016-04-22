package io.github.expugn.expugnextras.expugn.Warps;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'Warps' Function</b>
 * 
 * @version 2.0
 * @author Expugn <i>(https://github.com/Expugn)</i>
 */
public class Warps 
{
	private final io.github.expugn.expugnextras.Configs.Warps config;

	//-----------------------------------------------------------------------
	/**
	 * Constructor for the Warps class
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public Warps(ExpugnExtras plugin) 
	{
		config = new io.github.expugn.expugnextras.Configs.Warps(plugin);
	}

	//-----------------------------------------------------------------------
	/**
	 * Teleports a player to a warp's location.
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the warp.
	 */
	public void warp(Player player, String name) 
	{
		if (!config.checkWarp(name)) 
		{
			player.sendMessage("§cInvalid warp. Use §6/expugn warplist §cfor a list of warps.");
			return;
		}
		
		int value = config.getWarp_Value(name);
		UUID playerUUID = player.getUniqueId();
		
		if (value != 0) 
		{
			switch (config.getWarp_Type(name)) 
			{
				case "cooldown":
					long canUseAgain = config.getData_Cooldown(name, playerUUID);
	
					if (canUseAgain >= System.currentTimeMillis()) 
					{
						player.sendMessage("§cYou cannot use this warp again just yet.");
						convert(player, (canUseAgain - System.currentTimeMillis()));
						
						if (config.getWarp_Else(name) != null && !config.getWarp_Else(name).isEmpty()) 
						{
							warp(player, config.getWarp_Else(name));
							player.sendMessage("§cYou have been moved to a different location because you failed to warp.");
						}
						return;
					}
	
					long addedTime = value * 3600000;
					config.setData_Cooldown(name, playerUUID, (System.currentTimeMillis() + addedTime));
					break;
	
				case "limit":
					config.checkMidnight();
					int amountUsed = config.getData_Limit(name, playerUUID);
					if (amountUsed >= value) 
					{
						player.sendMessage("§cYou have exceeded the daily limit to use this warp.");
						convert(player, config.getTimeTilMidnightInMillis());
						
						if (config.getWarp_Else(name) != null && !config.getWarp_Else(name).isEmpty()) 
						{
							warp(player, config.getWarp_Else(name));
							player.sendMessage("§cYou have been moved to a different location because you failed to warp.");
						}
						return;
					}
					else if (value != 0) 
					{
						amountUsed++;
						config.setData_Limit(name, playerUUID, amountUsed);
					}
					break;
			}
		}
		
		player.teleport(config.getWarp_Location(name));
	}

	//-----------------------------------------------------------------------
	/**
	 * Gets the player's location and saves it onto the configuration file.
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the warp.
	 */
	public void setWarp(Player player, String name) 
	{
		Location loc = player.getLocation();
		
		if (!config.checkWarp(name)) 
		{
			player.sendMessage("§6Warp does not exist. Creating a new warp.");

			config.set("warps." + name + ".x", loc.getX());
			config.set("warps." + name + ".y", loc.getY());
			config.set("warps." + name + ".z", loc.getZ());
			config.set("warps." + name + ".yaw", loc.getYaw());
			config.set("warps." + name + ".pitch", loc.getPitch());
			config.set("warps." + name + ".world", player.getWorld().getName());
			config.set("warps." + name + ".type", "cooldown");
			config.set("warps." + name + ".value", 0);

			player.sendMessage("§a- Use /expugn warpsetting " + name + " <cooldown|limit> to modify the type of warp.");
			player.sendMessage("§a- Use /expugn warpsetting " + name + " [number] to modify the cooldown/daily limit of the warp.");
			player.sendMessage("§a- Use /expugn warpsetting " + name + " [warpname] to add a alternate location to send the player if they failed to warp.");
		} 
		else 
		{
			player.sendMessage("§6There is an existing warp. Defining new position.");

			config.set("warps." + name + ".x", loc.getX());
			config.set("warps." + name + ".y", loc.getY());
			config.set("warps." + name + ".z", loc.getZ());
			config.set("warps." + name + ".yaw", loc.getYaw());
			config.set("warps." + name + ".pitch", loc.getPitch());
			config.set("warps." + name + ".world", player.getWorld().getName());
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Removes a warp from the configuration file.
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the warp.
	 */
	public void delWarp(Player player, String name) 
	{
		if (!config.checkWarp(name)) 
			player.sendMessage("§cInvalid warp. Use §6/expugn warplist §cfor a list of warps.");
		else 
		{
			config.set("warps." + name, null);
			config.set("data.cooldown." + name, null);
			config.set("data.limit." + name, null);

			player.sendMessage("§aWarp §6" + name + " §ahas been removed.");
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Lists all the warps available and written onto the configuration file.
	 * 
	 * @param player  The player who sent the command.
	 */
	public void warpList(Player player) 
	{
		player.sendMessage("§6There are currently §c" + config.getWarpList().size() + " §6warps");
		for (String s : config.getWarpList()) 
		{
			player.sendMessage("- " + s);
		}
		player.sendMessage("§6For more info on a warp: use §c/expugn warpinfo [warpname]§6.");
	}

	//-----------------------------------------------------------------------
	/**
	 * Reads the information of the warp in the configuration file and 
	 * returns it back to the player.
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the warp.
	 */
	public void warpInfo(Player player, String name) 
	{
		if (!config.checkWarp(name)) 
		{
			player.sendMessage("§cInvalid warp. Use §6/expugn warplist §cfor a list of warps.");
			return;
		}
		player.sendMessage("§6Information for warp §c" + name + "§6:");
		player.sendMessage("x: " + config.getWarp_X(name));
		player.sendMessage("y: " + config.getWarp_Y(name));
		player.sendMessage("z: " + config.getWarp_Z(name));
		player.sendMessage("yaw: " + config.getWarp_Yaw(name));
		player.sendMessage("pitch: " + config.getWarp_Pitch(name));
		player.sendMessage("world: " + config.getWarp_World(name));
		player.sendMessage("type: " + config.getWarp_Type(name));
		player.sendMessage("value: " + config.getWarp_Value(name) + " hours/daily entry limit");
		player.sendMessage("else: " + config.getWarp_Else(name) + " alternate warp");
	}

	//-----------------------------------------------------------------------
	/**
	 * Sets the 'type' setting of a warp.
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the warp.
	 * @param param  'cooldown' or 'limit', alternate warp, or a value to
	 *            determine the hourly cooldown or daily limit
	 */
	public void warpSetting(Player player, String name, String param) 
	{
		if (!config.checkWarp(name)) 
		{
			player.sendMessage("§cInvalid warp. Use §6/expugn warplist §cfor a list of warps.");
			return;
		}
		
		if (param.equals("cooldown") || param.equals("limit")) 
		{
			config.set("warps." + name + ".type", param);
			player.sendMessage("§aWarp " + name + " type modified to §6" + param);
		} 
		else if (config.checkWarp(param)) 
		{
			if (param.equalsIgnoreCase(name))
				player.sendMessage("§cYou cannot assign the same warp to be an alternate warp.");
			else
			{
				config.set("warps." + name + ".else", param);
				player.sendMessage("§aWarp " + name + " alternate warp modified to §6" + param);
			}
		} 
		else 
		{
			int value = Integer.parseInt(param);
			config.set("warps." + name + ".value", value);
			player.sendMessage("§aWarp " + name + " value modified to §6" + value);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Determines if the player has warped over the max limit
	 * 
	 * @param player  The player who sent the command
	 * @param name  The name of the location
	 */
	public void checkCanWarp(Player player, String name) 
	{
		if (config.checkCanWarp(name, player.getUniqueId()))
			player.sendMessage("§cYou cannot warp into the dungeon just yet.");
		else
			player.sendMessage("§aYou can warp into the dungeon.");
	}

	//-----------------------------------------------------------------------
	/**
	 * Takes a variable of milliseconds and converts into easy to read text.
	 * 
	 * @param player  The player who sent the command
	 * @param milliseconds  User inputted milliseconds.
	 */
	public void convert(Player player, long milliseconds) 
	{
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		
		if (milliseconds >= 3600000) 
		{
			hours = (int) milliseconds / 3600000;
			milliseconds = milliseconds % 3600000;
		}
		if (milliseconds >= 60000) 
		{
			minutes = (int) milliseconds / 60000;
			milliseconds = milliseconds % 60000;
		}
		if (milliseconds >= 1000) 
		{
			seconds = (int) milliseconds / 1000;
			milliseconds = milliseconds % 1000;
		}
		
		player.sendMessage("§cYou can use this warp again in: " 
				+ hours + " hours " 
				+ minutes + " minutes and " 
				+ seconds + " seconds.");
	}
}
