package io.github.expugn.expugnextras;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 'Expugn' Command
 * 
 * @author Expugn
 * https://github.com/Expugn
 * 
 * @version 1.0
 */
public class ExpugnCommand implements CommandExecutor
{
	private final ExpugnExtras plugin;
	/**
	 * Constructor for the class
	 * 
	 * @param plugin - Refers back to main class.
	 */
	public ExpugnCommand(ExpugnExtras plugin)
	{
		this.plugin = plugin;
	}
	/**
	 * Command Manager:
	 * Reviews the arguments a player inputs and redirects them to the appropriate function.
	 * 
	 * @param sender - Whoever sent the command. (player/console)
	 * @param cmd - "expugn"
	 * @param label - null
	 * @param args - Arguments following the command "expugn"
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length == 0)
			{
				player.sendMessage(ChatColor.GOLD + "Welcome to ExpugnExtras. Use /expugn help for a help menu.");
			}
			else
			{
				switch(args[0])
				{ 
					case "help": 
						helpMenu(player);
						break;
					case "warplist":
						warpList(player);
						break;
					case "warpinfo":
						if (args.length >= 2)
							warpInfo(player, args[1]);
						else
							invalidParam(player);
						break;
					case "warp":
						if (args.length >= 2)
							warp(player, args[1]);
						else
							invalidParam(player);
						break;
					case "setwarp":
						if (args.length >= 2)
							setWarp(player, args[1]);
						else
							invalidParam(player);
						break;
					case "delwarp":
						if (args.length >= 2)
							delWarp(player, args[1]);
						else
							invalidParam(player);
						break;
					case "warpsetting":
						if (args.length >= 2)
							warpSetting(player, args[1], args[2]);
						else
							invalidParam(player);
					default:
						player.sendMessage(ChatColor.RED + "Invalid Command. Use /expugn help for a help menu.");
						break;
				}
			}
			return true;
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Only players can run this command.");
			return false;
		}
	}
	/**
	 * Help Menu:
	 * Displays a helpful guide of the available commands involving /expugn
	 * 
	 * @param player - The player who sent the command
	 */
	public void helpMenu(Player player)
	{
		player.sendMessage(ChatColor.GOLD + "ExpugnExtras Help Menu:");
		player.sendMessage(ChatColor.GOLD + "(All commands begin with /expugn)");
		player.sendMessage(ChatColor.GREEN + "- General:");
		player.sendMessage("  - help - Help Menu");
		player.sendMessage(ChatColor.GREEN + "- Warps:");
		player.sendMessage("  - warplist - Lists warps managed by ExpugnExtras.");
		player.sendMessage("  - warpinfo [warpname] - Get details of a warp.");
		player.sendMessage("  - warp [warpname] - Warp to a destination.");
		player.sendMessage("  - setwarp [warpname] - Define a warp location.");
		player.sendMessage("  - delwarp [warpname] - Remove a warp location.");
		player.sendMessage("  - warpsetting [warpname] <cooldown|limit> - Defines a warp to use a cooldown system or a daily limit.");
		player.sendMessage("  - warpsetting [warpname] [number] - Sets the hours for a cooldown or the daily limit.");
	}
	/**
	 * Warp List: Lists all the warps available and written onto the configuration file.
	 * 
	 * @param player - The player who sent the command.
	 */
	public void warpList(Player player)
	{
		List<String> warpList = plugin.getConfig().getStringList("warps");
		player.sendMessage(ChatColor.GOLD + "There are currently " + warpList.size() + " warps");
		for (String s : warpList)
		{
			player.sendMessage("- " + s);
		}
		player.sendMessage(ChatColor.GOLD + "For more info on a warp: use /expugn warpinfo [warpname].");
	}
	/**
	 * Warp Info: Reads the information of the warp in the configuration file and returns it back to the player.
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the warp.
	 */
	public void warpInfo(Player player, String name)
	{
		if (this.checkWarp(name) == false)
		{
			player.sendMessage(ChatColor.RED + "Invalid warp. Use /expugn warplist for a list of warps.");
			return;
		}
		player.sendMessage(ChatColor.GOLD + "Information for warp " + name + ":");
		player.sendMessage("x: " + plugin.getConfig().getInt(name + ".x"));
		player.sendMessage("y: " + plugin.getConfig().getInt(name + ".y"));
		player.sendMessage("z: " + plugin.getConfig().getInt(name + ".z"));
		player.sendMessage("yaw: " + plugin.getConfig().getInt(name + ".yaw"));
		player.sendMessage("pitch: " + plugin.getConfig().getInt(name + ".pitch"));
		player.sendMessage("world: " + plugin.getConfig().getString(name + ".world"));
		player.sendMessage("type: " + plugin.getConfig().getString(name + ".type"));
		player.sendMessage("value: " + plugin.getConfig().getInt(name + ".value") + " hours/daily entry limit");
	}
	/**
	 * Warp: Teleports a player to a warp's location.
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the warp.
	 */
	public void warp(Player player, String name)
	{
		// TODO - Cooldown/Limit Check
		World warpWorld = Bukkit.getWorld(plugin.getConfig().getString(name + ".world"));
		double warpX = plugin.getConfig().getDouble(name + ".x");
		double warpY = plugin.getConfig().getDouble(name + ".y");
		double warpZ = plugin.getConfig().getDouble(name + ".z");
		float warpYaw = (float) plugin.getConfig().getDouble(name + ".yaw");
		float warpPitch = (float) plugin.getConfig().getDouble(name + ".pitch");
		Location loc = new Location(warpWorld, warpX, warpY, warpZ, warpYaw, warpPitch);
		player.teleport(loc);
	}
	/**
	 * Set Warp: Gets the player's location and saves it onto the configuration file.
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the warp.
	 */
	public void setWarp(Player player, String name)
	{
		// Get player location
		Location loc = player.getLocation();
		if (this.checkWarp(name) == false)
		{
			player.sendMessage(ChatColor.GOLD + "Warp does not exist. Creating a new warp.");
			
			// Setup a new path in the configuration.
			plugin.getConfig().addDefault(name + ".x", loc.getX());
			plugin.getConfig().addDefault(name + ".y", loc.getY());
			plugin.getConfig().addDefault(name + ".z", loc.getZ());
			plugin.getConfig().addDefault(name + ".yaw", loc.getYaw());
			plugin.getConfig().addDefault(name + ".pitch", loc.getPitch());
			plugin.getConfig().addDefault(name + ".world", player.getWorld().getName());
			plugin.getConfig().addDefault(name + ".type", "cooldown");
			plugin.getConfig().addDefault(name + ".value", 0);
			
			// Add warp name to the list of warps
			List<String> warpList = plugin.getConfig().getStringList("warps");
			warpList.add(name);
			plugin.getConfig().set("warps", warpList);
			
			// Remind player to modify settings
			player.sendMessage(ChatColor.GREEN + "- Use /expugn warpsetting " + name + " <cooldown|limit> to modify the type of warp.");
			player.sendMessage(ChatColor.GREEN + "- Use /expugn warpsetting " + name + " [number] to modify the cooldown/daily limit of the warp.");
		}
		else
		{
			player.sendMessage(ChatColor.GOLD + "There is an existing warp. Defining new position.");
			
			// Set new values in the configuration file
			plugin.getConfig().set(name + ".x", loc.getX());
			plugin.getConfig().set(name + ".y", loc.getY());
			plugin.getConfig().set(name + ".z", loc.getZ());
			plugin.getConfig().set(name + ".yaw", loc.getYaw());
			plugin.getConfig().set(name + ".pitch", loc.getPitch());
			plugin.getConfig().set(name + ".world", player.getWorld().getName());
		}
		// Save configuration file
		this.saveConfig();
	}
	/**
	 * Delete Warp: Removes a warp from the configuration file.
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the warp.
	 */
	public void delWarp(Player player, String name)
	{
		if (this.checkWarp(name) == false)
		{
			player.sendMessage(ChatColor.RED + "This warp does not exist. Use /expugn warplist for a list of warps.");
		}
		else
		{
			// Remove warp from configuration file
			plugin.getConfig().set(name, null);
			
			// Remove warp from warp list
			List<String> warpList = plugin.getConfig().getStringList("warps");
			warpList.remove(name);
			plugin.getConfig().set("warps", warpList);
			
			// Save configuration file
			this.saveConfig();
			
			player.sendMessage(ChatColor.GREEN + "Warp " + name + " has been removed.");
		}
	}
	/**
	 * Warp Setting: Sets the 'type' setting of a warp.
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the warp.
	 * @param param - 'cooldown' or 'limit' or a value to determine the hourly cooldown or daily limit
	 */
	public void warpSetting(Player player, String name, String param)
	{
		if (param.equals("cooldown") || param.equals("limit"))
		{
			plugin.getConfig().set(name + ".type", param);
			player.sendMessage(ChatColor.GREEN + "Warp " + name + " type modified to " + ChatColor.GOLD + param);
		}
		else
		{
			int value = Integer.parseInt(param);
			plugin.getConfig().set(name + ".value", value);
			player.sendMessage(ChatColor.GREEN + "Warp " + name + " value modified to " + ChatColor.GOLD + value);
		}
		saveConfig();
	}
	/**
	 * Check Warp: Determines if the warp exists in the configuration file.
	 * 
	 * @param name - The name of the warp.
	 * @return - Returns true if warp exists in the file. Returns false if warp is nonexistant.
	 */
	public boolean checkWarp(String name)
	{
		List<String> warpList = plugin.getConfig().getStringList("warps");
		if (warpList.contains(name))
			return true;
		return false;
	}
	/**
	 * Save Config: Saves the configuration file.
	 */
	public void saveConfig()
	{
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
	public void invalidParam(Player player)
	{
		player.sendMessage(ChatColor.RED + "Invalid parameters. Use /expugn help to check if you typed the command correctly.");
	}
}
