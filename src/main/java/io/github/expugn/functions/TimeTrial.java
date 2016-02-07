package io.github.expugn.functions;

import java.io.File;
import java.io.IOException;
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
 * 'Time Trial' Function
 * Manages the "time trial" feature in /expugn
 * 
 * @author Expugn
 * https://github.com/Expugn
 * 
 * @version 1.0
 */
public class TimeTrial 
{
	/* example timetrial.yml format
	 * 
	 * location:
	 *   <location1>:
	 *     times:
	 *       <uuid>: 
	 *         playername: <String>
	 *         timestring: <String>
	 *         minutes: <int>
	 *         seconds: <int>
	 *         milliseconds: <int>
	 *       <uuid2>:
	 *         playername: <String>
	 *         timestring: <String>
	 *         minutes: <int>
	 *         seconds: <int>
	 *         milliseconds: <int>
	 *     rankinglist:
	 *       '1':
	 *         playername: <String>
	 *         playeruuid: <String>
	 *         timestring: <String>
	 *         minutes: <int>
	 *         seconds: <int>
	 *         milliseconds: <int>
	 *       '2':
	 *         playername: <String>
	 *         playeruuid: <String>
	 *         timestring: <String>
	 *         minutes: <int>
	 *         seconds: <int>
	 *         milliseconds: <int>
	 *       '3': null
	 *       '4': null
	 *       '5': null
	 *   <location2>:
	 *     times:
	 *       <uuid>:
	 *         playername: <String>
	 *         timestring: <String>
	 *         minutes: <int>
	 *         seconds: <int>
	 *         milliseconds: <int>
	 *       <uuid2>:
	 *         playername: <String>
	 *         timestring: <String>
	 *         minutes: <int>
	 *         seconds: <int>
	 *         milliseconds: <int>
	 *     rankinglist:
	 *       '1':
	 *         playername: <String>
	 *         playeruuid: <String>
	 *         timestring: <String>
	 *         minutes: <int>
	 *         seconds: <int>
	 *         milliseconds: <int>
	 *       '2':
	 *         playername: <String>
	 *         playeruuid: <String>
	 *         timestring: <String>
	 *         minutes: <int>
	 *         seconds: <int>
	 *         milliseconds: <int>
	 *       '3': null
	 *       '4': null
	 *       '5': null
	 * list:
	 * - <location1>
	 * - <location2>
	 * inprogress:
	 *   <uuid>:
	 *     location: <String>
	 *     time: <long>
	 *   <uuid2>:
	 *     location: <String>
	 *     time: <long>
	 * inprogresslist:
	 * - <uuid>
	 * - <uuid2>
	 */
	File ymlFile;
	FileConfiguration config;
	File warpYmlFile;
	FileConfiguration warpConfig;
	ExpugnExtras plugin;
	private static final int DEFAULT_MAX_TIME = 3600000; // 1 Hour
	
	/* Console Debug Messages:
	 * True | Debug messages will be displayed.
	 * False | Debug messages will not be displayed.
	 */
	private static boolean CONSOLE_DEBUG_MESSAGES;
	
	// System Messages
	private static final String TIME_TRIAL_BEGIN_MESSAGE = ChatColor.GREEN + "Your time trial has begun. Good luck!";
	private static final String STARTING_NEW_TRIAL_MESSAGE = ChatColor.GOLD + "You have an existing trial in progress for another location. Deleting that trial and creating a new one.";
	private static final String RESET_TIMES_MESSAGE = ChatColor.GOLD + "Times have been reset.";
	private static final String NEW_RECORD_MESSAGE = ChatColor.GOLD + "A new record! Congratulations!";
	private static final String HALL_OF_GLORY_MESSAGE = ChatColor.GRAY + "This is the " + ChatColor.GOLD + "Hall of Glory" + ChatColor.GRAY + ".\n"
			                                                           + "Only players who have placed " + ChatColor.RED + "first " + ChatColor.GRAY + "in a time trial may enter.";
	private static final String ENTER_HALL_OF_GLORY_MESSAGE = ChatColor.GRAY + "Now entering the " + ChatColor.GOLD + "Hall of Glory" + ChatColor.GRAY + ".";
	// Error Messages
	private static final String INVALID_LOCATION_ERROR = ChatColor.RED + "This location does not exist. Use /expugn locationlist for a list of locations.";
	private static final String ALREADY_IN_PROGRESS_ERROR = ChatColor.RED + "You already have a time trial in progress. Restarting your time.";
	private static final String COMPLETE_WRONG_TRIAL_ERROR = ChatColor.RED + "You have a time trial in progress but it's for a different location.\n" +
																			 "Your time trial in progress will be removed.";
	private static final String COMPLETE_NON_EXISTING_TRIAL_ERROR = ChatColor.RED + "You do not have a time trial in progress.\n" +
																			 "To start a time trial, please click the" + ChatColor.GOLD + " Gold Block " + ChatColor.RED +
																			 "at the entrance of the dungeon.";
	private static final String UNKNOWN_ERROR = ChatColor.RED + "Oops. Something went wrong. Inform " + ChatColor.GOLD + "Expugn " + ChatColor.RED + "about the problem.";
	
	/**
	 * Constructor for the TimeTrial class
	 * Loads timetrial.yml and runs checkProgress()
	 * 
	 * @param plugin - Refers back to the ExpugnExtras plugin
	 */
	public TimeTrial(ExpugnExtras plugin)
	{
		ymlFile = new File(plugin.getDataFolder() + "/timetrial.yml");
		config = YamlConfiguration.loadConfiguration(ymlFile);
		warpYmlFile = new File(plugin.getDataFolder() + "/warps.yml");
		warpConfig = YamlConfiguration.loadConfiguration(warpYmlFile);
		CONSOLE_DEBUG_MESSAGES = plugin.getConfig().getBoolean("timetrialdebugmessages");
		checkProgress();
	}
	
