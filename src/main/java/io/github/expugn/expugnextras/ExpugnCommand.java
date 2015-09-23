package io.github.expugn.expugnextras;

import java.util.Calendar;
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
	private Calendar midnight; 
	/**
	 * Constructor for the class
	 * 
	 * @param plugin - Refers back to main class.
	 */
	public ExpugnCommand(ExpugnExtras plugin)
	{
		this.plugin = plugin;
		if(plugin.getConfig().getLong("midnighttime") == 0L)
		{
			resetTimer();
		}
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
						if (args.length >= 3)
							warpSetting(player, args[1], args[2]);
						else
							invalidParam(player);
						break;
					case "cleardata":
						clearData(player);
						break;
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
		player.sendMessage("  - warp [warpname] - Warp to a destination.");
		player.sendMessage("  - setwarp [warpname] - Define a warp location.");
		player.sendMessage("  - delwarp [warpname] - Remove a warp location.");
		player.sendMessage("  - warplist - Lists warps managed by ExpugnExtras.");
		player.sendMessage("  - warpinfo [warpname] - Get details of a warp.");
		player.sendMessage("  - warpsetting [warpname] <cooldown|limit> - Defines a warp to use a cooldown system or a daily limit.");
		player.sendMessage("  - warpsetting [warpname] [number] - Sets the hours for a cooldown or the daily limit.");
		player.sendMessage("  - warpsetting [warpname] [warpname] - Sets an alternate warp to move the player if the player could not warp.");
		player.sendMessage("  - cleardata - Deletes all cooldown/limit data from the configuration file.");
	}
	/**
	 * Warp List: Lists all the warps available and written onto the configuration file.
	 * 
	 * @param player - The player who sent the command.
	 */
	public void warpList(Player player)
	{
		List<String> warpList = plugin.getConfig().getStringList("warps.list");
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
		player.sendMessage("x: " + plugin.getConfig().getInt("warps.warp." + name + ".x"));
		player.sendMessage("y: " + plugin.getConfig().getInt("warps.warp." + name + ".y"));
		player.sendMessage("z: " + plugin.getConfig().getInt("warps.warp." + name + ".z"));
		player.sendMessage("yaw: " + plugin.getConfig().getInt("warps.warp." + name + ".yaw"));
		player.sendMessage("pitch: " + plugin.getConfig().getInt("warps.warp." + name + ".pitch"));
		player.sendMessage("world: " + plugin.getConfig().getString("warps.warp." + name + ".world"));
		player.sendMessage("type: " + plugin.getConfig().getString("warps.warp." + name + ".type"));
		player.sendMessage("value: " + plugin.getConfig().getInt("warps.warp." + name + ".value") + " hours/daily entry limit");
		player.sendMessage("else: " + plugin.getConfig().getString("warps.warp." + name + ".else") + " alternate warp");
	}
	/**
	 * Warp: Teleports a player to a warp's location.
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the warp.
	 */
	public void warp(Player player, String name)
	{
		// Check if warp exists
		// if not: quit the function.
		if (this.checkWarp(name) == false)
		{
			player.sendMessage(ChatColor.RED + "Invalid warp. Use /expugn warplist for a list of warps.");
			return;
		}
		// Determine if the warp is a cooldown or limit warp.
		int value = plugin.getConfig().getInt("warps.warp." + name + ".value");
		if (value != 0)
		{
			switch(plugin.getConfig().getString("warps.warp." + name + ".type"))
			{
				case "cooldown":
					// Store player time till they can use again
					long canUseAgain = plugin.getConfig().getLong("warps.data.cooldown." + name + "." + player.getUniqueId());
					// Check if the time they can use it again is higher than the current time.
					// if true: inform the player and quit the function.
					if (canUseAgain >= System.currentTimeMillis())
					{
						player.sendMessage(ChatColor.RED + "You cannot use this warp again just yet.");
						convertMilliseconds(player, (canUseAgain - System.currentTimeMillis()));
						if (plugin.getConfig().getString("warps.warp." + name + ".else") != null && !plugin.getConfig().getString("warps.warp." + name + ".else").isEmpty())
						{
							warp(player, plugin.getConfig().getString("warps.warp." + name + ".else"));
							player.sendMessage(ChatColor.RED + "You have been moved to a different location because you failed to warp.");
						}
						return;
					}
					// Determine how many milliseconds a player has to wait until they warp again.
					long addedTime = plugin.getConfig().getInt("warps.warp." + name + ".value") * 3600000;
					// Store and save data.
					plugin.getConfig().set("warps.data.cooldown." + name + "." + player.getUniqueId(), System.currentTimeMillis() + addedTime);
					saveConfig();
					break;
				case "limit":
					// Check if midnight has passed.
					// if true: delete all player limit data
					checkMidnight();
					// Store amount of times a player used a warp and the max daily amount
					int amountUsed = plugin.getConfig().getInt("warps.data.limit." + name + "." + player.getUniqueId());
					int maxAmount = plugin.getConfig().getInt("warps.warp." + name + ".value");
					// if the amount they used is higher/equal to the max daily amount:
					// inform the player and quit the function
					if (amountUsed >= maxAmount)
					{
						player.sendMessage(ChatColor.RED + "You have exceeded the daily limit to use this warp.");
						convertMilliseconds(player, (plugin.getConfig().getLong("midnighttime") - System.currentTimeMillis()));
						if (plugin.getConfig().getString("warps.warp." + name + ".else") != null && !plugin.getConfig().getString("warps.warp." + name + ".else").isEmpty())
						{
							warp(player, plugin.getConfig().getString("warps.warp." + name + ".else"));
							player.sendMessage(ChatColor.RED + "You have been moved to a different location because you failed to warp.");
						}
						return;
					}
					// if the limit is not zero:
					// add 1 to the amount of times a player used the warp
					else if (maxAmount != 0)
					{
						amountUsed++;
						plugin.getConfig().set("warps.data.limit." + name + "." + player.getUniqueId(), amountUsed);
					}
					// Save the configuration
					saveConfig();
					break;
			}
		}
		// Warp player to the name of the warp.
		World warpWorld = Bukkit.getWorld(plugin.getConfig().getString("warps.warp." + name + ".world"));
		double warpX = plugin.getConfig().getDouble("warps.warp." + name + ".x");
		double warpY = plugin.getConfig().getDouble("warps.warp." + name + ".y");
		double warpZ = plugin.getConfig().getDouble("warps.warp." + name + ".z");
		float warpYaw = (float) plugin.getConfig().getDouble("warps.warp." + name + ".yaw");
		float warpPitch = (float) plugin.getConfig().getDouble("warps.warp." + name + ".pitch");
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
			plugin.getConfig().set("warps.warp." + name + ".x", loc.getX());
			plugin.getConfig().set("warps.warp." + name + ".y", loc.getY());
			plugin.getConfig().set("warps.warp." + name + ".z", loc.getZ());
			plugin.getConfig().set("warps.warp." + name + ".yaw", loc.getYaw());
			plugin.getConfig().set("warps.warp." + name + ".pitch", loc.getPitch());
			plugin.getConfig().set("warps.warp." + name + ".world", player.getWorld().getName());
			plugin.getConfig().set("warps.warp." + name + ".type", "cooldown");
			plugin.getConfig().set("warps.warp." + name + ".value", 0);
			
			// Add warp name to the list of warps
			List<String> warpList = plugin.getConfig().getStringList("warps.list");
			warpList.add(name);
			plugin.getConfig().set("warps.list", warpList);
			
			// Remind player to modify settings
			player.sendMessage(ChatColor.GREEN + "- Use /expugn warpsetting " + name + " <cooldown|limit> to modify the type of warp.");
			player.sendMessage(ChatColor.GREEN + "- Use /expugn warpsetting " + name + " [number] to modify the cooldown/daily limit of the warp.");
			player.sendMessage(ChatColor.GREEN + "- Use /expugn warpsetting " + name + " [warpname] to add a alternate location to send the player if they failed to warp.");
		}
		else
		{
			player.sendMessage(ChatColor.GOLD + "There is an existing warp. Defining new position.");
			
			// Set new values in the configuration file
			plugin.getConfig().set("warps.warp." + name + ".x", loc.getX());
			plugin.getConfig().set("warps.warp." + name + ".y", loc.getY());
			plugin.getConfig().set("warps.warp." + name + ".z", loc.getZ());
			plugin.getConfig().set("warps.warp." + name + ".yaw", loc.getYaw());
			plugin.getConfig().set("warps.warp." + name + ".pitch", loc.getPitch());
			plugin.getConfig().set("warps.warp." + name + ".world", player.getWorld().getName());
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
			plugin.getConfig().set("warps.warp." + name, null);
			// Remove warp from warp list
			List<String> warpList = plugin.getConfig().getStringList("warps.list");
			warpList.remove(name);
			plugin.getConfig().set("warps.list", warpList);
			// Remove warp data
			plugin.getConfig().set("warps.data.cooldown." + name, null);
			plugin.getConfig().set("warps.data.limit." + name, null);
			// Save configuration file
			saveConfig();
			
			player.sendMessage(ChatColor.GREEN + "Warp " + name + " has been removed.");
		}
	}
	/**
	 * Warp Setting: Sets the 'type' setting of a warp.
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the warp.
	 * @param param - 'cooldown' or 'limit', alternate warp, or a value to determine the hourly cooldown or daily limit
	 */
	public void warpSetting(Player player, String name, String param)
	{
		if (!this.checkWarp(name))
		{
			player.sendMessage(ChatColor.RED + "This warp does not exist. Use /expugn warplist for a list of warps.");
			return;
		}
		if (param.equals("cooldown") || param.equals("limit"))
		{
			plugin.getConfig().set("warps.warp." + name + ".type", param);
			player.sendMessage(ChatColor.GREEN + "Warp " + name + " type modified to " + ChatColor.GOLD + param);
		}
		else if (checkWarp(param))
		{
			plugin.getConfig().set("warps.warp." + name + ".else", param);
			player.sendMessage(ChatColor.GREEN + "Warp " + name + " alternate warp modified to " + ChatColor.GOLD + param);
		}
		else
		{
			int value = Integer.parseInt(param);
			plugin.getConfig().set("warps.warp." + name + ".value", value);
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
		List<String> warpList = plugin.getConfig().getStringList("warps.list");
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
	/**
	 * Invalid Parameter: Returns a error message to the player informing them of invalid parameters.
	 * 
	 * @param player - The player who sent the command.
	 */
	public void invalidParam(Player player)
	{
		player.sendMessage(ChatColor.RED + "Invalid parameters. Use /expugn help to check if you typed the command correctly.");
	}
	/**
	 * Check Midnight: Determines if midnight has passed. Resets the daily limits if true.
	 */
	public void checkMidnight()
	{
		if (plugin.getConfig().getLong("midnighttime") <= System.currentTimeMillis() - 1)
		{
			// Midnight has passed. Run Reset.
			plugin.getConfig().set("warps.data.limit", null);
			resetTimer();
		}
	}
	/**
	 * Reset Timer: Determines the amount of milliseconds it takes to be midnight and saves it.
	 */
	public void resetTimer()
	{
		midnight = Calendar.getInstance();
		midnight.set(Calendar.HOUR_OF_DAY, 0);
		midnight.set(Calendar.MINUTE, 0);
		midnight.set(Calendar.SECOND, 0);
		midnight.set(Calendar.MILLISECOND, 0);
		midnight.set(Calendar.DAY_OF_YEAR, midnight.get(Calendar.DAY_OF_YEAR) + 1);
		plugin.getConfig().set("midnighttime", midnight.getTimeInMillis());
		saveConfig();
	}
	/**
	 * Convert Milliseconds: Takes a variable of milliseconds and converts into easy to read text.
	 * 
	 * @param player - The player who sent the command
	 * @param milliseconds - User inputted milliseconds.
	 */
	public void convertMilliseconds(Player player, long milliseconds)
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
		player.sendMessage(ChatColor.RED + "You can use this warp again in: " + hours + " hours " + minutes + " minutes and " + seconds + " seconds.");
	}
	/**
	 * Clear Data - Removes all cooldown/limit data from the configuration file.
	 * 
	 * @param player - The player who sent the command.
	 */
	public void clearData(Player player)
	{
		plugin.getConfig().set("warps.data", null);
		saveConfig();
		player.sendMessage(ChatColor.GREEN + "Cooldown and limit data has been cleared.");
	}
}
