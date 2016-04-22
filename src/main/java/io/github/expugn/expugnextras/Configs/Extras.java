package io.github.expugn.expugnextras.Configs;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'Extras' Configuration File</b>
 * 
 * @version 1.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class Extras extends ConfigurationFile
{
	//-----------------------------------------------------------------------
	/**
	 * Constructor for the Extras class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 * @param configName  The name of the configuration file to be loaded.
	 */
	public Extras(ExpugnExtras plugin) 
	{
		super(plugin, "extras");
	}
}