	/**
	 * SetLocation - Creates a new location entry in timetrial.yml
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the location.
	 */
	public void setLocation(Player player, String name)
	{
		// CONSOLE - Running setLocation.
		System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Running setLocation." : "");
		
		if (this.checkLocation(name) == false)
		{
			/* Draw on the config:
			 * location:
			 *   <name>:
			 *     times: {}
			 *     rankinglist:
			 *       '1':
			 *         playername: 'null'
			 *         playeruuid: '_'
			 *         timestring: '99:99.999'
			 *         minutes: 99
			 *         seconds: 99
			 *         milliseconds: 999
			 * etc...
			 */
			
			// CONSOLE - Creating new location.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Creating new location." : "");
			
			config.set("location." + name + ".times.key", null);
			config.set("location." + name + ".rankinglist.1.playername", "null");
			config.set("location." + name + ".rankinglist.1.playeruuid", "_");
			config.set("location." + name + ".rankinglist.1.timestring", "99:99.999");
			config.set("location." + name + ".rankinglist.1.minutes", 99);
			config.set("location." + name + ".rankinglist.1.seconds", 99);
			config.set("location." + name + ".rankinglist.1.milliseconds", 999);
			
			config.set("location." + name + ".rankinglist.2.playername", "null");
			config.set("location." + name + ".rankinglist.2.playeruuid", "_");
			config.set("location." + name + ".rankinglist.2.timestring", "99:99.999");
			config.set("location." + name + ".rankinglist.2.minutes", 99);
			config.set("location." + name + ".rankinglist.2.seconds", 99);
			config.set("location." + name + ".rankinglist.2.milliseconds", 999);
			
			config.set("location." + name + ".rankinglist.3.playername", "null");
			config.set("location." + name + ".rankinglist.3.playeruuid", "_");
			config.set("location." + name + ".rankinglist.3.timestring", "99:99.999");
			config.set("location." + name + ".rankinglist.3.minutes", 99);
			config.set("location." + name + ".rankinglist.3.seconds", 99);
			config.set("location." + name + ".rankinglist.3.milliseconds", 999);
			
			config.set("location." + name + ".rankinglist.4.playername", "null");
			config.set("location." + name + ".rankinglist.4.playeruuid", "_");
			config.set("location." + name + ".rankinglist.4.timestring", "99:99.999");
			config.set("location." + name + ".rankinglist.4.minutes", 99);
			config.set("location." + name + ".rankinglist.4.seconds", 99);
			config.set("location." + name + ".rankinglist.4.milliseconds", 999);
			
			config.set("location." + name + ".rankinglist.5.playername", "null");
			config.set("location." + name + ".rankinglist.5.playeruuid", "_");
			config.set("location." + name + ".rankinglist.5.timestring", "99:99.999");
			config.set("location." + name + ".rankinglist.5.minutes", 99);
			config.set("location." + name + ".rankinglist.5.seconds", 99);
			config.set("location." + name + ".rankinglist.5.milliseconds", 999);
			
			// Add location to List
			List<String> locationList = config.getStringList("list");
			locationList.add(name);
			config.set("list", locationList);
			
			// Save the configuration
			saveConfig();
			
			player.sendMessage(ChatColor.GREEN + "Created location " + ChatColor.GOLD + name);
		}
		else
		{
			player.sendMessage(ChatColor.RED + "Location " + ChatColor.GOLD + name + ChatColor.RED + " already exists.");
		}
	}
	
	/**
	 * DeleteLocation - Deletes a location entry in timetrial.yml
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the location.
	 */
	public void delLocation(Player player, String name)
	{
		// CONSOLE - Running delLocation.
		System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Running delLocation." : "");
		
		if (this.checkLocation(name) == false)
		{
			player.sendMessage(INVALID_LOCATION_ERROR);
		}
		else
		{	
			// CONSOLE - Deleting location from file.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Deleting location from file." : "");
			
			// Remove location from configuration file
			config.set("location." + name, null);
			
			// Remove location from location list
			List<String> locationList = config.getStringList("list");
			locationList.remove(name);
			config.set("list", locationList);
			
			// Save configuration file
			saveConfig();
			
			player.sendMessage(ChatColor.GREEN + "Location " + name + " has been removed.");
		}
	}
	
	/**
	 * LocationList - Generates and tells a player the list of available locations
	 * 
	 * @param player - The player who sent the command.
	 */
	public void locationList(Player player)
	{
		// CONSOLE - Running locationList.
		System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Running locationList." : "");
		
		List<String> locationList = config.getStringList("list");
		player.sendMessage(ChatColor.GOLD + "There are currently " + locationList.size() + " locations");
		for (String s : locationList)
		{
			player.sendMessage("- " + s);
		}
	}
	
	/**
	 * CheckLocation - Checks to see if the location exists
	 * 
	 * @param name - The name of the location.
	 * @return true if location exists | false if location does not exist
	 */
	public boolean checkLocation(String name)
	{
		List<String> locationList = config.getStringList("list");
		if (locationList.contains(name))
			return true;
		return false;
	}
	
	/**
	 * CheckProgress - Checks the "inprogresslist" to see if there are any trials pending that have exceeded the hour limit.
	 *                 trials that are above the hour limit are deleted.
	 */
	public void checkProgress()
	{		
		long playerTime;
		long expiredPlayerTime;
		List<String> inProgressList = config.getStringList("inprogresslist");
		List<String> newInProgressList = config.getStringList("inprogresslist");
		
		for (String playerUUID : inProgressList)
		{
			playerTime = config.getLong("inprogress." + playerUUID + ".time");
			expiredPlayerTime = playerTime + DEFAULT_MAX_TIME;
			if (expiredPlayerTime <= System.currentTimeMillis())
			{
				// CONSOLE - Located a entry that expired. Deleting.
				System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Located a entry that expired. Deleting." : "");
				
				config.set("inprogress." + playerUUID, null);
				
				newInProgressList.remove(playerUUID);
				config.set("inprogresslist", newInProgressList);
				
				// Save configuration file
				saveConfig();
			}
		}
	}
	
