package io.github.expugn.functions;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * 'Warp' Function
 * Manages the "warp" feature in /expugn
 * 
 * @author Expugn
 * https://github.com/Expugn
 * 
 * @version 1.0
 */
public class Warps 
{
	/* example warps.yml format
	 * 
	 * midnighttime: <long>
	 * warps:
	 *   <warp>:
	 *     x: <int>
	 *     y: <int>
	 *     z: <int>
	 *     yaw: <int>
	 *     pitch: <int>
	 *     world: <string>
	 *     type: <string>
	 *     value: <int>
	 *     list: <StringList>
	 *   <warp2>:
	 *     x: <int>
	 *     y: <int>
	 *     z: <int>
	 *     yaw: <int>
	 *     pitch: <int>
	 *     world: <string>
	 *     type: <string>
	 *     value: <int>
	 * list:
	 *   - <warp>
	 *   - <warp2>
	 * data:
	 *   cooldown:
	 *     <warp>:
	 *       <uuid>: <long>
	 *   limit:
	 *     <warp2>:
	 *       <uuid>: <int>
	 */
	private Calendar midnight; 
	File ymlFile;
	FileConfiguration config;
	
	// System Messages
	private static final String MORE_WARP_INFO_REMINDER_MESSAGE = ChatColor.GOLD + "For more info on a warp: use /expugn warpinfo [warpname].";
	private static final String NEW_WARP_NOT_EXISTING_MESSAGE = ChatColor.GOLD + "Warp does not exist. Creating a new warp.";
	private static final String NEW_WARP_EXISTING_MESSAGE = ChatColor.GOLD + "There is an existing warp. Defining new position.";
	// Error Messages
	private static final String INVALID_WARP_ERROR = ChatColor.RED + "Invalid warp. Use /expugn warplist for a list of warps.";
	private static final String WARP_FAILED_COOLDOWN_ERROR = ChatColor.RED + "You cannot use this warp again just yet.";
	private static final String WARP_FAILED_SENT_ELSEWHERE_ERROR = ChatColor.RED + "You have been moved to a different location because you failed to warp.";
	private static final String WARP_FAILED_LIMIT_EXCEEDED_ERROR = ChatColor.RED + "You have exceeded the daily limit to use this warp.";
	
