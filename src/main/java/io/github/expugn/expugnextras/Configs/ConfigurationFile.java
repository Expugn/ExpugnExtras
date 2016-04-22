package io.github.expugn.expugnextras.Configs;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'ConfigurationFile' Configuration File</b>
 * 
 * @version 1.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class ConfigurationFile 
{
	private String configName;
	private static File ymlFile;
	private static FileConfiguration config;
	private ExpugnExtras plugin;
	
	//-----------------------------------------------------------------------
	/**
	 * Constructor for the ConfigurationFile class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 * @param configName  The name of the configuration file to be loaded.
	 */
	public ConfigurationFile(ExpugnExtras plugin, String configName)
	{
		this.configName = configName;
		this.plugin = plugin;
		
		ymlFile = new File(plugin.getDataFolder() + "/" + configName + ".yml");
		config = YamlConfiguration.loadConfiguration(ymlFile);
		
		saveDefaultConfig();
		reloadConfig();
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Sets data into the configuration file in the defined path.
	 * 
	 * @param path  The path in the configuration file.
	 * @param data  The data to be added.
	 */
	public void set(String path, Object data)
	{
		config.set(path, data);
		saveConfig();
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets an Object from the configuration file in the defined path.
	 * 
	 * @param path  The path in the configuration file.
	 * @return  Object stored in the path; null if empty
	 */
	public Object get(String path)
	{
		return config.get(path);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets a String object from the configuration file in the defined path.
	 * 
	 * @param path  The path in the configuration file.
	 * @return  String stored in the path; null if empty.
	 */
	public String getString(String path)
	{
		return config.getString(path);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets an Integer object from the configuration file in the defined path.
	 * 
	 * @param path  The path in the configuration file.
	 * @return  Integer stored in the path; 0 if empty.
	 */
	public int getInt(String path)
	{
		return config.getInt(path);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets a Long object from the configuration file in the defined path.
	 * 
	 * @param path  The path in the configuration file.
	 * @return  Long stored in the path; 0 if empty.
	 */
	public long getLong(String path)
	{
		return config.getLong(path);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets a boolean object from the configuration file in the defined path.
	 * 
	 * @param path  The path in the configuration file.
	 * @return  Boolean stored in the path; false if empty.
	 */
	public boolean getBoolean(String path)
	{
		return config.getBoolean(path);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets a StringList from the configuration file in the defined path.
	 * 
	 * @param path  The path in the configuration file.
	 * @return  List<String> stored in the path, null if empty.
	 */
	public List<String> getStringList(String path)
	{
		return config.getStringList(path);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Checks if the ConfigurationSection from the configuration file in the
	 * defined path exists.
	 * 
	 * @param path  The path in the configuration file.
	 * @return  true if it is a ConfigurationSection, else false.
	 */
	public boolean isConfigurationSection(String path)
	{
		return config.isConfigurationSection(path);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets a ConfigurationSection from the configuration file in the 
	 * defined path.
	 * 
	 * @param path  The path in the configuration file.
	 * @return  ConfigurationSection stored in the path, null if empty.
	 */
	public ConfigurationSection getConfigurationSection(String path)
	{
		return config.getConfigurationSection(path);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets ConfigurationSection Keys from the configuration file in the 
	 * defined path.
	 * 
	 * @param path  The path in the configuration file.
	 * @return  Set<String> of keys of a ConfigurationSection stored in the path, null if empty.
	 */
	public Set<String> getConfigurationSectionKeys(String path)
	{
		return config.getConfigurationSection(path).getKeys(false);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Saves the configuration file.
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
	
	//-----------------------------------------------------------------------
	/**
	 * Reloads the configuration file. It will get a default configuration 
	 * file if it exists in the JAR.
	 */
	public void reloadConfig()
	{
		if (ymlFile == null)
			ymlFile = new File(plugin.getDataFolder() + "/" + configName + ".yml");
		config = YamlConfiguration.loadConfiguration(ymlFile);	
		
		/* Look for defaults */
		Reader defConfigStream = null;
		try 
		{
			defConfigStream = new InputStreamReader(plugin.getResource(configName + ".yml"), "UTF8");
		} 
		catch (UnsupportedEncodingException e) 
		{
			e.printStackTrace();
		}
		if (defConfigStream != null)
		{
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Copies data over from the default config file stored in the JAR file.
	 */
	public void saveDefaultConfig()
	{
		if (ymlFile == null)
			ymlFile = new File(plugin.getDataFolder() + "/" + configName + ".yml");
		if (!ymlFile.exists())
		{
			plugin.saveResource(configName + ".yml", false);
		}
	}
}
