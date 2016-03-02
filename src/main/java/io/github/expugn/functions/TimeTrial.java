package io.github.expugn.functions;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
 * <b>'TimeTrial' Function</b>
 * 
 * @version 1.2
 * @author Expugn <i>(https://github.com/Expugn)</i>
 */
public class TimeTrial 
{
	File ymlFile;
	FileConfiguration config;
	FileConfiguration warpConfig;
	ExpugnExtras plugin;
	private static final int DEFAULT_MAX_TIME = 3600000;

	/* System and error messages */
	private static final String TIME_TRIAL_BEGIN_MESSAGE = ChatColor.GREEN + "Your time trial has begun. Good luck!";
	private static final String STARTING_NEW_TRIAL_MESSAGE = ChatColor.GOLD
			+ "You have an existing trial in progress for another location. Deleting that trial and creating a new one.";
	private static final String RESET_TIMES_MESSAGE = ChatColor.GOLD + "Times have been reset.";
	private static final String NEW_RECORD_MESSAGE = ChatColor.GOLD + "A new record! Congratulations!";
	private static final String HALL_OF_GLORY_MESSAGE = ChatColor.GRAY + "This is the " + ChatColor.GOLD
			+ "Hall of Glory" + ChatColor.GRAY + ".\n" + "Only players who have placed " + ChatColor.RED + "first "
			+ ChatColor.GRAY + "in a time trial may enter.";
	private static final String ENTER_HALL_OF_GLORY_MESSAGE = ChatColor.GRAY + "Now entering the " + ChatColor.GOLD
			+ "Hall of Glory" + ChatColor.GRAY + ".";
	private static final String INVALID_LOCATION_ERROR = ChatColor.RED
			+ "This location does not exist. Use /expugn locationlist for a list of locations.";
	private static final String ALREADY_IN_PROGRESS_ERROR = ChatColor.RED
			+ "You already have a time trial in progress. Restarting your time.";
	private static final String COMPLETE_WRONG_TRIAL_ERROR = ChatColor.RED
			+ "You have a time trial in progress but it's for a different location.\n"
			+ "Your time trial in progress will be removed.";
	private static final String COMPLETE_NON_EXISTING_TRIAL_ERROR = ChatColor.RED
			+ "You do not have a time trial in progress.\n" + "To start a time trial, please click the" + ChatColor.GOLD
			+ " Gold Block " + ChatColor.RED + "at the entrance of the dungeon.";
	private static final String UNKNOWN_ERROR = ChatColor.RED + "Oops. Something went wrong. Inform " + ChatColor.GOLD
			+ "Expugn " + ChatColor.RED + "about the problem.";

	//-----------------------------------------------------------------------
	/**
	 * Constructor for the {@code TimeTrial} class
	 * 
	 * <ul>
	 * <li> Links to a method 'checkProgress' on this class:
	 * 		{@link #checkProgress}.
	 * </ul>
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public TimeTrial(ExpugnExtras plugin) 
	{
		ymlFile = new File(plugin.getDataFolder() + "/timetrial.yml");
		config = YamlConfiguration.loadConfiguration(ymlFile);
		warpConfig = plugin.readConfig("warps");
		checkProgress();
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code setLocation}: Creates a new location entry in timetrial.yml
	 * 
	 * <ul>
	 * <li> Links to a method 'checkLocation' on this class:
	 * 		{@link #checkLocation}.
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the location.
	 */
	public void setLocation(Player player, String name) 
	{
		if (this.checkLocation(name) == false) 
		{
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

			saveConfig();
			player.sendMessage(ChatColor.GREEN + "Created location " + ChatColor.GOLD + name);
		} 
		else 
			player.sendMessage(
					ChatColor.RED + "Location " + ChatColor.GOLD + name + ChatColor.RED + " already exists.");
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code delLocation}: Deletes a location entry in timetrial.yml
	 * 
	 * <ul>
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the location.
	 */
	public void delLocation(Player player, String name) 
	{
		if (this.checkLocation(name) == false) 
			player.sendMessage(INVALID_LOCATION_ERROR);
		else 
		{
			config.set("location." + name, null);
			saveConfig();

			player.sendMessage(ChatColor.GREEN + "Location " + name + " has been removed.");
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code LocationList}: Generates and tells a player the list of available
	 * locations
	 * 
	 * <ul>
	 * <li> Links to a method 'getLocationList' on this class:
	 * 		{@link #getLocationList}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 */
	public void locationList(Player player) 
	{
		player.sendMessage(ChatColor.GOLD + "There are currently " + getLocationList().size() + " locations");
		for (String s : getLocationList()) 
		{
			player.sendMessage("- " + s);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code checkLocation}: Checks to see if the location exists
	 * 
	 * <ul>
	 * <li> Links to a method 'getLocationList' on this class:
	 * 		{@link #getLocationList}.
	 * </ul>
	 * 
	 * @param name  The name of the location.
	 * @return 
	 * 		<li> {@code true}  if location exists 
	 * 		<li> {@code false}  if location does not exist
	 */
	public boolean checkLocation(String name) 
	{
		if (getLocationList().contains(name))
			return true;
		return false;
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code checkProgress}: Checks the "inprogresslist" to see if there are any
	 * trials pending that have exceeded the hour limit. trials that are above
	 * the hour limit are deleted.
	 * 
	 * <ul>
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
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
				config.set("inprogress." + playerUUID, null);
				newInProgressList.remove(playerUUID);
				config.set("inprogresslist", newInProgressList);
				saveConfig();
			}
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code checkInProgress}: Checks the "inprogresslist" to see if a player has a
	 * trial in progress
	 * 
	 * @param player  The player who sent the command.
	 * @param playerUUID  The player who sent the command's UUID
	 * @return  
	 * 		<li> {@code true}  if in progress
	 * 	 	<li> {@code false}  if not in progress
	 */
	public boolean checkInProgress(Player player, String playerUUID) 
	{
		List<String> inProgressList = config.getStringList("inprogresslist");
		if (inProgressList.contains(playerUUID))
			return true;
		else
			return false;
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code startTrial}: Starts a time trial
	 * 
	 * <ul>
	 * <li> Links to a method 'checkLocation' on this class:
	 * 		{@link #checkLocation}.
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * <li> Links to a method 'checkInProgress' on this class:
	 * 		{@link #checkInProgress}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the location.
	 */
	public void startTrial(Player player, String name) 
	{
		String inProgressLocation;
		String playerUUID = player.getUniqueId() + "";
		if (null == config.getString("inprogress." + playerUUID + ".location")) 
			inProgressLocation = "null_object";
		else 
			inProgressLocation = config.getString("inprogress." + playerUUID + ".location");

		if (!this.checkLocation(name)) 
			player.sendMessage(INVALID_LOCATION_ERROR);
		else if (inProgressLocation.equals(name)) 
		{
			player.sendMessage(ALREADY_IN_PROGRESS_ERROR);
			config.set("inprogress." + playerUUID + ".time", System.currentTimeMillis());
			saveConfig();
		}
		else if (this.checkInProgress(player, playerUUID)) 
		{
			player.sendMessage(STARTING_NEW_TRIAL_MESSAGE);
			config.set("inprogress." + playerUUID + ".location", name);
			config.set("inprogress." + playerUUID + ".time", System.currentTimeMillis());
			player.sendMessage(TIME_TRIAL_BEGIN_MESSAGE);
			saveConfig();
		}
		else 
		{
			List<String> inProgressList = config.getStringList("inprogresslist");
			inProgressList.add(playerUUID);
			config.set("inprogresslist", inProgressList);
			config.set("inprogress." + playerUUID + ".location", name);
			config.set("inprogress." + playerUUID + ".time", System.currentTimeMillis());
			player.sendMessage(TIME_TRIAL_BEGIN_MESSAGE);
			saveConfig();
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code endTrial}: Ends a time trial
	 * 
	 * <ul>
	 * <li> Links to a method 'checkLocation' on this class:
	 * 		{@link #checkLocation}.
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * <li> Links to a method 'updateRanking' on this class:
	 * 		{@link #updateRanking}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command
	 * @param name  The name of the location.
	 */
	public void endTrial(Player player, String name) 
	{
		String inProgressLocation;
		String playerUUID = player.getUniqueId() + "";
		if (null == config.getString("inprogress." + playerUUID + ".location")) 
			inProgressLocation = "null_object";
		else 
			inProgressLocation = config.getString("inprogress." + playerUUID + ".location");

		if (!this.checkLocation(name))
			player.sendMessage(INVALID_LOCATION_ERROR);
		else if (inProgressLocation.equals(name)) 
		{
			long playerTime = System.currentTimeMillis() - config.getLong("inprogress." + playerUUID + ".time");

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
			player.sendMessage(ChatColor.GOLD + "Time trial for " + ChatColor.DARK_PURPLE + name + ChatColor.GOLD
					+ " complete!\n" + ChatColor.GREEN + "Your time: " + ChatColor.GRAY + timeString);

			int oldMinutes, oldSeconds, oldMilliseconds;
			if (null == config.getString("location." + name + ".times." + playerUUID + ".minutes")
					&& null == config.getString("location." + name + ".times." + playerUUID + ".seconds")
					&& null == config.getString("location." + name + ".times." + playerUUID + ".milliseconds")) 
			{
				oldMinutes = 99;
				oldSeconds = 99;
				oldMilliseconds = 999;
			} 
			else 
			{
				oldMinutes = Integer
						.parseInt(config.getString("location." + name + ".times." + playerUUID + ".minutes"));
				oldSeconds = Integer
						.parseInt(config.getString("location." + name + ".times." + playerUUID + ".seconds"));
				oldMilliseconds = Integer
						.parseInt(config.getString("location." + name + ".times." + playerUUID + ".milliseconds"));
			}

			if ((oldMinutes > minutes) || (oldMinutes == minutes && oldSeconds > seconds)
					|| (oldMinutes == minutes && oldSeconds == seconds && oldMilliseconds > milliseconds)) 
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
		else if (this.checkInProgress(player, playerUUID)) 
		{
			player.sendMessage(COMPLETE_WRONG_TRIAL_ERROR);
			config.set("inprogress." + playerUUID, null);
			List<String> inProgressList = config.getStringList("inprogresslist");
			inProgressList.remove(playerUUID);
			config.set("inprogresslist", inProgressList);
			saveConfig();
		}
		else if (!this.checkInProgress(player, playerUUID)) 
			player.sendMessage(COMPLETE_NON_EXISTING_TRIAL_ERROR);
		else 
			player.sendMessage(UNKNOWN_ERROR);
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code updateRanking}: Updates a location's ranking.
	 * 
	 * <ul>
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * <li> Links to a method 'insertRank1' on this class:
	 * 		{@link #insertRank1}.
	 * <li> Links to a method 'insertRank2' on this class:
	 * 		{@link #insertRank2}.
	 * <li> Links to a method 'insertRank3' on this class:
	 * 		{@link #insertRank3}.
	 * <li> Links to a method 'insertRank4' on this class:
	 * 		{@link #insertRank4}.
	 * <li> Links to a method 'insertRank5' on this class:
	 * 		{@link #insertRank5}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 * @param playerUUID  The player who sent the command's UUID
	 * @param name  The name of the location.
	 * @param timeString  The time of the trial in string format
	 * @param minutes  The minutes of the time trial results
	 * @param seconds  The seconds of the time trial results
	 * @param milliseconds  The milliseconds of the time trial results
	 */
	public void updateRanking(Player player, String playerUUID, String name, String timeString, int minutes,
			int seconds, int milliseconds) 
	{
		String rivalName;
		int rivalMinutes = 0;
		int rivalSeconds = 0;
		int rivalMilliseconds = 0;

		rivalName = (!config.getString("location." + name + ".rankinglist.1.playername").equals("null"))
				? config.getString("location." + name + ".rankinglist.1.playername") : "null_string";
		if (rivalName.equals("null_string")) 
		{
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
			rivalName = config.getString("location." + name + ".rankinglist.1.playername");
			rivalMinutes = config.getInt("location." + name + ".rankinglist.1.minutes");
			rivalSeconds = config.getInt("location." + name + ".rankinglist.1.seconds");
			rivalMilliseconds = config.getInt("location." + name + ".rankinglist.1.milliseconds");

			if ((rivalMinutes > minutes) || (rivalMinutes == minutes && rivalSeconds > seconds)
					|| (rivalMinutes == minutes && rivalSeconds == seconds && rivalMilliseconds > milliseconds)) 
			{
				insertRank1(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
				return;
			}
		}

		rivalName = (!config.getString("location." + name + ".rankinglist.2.playername").equals("null"))
				? config.getString("location." + name + ".rankinglist.2.playername") : "null_string";
		if (rivalName.equals("null_string")) 
		{
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
			rivalName = config.getString("location." + name + ".rankinglist.2.playername");
			rivalMinutes = config.getInt("location." + name + ".rankinglist.2.minutes");
			rivalSeconds = config.getInt("location." + name + ".rankinglist.2.seconds");
			rivalMilliseconds = config.getInt("location." + name + ".rankinglist.2.milliseconds");

			if ((rivalMinutes > minutes) || (rivalMinutes == minutes && rivalSeconds > seconds)
					|| (rivalMinutes == minutes && rivalSeconds == seconds && rivalMilliseconds > milliseconds)) 
			{
				insertRank2(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
				return;
			}
		}

		rivalName = (!config.getString("location." + name + ".rankinglist.3.playername").equals("null"))
				? config.getString("location." + name + ".rankinglist.3.playername") : "null_string";
		if (rivalName.equals("null_string")) 
		{
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
			rivalName = config.getString("location." + name + ".rankinglist.3.playername");
			rivalMinutes = config.getInt("location." + name + ".rankinglist.3.minutes");
			rivalSeconds = config.getInt("location." + name + ".rankinglist.3.seconds");
			rivalMilliseconds = config.getInt("location." + name + ".rankinglist.3.milliseconds");

			if ((rivalMinutes > minutes) || (rivalMinutes == minutes && rivalSeconds > seconds)
					|| (rivalMinutes == minutes && rivalSeconds == seconds && rivalMilliseconds > milliseconds)) 
			{
				insertRank3(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
				return;
			}
		}

		rivalName = (!config.getString("location." + name + ".rankinglist.4.playername").equals("null"))
				? config.getString("location." + name + ".rankinglist.4.playername") : "null_string";
		if (rivalName.equals("null_string")) 
		{
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
			rivalName = config.getString("location." + name + ".rankinglist.4.playername");
			rivalMinutes = config.getInt("location." + name + ".rankinglist.4.minutes");
			rivalSeconds = config.getInt("location." + name + ".rankinglist.4.seconds");
			rivalMilliseconds = config.getInt("location." + name + ".rankinglist.4.milliseconds");

			if ((rivalMinutes > minutes) || (rivalMinutes == minutes && rivalSeconds > seconds)
					|| (rivalMinutes == minutes && rivalSeconds == seconds && rivalMilliseconds > milliseconds)) 
			{
				insertRank4(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
				return;
			}
		}

		rivalName = (!config.getString("location." + name + ".rankinglist.5.playername").equals("null"))
				? config.getString("location." + name + ".rankinglist.5.playername") : "null_string";
		if (rivalName.equals("null_string")) 
		{
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
			rivalName = config.getString("location." + name + ".rankinglist.5.playername");
			rivalMinutes = config.getInt("location." + name + ".rankinglist.5.minutes");
			rivalSeconds = config.getInt("location." + name + ".rankinglist.5.seconds");
			rivalMilliseconds = config.getInt("location." + name + ".rankinglist.5.milliseconds");

			if ((rivalMinutes > minutes) || (rivalMinutes == minutes && rivalSeconds > seconds)
					|| (rivalMinutes == minutes && rivalSeconds == seconds && rivalMilliseconds > milliseconds)) 
			{
				insertRank5(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
				return;
			}
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code insertRank1}: Inserts player data into the first place position of a location
	 * and pushes older entries down.
	 * 
	 * <ul>
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command
	 * @param playerUUID  The player who sent the command's UUID
	 * @param name  The name of the location
	 * @param timeString  The time of the trial in string format
	 * @param minutes  The amount of minutes of the trial
	 * @param seconds  The amount of seconds of the trial
	 * @param milliseconds  The amount of milliseconds of the trial
	 */
	public void insertRank1(Player player, String playerUUID, String name, String timeString, int minutes, int seconds,
			int milliseconds) 
	{
		config.set("location." + name + ".rankinglist.5.playername",
				config.getString("location." + name + ".rankinglist.4.playername"));
		config.set("location." + name + ".rankinglist.5.playeruuid",
				config.getString("location." + name + ".rankinglist.4.playeruuid"));
		config.set("location." + name + ".rankinglist.5.timestring",
				config.getString("location." + name + ".rankinglist.4.timestring"));
		config.set("location." + name + ".rankinglist.5.minutes",
				config.getInt("location." + name + ".rankinglist.4.minutes"));
		config.set("location." + name + ".rankinglist.5.seconds",
				config.getInt("location." + name + ".rankinglist.4.seconds"));
		config.set("location." + name + ".rankinglist.5.milliseconds",
				config.getInt("location." + name + ".rankinglist.4.milliseconds"));

		config.set("location." + name + ".rankinglist.4.playername",
				config.getString("location." + name + ".rankinglist.3.playername"));
		config.set("location." + name + ".rankinglist.4.playeruuid",
				config.getString("location." + name + ".rankinglist.3.playeruuid"));
		config.set("location." + name + ".rankinglist.4.timestring",
				config.getString("location." + name + ".rankinglist.3.timestring"));
		config.set("location." + name + ".rankinglist.4.minutes",
				config.getInt("location." + name + ".rankinglist.3.minutes"));
		config.set("location." + name + ".rankinglist.4.seconds",
				config.getInt("location." + name + ".rankinglist.3.seconds"));
		config.set("location." + name + ".rankinglist.4.milliseconds",
				config.getInt("location." + name + ".rankinglist.3.milliseconds"));

		config.set("location." + name + ".rankinglist.3.playername",
				config.getString("location." + name + ".rankinglist.2.playername"));
		config.set("location." + name + ".rankinglist.3.playeruuid",
				config.getString("location." + name + ".rankinglist.2.playeruuid"));
		config.set("location." + name + ".rankinglist.3.timestring",
				config.getString("location." + name + ".rankinglist.2.timestring"));
		config.set("location." + name + ".rankinglist.3.minutes",
				config.getInt("location." + name + ".rankinglist.2.minutes"));
		config.set("location." + name + ".rankinglist.3.seconds",
				config.getInt("location." + name + ".rankinglist.2.seconds"));
		config.set("location." + name + ".rankinglist.3.milliseconds",
				config.getInt("location." + name + ".rankinglist.2.milliseconds"));

		config.set("location." + name + ".rankinglist.2.playername",
				config.getString("location." + name + ".rankinglist.1.playername"));
		config.set("location." + name + ".rankinglist.2.playeruuid",
				config.getString("location." + name + ".rankinglist.1.playeruuid"));
		config.set("location." + name + ".rankinglist.2.timestring",
				config.getString("location." + name + ".rankinglist.1.timestring"));
		config.set("location." + name + ".rankinglist.2.minutes",
				config.getInt("location." + name + ".rankinglist.1.minutes"));
		config.set("location." + name + ".rankinglist.2.seconds",
				config.getInt("location." + name + ".rankinglist.1.seconds"));
		config.set("location." + name + ".rankinglist.2.milliseconds",
				config.getInt("location." + name + ".rankinglist.1.milliseconds"));

		config.set("location." + name + ".rankinglist.1.playername", player.getName());
		config.set("location." + name + ".rankinglist.1.playeruuid", playerUUID);
		config.set("location." + name + ".rankinglist.1.timestring", timeString);
		config.set("location." + name + ".rankinglist.1.minutes", minutes);
		config.set("location." + name + ".rankinglist.1.seconds", seconds);
		config.set("location." + name + ".rankinglist.1.milliseconds", milliseconds);

		saveConfig();
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code insertRank2}: Inserts player data into the second place position of a location
	 * and pushes older entries down.
	 * 
	 * <ul>
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command
	 * @param playerUUID  The player who sent the command's UUID
	 * @param name  The name of the location
	 * @param timeString  The time of the trial in string format
	 * @param minutes  The amount of minutes of the trial
	 * @param seconds  The amount of seconds of the trial
	 * @param milliseconds  The amount of milliseconds of the trial
	 */
	public void insertRank2(Player player, String playerUUID, String name, String timeString, int minutes, int seconds,
			int milliseconds) 
	{
		config.set("location." + name + ".rankinglist.5.playername",
				config.getString("location." + name + ".rankinglist.4.playername"));
		config.set("location." + name + ".rankinglist.5.playeruuid",
				config.getString("location." + name + ".rankinglist.4.playeruuid"));
		config.set("location." + name + ".rankinglist.5.timestring",
				config.getString("location." + name + ".rankinglist.4.timestring"));
		config.set("location." + name + ".rankinglist.5.minutes",
				config.getInt("location." + name + ".rankinglist.4.minutes"));
		config.set("location." + name + ".rankinglist.5.seconds",
				config.getInt("location." + name + ".rankinglist.4.seconds"));
		config.set("location." + name + ".rankinglist.5.milliseconds",
				config.getInt("location." + name + ".rankinglist.4.milliseconds"));

		config.set("location." + name + ".rankinglist.4.playername",
				config.getString("location." + name + ".rankinglist.3.playername"));
		config.set("location." + name + ".rankinglist.4.playeruuid",
				config.getString("location." + name + ".rankinglist.3.playeruuid"));
		config.set("location." + name + ".rankinglist.4.timestring",
				config.getString("location." + name + ".rankinglist.3.timestring"));
		config.set("location." + name + ".rankinglist.4.minutes",
				config.getInt("location." + name + ".rankinglist.3.minutes"));
		config.set("location." + name + ".rankinglist.4.seconds",
				config.getInt("location." + name + ".rankinglist.3.seconds"));
		config.set("location." + name + ".rankinglist.4.milliseconds",
				config.getInt("location." + name + ".rankinglist.3.milliseconds"));

		config.set("location." + name + ".rankinglist.3.playername",
				config.getString("location." + name + ".rankinglist.2.playername"));
		config.set("location." + name + ".rankinglist.3.playeruuid",
				config.getString("location." + name + ".rankinglist.2.playeruuid"));
		config.set("location." + name + ".rankinglist.3.timestring",
				config.getString("location." + name + ".rankinglist.2.timestring"));
		config.set("location." + name + ".rankinglist.3.minutes",
				config.getInt("location." + name + ".rankinglist.2.minutes"));
		config.set("location." + name + ".rankinglist.3.seconds",
				config.getInt("location." + name + ".rankinglist.2.seconds"));
		config.set("location." + name + ".rankinglist.3.milliseconds",
				config.getInt("location." + name + ".rankinglist.2.milliseconds"));

		config.set("location." + name + ".rankinglist.2.playername", player.getName());
		config.set("location." + name + ".rankinglist.2.playeruuid", playerUUID);
		config.set("location." + name + ".rankinglist.2.timestring", timeString);
		config.set("location." + name + ".rankinglist.2.minutes", minutes);
		config.set("location." + name + ".rankinglist.2.seconds", seconds);
		config.set("location." + name + ".rankinglist.2.milliseconds", milliseconds);

		saveConfig();
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code insertRank3}: Inserts player data into the third place position of a location
	 * and pushes older entries down.
	 * 
	 * <ul>
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command
	 * @param playerUUID  The player who sent the command's UUID
	 * @param name  The name of the location
	 * @param timeString  The time of the trial in string format
	 * @param minutes  The amount of minutes of the trial
	 * @param seconds  The amount of seconds of the trial
	 * @param milliseconds  The amount of milliseconds of the trial
	 */
	public void insertRank3(Player player, String playerUUID, String name, String timeString, int minutes, int seconds,
			int milliseconds) 
	{
		config.set("location." + name + ".rankinglist.5.playername",
				config.getString("location." + name + ".rankinglist.4.playername"));
		config.set("location." + name + ".rankinglist.5.playeruuid",
				config.getString("location." + name + ".rankinglist.4.playeruuid"));
		config.set("location." + name + ".rankinglist.5.timestring",
				config.getString("location." + name + ".rankinglist.4.timestring"));
		config.set("location." + name + ".rankinglist.5.minutes",
				config.getInt("location." + name + ".rankinglist.4.minutes"));
		config.set("location." + name + ".rankinglist.5.seconds",
				config.getInt("location." + name + ".rankinglist.4.seconds"));
		config.set("location." + name + ".rankinglist.5.milliseconds",
				config.getInt("location." + name + ".rankinglist.4.milliseconds"));

		config.set("location." + name + ".rankinglist.4.playername",
				config.getString("location." + name + ".rankinglist.3.playername"));
		config.set("location." + name + ".rankinglist.4.playeruuid",
				config.getString("location." + name + ".rankinglist.3.playeruuid"));
		config.set("location." + name + ".rankinglist.4.timestring",
				config.getString("location." + name + ".rankinglist.3.timestring"));
		config.set("location." + name + ".rankinglist.4.minutes",
				config.getInt("location." + name + ".rankinglist.3.minutes"));
		config.set("location." + name + ".rankinglist.4.seconds",
				config.getInt("location." + name + ".rankinglist.3.seconds"));
		config.set("location." + name + ".rankinglist.4.milliseconds",
				config.getInt("location." + name + ".rankinglist.3.milliseconds"));

		config.set("location." + name + ".rankinglist.3.playername", player.getName());
		config.set("location." + name + ".rankinglist.3.playeruuid", playerUUID);
		config.set("location." + name + ".rankinglist.3.timestring", timeString);
		config.set("location." + name + ".rankinglist.3.minutes", minutes);
		config.set("location." + name + ".rankinglist.3.seconds", seconds);
		config.set("location." + name + ".rankinglist.3.milliseconds", milliseconds);

		saveConfig();
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code insertRank4}: Inserts player data into the fourth place position of a location
	 * and pushes older entries down.
	 * 
	 * <ul>
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command
	 * @param playerUUID  The player who sent the command's UUID
	 * @param name  The name of the location
	 * @param timeString  The time of the trial in string format
	 * @param minutes  The amount of minutes of the trial
	 * @param seconds  The amount of seconds of the trial
	 * @param milliseconds  The amount of milliseconds of the trial
	 */
	public void insertRank4(Player player, String playerUUID, String name, String timeString, int minutes, int seconds,
			int milliseconds) 
	{
		config.set("location." + name + ".rankinglist.5.playername",
				config.getString("location." + name + ".rankinglist.4.playername"));
		config.set("location." + name + ".rankinglist.5.playeruuid",
				config.getString("location." + name + ".rankinglist.4.playeruuid"));
		config.set("location." + name + ".rankinglist.5.timestring",
				config.getString("location." + name + ".rankinglist.4.timestring"));
		config.set("location." + name + ".rankinglist.5.minutes",
				config.getInt("location." + name + ".rankinglist.4.minutes"));
		config.set("location." + name + ".rankinglist.5.seconds",
				config.getInt("location." + name + ".rankinglist.4.seconds"));
		config.set("location." + name + ".rankinglist.5.milliseconds",
				config.getInt("location." + name + ".rankinglist.4.milliseconds"));

		config.set("location." + name + ".rankinglist.4.playername", player.getName());
		config.set("location." + name + ".rankinglist.4.playeruuid", playerUUID);
		config.set("location." + name + ".rankinglist.4.timestring", timeString);
		config.set("location." + name + ".rankinglist.4.minutes", minutes);
		config.set("location." + name + ".rankinglist.4.seconds", seconds);
		config.set("location." + name + ".rankinglist.4.milliseconds", milliseconds);

		saveConfig();
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code insertRank5}: Inserts player data into the fifth place position of a location.
	 * 
	 * <ul>
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command
	 * @param playerUUID  The player who sent the command's UUID
	 * @param name  The name of the location
	 * @param timeString  The time of the trial in string format
	 * @param minutes  The amount of minutes of the trial
	 * @param seconds  The amount of seconds of the trial
	 * @param milliseconds  The amount of milliseconds of the trial
	 */
	public void insertRank5(Player player, String playerUUID, String name, String timeString, int minutes, int seconds,
			int milliseconds) 
	{
		config.set("location." + name + ".rankinglist.5.playername", player.getName());
		config.set("location." + name + ".rankinglist.5.playeruuid", playerUUID);
		config.set("location." + name + ".rankinglist.5.timestring", timeString);
		config.set("location." + name + ".rankinglist.5.minutes", minutes);
		config.set("location." + name + ".rankinglist.5.seconds", seconds);
		config.set("location." + name + ".rankinglist.5.milliseconds", milliseconds);

		saveConfig();
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code getRankings}: Returns the rankings of a location
	 * 
	 * <ul>
	 * <li> Links to a method 'checkLocation' on this class:
	 * 		{@link #checkLocation}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the location.
	 */
	public void getRankings(Player player, String name) 
	{
		if (!this.checkLocation(name)) 
			player.sendMessage(INVALID_LOCATION_ERROR);
		else 
		{
			String rank1PlayerName = ChatColor.LIGHT_PURPLE
					+ config.getString("location." + name + ".rankinglist.1" + ".playername");
			String rank2PlayerName = ChatColor.LIGHT_PURPLE
					+ config.getString("location." + name + ".rankinglist.2" + ".playername");
			String rank3PlayerName = ChatColor.LIGHT_PURPLE
					+ config.getString("location." + name + ".rankinglist.3" + ".playername");
			String rank4PlayerName = ChatColor.LIGHT_PURPLE
					+ config.getString("location." + name + ".rankinglist.4" + ".playername");
			String rank5PlayerName = ChatColor.LIGHT_PURPLE
					+ config.getString("location." + name + ".rankinglist.5" + ".playername");

			String playerBestTimeString = ChatColor.GRAY
					+ config.getString("location." + name + ".times." + player.getUniqueId() + ".timestring");
			String rank1TimeString = ChatColor.GRAY
					+ config.getString("location." + name + ".rankinglist.1" + ".timestring");
			String rank2TimeString = ChatColor.GRAY
					+ config.getString("location." + name + ".rankinglist.2" + ".timestring");
			String rank3TimeString = ChatColor.GRAY
					+ config.getString("location." + name + ".rankinglist.3" + ".timestring");
			String rank4TimeString = ChatColor.GRAY
					+ config.getString("location." + name + ".rankinglist.4" + ".timestring");
			String rank5TimeString = ChatColor.GRAY
					+ config.getString("location." + name + ".rankinglist.5" + ".timestring");

			player.sendMessage(ChatColor.GOLD + "Time trial rankings for " + ChatColor.DARK_PURPLE + name + "\n"
					+ ChatColor.DARK_GRAY + "1) " + rank1PlayerName + ChatColor.DARK_GRAY + " - " + rank1TimeString
					+ "\n" + ChatColor.DARK_GRAY + "2) " + rank2PlayerName + ChatColor.DARK_GRAY + " - "
					+ rank2TimeString + "\n" + ChatColor.DARK_GRAY + "3) " + rank3PlayerName + ChatColor.DARK_GRAY
					+ " - " + rank3TimeString + "\n" + ChatColor.DARK_GRAY + "4) " + rank4PlayerName
					+ ChatColor.DARK_GRAY + " - " + rank4TimeString + "\n" + ChatColor.DARK_GRAY + "5) "
					+ rank5PlayerName + ChatColor.DARK_GRAY + " - " + rank5TimeString + "\n" + ChatColor.GREEN
					+ "Your personal best: " + ChatColor.DARK_GRAY + playerBestTimeString);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code resetTimes}: Erases all the time entries recorded in a location
	 * 
	 * <ul>
	 * <li> Links to a method 'saveConfig' on this class:
	 * 		{@link #saveConfig}.
	 * </ul>
	 * 
	 * @param player  The player who sent the command
	 * @param name  The name of the location
	 */
	public void resetTimes(Player player, String name) 
	{
		if (this.checkLocation(name)) 
		{
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

			saveConfig();

			player.sendMessage(RESET_TIMES_MESSAGE);
		} 
		else 
			player.sendMessage(INVALID_LOCATION_ERROR);
	}

	//-----------------------------------------------------------------------
	/**
	 * CheckFirstPlace - Checks all locations to see if the player has placed
	 * first in any trial.
	 * 
	 * <ul>
	 * <li> Links to a method 'getLocationList' on this class:
	 * 		{@link #getLocationList}.
	 * </ul>
	 * 
	 * @param playerUUID  The player who ran the command's UUID.
	 * @return
	 * 		<li> {@code true}  if the player has placed first in any location 
	 * 		<li> {@code false}  if the player has not placed first.
	 */
	public boolean checkFirstPlaces(String playerUUID) 
	{
		for (String location : getLocationList()) 
		{
			if (config.getString("location." + location + ".rankinglist.1.playeruuid").equals(playerUUID))
				return true;
		}
		return false;
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code warpHallOfGlory}: Warps a player who placed first in a time trial to a
	 * ExpugnExtras warp named HallOfGlory.
	 * 
	 * @param player  The player who sent the command.
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
			player.sendMessage(HALL_OF_GLORY_MESSAGE);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code getLocationList}: Returns all list of all locations created.
	 * 
	 * @return  A set of all entries under 'location' in the config
	 */
	public Set<String> getLocationList()
	{
		return config.getConfigurationSection("location").getKeys(false);
	}

	//-----------------------------------------------------------------------
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