	public Warps (ExpugnExtras plugin)
	{
		ymlFile = new File(plugin.getDataFolder() + "/warps.yml");
		config = YamlConfiguration.loadConfiguration(ymlFile);
		if(config.getLong("midnighttime") == 0L)
		{
			resetTimer();
		}
	}
	/**
	 * Warp List: Lists all the warps available and written onto the configuration file.
	 * 
	 * @param player - The player who sent the command.
	 */
	public void warpList(Player player)
	{
		List<String> warpList = config.getStringList("list");
		player.sendMessage(ChatColor.GOLD + "There are currently " + warpList.size() + " warps");
		for (String s : warpList)
		{
			player.sendMessage("- " + s);
		}
		player.sendMessage(MORE_WARP_INFO_REMINDER_MESSAGE);
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
			player.sendMessage(INVALID_WARP_ERROR);
			return;
		}
		player.sendMessage(ChatColor.GOLD + "Information for warp " + name + ":");
		player.sendMessage("x: " + config.getInt("warps." + name + ".x"));
		player.sendMessage("y: " + config.getInt("warps." + name + ".y"));
		player.sendMessage("z: " + config.getInt("warps." + name + ".z"));
		player.sendMessage("yaw: " + config.getInt("warps." + name + ".yaw"));
		player.sendMessage("pitch: " + config.getInt("warps." + name + ".pitch"));
		player.sendMessage("world: " + config.getString("warps." + name + ".world"));
		player.sendMessage("type: " + config.getString("warps." + name + ".type"));
		player.sendMessage("value: " + config.getInt("warps." + name + ".value") + " hours/daily entry limit");
		player.sendMessage("else: " + config.getString("warps." + name + ".else") + " alternate warp");
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
			player.sendMessage(INVALID_WARP_ERROR);
			return;
		}
		// Determine if the warp is a cooldown or limit warp.
		int value = config.getInt("warps." + name + ".value");
		if (value != 0)
		{
			switch(config.getString("warps." + name + ".type"))
			{
			case "cooldown":
				// Store player time till they can use again
				long canUseAgain = config.getLong("data.cooldown." + name + "." + player.getUniqueId());
				// Check if the time they can use it again is higher than the current time.
				// if true: inform the player and quit the function.
				if (canUseAgain >= System.currentTimeMillis())
				{
					player.sendMessage(WARP_FAILED_COOLDOWN_ERROR);
					convertMilliseconds(player, (canUseAgain - System.currentTimeMillis()));
					if (config.getString("warps." + name + ".else") != null && !config.getString("warps." + name + ".else").isEmpty())
					{
						warp(player, config.getString("warps." + name + ".else"));
						player.sendMessage(WARP_FAILED_SENT_ELSEWHERE_ERROR);
					}
					return;
				}
				// Determine how many milliseconds a player has to wait until they warp again.
				long addedTime = config.getInt("warps." + name + ".value") * 3600000;
				// Store and save data.
				config.set("data.cooldown." + name + "." + player.getUniqueId(), System.currentTimeMillis() + addedTime);
				saveConfig();
				break;
			case "limit":
				// Check if midnight has passed.
				// if true: delete all player limit data
				checkMidnight();
				// Store amount of times a player used a warp and the max daily amount
				int amountUsed = config.getInt("data.limit." + name + "." + player.getUniqueId());
				int maxAmount = config.getInt("warps." + name + ".value");
				// if the amount they used is higher/equal to the max daily amount:
				// inform the player and quit the function
				if (amountUsed >= maxAmount)
				{
					player.sendMessage(WARP_FAILED_LIMIT_EXCEEDED_ERROR);
					convertMilliseconds(player, (config.getLong("midnighttime") - System.currentTimeMillis()));
					if (config.getString("warps." + name + ".else") != null && !config.getString("warps." + name + ".else").isEmpty())
					{
						warp(player, config.getString("warps." + name + ".else"));
						player.sendMessage(WARP_FAILED_SENT_ELSEWHERE_ERROR);
					}
					return;
				}
				// if the limit is not zero:
				// add 1 to the amount of times a player used the warp
				else if (maxAmount != 0)
				{
					amountUsed++;
					config.set("data.limit." + name + "." + player.getUniqueId(), amountUsed);
				}
				// Save the configuration
				saveConfig();
				break;
			}
		}
		// Warp player to the name of the warp.
		World warpWorld = Bukkit.getWorld(config.getString("warps." + name + ".world"));
		double warpX = config.getDouble("warps." + name + ".x");
		double warpY = config.getDouble("warps." + name + ".y");
		double warpZ = config.getDouble("warps." + name + ".z");
		float warpYaw = (float) config.getDouble("warps." + name + ".yaw");
		float warpPitch = (float) config.getDouble("warps." + name + ".pitch");
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
			player.sendMessage(NEW_WARP_NOT_EXISTING_MESSAGE);

			// Setup a new path in the configuration.
			config.set("warps." + name + ".x", loc.getX());
			config.set("warps." + name + ".y", loc.getY());
			config.set("warps." + name + ".z", loc.getZ());
			config.set("warps." + name + ".yaw", loc.getYaw());
			config.set("warps." + name + ".pitch", loc.getPitch());
			config.set("warps." + name + ".world", player.getWorld().getName());
			config.set("warps." + name + ".type", "cooldown");
			config.set("warps." + name + ".value", 0);

			// Add warp name to the list of warps
			List<String> warpList = config.getStringList("list");
			warpList.add(name);
			config.set("list", warpList);

			// Remind player to modify settings
			player.sendMessage(ChatColor.GREEN + "- Use /expugn warpsetting " + name + " <cooldown|limit> to modify the type of warp.");
			player.sendMessage(ChatColor.GREEN + "- Use /expugn warpsetting " + name + " [number] to modify the cooldown/daily limit of the warp.");
			player.sendMessage(ChatColor.GREEN + "- Use /expugn warpsetting " + name + " [warpname] to add a alternate location to send the player if they failed to warp.");
		}
		else
		{
			player.sendMessage(NEW_WARP_EXISTING_MESSAGE);

			// Set new values in the configuration file
			config.set("warps." + name + ".x", loc.getX());
			config.set("warps." + name + ".y", loc.getY());
			config.set("warps." + name + ".z", loc.getZ());
			config.set("warps." + name + ".yaw", loc.getYaw());
			config.set("warps." + name + ".pitch", loc.getPitch());
			config.set("warps." + name + ".world", player.getWorld().getName());
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
			player.sendMessage(INVALID_WARP_ERROR);
		}
		else
		{	
			// Remove warp from configuration file
			config.set("warps." + name, null);
			// Remove warp from warp list
			List<String> warpList = config.getStringList("list");
			warpList.remove(name);
			config.set("list", warpList);
			// Remove warp data
			config.set("data.cooldown." + name, null);
			config.set("data.limit." + name, null);
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
			player.sendMessage(INVALID_WARP_ERROR);
			return;
		}
		if (param.equals("cooldown") || param.equals("limit"))
		{
			config.set("warps." + name + ".type", param);
			player.sendMessage(ChatColor.GREEN + "Warp " + name + " type modified to " + ChatColor.GOLD + param);
		}
		else if (checkWarp(param))
		{
			config.set("warps." + name + ".else", param);
			player.sendMessage(ChatColor.GREEN + "Warp " + name + " alternate warp modified to " + ChatColor.GOLD + param);
		}
		else
		{
			int value = Integer.parseInt(param);
			config.set("warps." + name + ".value", value);
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
		List<String> warpList = config.getStringList("list");
		if (warpList.contains(name))
			return true;
		return false;
	}

	/**
	 * Check Midnight: Determines if midnight has passed. Resets the daily limits if true.
	 */
	public void checkMidnight()
	{
		if (config.getLong("midnighttime") <= System.currentTimeMillis() - 1)
		{
			// Midnight has passed. Run Reset.
			config.set("data.limit", null);
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
		config.set("midnighttime", midnight.getTimeInMillis());
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
	 * Save Config: Saves the configuration file.
	 */
	public void saveConfig()
	{
		try
		{
			config.save(ymlFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
