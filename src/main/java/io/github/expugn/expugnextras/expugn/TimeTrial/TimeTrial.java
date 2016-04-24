package io.github.expugn.expugnextras.expugn.TimeTrial;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'TimeTrial' Function</b>
 * 
 * @version 1.2.1
 * @author Expugn <i>(https://github.com/Expugn)</i>
 */
public class TimeTrial 
{
	private final io.github.expugn.expugnextras.Configs.TimeTrial config;
	private final io.github.expugn.expugnextras.Configs.Warps warp_config;

	//-----------------------------------------------------------------------
	/**
	 * Constructor for the TimeTrial class
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public TimeTrial(ExpugnExtras plugin) 
	{
		config = new io.github.expugn.expugnextras.Configs.TimeTrial(plugin);
		warp_config = new io.github.expugn.expugnextras.Configs.Warps(plugin);
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
		if (config.checkLocation(name) == false) 
		{
			config.createLocation(name);
			player.sendMessage("§aCreated location §6" + name);
		} 
		else 
			player.sendMessage("§cLocation §6" + name + " §calready exists.");
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
		if (config.checkLocation(name) == false) 
			player.sendMessage("§cThis location does not exist. Use §6/expugn locationlist §cfor a list of locations.");
		else 
		{
			config.set("location." + name, null);
			player.sendMessage("§aLocation §6" + name + " §ahas been removed.");
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Generates and tells a player the list of available locations.
	 * 
	 * @param player  The player who sent the command.
	 */
	public void locationList(Player player) 
	{
		player.sendMessage("§6There are currently §c" + config.getLocationList().size() + "§6 locations");
		for (String s : config.getLocationList()) 
		{
			player.sendMessage("- " + s);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Starts a time trial
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

		if (!config.checkLocation(name)) 
			player.sendMessage("§cThis location does not exist. Use §6/expugn locationlist §cfor a list of locations.");
		else if (inProgressLocation.equals(name)) 
		{
			player.sendMessage("§cYou already have a time trial in progress. Restarting your time.");
			config.set("inprogress." + playerUUID + ".time", System.currentTimeMillis());
		}
		else if (config.checkInProgress(player, playerUUID)) 
		{
			player.sendMessage("§6You have an existing trial in progress for another location. Deleting that trial and creating a new one.");
			config.set("inprogress." + playerUUID + ".location", name);
			config.set("inprogress." + playerUUID + ".time", System.currentTimeMillis());
			player.sendMessage("§aYour time trial has begun. Good luck!");
		}
		else 
		{
			List<String> inProgressList = config.getStringList("inprogresslist");
			inProgressList.add(playerUUID);
			config.set("inprogresslist", inProgressList);
			config.set("inprogress." + playerUUID + ".location", name);
			config.set("inprogress." + playerUUID + ".time", System.currentTimeMillis());
			player.sendMessage("§aYour time trial has begun. Good luck!");
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Ends a time trial
	 * 
	 * @param player  The player who sent the command.
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

		if (!config.checkLocation(name))
			player.sendMessage("§cThis location does not exist. Use §6/expugn locationlist §cfor a list of locations.");
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
			player.sendMessage("§6Time trial for §5" + name + " §6complete!\n" 
			+ "§aYour Time: §8" + timeString);

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
				player.sendMessage("§6A new record! Congratulations!");

				config.set("location." + name + ".times." + playerUUID + ".timestring", timeString);
				config.set("location." + name + ".times." + playerUUID + ".minutes", minutes);
				config.set("location." + name + ".times." + playerUUID + ".seconds", seconds);
				config.set("location." + name + ".times." + playerUUID + ".milliseconds", milliseconds);
			}
			config.set("inprogress." + playerUUID, null);
			List<String> inProgressList = config.getStringList("inprogresslist");
			inProgressList.remove("" + playerUUID);
			config.set("inprogresslist", inProgressList);

			updateRanking(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
		}
		else if (config.checkInProgress(player, playerUUID)) 
		{
			player.sendMessage("§cYou have a time trial in progress but it's for a different location.\n"
					+ "Your time trial in progress will be removed.");
			config.set("inprogress." + playerUUID, null);
			List<String> inProgressList = config.getStringList("inprogresslist");
			inProgressList.remove(playerUUID);
			config.set("inprogresslist", inProgressList);
		}
		else if (!config.checkInProgress(player, playerUUID)) 
			player.sendMessage("§cYou do not have a time trial in progress.\n" 
					+ "§cTo start a time trial, please walk over the §6Gold Pressure Plate §cat the entrance of the dungeon.");
		else 
			player.sendMessage("§cOops. Something went wrong. Inform a server administrator about the problem.");
	}

	//-----------------------------------------------------------------------
	/**
	 * Updates a location's ranking.
	 * 
	 * @param player  The player who sent the command.
	 * @param playerUUID  The player who sent the command's UUID.
	 * @param name  The name of the location.
	 * @param timeString  The time of the trial in string format.
	 * @param minutes  The minutes of the time trial results.
	 * @param seconds  The seconds of the time trial results.
	 * @param milliseconds  The milliseconds of the time trial results.
	 */
	public void updateRanking(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds) 
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
				config.insertRank1(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
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
				config.insertRank2(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
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
				config.insertRank3(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
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
				config.insertRank4(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
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
				config.insertRank5(player, playerUUID, name, timeString, minutes, seconds, milliseconds);
				return;
			}
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Returns the rankings of a location.
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the location.
	 */
	public void getRankings(Player player, String name) 
	{
		if (!config.checkLocation(name)) 
			player.sendMessage("§cThis location does not exist. Use §6/expugn locationlist §cfor a list of locations.");
		else 
		{
			String rank1PlayerName = "§d" + config.getString("location." + name + ".rankinglist.1" + ".playername");
			String rank2PlayerName = "§d" + config.getString("location." + name + ".rankinglist.2" + ".playername");
			String rank3PlayerName = "§d" + config.getString("location." + name + ".rankinglist.3" + ".playername");
			String rank4PlayerName = "§d" + config.getString("location." + name + ".rankinglist.4" + ".playername");
			String rank5PlayerName = "§d" + config.getString("location." + name + ".rankinglist.5" + ".playername");

			String playerBestTimeString = "§7" + config.getString("location." + name + ".times." + player.getUniqueId() + ".timestring");
			String rank1TimeString = "§7" + config.getString("location." + name + ".rankinglist.1" + ".timestring");
			String rank2TimeString = "§7" + config.getString("location." + name + ".rankinglist.2" + ".timestring");
			String rank3TimeString = "§7" + config.getString("location." + name + ".rankinglist.3" + ".timestring");
			String rank4TimeString = "§7" + config.getString("location." + name + ".rankinglist.4" + ".timestring");
			String rank5TimeString = "§7" + config.getString("location." + name + ".rankinglist.5" + ".timestring");

			player.sendMessage("§6Time trial rankings for: §5" + name + "\n"
					+ "§81) " + rank1PlayerName + " §8- " + rank1TimeString + "\n" 
					+ "§82) " + rank2PlayerName + " §8- " + rank2TimeString + "\n" 
					+ "§83) " + rank3PlayerName + " §8- " + rank3TimeString + "\n" 
					+ "§84) " + rank4PlayerName + " §8- " + rank4TimeString + "\n" 
					+ "§85) " + rank5PlayerName + " §8- " + rank5TimeString + "\n" 
					+ "§aYour Personal Best: §8" + playerBestTimeString);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Erases all the time entries recorded in a location.
	 * 
	 * @param player  The player who sent the command.
	 * @param name  The name of the location.
	 */
	public void resetTimes(Player player, String name) 
	{
		if (config.checkLocation(name)) 
		{
			config.resetRankings(name);
			player.sendMessage("§6Times have been reset.");
		} 
		else 
			player.sendMessage("§cThis location does not exist. Use §6/expugn locationlist §cfor a list of locations.");
	}

	//-----------------------------------------------------------------------
	/**
	 * Warps a player who placed first in a time trial to a ExpugnExtras 
	 * warp named 'HallOfGlory'.
	 * 
	 * @param player  The player who sent the command.
	 */
	public void warpHallOfGlory(Player player) 
	{
		String playerUUID = player.getUniqueId() + "";
		
		if (config.checkFirstPlace(playerUUID)) 
		{
			Location loc = warp_config.getWarp_Location("HallOfGlory");
			player.teleport(loc);
			player.sendMessage("§7Now entering the §6Hall of Glory §7.");
		} 
		else 
			player.sendMessage("§7This is the §6Hall of Glory§7.\n" 
					+ "§7Only players who have placed §cfirst §7in a time trial may enter.");
	}
	

}
