package io.github.expugn.expugnextras.functions;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'Warps' Function</b>
 * 
 * @version 1.2
 * @author Expugn <i>(https://github.com/Expugn)</i>
 */
public class Warps 
{
	private Calendar midnight;
	File ymlFile;
	FileConfiguration config;

	/* System and error messages */
	private static final String MORE_WARP_INFO_REMINDER_MESSAGE = ChatColor.GOLD
			+ "For more info on a warp: use /expugn warpinfo [warpname].";
	private static final String NEW_WARP_NOT_EXISTING_MESSAGE = ChatColor.GOLD
			+ "Warp does not exist. Creating a new warp.";
	private static final String NEW_WARP_EXISTING_MESSAGE = ChatColor.GOLD
			+ "There is an existing warp. Defining new position.";
	private static final String INVALID_WARP_ERROR = ChatColor.RED
			+ "Invalid warp. Use /expugn warplist for a list of warps.";
	private static final String WARP_FAILED_COOLDOWN_ERROR = ChatColor.RED + "You cannot use this warp again just yet.";
	private static final String WARP_FAILED_SENT_ELSEWHERE_ERROR = ChatColor.RED
			+ "You have been moved to a different location because you failed to warp.";
	private static final String WARP_FAILED_LIMIT_EXCEEDED_ERROR = ChatColor.RED
			+ "You have exceeded the daily limit to use this warp.";

	//-----------------------------------------------------------------------
	/**
	 * Constructor for the {@code Warps} class
	 * 
	 * <ul>
	 * <li> Links to a method 'resetTimer' on this class:
	 * 		{@link #resetTimer}.
	 * </ul>
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public Warps(ExpugnExtras plugin) 
	{
		ymlFile = new File(plugin.getDataFolder() + "/warps.yml");
		config = YamlConfiguration.loadConfiguration(ymlFile);
		if (config.getLong("midnighttime") == 0L)
			resetTimer();
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code warpList}: Lists all the warps available and written onto the
	 * configuration file.
	 * 
	 * <ul>
	 * <li> Links to a method 'getWarpList' on this class:
	 * 		{@link #getWarpList}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 */
	public void warpList(Player player) 
	{
		player.sendMessage(ChatColor.GOLD + "There are currently " + getWarpList().size() + " warps");
		for (String s : getWarpList()) 
		{
			player.sendMessage("- " + s);
		}
		player.sendMessage(MORE_WARP_INFO_REMINDER_MESSAGE);
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code warpInfo}: Reads the information of the warp in the configuration file
	 * and returns it back to the player.
	 * 
	 * <ul>
	 * <li> Links to a method 'checkWarp' on this class:
	 * 		{@link #checkWarp}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the warp.
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

	//-----------------------------------------------------------------------
	/**
	 * {@code warp}: Teleports a player to a warp's location.
	 * 
	 * <ul>
	 * <li> Links to a method 'checkWarp' on this class:
	 * 		{@link #checkWarp}.
	 * <li> Links to a method 'convertMilliseconds' on this class:
	 * 		{@link #convertMilliseconds}.
	 * <li> Links to a method 'warp' on this class:
	 * 		{@link #warp}.
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the warp.
	 */
	public void warp(Player player, String name) 
	{
		if (this.checkWarp(name) == false) 
		{
			player.sendMessage(INVALID_WARP_ERROR);
			return;
		}
		int value = config.getInt("warps." + name + ".value");
		if (value != 0) 
		{
			switch (config.getString("warps." + name + ".type")) 
			{
			case "cooldown":
				long canUseAgain = config.getLong("data.cooldown." + name + "." + player.getUniqueId());
				if (canUseAgain >= System.currentTimeMillis()) 
				{
					player.sendMessage(WARP_FAILED_COOLDOWN_ERROR);
					convertMilliseconds(player, (canUseAgain - System.currentTimeMillis()));
					if (config.getString("warps." + name + ".else") != null
							&& !config.getString("warps." + name + ".else").isEmpty()) 
					{
						warp(player, config.getString("warps." + name + ".else"));
						player.sendMessage(WARP_FAILED_SENT_ELSEWHERE_ERROR);
					}
					return;
				}
				long addedTime = config.getInt("warps." + name + ".value") * 3600000;
				config.set("data.cooldown." + name + "." + player.getUniqueId(),
						System.currentTimeMillis() + addedTime);
				saveConfig();
				break;
			case "limit":
				checkMidnight();
				int amountUsed = config.getInt("data.limit." + name + "." + player.getUniqueId());
				int maxAmount = config.getInt("warps." + name + ".value");
				if (amountUsed >= maxAmount) 
				{
					player.sendMessage(WARP_FAILED_LIMIT_EXCEEDED_ERROR);
					convertMilliseconds(player, (config.getLong("midnighttime") - System.currentTimeMillis()));
					if (config.getString("warps." + name + ".else") != null
							&& !config.getString("warps." + name + ".else").isEmpty()) 
					{
						warp(player, config.getString("warps." + name + ".else"));
						player.sendMessage(WARP_FAILED_SENT_ELSEWHERE_ERROR);
					}
					return;
				}
				else if (maxAmount != 0) 
				{
					amountUsed++;
					config.set("data.limit." + name + "." + player.getUniqueId(), amountUsed);
				}
				saveConfig();
				break;
			}
		}
		World warpWorld = Bukkit.getWorld(config.getString("warps." + name + ".world"));
		double warpX = config.getDouble("warps." + name + ".x");
		double warpY = config.getDouble("warps." + name + ".y");
		double warpZ = config.getDouble("warps." + name + ".z");
		float warpYaw = (float) config.getDouble("warps." + name + ".yaw");
		float warpPitch = (float) config.getDouble("warps." + name + ".pitch");
		Location loc = new Location(warpWorld, warpX, warpY, warpZ, warpYaw, warpPitch);
		player.teleport(loc);
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code setWarp}: Gets the player's location and saves it onto the configuration
	 * file.
	 * 
	 * <ul>
	 * <li> Links to a method 'checkWarp' on this class:
	 * 		{@link #checkWarp}.
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the warp.
	 */
	public void setWarp(Player player, String name) 
	{
		Location loc = player.getLocation();
		if (this.checkWarp(name) == false) 
		{
			player.sendMessage(NEW_WARP_NOT_EXISTING_MESSAGE);

			config.set("warps." + name + ".x", loc.getX());
			config.set("warps." + name + ".y", loc.getY());
			config.set("warps." + name + ".z", loc.getZ());
			config.set("warps." + name + ".yaw", loc.getYaw());
			config.set("warps." + name + ".pitch", loc.getPitch());
			config.set("warps." + name + ".world", player.getWorld().getName());
			config.set("warps." + name + ".type", "cooldown");
			config.set("warps." + name + ".value", 0);

			player.sendMessage(ChatColor.GREEN + "- Use /expugn warpsetting " + name
					+ " <cooldown|limit> to modify the type of warp.");
			player.sendMessage(ChatColor.GREEN + "- Use /expugn warpsetting " + name
					+ " [number] to modify the cooldown/daily limit of the warp.");
			player.sendMessage(ChatColor.GREEN + "- Use /expugn warpsetting " + name
					+ " [warpname] to add a alternate location to send the player if they failed to warp.");
		} 
		else 
		{
			player.sendMessage(NEW_WARP_EXISTING_MESSAGE);

			config.set("warps." + name + ".x", loc.getX());
			config.set("warps." + name + ".y", loc.getY());
			config.set("warps." + name + ".z", loc.getZ());
			config.set("warps." + name + ".yaw", loc.getYaw());
			config.set("warps." + name + ".pitch", loc.getPitch());
			config.set("warps." + name + ".world", player.getWorld().getName());
		}
		saveConfig();
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code delWarp}: Removes a warp from the configuration file.
	 * 
	 * <ul>
	 * <li> Links to a method 'checkWarp' on this class:
	 * 		{@link #checkWarp}.
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the warp.
	 */
	public void delWarp(Player player, String name) 
	{
		if (this.checkWarp(name) == false) 
			player.sendMessage(INVALID_WARP_ERROR);
		else 
		{
			config.set("warps." + name, null);
			config.set("data.cooldown." + name, null);
			config.set("data.limit." + name, null);
			saveConfig();

			player.sendMessage(ChatColor.GREEN + "Warp " + name + " has been removed.");
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code warpSetting}: Sets the 'type' setting of a warp.
	 * 
	 * <ul>
	 * <li> Links to a method 'checkWarp' on this class:
	 * 		{@link #checkWarp}.
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the warp.
	 * @param param  'cooldown' or 'limit', alternate warp, or a value to
	 *            determine the hourly cooldown or daily limit
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
			player.sendMessage(
					ChatColor.GREEN + "Warp " + name + " alternate warp modified to " + ChatColor.GOLD + param);
		} 
		else 
		{
			int value = Integer.parseInt(param);
			config.set("warps." + name + ".value", value);
			player.sendMessage(ChatColor.GREEN + "Warp " + name + " value modified to " + ChatColor.GOLD + value);
		}
		saveConfig();
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code checkWarp}: Determines if the warp exists in the configuration file.
	 * 
	 * <ul>
	 * <li> Links to a method 'getWarpList' on this class:
	 * 		{@link #getWarpList}.
	 * </ul>
	 * 
	 * @param name  The name of the warp.
	 * @return
	 * 		<li> {@code true}  if warp exists in the file.
	 * 		<li> {@code false}  if warp is nonexistant.
	 */
	public boolean checkWarp(String name) 
	{
		if (getWarpList().contains(name))
			return true;
		return false;
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code checkCanWarp}: Determines if the player has warped over the max limit
	 * 
	 * @param player  The player who sent the command
	 * @param name  The name of the location
	 */
	public void checkCanWarp(Player player, String name) 
	{
		int amountUsed = config.getInt("data.limit." + name + "." + player.getUniqueId());
		int maxAmount = config.getInt("warps." + name + ".value");

		if (amountUsed >= maxAmount)
			player.sendMessage(ChatColor.RED + "You cannot warp into the dungeon just yet.");
		else
			player.sendMessage(ChatColor.GREEN + "You can warp into the dungeon.");
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code checkMidnight}: Determines if midnight has passed. Resets the daily
	 * limits if true.
	 * 
	 * <ul>
	 * <li> Links to a method 'resetTimer' on this class:
	 * 		{@link #resetTimer}.
	 * </ul>
	 */
	public void checkMidnight() 
	{
		if (config.getLong("midnighttime") <= System.currentTimeMillis() - 1) 
		{
			config.set("data.limit", null);
			resetTimer();
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code resetTimer}: Determines the amount of milliseconds it takes to be
	 * midnight and saves it.
	 * 
	 * <ul>
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
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

	//-----------------------------------------------------------------------
	/**
	 * {@code convertMilliseconds}: Takes a variable of milliseconds and converts into
	 * easy to read text.
	 * 
	 * @param player  The player who sent the command
	 * @param milliseconds  User inputted milliseconds.
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
		player.sendMessage(ChatColor.RED + "You can use this warp again in: " + hours + " hours " + minutes
				+ " minutes and " + seconds + " seconds.");
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code getWarpList}: Returns all list of all warps created.
	 * 
	 * @return  A set of all entries under 'warps' in the config
	 */
	public Set<String> getWarpList()
	{
		return config.getConfigurationSection("warps").getKeys(false);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code saveConfig}: Saves the configuration file.
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
