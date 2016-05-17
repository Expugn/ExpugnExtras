package io.github.expugn.expugnextras.Configs;

import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'TimeTrial' Configuration File</b>
 * 
 * @version 1.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class TimeTrial extends ConfigurationFile
{
	//-----------------------------------------------------------------------
	/**
	 * Constructor for the TimeTrial class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public TimeTrial(ExpugnExtras plugin) 
	{
		super(plugin, "timetrial");
		checkProgress();
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Creates a new location instance in the configuration file.
	 * 
	 * @param location_name  Name of the location.
	 */
	public void createLocation(String location_name)
	{
		set("location." + location_name + ".times.key", null);
		set("location." + location_name + ".rankinglist.1.playername", "null");
		set("location." + location_name + ".rankinglist.1.playeruuid", "_");
		set("location." + location_name + ".rankinglist.1.timestring", "99:99.999");
		set("location." + location_name + ".rankinglist.1.minutes", 99);
		set("location." + location_name + ".rankinglist.1.seconds", 99);
		set("location." + location_name + ".rankinglist.1.milliseconds", 999);

		set("location." + location_name + ".rankinglist.2.playername", "null");
		set("location." + location_name + ".rankinglist.2.playeruuid", "_");
		set("location." + location_name + ".rankinglist.2.timestring", "99:99.999");
		set("location." + location_name + ".rankinglist.2.minutes", 99);
		set("location." + location_name + ".rankinglist.2.seconds", 99);
		set("location." + location_name + ".rankinglist.2.milliseconds", 999);

		set("location." + location_name + ".rankinglist.3.playername", "null");
		set("location." + location_name + ".rankinglist.3.playeruuid", "_");
		set("location." + location_name + ".rankinglist.3.timestring", "99:99.999");
		set("location." + location_name + ".rankinglist.3.minutes", 99);
		set("location." + location_name + ".rankinglist.3.seconds", 99);
		set("location." + location_name + ".rankinglist.3.milliseconds", 999);

		set("location." + location_name + ".rankinglist.4.playername", "null");
		set("location." + location_name + ".rankinglist.4.playeruuid", "_");
		set("location." + location_name + ".rankinglist.4.timestring", "99:99.999");
		set("location." + location_name + ".rankinglist.4.minutes", 99);
		set("location." + location_name + ".rankinglist.4.seconds", 99);
		set("location." + location_name + ".rankinglist.4.milliseconds", 999);

		set("location." + location_name + ".rankinglist.5.playername", "null");
		set("location." + location_name + ".rankinglist.5.playeruuid", "_");
		set("location." + location_name + ".rankinglist.5.timestring", "99:99.999");
		set("location." + location_name + ".rankinglist.5.minutes", 99);
		set("location." + location_name + ".rankinglist.5.seconds", 99);
		set("location." + location_name + ".rankinglist.5.milliseconds", 999);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Moves Rank One data to Rank Two
	 * 
	 * @param location_name  Name of the location.
	 */
	public void RankOneToTwo(String location_name)
	{
		set("location." + location_name + ".rankinglist.2.playername",
				getString("location." + location_name + ".rankinglist.1.playername"));
		set("location." + location_name + ".rankinglist.2.playeruuid",
				getString("location." + location_name + ".rankinglist.1.playeruuid"));
		set("location." + location_name + ".rankinglist.2.timestring",
				getString("location." + location_name + ".rankinglist.1.timestring"));
		set("location." + location_name + ".rankinglist.2.minutes",
				getInt("location." + location_name + ".rankinglist.1.minutes"));
		set("location." + location_name + ".rankinglist.2.seconds",
				getInt("location." + location_name + ".rankinglist.1.seconds"));
		set("location." + location_name + ".rankinglist.2.milliseconds",
				getInt("location." + location_name + ".rankinglist.1.milliseconds"));
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Moves Rank Two data to Rank Three.
	 * 
	 * @param location_name  Name of the location.
	 */
	public void RankTwoToThree(String location_name)
	{
		set("location." + location_name + ".rankinglist.3.playername",
				getString("location." + location_name + ".rankinglist.2.playername"));
		set("location." + location_name + ".rankinglist.3.playeruuid",
				getString("location." + location_name + ".rankinglist.2.playeruuid"));
		set("location." + location_name + ".rankinglist.3.timestring",
				getString("location." + location_name + ".rankinglist.2.timestring"));
		set("location." + location_name + ".rankinglist.3.minutes",
				getInt("location." + location_name + ".rankinglist.2.minutes"));
		set("location." + location_name + ".rankinglist.3.seconds",
				getInt("location." + location_name + ".rankinglist.2.seconds"));
		set("location." + location_name + ".rankinglist.3.milliseconds",
				getInt("location." + location_name + ".rankinglist.2.milliseconds"));
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Moves Rank Three data to Rank Four.
	 * 
	 * @param location_name  Name of the location.
	 */
	public void RankThreeToFour(String location_name)
	{
		set("location." + location_name + ".rankinglist.4.playername",
				getString("location." + location_name + ".rankinglist.3.playername"));
		set("location." + location_name + ".rankinglist.4.playeruuid",
				getString("location." + location_name + ".rankinglist.3.playeruuid"));
		set("location." + location_name + ".rankinglist.4.timestring",
				getString("location." + location_name + ".rankinglist.3.timestring"));
		set("location." + location_name + ".rankinglist.4.minutes",
				getInt("location." + location_name + ".rankinglist.3.minutes"));
		set("location." + location_name + ".rankinglist.4.seconds",
				getInt("location." + location_name + ".rankinglist.3.seconds"));
		set("location." + location_name + ".rankinglist.4.milliseconds",
				getInt("location." + location_name + ".rankinglist.3.milliseconds"));
	}
	
	/**
	 * Moves Rank Four data to Rank Five.
	 * 
	 * @param location_name  Name of the location.
	 */
	public void RankFourToFive(String location_name)
	{
		set("location." + location_name + ".rankinglist.5.playername",
				getString("location." + location_name + ".rankinglist.4.playername"));
		set("location." + location_name + ".rankinglist.5.playeruuid",
				getString("location." + location_name + ".rankinglist.4.playeruuid"));
		set("location." + location_name + ".rankinglist.5.timestring",
				getString("location." + location_name + ".rankinglist.4.timestring"));
		set("location." + location_name + ".rankinglist.5.minutes",
				getInt("location." + location_name + ".rankinglist.4.minutes"));
		set("location." + location_name + ".rankinglist.5.seconds",
				getInt("location." + location_name + ".rankinglist.4.seconds"));
		set("location." + location_name + ".rankinglist.5.milliseconds",
				getInt("location." + location_name + ".rankinglist.4.milliseconds"));
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Inserts player data into the first place position of a location
	 * and pushes older entries down.
	 * 
	 * @param player  The player who sent the command.
	 * @param playerUUID  The player who sent the command's UUID.
	 * @param name  The name of the location.
	 * @param timeString  The time of the trial in string format.
	 * @param minutes  The amount of minutes of the trial.
	 * @param seconds  The amount of seconds of the trial.
	 * @param milliseconds  The amount of milliseconds of the trial.
	 */
	public void insertRank1(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds) 
	{
		RankFourToFive(name);
		RankThreeToFour(name);
		RankTwoToThree(name);
		RankOneToTwo(name);

		set("location." + name + ".rankinglist.1.playername", player.getName());
		set("location." + name + ".rankinglist.1.playeruuid", playerUUID);
		set("location." + name + ".rankinglist.1.timestring", timeString);
		set("location." + name + ".rankinglist.1.minutes", minutes);
		set("location." + name + ".rankinglist.1.seconds", seconds);
		set("location." + name + ".rankinglist.1.milliseconds", milliseconds);
	}

	//-----------------------------------------------------------------------
	/**
	 * Inserts player data into the second place position of a location
	 * and pushes older entries down.
	 * 
	 * @param player  The player who sent the command.
	 * @param playerUUID  The player who sent the command's UUID.
	 * @param name  The name of the location.
	 * @param timeString  The time of the trial in string format.
	 * @param minutes  The amount of minutes of the trial.
	 * @param seconds  The amount of seconds of the trial.
	 * @param milliseconds  The amount of milliseconds of the trial.
	 */
	public void insertRank2(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds) 
	{
		RankFourToFive(name);
		RankThreeToFour(name);
		RankTwoToThree(name);

		set("location." + name + ".rankinglist.2.playername", player.getName());
		set("location." + name + ".rankinglist.2.playeruuid", playerUUID);
		set("location." + name + ".rankinglist.2.timestring", timeString);
		set("location." + name + ".rankinglist.2.minutes", minutes);
		set("location." + name + ".rankinglist.2.seconds", seconds);
		set("location." + name + ".rankinglist.2.milliseconds", milliseconds);
	}

	//-----------------------------------------------------------------------
	/**
	 * Inserts player data into the third place position of a location
	 * and pushes older entries down.
	 * 
	 * @param player  The player who sent the command.
	 * @param playerUUID  The player who sent the command's UUID.
	 * @param name  The name of the location.
	 * @param timeString  The time of the trial in string format.
	 * @param minutes  The amount of minutes of the trial.
	 * @param seconds  The amount of seconds of the trial.
	 * @param milliseconds  The amount of milliseconds of the trial.
	 */
	public void insertRank3(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds) 
	{
		RankFourToFive(name);
		RankThreeToFour(name);

		set("location." + name + ".rankinglist.3.playername", player.getName());
		set("location." + name + ".rankinglist.3.playeruuid", playerUUID);
		set("location." + name + ".rankinglist.3.timestring", timeString);
		set("location." + name + ".rankinglist.3.minutes", minutes);
		set("location." + name + ".rankinglist.3.seconds", seconds);
		set("location." + name + ".rankinglist.3.milliseconds", milliseconds);
	}

	//-----------------------------------------------------------------------
	/**
	 * Inserts player data into the fourth place position of a location
	 * and pushes older entries down.
	 * 
	 * @param player  The player who sent the command.
	 * @param playerUUID  The player who sent the command's UUID.
	 * @param name  The name of the location.
	 * @param timeString  The time of the trial in string format.
	 * @param minutes  The amount of minutes of the trial.
	 * @param seconds  The amount of seconds of the trial.
	 * @param milliseconds  The amount of milliseconds of the trial.
	 */
	public void insertRank4(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds) 
	{
		RankFourToFive(name);

		set("location." + name + ".rankinglist.4.playername", player.getName());
		set("location." + name + ".rankinglist.4.playeruuid", playerUUID);
		set("location." + name + ".rankinglist.4.timestring", timeString);
		set("location." + name + ".rankinglist.4.minutes", minutes);
		set("location." + name + ".rankinglist.4.seconds", seconds);
		set("location." + name + ".rankinglist.4.milliseconds", milliseconds);
	}

	//-----------------------------------------------------------------------
	/**
	 * Inserts player data into the fifth place position of a location.
	 * 
	 * @param player  The player who sent the command.
	 * @param playerUUID  The player who sent the command's UUID.
	 * @param name  The name of the location.
	 * @param timeString  The time of the trial in string format.
	 * @param minutes  The amount of minutes of the trial.
	 * @param seconds  The amount of seconds of the trial.
	 * @param milliseconds  The amount of milliseconds of the trial.
	 */
	public void insertRank5(Player player, String playerUUID, String name, String timeString, int minutes, int seconds, int milliseconds) 
	{
		set("location." + name + ".rankinglist.5.playername", player.getName());
		set("location." + name + ".rankinglist.5.playeruuid", playerUUID);
		set("location." + name + ".rankinglist.5.timestring", timeString);
		set("location." + name + ".rankinglist.5.minutes", minutes);
		set("location." + name + ".rankinglist.5.seconds", seconds);
		set("location." + name + ".rankinglist.5.milliseconds", milliseconds);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Resets all top five rankings.
	 * 
	 * @param location_name  Name of the location.
	 */
	public void resetRankings(String location_name)
	{
		set("location." + location_name + ".times", null);
		set("location." + location_name + ".times.key", null);
		set("location." + location_name + ".rankinglist.1.playername", "null");
		set("location." + location_name + ".rankinglist.1.playeruuid", "_");
		set("location." + location_name + ".rankinglist.1.timestring", "99:99.999");
		set("location." + location_name + ".rankinglist.1.minutes", 99);
		set("location." + location_name + ".rankinglist.1.seconds", 99);
		set("location." + location_name + ".rankinglist.1.milliseconds", 999);

		set("location." + location_name + ".rankinglist.2.playername", "null");
		set("location." + location_name + ".rankinglist.2.playeruuid", "_");
		set("location." + location_name + ".rankinglist.2.timestring", "99:99.999");
		set("location." + location_name + ".rankinglist.2.minutes", 99);
		set("location." + location_name + ".rankinglist.2.seconds", 99);
		set("location." + location_name + ".rankinglist.2.milliseconds", 999);

		set("location." + location_name + ".rankinglist.3.playername", "null");
		set("location." + location_name + ".rankinglist.3.playeruuid", "_");
		set("location." + location_name + ".rankinglist.3.timestring", "99:99.999");
		set("location." + location_name + ".rankinglist.3.minutes", 99);
		set("location." + location_name + ".rankinglist.3.seconds", 99);
		set("location." + location_name + ".rankinglist.3.milliseconds", 999);

		set("location." + location_name + ".rankinglist.4.playername", "null");
		set("location." + location_name + ".rankinglist.4.playeruuid", "_");
		set("location." + location_name + ".rankinglist.4.timestring", "99:99.999");
		set("location." + location_name + ".rankinglist.4.minutes", 99);
		set("location." + location_name + ".rankinglist.4.seconds", 99);
		set("location." + location_name + ".rankinglist.4.milliseconds", 999);

		set("location." + location_name + ".rankinglist.5.playername", "null");
		set("location." + location_name + ".rankinglist.5.playeruuid", "_");
		set("location." + location_name + ".rankinglist.5.timestring", "99:99.999");
		set("location." + location_name + ".rankinglist.5.minutes", 99);
		set("location." + location_name + ".rankinglist.5.seconds", 99);
		set("location." + location_name + ".rankinglist.5.milliseconds", 999);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Checks the "inprogresslist" to see if there are any trials pending 
	 * that have exceeded the hour limit. trials that are above the hour 
	 * limit are deleted.
	 */
	public void checkProgress() 
	{
		long playerTime;
		long expiredPlayerTime;
		List<String> inProgressList = getStringList("inprogresslist");
		List<String> newInProgressList = getStringList("inprogresslist");

		for (String playerUUID : inProgressList) 
		{
			playerTime = getLong("inprogress." + playerUUID + ".time");
			expiredPlayerTime = playerTime + 3600000;
			if (expiredPlayerTime <= System.currentTimeMillis()) 
			{
				set("inprogress." + playerUUID, null);
				newInProgressList.remove(playerUUID);
				set("inprogresslist", newInProgressList);
			}
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Checks the "inprogresslist" to see if a player has a trial in 
	 * progress.
	 * 
	 * @param player  The player who sent the command.
	 * @param playerUUID  The player who sent the command's UUID.
	 * @return  true if in progress, else false.
	 */
	public boolean checkInProgress(Player player, String playerUUID) 
	{
		List<String> inProgressList = getStringList("inprogresslist");
		if (inProgressList.contains(playerUUID))
			return true;
		else
			return false;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Checks all locations to see if the player has placed first in any trial.
	 * 
	 * @param playerUUID  The player who ran the command's UUID.
	 * @return  true if the player has placed first at any location, else false.
	 */
	public boolean checkFirstPlace(String playerUUID) 
	{
		for (String location : getLocationList()) 
		{
			if (getString("location." + location + ".rankinglist.1.playeruuid").equals(playerUUID))
				return true;
		}
		return false;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns all list of all locations created.
	 * 
	 * @return  A set of all entries under 'location' in the config
	 */
	public Set<String> getLocationList()
	{
		return getConfigurationSectionKeys("location");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Checks to see if the location exists.
	 * 
	 * @param name  The name of the location.
	 * @return  true if the location exists, else false.
	 */
	public boolean checkLocation(String name) 
	{
		if (getLocationList().contains(name))
			return true;
		return false;
	}
}
