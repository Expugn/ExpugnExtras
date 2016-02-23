package io.github.expugn.functions;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'DungeonTimers' Function</b>
 * 
 * @version 1.1
 * @author Expugn <i>(https://github.com/Expugn)</i>
 */
public class DungeonTimers 
{
	/* Private variables */
	File ymlFile;
	FileConfiguration config;

	/* System messages and errors */
	private static final String CAN_LOOT_MESSAGE = ChatColor.GREEN
			+ "You can claim the loot chest at the end of this dungeon.";
	private static final String CANNOT_LOOT_MESSAGE = ChatColor.RED + "You can't claim the loot chest just yet!";
	private static final String INVALID_DUNGEON_ERROR = ChatColor.RED
			+ "This dungeon does not exist. Use /expugn dungeonlist for a list of dungeons.";
	private static final String NONEXISTING_DUNGEON_ERROR = ChatColor.RED + "There is an error. Please inform Expugn.";

	//-----------------------------------------------------------------------
	/**
	 * Constructor for the {@code DungeonTimers} class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public DungeonTimers(ExpugnExtras plugin) 
	{
		ymlFile = new File(plugin.getDataFolder() + "/timers.yml");
		config = YamlConfiguration.loadConfiguration(ymlFile);
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code setDungeon}: Defines a new "dungeon" in the configuration file.
	 * 
	 * <ul>
	 * <li> Links to a method 'checkDungeon' on this class:
	 * 		{@link #checkDungeon}.
	 * </ul>
	 * 
	 * @param player  Player who sent the command
	 * @param name  Name of the dungeon
	 * @param milliseconds  Time in milliseconds when the timer will end.
	 */
	public void setDungeon(Player player, String name, long milliseconds) 
	{
		if (this.checkDungeon(name) == false) 
		{
			config.set("dungeons." + name + ".cooldown", milliseconds);
			List<String> dungeonList = config.getStringList("list");
			dungeonList.add(name);
			config.set("list", dungeonList);
			saveConfig();

			player.sendMessage(ChatColor.GREEN + "Created dungeon " + ChatColor.GOLD + name);
		} 
		else 
		{
			config.set("dungeons." + name + ".cooldown", milliseconds);
			saveConfig();

			player.sendMessage(ChatColor.GREEN + "Updated dungeon " + ChatColor.GOLD + name);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code setTime}: Records the current time into the config for a player.
	 * 
	 * <ul>
	 * <li> Links to a method 'checkDungeon' on this class:
	 * 		{@link #checkDungeon}.
	 * </ul>
	 * 
	 * @param player  Player who sent the command
	 * @param name  Name of the dungeon
	 */
	public void setTime(Player player, String name) 
	{
		if (this.checkDungeon(name) == true) 
		{
			long timeTilNext = System.currentTimeMillis() + config.getLong("dungeons." + name + ".cooldown");
			config.set("dungeons." + name + ".players." + player.getUniqueId(), timeTilNext);
			saveConfig();
		} 
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code delDungeon}: Deletes a dungeon from the supported list.
	 * 
	 * <ul>
	 * <li> Links to a method 'checkDungeon' on this class:
	 * 		{@link #checkDungeon}.
	 * </ul>
	 * 
	 * @param player  Player who sent the command
	 * @param name  Name of the dungeon
	 */
	public void delDungeon(Player player, String name) 
	{
		if (this.checkDungeon(name) == false) 
			player.sendMessage(INVALID_DUNGEON_ERROR);
		else 
		{
			config.set("dungeons." + name, null);
			List<String> dungeonList = config.getStringList("list");
			dungeonList.remove(name);
			config.set("list", dungeonList);
			saveConfig();

			player.sendMessage(ChatColor.GREEN + "Dungeon " + name + " has been removed.");
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code checkTime}: Checks how much time a player has before they can loot a
	 * chest again.
	 * 
	 * <ul>
	 * <li> Links to a method 'checkDungeon' on this class:
	 * 		{@link #checkDungeon}.
	 * </ul>
	 * 
	 * @param player  Player who sent the command
	 * @param name  Name of the dungeon
	 */
	public void checkTime(Player player, String name) 
	{
		if (this.checkDungeon(name) == false) 
			player.sendMessage(NONEXISTING_DUNGEON_ERROR);
		else 
		{
			long playerTime = config.getLong("dungeons." + name + ".players." + player.getUniqueId());
			if (playerTime <= System.currentTimeMillis()) 
			{
				player.sendMessage(CAN_LOOT_MESSAGE);
				config.set("dungeons." + name + ".players." + player.getUniqueId(), null);
				saveConfig();
			} 
			else 
			{
				long timeLeft = playerTime - System.currentTimeMillis();
				int hours = 0;
				int minutes = 0;
				int seconds = 0;
				if (playerTime >= 3600000) 
				{
					hours = (int) timeLeft / 3600000;
					timeLeft = timeLeft % 3600000;
				}
				if (timeLeft >= 60000) 
				{
					minutes = (int) timeLeft / 60000;
					timeLeft = timeLeft % 60000;
				}
				if (timeLeft >= 1000) {
					seconds = (int) timeLeft / 1000;
					timeLeft = timeLeft % 1000;
				}
				player.sendMessage(CANNOT_LOOT_MESSAGE);
				player.sendMessage(ChatColor.RED + "You can claim it again in: " + hours + " hours " + minutes
						+ " minutes and " + seconds + " seconds.");
			}
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code dungeonList}: Displays a list of supported dungeons to the player.
	 * 
	 * @param player  Player who sent the command
	 */
	public void dungeonList(Player player) 
	{
		List<String> dungeonList = config.getStringList("list");
		player.sendMessage(ChatColor.GOLD + "There are currently " + dungeonList.size() + " dungeons");
		for (String s : dungeonList) 
		{
			player.sendMessage("- " + s);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code checkDungeon}: Checks if the supplied dungeon is supported.
	 * 
	 * @param name  Player who sent the command
	 * @return 
	 * 		<li> {@code true}  if the dungeon exists in the file.
	 * 		<li> {@code false}  if the dungeon does not exist in the file.
	 */
	public boolean checkDungeon(String name) {
		List<String> dungeonList = config.getStringList("list");
		if (dungeonList.contains(name))
			return true;
		return false;
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code saveConfig}: Saves timers.yml.
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
