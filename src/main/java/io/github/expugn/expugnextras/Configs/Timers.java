package io.github.expugn.expugnextras.Configs;

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
	}
}