	/**
	 * CheckInProgress - Checks the "inprogresslist" to see if a player has a trial in progress
	 * 
	 * @param player - The player who sent the command.
	 * @return true if in progress | false if not in progress
	 */
	public boolean checkInProgress(Player player, String playerUUID)
	{
		List<String> inProgressList = config.getStringList("inprogresslist");
		if (inProgressList.contains(playerUUID))
			return true;
		else
			return false;
	}
	
	/**
	 * StartTrial - Starts a time trial
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the location.
	 */
	public void startTrial(Player player, String name)
	{
		// CONSOLE - Running startTrial.
		System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Running startTrial." : "");
		
		String inProgressLocation;
		String playerUUID = player.getUniqueId() + "";
		if (null == config.getString("inprogress." + playerUUID + ".location"))
		{
			inProgressLocation = "null_object";
		}
		else
		{
			inProgressLocation = config.getString("inprogress." + playerUUID + ".location");
		}
		
		if (!this.checkLocation(name))
		{
			player.sendMessage(INVALID_LOCATION_ERROR);
		}
		
		// check if the player has a existing trial in this location
		else if (inProgressLocation.equals(name))
		{
			// CONSOLE - Restarting time.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Restarting time." : "");
			
			player.sendMessage(ALREADY_IN_PROGRESS_ERROR);
			config.set("inprogress." + playerUUID + ".time", System.currentTimeMillis());
			
			// Save configuration file
			saveConfig();
		}
		
		// check if the player has a existing trial in another location
		else if (this.checkInProgress(player, playerUUID))
		{
			// CONSOLE - Overwriting current trial.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Overwriting current trial." : "");
			
			player.sendMessage(STARTING_NEW_TRIAL_MESSAGE);
			config.set("inprogress." + playerUUID + ".location", name);
			config.set("inprogress." + playerUUID + ".time", System.currentTimeMillis());
			player.sendMessage(TIME_TRIAL_BEGIN_MESSAGE);
			
			// Save configuration file
			saveConfig();
		}
		
		// player has no existing trial
		else
		{
			// CONSOLE - Creating new trial.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Creating new trial." : "");
			
			// Record UUID to inprogress list
			List<String> inProgressList = config.getStringList("inprogresslist");
			inProgressList.add(playerUUID);
			config.set("inprogresslist", inProgressList);
			
			// Record current time
			config.set("inprogress." + playerUUID + ".location", name);
			config.set("inprogress." + playerUUID + ".time", System.currentTimeMillis());
			
			player.sendMessage(TIME_TRIAL_BEGIN_MESSAGE);
			
			// Save configuration file
			saveConfig();
		}
	}
	
