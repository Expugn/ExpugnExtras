package io.github.expugn.expugnextras;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * 'Marriage' Command
 * 
 * @author Expugn
 * https://github.com/Expugn
 * 
 * @version 1.0
 */
public class MarriageCommand implements CommandExecutor
{
	private final ExpugnExtras plugin;
	/**
	 * Constructor for the class
	 * 
	 * @param plugin - Refers back to the main class.
	 */
	public MarriageCommand(ExpugnExtras plugin)
	{
		this.plugin = plugin;
	}
	/**
	 * Command Manager:
	 * Reviews the arguments a player inputs and redirects them to the appropriate function.
	 * 
	 * @param sender - Whoever sent the command. (player/console)
	 * @param cmd - "marriage"
	 * @param label - null
	 * @param args - Arguments following the command "marriage"
	 * @return true if command successfully executed
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		sender.sendMessage("hello world!");
		return true;
	}
}
