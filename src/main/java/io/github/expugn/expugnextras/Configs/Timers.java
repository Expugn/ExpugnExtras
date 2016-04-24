package io.github.expugn.expugnextras.Configs;

import java.util.Set;
import java.util.UUID;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'Timers' Configuration File</b>
 * 
 * @version 1.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class Timers extends ConfigurationFile
{
	//-----------------------------------------------------------------------
	/**
	 * Constructor for the Timers class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 * @param configName  The name of the configuration file to be loaded.
	 */
	public Timers(ExpugnExtras plugin) 
	{
		super(plugin, "timers");
		cleanConfig();
	}

	//-----------------------------------------------------------------------
	/**
	 * Creates a new dungeon instance in the configuration file.
	 * 
	 * @param dungeon_name  The name of the dungeon.
	 * @param time  The time in milliseconds for the timer to end.
	 */
	public void createDungeon(String dungeon_name, long time)
	{
		set("dungeons." + dungeon_name + ".cooldown", time);
		set("dungeons." + dungeon_name + ".players.key", null);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Deletes a dungeon's data from the configuration file.
	 * 
	 * @param dungeon_name  The name of the dungeon.
	 */
	public void deleteDungeon(String dungeon_name)
	{
		set("dungeons." + dungeon_name, null);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Changes the duration of the timer.
	 * 
	 * @param dungeon_name  The name of the dungeon.
	 * @param time  The time in milliseconds for the timer to end.
	 */
	public void changeDungeonTime(String dungeon_name, long time)
	{
		set("dungeons." + dungeon_name + ".cooldown", time);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Sets the amount of time before the timer will end.
	 * 
	 * @param dungeon_name  The name of the dungeon.
	 * @param playerUUID  Player's UUID.
	 * @param data  The long to be set.
	 */
	public void setPlayerTime(String dungeon_name, UUID playerUUID, Object data)
	{
		set("dungeons." + dungeon_name + ".players." + playerUUID, data);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the milliseconds the player's timer will end
	 * 
	 * @param dungeon_name  The name of the dungeon.
	 * @param playerUUID  Player's UUID.
	 * @return  long, 0L if empty.
	 */
	public long getPlayerTime(String dungeon_name, UUID playerUUID)
	{
		return getLong("dungeons." + dungeon_name + ".players." + playerUUID);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the specific amount of time a player has left until the timer ends in milliseconds.
	 * 
	 * @param dungeon_name  The name of the dungeon.
	 * @param playerUUID  Player's UUID.
	 * @return  long, 0L if empty.
	 */
	public long getPlayerTimeLeft(String dungeon_name, UUID playerUUID)
	{
		return getPlayerTime(dungeon_name, playerUUID) - System.currentTimeMillis();
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Checks if the supplied dungeon is supported.
	 * 
	 * @param name  Player who sent the command
	 * @return  true if the dungeon exists in the file, else false.
	 */
	public boolean checkDungeon(String dungeon_name) 
	{
		if (getDungeonList().contains(dungeon_name))
			return true;
		return false;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Removes expired entries in the config file.
	 */
	public void cleanConfig()
	{
		for (String dungeon_name : getDungeonList()) 
		{
			for (String key : getConfigurationSectionKeys("dungeons." + dungeon_name + ".players"))
			{
				if (System.currentTimeMillis() > getLong("dungeons." + dungeon_name + ".players." + key))
					set("dungeons." + dungeon_name + ".players." + key, null);
			}
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns all list of all dungeons created.
	 * 
	 * @return  A set of all entries under 'dungeons' in the config
	 */
	public Set<String> getDungeonList()
	{
		return getConfigurationSectionKeys("dungeons");
	}
}