	/**
	 * EndTrial - Ends a time trial
	 * 
	 * @param player - The player who sent the command
	 * @param name - The name of the location.
	 */
	public void endTrial(Player player, String name)
	{
		// CONSOLE - Running endTrial.
		System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Running endTrial." : "");
		
		String inProgressLocation;
		String playerUUID = player.getUniqueId() + "";
		if (null == config.getString("inprogress." + playerUUID + ".location"))
		{
			inProgressLocation = "null_object";
		}
		else
		{
			inProgressLocation = config.getString("inprogress." + playerUUID + ".location");
		}
		
		// check if the location actually exists
		if (!this.checkLocation(name))
			player.sendMessage(INVALID_LOCATION_ERROR);

		// check if the player has a existing trial in this location
		else if (inProgressLocation.equals(name))
		{
			// CONSOLE - Ending trial and saving time...
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Ending trial and saving time..." : "");
			
			// Get time in milliseconds of how long it took to complete
			long playerTime = System.currentTimeMillis() - config.getLong("inprogress." + playerUUID + ".time");
			
			// Convert time to minutes, seconds, and milliseconds
			int minutes = 0;
			int seconds = 0;
			int milliseconds = 0;
			String minuteString, secondString, millisecondString, timeString;
			if (playerTime >= 60000)
			{
				minutes = (int) playerTime / 60000;
				playerTime = playerTime % 60000;
			}
			if (playerTime >= 1000)
			{
				seconds = (int) playerTime / 1000;
				milliseconds = (int) playerTime % 1000;
			}
			
			// Convert minutes, seconds, and milliseconds variables to string
			if (minutes < 10)
				minuteString = "0" + minutes;
			else
				minuteString = "" + minutes;
			
			if (seconds < 10)
				secondString = "0" + seconds;
			else
				secondString = "" + seconds;
			
			if (milliseconds < 10)
				millisecondString = "00" + milliseconds;
			else if (milliseconds < 100)
				millisecondString = "0" + milliseconds;
			else
				millisecondString = "" + milliseconds;
			
			timeString = minuteString + ":" + secondString + "." + millisecondString;
			
			// end trial and record better time to times
			player.sendMessage(ChatColor.GOLD + "Time trial for " + ChatColor.DARK_PURPLE + name + ChatColor.GOLD + " complete!\n" +
					           ChatColor.GREEN + "Your time: " + ChatColor.GRAY + timeString);
			
			int oldMinutes, oldSeconds, oldMilliseconds;
			if (null == config.getString("location." + name + ".times." + playerUUID + ".minutes") &&
					null == config.getString("location." + name + ".times." + playerUUID + ".seconds") &&
					null == config.getString("location." + name + ".times." + playerUUID + ".milliseconds"))
			{
				oldMinutes = 99;
				oldSeconds = 99;
				oldMilliseconds = 999;
			}
			else
			{
				oldMinutes = Integer.parseInt(config.getString("location." + name + ".times." + playerUUID + ".minutes"));
				oldSeconds = Integer.parseInt(config.getString("location." + name + ".times." + playerUUID + ".seconds"));
				oldMilliseconds = Integer.parseInt(config.getString("location." + name + ".times." + playerUUID + ".milliseconds"));
			}
			
			if ((oldMinutes > minutes) ||
					(oldMinutes == minutes && oldSeconds > seconds)	||
					(oldMinutes == minutes && oldSeconds == seconds && oldMilliseconds > milliseconds))
			{
				player.sendMessage(NEW_RECORD_MESSAGE);
				
				config.set("location." + name + ".times." + playerUUID + ".timestring", timeString);
				config.set("location." + name + ".times." + playerUUID + ".minutes", minutes);
				config.set("location." + name + ".times." + playerUUID + ".seconds", seconds);
				config.set("location." + name + ".times." + playerUUID + ".milliseconds", milliseconds);
			}
			
			config.set("inprogress." + playerUUID, null);
			
			List<String> inProgressList = config.getStringList("inprogresslist");
			inProgressList.remove("" + playerUUID);
			config.set("inprogresslist", inProgressList);
			
			saveConfig();
			
			updateRanking(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
		}

		// check if the player has a existing trial in another location
		else if (this.checkInProgress(player, playerUUID))
		{
			// CONSOLE - Deleting invalid trial.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Deleting invalid trial." : "");
			
			player.sendMessage(COMPLETE_WRONG_TRIAL_ERROR);
			config.set("inprogress." + playerUUID, null);
			
			List<String> inProgressList = config.getStringList("inprogresslist");
			inProgressList.remove(playerUUID);
			config.set("inprogresslist", inProgressList);
			
			// Save configuration file.
			saveConfig();
		}
		
		// player has no existing trial
		else if (!this.checkInProgress(player, playerUUID))
		{
			player.sendMessage(COMPLETE_NON_EXISTING_TRIAL_ERROR);
		}
		
		else
		{
			player.sendMessage(UNKNOWN_ERROR);
		}	
	}
	
	/**
	 * UpdateRanking - Updates a location's ranking.
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the location.
	 * @param minutes - The minutes of the time trial results
	 * @param seconds - The seconds of the time trial results
	 * @param milliseconds - The milliseconds of the time trial results
	 */
	public void updateRanking(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds)
	{
		// CONSOLE - Updating ranking.
		System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Updating ranking." : "");
		
		String rivalName;
		int rivalMinutes = 0;
		int rivalSeconds = 0;
		int rivalMilliseconds = 0;

		rivalName = (!config.getString("location." + name + ".rankinglist.1.playername").equals("null")) 
				? config.getString("location." + name + ".rankinglist.1.playername") : "null_string";
		if (rivalName.equals("null_string"))
		{
			// CONSOLE - Recording data to rank one.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Recording data to rank one." : "");
			
			config.set("location." + name + ".rankinglist.1.playername", player.getName());
			config.set("location." + name + ".rankinglist.1.playeruuid", playerUUID);
			config.set("location." + name + ".rankinglist.1.timestring", timeString);
			config.set("location." + name + ".rankinglist.1.minutes", minutes);
			config.set("location." + name + ".rankinglist.1.seconds", seconds);
			config.set("location." + name + ".rankinglist.1.milliseconds", milliseconds);
			
			saveConfig();
			return;
		}
		else
		{
			// CONSOLE - Collecting rank one data and comparing it with player's...
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Collecting rank one data and comparing it with player's..." : "");
			
			rivalName = config.getString("location." + name + ".rankinglist.1.playername");
			rivalMinutes = config.getInt("location." + name + ".rankinglist.1.minutes");
			rivalSeconds = config.getInt("location." + name + ".rankinglist.1.seconds");
			rivalMilliseconds = config.getInt("location." + name + ".rankinglist.1.milliseconds");
			
			if ((rivalMinutes > minutes) ||
				(rivalMinutes == minutes && rivalSeconds > seconds)	||
				(rivalMinutes == minutes && rivalSeconds == seconds && rivalMilliseconds > milliseconds))
			{
				// CONSOLE - Rank one is slower than the player! Updating ranking.
				System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Rank one is slower than the player! Updating ranking." : "");
				
				insertRank1(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
				return;
			}
		}
		
		rivalName = (!config.getString("location." + name + ".rankinglist.2.playername").equals("null"))  
				? config.getString("location." + name + ".rankinglist.2.playername") : "null_string";
		if (rivalName.equals("null_string"))
		{
			// CONSOLE - Recording data to rank two.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Recording data to rank two." : "");

			config.set("location." + name + ".rankinglist.2.playername", player.getName());
			config.set("location." + name + ".rankinglist.2.playeruuid", playerUUID);
			config.set("location." + name + ".rankinglist.2.timestring", timeString);
			config.set("location." + name + ".rankinglist.2.minutes", minutes);
			config.set("location." + name + ".rankinglist.2.seconds", seconds);
			config.set("location." + name + ".rankinglist.2.milliseconds", milliseconds);
			
			saveConfig();
			return;
		}
		else
		{
			// CONSOLE - Collecting rank two data and comparing it with player's...
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Collecting rank two data and comparing it with player's..." : "");

			rivalName = config.getString("location." + name + ".rankinglist.2.playername");
			rivalMinutes = config.getInt("location." + name + ".rankinglist.2.minutes");
			rivalSeconds = config.getInt("location." + name + ".rankinglist.2.seconds");
			rivalMilliseconds = config.getInt("location." + name + ".rankinglist.2.milliseconds");

			if ((rivalMinutes > minutes) ||
					(rivalMinutes == minutes && rivalSeconds > seconds)	||
					(rivalMinutes == minutes && rivalSeconds == seconds && rivalMilliseconds > milliseconds))
			{
				// CONSOLE - Rank two is slower than the player! Updating ranking.
				System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Rank two is slower than the player! Updating ranking." : "");

				insertRank2(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
				return;
			}
		}
		
		rivalName = (!config.getString("location." + name + ".rankinglist.3.playername").equals("null"))  
				? config.getString("location." + name + ".rankinglist.3.playername") : "null_string";
		if (rivalName.equals("null_string"))
		{
			// CONSOLE - Recording data to rank three.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Recording data to rank three." : "");

			config.set("location." + name + ".rankinglist.3.playername", player.getName());
			config.set("location." + name + ".rankinglist.3.playeruuid", playerUUID);
			config.set("location." + name + ".rankinglist.3.timestring", timeString);
			config.set("location." + name + ".rankinglist.3.minutes", minutes);
			config.set("location." + name + ".rankinglist.3.seconds", seconds);
			config.set("location." + name + ".rankinglist.3.milliseconds", milliseconds);

			saveConfig();
			return;
		}
		else
		{
			// CONSOLE - Collecting rank three data and comparing it with player's...
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Collecting rank three data and comparing it with player's..." : "");

			rivalName = config.getString("location." + name + ".rankinglist.3.playername");
			rivalMinutes = config.getInt("location." + name + ".rankinglist.3.minutes");
			rivalSeconds = config.getInt("location." + name + ".rankinglist.3.seconds");
			rivalMilliseconds = config.getInt("location." + name + ".rankinglist.3.milliseconds");

			if ((rivalMinutes > minutes) ||
					(rivalMinutes == minutes && rivalSeconds > seconds)	||
					(rivalMinutes == minutes && rivalSeconds == seconds && rivalMilliseconds > milliseconds))
			{
				// CONSOLE - Rank three is slower than the player! Updating ranking.
				System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Rank three is slower than the player! Updating ranking." : "");

				insertRank3(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
				return;
			}
		}
		
		rivalName = (!config.getString("location." + name + ".rankinglist.4.playername").equals("null"))  
				? config.getString("location." + name + ".rankinglist.4.playername") : "null_string";
		if (rivalName.equals("null_string"))
		{
			// CONSOLE - Recording data to rank four.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Recording data to rank four." : "");

			config.set("location." + name + ".rankinglist.4.playername", player.getName());
			config.set("location." + name + ".rankinglist.4.playeruuid", playerUUID);
			config.set("location." + name + ".rankinglist.4.timestring", timeString);
			config.set("location." + name + ".rankinglist.4.minutes", minutes);
			config.set("location." + name + ".rankinglist.4.seconds", seconds);
			config.set("location." + name + ".rankinglist.4.milliseconds", milliseconds);

			saveConfig();
			return;
		}
		else
		{
			// CONSOLE - Collecting rank four data and comparing it with player's...
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Collecting rank four data and comparing it with player's..." : "");

			rivalName = config.getString("location." + name + ".rankinglist.4.playername");
			rivalMinutes = config.getInt("location." + name + ".rankinglist.4.minutes");
			rivalSeconds = config.getInt("location." + name + ".rankinglist.4.seconds");
			rivalMilliseconds = config.getInt("location." + name + ".rankinglist.4.milliseconds");

			if ((rivalMinutes > minutes) ||
					(rivalMinutes == minutes && rivalSeconds > seconds)	||
					(rivalMinutes == minutes && rivalSeconds == seconds && rivalMilliseconds > milliseconds))
			{
				// CONSOLE - Rank four is slower than the player! Updating ranking.
				System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Rank four is slower than the player! Updating ranking." : "");

				insertRank4(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
				return;
			}
		}
		
		rivalName = (!config.getString("location." + name + ".rankinglist.5.playername").equals("null"))  
				? config.getString("location." + name + ".rankinglist.5.playername") : "null_string";
		if (rivalName.equals("null_string"))
		{
			// CONSOLE - Recording data to rank five.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Recording data to rank five." : "");

			config.set("location." + name + ".rankinglist.5.playername", player.getName());
			config.set("location." + name + ".rankinglist.5.playeruuid", playerUUID);
			config.set("location." + name + ".rankinglist.5.timestring", timeString);
			config.set("location." + name + ".rankinglist.5.minutes", minutes);
			config.set("location." + name + ".rankinglist.5.seconds", seconds);
			config.set("location." + name + ".rankinglist.5.milliseconds", milliseconds);

			saveConfig();
			return;
		}
		else
		{
			// CONSOLE - Collecting rank five data and comparing it with player's...
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Collecting rank five data and comparing it with player's..." : "");

			rivalName = config.getString("location." + name + ".rankinglist.5.playername");
			rivalMinutes = config.getInt("location." + name + ".rankinglist.5.minutes");
			rivalSeconds = config.getInt("location." + name + ".rankinglist.5.seconds");
			rivalMilliseconds = config.getInt("location." + name + ".rankinglist.5.milliseconds");

			if ((rivalMinutes > minutes) ||
					(rivalMinutes == minutes && rivalSeconds > seconds)	||
					(rivalMinutes == minutes && rivalSeconds == seconds && rivalMilliseconds > milliseconds))
			{
				// CONSOLE - Rank five is slower than the player! Updating ranking.
				System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Rank five is slower than the player! Updating ranking." : "");

				insertRank5(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
				return;
			}
		}
	}
	
	public void insertRank1(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds)
	{	
		config.set("location." + name + ".rankinglist.5.playername", config.getString("location." + name + ".rankinglist.4.playername"));
		config.set("location." + name + ".rankinglist.5.playeruuid", config.getString("location." + name + ".rankinglist.4.playeruuid"));
		config.set("location." + name + ".rankinglist.5.timestring", config.getString("location." + name + ".rankinglist.4.timestring"));
		config.set("location." + name + ".rankinglist.5.minutes", config.getInt("location." + name + ".rankinglist.4.minutes"));
		config.set("location." + name + ".rankinglist.5.seconds", config.getInt("location." + name + ".rankinglist.4.seconds"));
		config.set("location." + name + ".rankinglist.5.milliseconds", config.getInt("location." + name + ".rankinglist.4.milliseconds"));

		config.set("location." + name + ".rankinglist.4.playername", config.getString("location." + name + ".rankinglist.3.playername"));
		config.set("location." + name + ".rankinglist.4.playeruuid", config.getString("location." + name + ".rankinglist.3.playeruuid"));
		config.set("location." + name + ".rankinglist.4.timestring", config.getString("location." + name + ".rankinglist.3.timestring"));
		config.set("location." + name + ".rankinglist.4.minutes", config.getInt("location." + name + ".rankinglist.3.minutes"));
		config.set("location." + name + ".rankinglist.4.seconds", config.getInt("location." + name + ".rankinglist.3.seconds"));
		config.set("location." + name + ".rankinglist.4.milliseconds", config.getInt("location." + name + ".rankinglist.3.milliseconds"));

		config.set("location." + name + ".rankinglist.3.playername", config.getString("location." + name + ".rankinglist.2.playername"));
		config.set("location." + name + ".rankinglist.3.playeruuid", config.getString("location." + name + ".rankinglist.2.playeruuid"));
		config.set("location." + name + ".rankinglist.3.timestring", config.getString("location." + name + ".rankinglist.2.timestring"));
		config.set("location." + name + ".rankinglist.3.minutes", config.getInt("location." + name + ".rankinglist.2.minutes"));
		config.set("location." + name + ".rankinglist.3.seconds", config.getInt("location." + name + ".rankinglist.2.seconds"));
		config.set("location." + name + ".rankinglist.3.milliseconds", config.getInt("location." + name + ".rankinglist.2.milliseconds"));

		config.set("location." + name + ".rankinglist.2.playername", config.getString("location." + name + ".rankinglist.1.playername"));
		config.set("location." + name + ".rankinglist.2.playeruuid", config.getString("location." + name + ".rankinglist.1.playeruuid"));
		config.set("location." + name + ".rankinglist.2.timestring", config.getString("location." + name + ".rankinglist.1.timestring"));
		config.set("location." + name + ".rankinglist.2.minutes", config.getInt("location." + name + ".rankinglist.1.minutes"));
		config.set("location." + name + ".rankinglist.2.seconds", config.getInt("location." + name + ".rankinglist.1.seconds"));
		config.set("location." + name + ".rankinglist.2.milliseconds", config.getInt("location." + name + ".rankinglist.1.milliseconds"));

		config.set("location." + name + ".rankinglist.1.playername", player.getName());
		config.set("location." + name + ".rankinglist.1.playeruuid", playerUUID);
		config.set("location." + name + ".rankinglist.1.timestring", timeString);
		config.set("location." + name + ".rankinglist.1.minutes", minutes);
		config.set("location." + name + ".rankinglist.1.seconds", seconds);
		config.set("location." + name + ".rankinglist.1.milliseconds", milliseconds);
		
		saveConfig();
	}
	
	public void insertRank2(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds)
	{
		config.set("location." + name + ".rankinglist.5.playername", config.getString("location." + name + ".rankinglist.4.playername"));
		config.set("location." + name + ".rankinglist.5.playeruuid", config.getString("location." + name + ".rankinglist.4.playeruuid"));
		config.set("location." + name + ".rankinglist.5.timestring", config.getString("location." + name + ".rankinglist.4.timestring"));
		config.set("location." + name + ".rankinglist.5.minutes", config.getInt("location." + name + ".rankinglist.4.minutes"));
		config.set("location." + name + ".rankinglist.5.seconds", config.getInt("location." + name + ".rankinglist.4.seconds"));
		config.set("location." + name + ".rankinglist.5.milliseconds", config.getInt("location." + name + ".rankinglist.4.milliseconds"));

		config.set("location." + name + ".rankinglist.4.playername", config.getString("location." + name + ".rankinglist.3.playername"));
		config.set("location." + name + ".rankinglist.4.playeruuid", config.getString("location." + name + ".rankinglist.3.playeruuid"));
		config.set("location." + name + ".rankinglist.4.timestring", config.getString("location." + name + ".rankinglist.3.timestring"));
		config.set("location." + name + ".rankinglist.4.minutes", config.getInt("location." + name + ".rankinglist.3.minutes"));
		config.set("location." + name + ".rankinglist.4.seconds", config.getInt("location." + name + ".rankinglist.3.seconds"));
		config.set("location." + name + ".rankinglist.4.milliseconds", config.getInt("location." + name + ".rankinglist.3.milliseconds"));

		config.set("location." + name + ".rankinglist.3.playername", config.getString("location." + name + ".rankinglist.2.playername"));
		config.set("location." + name + ".rankinglist.3.playeruuid", config.getString("location." + name + ".rankinglist.2.playeruuid"));
		config.set("location." + name + ".rankinglist.3.timestring", config.getString("location." + name + ".rankinglist.2.timestring"));
		config.set("location." + name + ".rankinglist.3.minutes", config.getInt("location." + name + ".rankinglist.2.minutes"));
		config.set("location." + name + ".rankinglist.3.seconds", config.getInt("location." + name + ".rankinglist.2.seconds"));
		config.set("location." + name + ".rankinglist.3.milliseconds", config.getInt("location." + name + ".rankinglist.2.milliseconds"));

		config.set("location." + name + ".rankinglist.2.playername", player.getName());
		config.set("location." + name + ".rankinglist.2.playeruuid", playerUUID);
		config.set("location." + name + ".rankinglist.2.timestring", timeString);
		config.set("location." + name + ".rankinglist.2.minutes", minutes);
		config.set("location." + name + ".rankinglist.2.seconds", seconds);
		config.set("location." + name + ".rankinglist.2.milliseconds", milliseconds);
		
		saveConfig();
	}
	
	public void insertRank3(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds)
	{
		config.set("location." + name + ".rankinglist.5.playername", config.getString("location." + name + ".rankinglist.4.playername"));
		config.set("location." + name + ".rankinglist.5.playeruuid", config.getString("location." + name + ".rankinglist.4.playeruuid"));
		config.set("location." + name + ".rankinglist.5.timestring", config.getString("location." + name + ".rankinglist.4.timestring"));
		config.set("location." + name + ".rankinglist.5.minutes", config.getInt("location." + name + ".rankinglist.4.minutes"));
		config.set("location." + name + ".rankinglist.5.seconds", config.getInt("location." + name + ".rankinglist.4.seconds"));
		config.set("location." + name + ".rankinglist.5.milliseconds", config.getInt("location." + name + ".rankinglist.4.milliseconds"));

		config.set("location." + name + ".rankinglist.4.playername", config.getString("location." + name + ".rankinglist.3.playername"));
		config.set("location." + name + ".rankinglist.4.playeruuid", config.getString("location." + name + ".rankinglist.3.playeruuid"));
		config.set("location." + name + ".rankinglist.4.timestring", config.getString("location." + name + ".rankinglist.3.timestring"));
		config.set("location." + name + ".rankinglist.4.minutes", config.getInt("location." + name + ".rankinglist.3.minutes"));
		config.set("location." + name + ".rankinglist.4.seconds", config.getInt("location." + name + ".rankinglist.3.seconds"));
		config.set("location." + name + ".rankinglist.4.milliseconds", config.getInt("location." + name + ".rankinglist.3.milliseconds"));
	
		config.set("location." + name + ".rankinglist.3.playername", player.getName());
		config.set("location." + name + ".rankinglist.3.playeruuid", playerUUID);
		config.set("location." + name + ".rankinglist.3.timestring", timeString);
		config.set("location." + name + ".rankinglist.3.minutes", minutes);
		config.set("location." + name + ".rankinglist.3.seconds", seconds);
		config.set("location." + name + ".rankinglist.3.milliseconds", milliseconds);
		
		saveConfig();
	}
	
	public void insertRank4(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds)
	{	
		config.set("location." + name + ".rankinglist.5.playername", config.getString("location." + name + ".rankinglist.4.playername"));
		config.set("location." + name + ".rankinglist.5.playeruuid", config.getString("location." + name + ".rankinglist.4.playeruuid"));
		config.set("location." + name + ".rankinglist.5.timestring", config.getString("location." + name + ".rankinglist.4.timestring"));
		config.set("location." + name + ".rankinglist.5.minutes", config.getInt("location." + name + ".rankinglist.4.minutes"));
		config.set("location." + name + ".rankinglist.5.seconds", config.getInt("location." + name + ".rankinglist.4.seconds"));
		config.set("location." + name + ".rankinglist.5.milliseconds", config.getInt("location." + name + ".rankinglist.4.milliseconds"));

		config.set("location." + name + ".rankinglist.4.playername", player.getName());
		config.set("location." + name + ".rankinglist.4.playeruuid", playerUUID);
		config.set("location." + name + ".rankinglist.4.timestring", timeString);
		config.set("location." + name + ".rankinglist.4.minutes", minutes);
		config.set("location." + name + ".rankinglist.4.seconds", seconds);
		config.set("location." + name + ".rankinglist.4.milliseconds", milliseconds);
		
		saveConfig();
	}
	
	public void insertRank5(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds)
	{				
		config.set("location." + name + ".rankinglist.5.playername", player.getName());
		config.set("location." + name + ".rankinglist.5.playeruuid", playerUUID);
		config.set("location." + name + ".rankinglist.5.timestring", timeString);
		config.set("location." + name + ".rankinglist.5.minutes", minutes);
		config.set("location." + name + ".rankinglist.5.seconds", seconds);
		config.set("location." + name + ".rankinglist.5.milliseconds", milliseconds);
		
		saveConfig();
	}
	
	/**
	 * GetRankings - Returns the rankings of a location
	 * 
	 * @param player - The player who sent the command.
	 * @param name - The name of the location.
	 */
	public void getRankings(Player player, String name)
	{
		if (!this.checkLocation(name))
		{
			player.sendMessage(INVALID_LOCATION_ERROR);
		}
		else
		{	
			// CONSOLE - Fetching rankings for <name>.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Fetching rankings for " + name : "");
			
			String rank1PlayerName = ChatColor.LIGHT_PURPLE + config.getString("location." + name + ".rankinglist.1" + ".playername");
			String rank2PlayerName = ChatColor.LIGHT_PURPLE + config.getString("location." + name + ".rankinglist.2" + ".playername");
			String rank3PlayerName = ChatColor.LIGHT_PURPLE + config.getString("location." + name + ".rankinglist.3" + ".playername");
			String rank4PlayerName = ChatColor.LIGHT_PURPLE + config.getString("location." + name + ".rankinglist.4" + ".playername");
			String rank5PlayerName = ChatColor.LIGHT_PURPLE + config.getString("location." + name + ".rankinglist.5" + ".playername");
			
			String playerBestTimeString = ChatColor.GRAY + config.getString("location." + name + ".times." + player.getUniqueId() + ".timestring");
			String rank1TimeString = ChatColor.GRAY + config.getString("location." + name + ".rankinglist.1" + ".timestring");
			String rank2TimeString = ChatColor.GRAY + config.getString("location." + name + ".rankinglist.2" + ".timestring");
			String rank3TimeString = ChatColor.GRAY + config.getString("location." + name + ".rankinglist.3" + ".timestring");
			String rank4TimeString = ChatColor.GRAY + config.getString("location." + name + ".rankinglist.4" + ".timestring");
			String rank5TimeString = ChatColor.GRAY + config.getString("location." + name + ".rankinglist.5" + ".timestring");
			
			player.sendMessage(ChatColor.GOLD + "Time trial rankings for " + ChatColor.DARK_PURPLE + name + "\n" +
					ChatColor.DARK_GRAY + "1) " + rank1PlayerName + ChatColor.DARK_GRAY + " - " + rank1TimeString + "\n" +
					ChatColor.DARK_GRAY + "2) " + rank2PlayerName + ChatColor.DARK_GRAY + " - " + rank2TimeString + "\n" +
					ChatColor.DARK_GRAY + "3) " + rank3PlayerName + ChatColor.DARK_GRAY + " - " + rank3TimeString + "\n" +
					ChatColor.DARK_GRAY + "4) " + rank4PlayerName + ChatColor.DARK_GRAY + " - " + rank4TimeString + "\n" +
					ChatColor.DARK_GRAY + "5) " + rank5PlayerName + ChatColor.DARK_GRAY + " - " + rank5TimeString + "\n" +
					ChatColor.GREEN + "Your personal best: " + ChatColor.DARK_GRAY + playerBestTimeString);
		}
	}
	
	public void resetTimes(Player player, String name)
	{
		if (this.checkLocation(name))
		{
			// CONSOLE - Resetting times.
			System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Resetting times." : "");

			config.set("location." + name + ".times", null);
			config.set("location." + name + ".times.key", null);
			config.set("location." + name + ".rankinglist.1.playername", "null");
			config.set("location." + name + ".rankinglist.1.playeruuid", "_");
			config.set("location." + name + ".rankinglist.1.timestring", "99:99.999");
			config.set("location." + name + ".rankinglist.1.minutes", 99);
			config.set("location." + name + ".rankinglist.1.seconds", 99);
			config.set("location." + name + ".rankinglist.1.milliseconds", 999);

			config.set("location." + name + ".rankinglist.2.playername", "null");
			config.set("location." + name + ".rankinglist.2.playeruuid", "_");
			config.set("location." + name + ".rankinglist.2.timestring", "99:99.999");
			config.set("location." + name + ".rankinglist.2.minutes", 99);
			config.set("location." + name + ".rankinglist.2.seconds", 99);
			config.set("location." + name + ".rankinglist.2.milliseconds", 999);

			config.set("location." + name + ".rankinglist.3.playername", "null");
			config.set("location." + name + ".rankinglist.3.playeruuid", "_");
			config.set("location." + name + ".rankinglist.3.timestring", "99:99.999");
			config.set("location." + name + ".rankinglist.3.minutes", 99);
			config.set("location." + name + ".rankinglist.3.seconds", 99);
			config.set("location." + name + ".rankinglist.3.milliseconds", 999);

			config.set("location." + name + ".rankinglist.4.playername", "null");
			config.set("location." + name + ".rankinglist.4.playeruuid", "_");
			config.set("location." + name + ".rankinglist.4.timestring", "99:99.999");
			config.set("location." + name + ".rankinglist.4.minutes", 99);
			config.set("location." + name + ".rankinglist.4.seconds", 99);
			config.set("location." + name + ".rankinglist.4.milliseconds", 999);

			config.set("location." + name + ".rankinglist.5.playername", "null");
			config.set("location." + name + ".rankinglist.5.playeruuid", "_");
			config.set("location." + name + ".rankinglist.5.timestring", "99:99.999");
			config.set("location." + name + ".rankinglist.5.minutes", 99);
			config.set("location." + name + ".rankinglist.5.seconds", 99);
			config.set("location." + name + ".rankinglist.5.milliseconds", 999);

			// Save the configuration
			saveConfig();
			
			player.sendMessage(RESET_TIMES_MESSAGE);
		}
		else
		{
			player.sendMessage(INVALID_LOCATION_ERROR);
		}
	}
	
	/**
	 * CheckFirstPlace - Checks all locations to see if the player has placed first in any trial.
	 * 
	 * @param player - The player who ran the command.
	 * @return - true if the player has placed first in any location | false if the player has not placed first.
	 */
	public boolean checkFirstPlaces(String playerUUID)
	{
		List<String> locationList = config.getStringList("list");
		for (String location : locationList)
		{
			if (config.getString("location." + location + ".rankinglist.1.playeruuid").equals(playerUUID))
				return true;
		}
		return false;
	}
	
	/**
	 * WarpHallOfGlory - Warps a player who placed first in a time trial to a ExpugnExtras warp named HallOfGlory.
	 * 
	 * @param player - The player who sent the command.
	 */
	public void warpHallOfGlory(Player player)
	{	
		String playerUUID = player.getUniqueId() + "";
		if (checkFirstPlaces(playerUUID))
		{
			World warpWorld = Bukkit.getWorld(warpConfig.getString("warps.HallOfGlory.world"));
			double warpX = warpConfig.getDouble("warps.HallOfGlory.x");
			double warpY = warpConfig.getDouble("warps.HallOfGlory.y");
			double warpZ = warpConfig.getDouble("warps.HallOfGlory.z");
			float warpYaw = (float) warpConfig.getDouble("warps.HallOfGlory.yaw");
			float warpPitch = (float) warpConfig.getDouble("warps.HallOfGlory.pitch");
			Location loc = new Location(warpWorld, warpX, warpY, warpZ, warpYaw, warpPitch);
			player.teleport(loc);
			player.sendMessage(ENTER_HALL_OF_GLORY_MESSAGE);
		}
		else
		{
			player.sendMessage(HALL_OF_GLORY_MESSAGE);
		}
	}
	
	/**
	 * Save Config: Saves the configuration file.
	 */
	public void saveConfig()
	{
		// CONSOLE - Saving configuration file...
		System.out.print(CONSOLE_DEBUG_MESSAGES ? "[ExpugnDebug](Time Trial) - Saving configuration file..." : "");
		
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
