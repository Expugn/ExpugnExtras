package io.github.expugn.expugnextras.Configs;

import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'Warps' Configuration File</b>
 * 
 * @version 1.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class Warps extends ConfigurationFile
{
	private Calendar midnight;
	
	//-----------------------------------------------------------------------
	/**
	 * Constructor for the Warps class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 * @param configName  The name of the configuration file to be loaded.
	 */
	public Warps(ExpugnExtras plugin) 
	{
		super(plugin, "warps");
		checkMidnight();
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Determines if midnight has passed. Resets the daily limits if true.
	 */
	public void checkMidnight()
	{
		if (getMidnightTime() <= System.currentTimeMillis() - 1) 
		{	
			midnight = Calendar.getInstance();
			midnight.set(Calendar.HOUR_OF_DAY, 0);
			midnight.set(Calendar.MINUTE, 0);
			midnight.set(Calendar.SECOND, 0);
			midnight.set(Calendar.MILLISECOND, 0);
			midnight.set(Calendar.DAY_OF_YEAR, midnight.get(Calendar.DAY_OF_YEAR) + 1);
			set("midnighttime", midnight.getTimeInMillis());
			
			set("data.limit", null);
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the value of midnighttime in the configuration file.
	 * 
	 * @return  long, 0L if empty.
	 */
	public long getMidnightTime()
	{
		return getLong("midnighttime");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns a long of how long it will be until midnight on the server.
	 * 
	 * @return  long, not empty.
	 */
	public long getTimeTilMidnightInMillis()
	{
		return getMidnightTime() - System.currentTimeMillis();
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the X value of a warp.
	 * 
	 * @param warp_name  Name of the warp.
	 * @return  Integer, 0 if empty.
	 */
	public int getWarp_X(String warp_name)
	{
		return getInt("warps." + warp_name + ".x");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the Y value of a warp.
	 * 
	 * @param warp_name  Name of the warp.
	 * @return  Integer, 0 if empty.
	 */
	public int getWarp_Y(String warp_name)
	{
		return getInt("warps." + warp_name + ".y");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the Z value of a warp.
	 * 
	 * @param warp_name  Name of the warp.
	 * @return  Integer, 0 if empty.
	 */
	public int getWarp_Z(String warp_name)
	{
		return getInt("warps." + warp_name + ".z");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the Yaw value of a warp.
	 * 
	 * @param warp_name  Name of the warp.
	 * @return  Integer, 0 if empty.
	 */
	public int getWarp_Yaw(String warp_name)
	{
		return getInt("warps." + warp_name + ".yaw");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the Pitch value of a warp.
	 * 
	 * @param warp_name  Name of the warp.
	 * @return  Integer, 0 if empty.
	 */
	public int getWarp_Pitch(String warp_name)
	{
		return getInt("warps." + warp_name + ".pitch");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the World value of a warp.
	 * 
	 * @param warp_name  Name of the warp.
	 * @return  String, null if empty.
	 */
	public String getWarp_World(String warp_name)
	{
		return getString("warps." + warp_name + ".world");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the Type value of a warp.
	 * 
	 * @param warp_name  Name of the warp.
	 * @return  String, null if empty.
	 */
	public String getWarp_Type(String warp_name)
	{
		return getString("warps." + warp_name + ".type");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the Value value of a warp.
	 * 
	 * @param warp_name  Name of the warp.
	 * @return  Integer, 0 if empty.
	 */
	public int getWarp_Value(String warp_name)
	{
		return getInt("warps." + warp_name + ".value");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns the Else value of a warp.
	 * 
	 * @param warp_name  Name of the warp.
	 * @return  String, null if empty.
	 */
	public String getWarp_Else(String warp_name)
	{
		return getString("warps." + warp_name + ".else");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns a Location variable of where the warp is.
	 * 
	 * @param warp_name  Name of the warp.
	 * @return  Location, null if incomplete.
	 */
	public Location getWarp_Location(String warp_name)
	{
		World warpWorld = Bukkit.getWorld(getWarp_World(warp_name));
		double warpX = getDouble("warps." + warp_name + ".x");
		double warpY = getDouble("warps." + warp_name + ".y");
		double warpZ = getDouble("warps." + warp_name + ".z");
		float warpYaw = (float) getDouble("warps." + warp_name + ".yaw");
		float warpPitch = (float) getDouble("warps." + warp_name + ".pitch");
		
		return new Location(warpWorld, warpX, warpY, warpZ, warpYaw, warpPitch);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns a player's warp cooldown data.
	 * 
	 * @param warp_name  Name of the warp.
	 * @param playerUUID  Player's UUID.
	 * @return  long, 0L if empty.
	 */
	public long getData_Cooldown(String warp_name, UUID playerUUID)
	{
		return getLong("data.cooldown." + warp_name + "." + playerUUID);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Sets a player's warp cooldown data.
	 * 
	 * @param warp_name Name of the warp.
	 * @param playerUUID  Player's UUID.
	 * @param data  A long of what should be set.
	 */
	public void setData_Cooldown(String warp_name, UUID playerUUID, long data)
	{
		set("data.cooldown." + warp_name + "." + playerUUID, data);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns a player's warp limit data.
	 * 
	 * @param warp_name  Name of the warp.
	 * @param playerUUID  Player's UUID.
	 * @return  int, 0 if empty.
	 */
	public int getData_Limit(String warp_name, UUID playerUUID)
	{
		return getInt("data.limit." + warp_name + "." + playerUUID);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Sets a player's warp limit data
	 * 
	 * @param warp_name  Name of the warp.
	 * @param playerUUID  Player's UUID.
	 * @param data  A int of what should be set.
	 */
	public void setData_Limit(String warp_name, UUID playerUUID, int data)
	{
		set("data.limit." + warp_name + "." + playerUUID, data);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns all Set<String> of all warps created.
	 * 
	 * @return  A set of all entries under 'warps' in the config.
	 */
	public Set<String> getWarpList()
	{
		return getConfigurationSectionKeys("warps");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Determines if the warp exists in the configuration file.
	 * 
	 * @param warp_name  The name of the warp.
	 * @return  true if the warp exists in the file, else false.
	 */
	public boolean checkWarp(String warp_name) 
	{
		if (getWarpList().contains(warp_name))
			return true;
		return false;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns a boolean depending on if a player can use a warp again or not.
	 * 
	 * @param warp_name  The name of the warp.
	 * @param playerUUID  Player UUID.
	 * @return  true if can warp, else false.
	 */
	public boolean checkCanWarp(String warp_name, UUID playerUUID)
	{
		if (getData_Limit(warp_name, playerUUID) >= getWarp_Value(warp_name))
			return true;
		return false;
	}
}
