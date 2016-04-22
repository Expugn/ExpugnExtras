package io.github.expugn.expugnextras.Configs;

import java.util.Calendar;

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
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Determines if midnight has passed. Resets the daily limits if true.
	 */
	public void checkMidnight()
	{
		if (getLong("midnighttime") <= System.currentTimeMillis() - 1) 
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
}
