package io.github.expugn.expugnextras.expugn.Timers;

import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'DungeonTimers' Function</b>
 * 
 * @version 1.2.1
 * @author Expugn <i>(https://github.com/Expugn)</i>
 */
public class DungeonTimers 
{
	private final io.github.expugn.expugnextras.Configs.Timers config;

	//-----------------------------------------------------------------------
	/**
	 * Constructor for the DungeonTimers class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public DungeonTimers(ExpugnExtras plugin) 
	{
		config = new io.github.expugn.expugnextras.Configs.Timers(plugin);
	}

	//-----------------------------------------------------------------------
	/**
	 * Defines a new "dungeon" in the configuration file.
	 * 
	 * @param player  Player who sent the command.
	 * @param name  Name of the dungeon.
	 * @param milliseconds  Time in milliseconds when the timer will end.
	 */
	public void setDungeon(Player player, String name, long milliseconds) 
	{
		if (!config.checkDungeon(name)) 
		{
			config.createDungeon(name, milliseconds);
			player.sendMessage("§aCreated dungeon §6" + name);
		} 
		else 
		{
			config.changeDungeonTime(name, milliseconds);
			player.sendMessage("§aUpdated dungeon §6" + name);
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Records the current time into the config for a player.
	 * 
	 * @param player  Player who sent the command.
	 * @param name  Name of the dungeon.
	 */
	public void setTime(Player player, String name) 
	{
		if (config.checkDungeon(name)) 
		{
			long timeTilNext = System.currentTimeMillis() + config.getLong("dungeons." + name + ".cooldown");
			config.setPlayerTime(name, player.getUniqueId(), timeTilNext);
		} 
	}

	//-----------------------------------------------------------------------
	/**
	 * Deletes a dungeon from the supported list.
	 * 
	 * @param player  Player who sent the command.
	 * @param name  Name of the dungeon.
	 */
	public void delDungeon(Player player, String name) 
	{
		if (!config.checkDungeon(name)) 
			player.sendMessage("§cThis dungeon does not exist. Use §6/expugn dungeonlist §cfor a list of dungeons.");
		else 
		{
			config.deleteDungeon(name);
			player.sendMessage("§aDungeon §6" + name + "§a has been removed.");
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Checks how much time a player has before they can loot a chest again.
	 * 
	 * @param player  Player who sent the command.
	 * @param name  Name of the dungeon.
	 */
	public void checkTime(Player player, String name) 
	{
		if (!config.checkDungeon(name)) 
			player.sendMessage("§cThere is an error. Please inform a server administrator.");
		else 
		{
			long playerTime = config.getPlayerTime(name, player.getUniqueId());
			if (playerTime <= System.currentTimeMillis()) 
			{
				player.sendMessage("§aYou can claim the loot chest at the end of this dungeon.");
				config.setPlayerTime(name, player.getUniqueId(), null);
			} 
			else 
			{
				long timeLeft = config.getPlayerTimeLeft(name, player.getUniqueId());
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
				player.sendMessage("§cYou can't claim the loot chest just yet!");
				player.sendMessage("§cYou can claim it again in: "
						+ hours + " hours "
						+ minutes + " minutes and "
						+ seconds + " seconds.");
			}
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Displays a list of supported dungeons to the player.
	 * 
	 * @param player  Player who sent the command.
	 */
	public void dungeonList(Player player) 
	{
		player.sendMessage("§6There are currently §c" + config.getDungeonList().size() + "§6 dungeons");
		for (String s : config.getDungeonList()) 
		{
			player.sendMessage("- " + s);
		}
	}


}
