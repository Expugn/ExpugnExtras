package io.github.expugn.expugnextras;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
	private final String prefix = ChatColor.BLACK + "[" + ChatColor.GOLD + "Marriage" + ChatColor.BLACK + "]" + ChatColor.DARK_GRAY + " - ";
	private final String miniPrefix = ChatColor.BLACK + "[" + ChatColor.GOLD + "*" + ChatColor.BLACK + "]" + ChatColor.DARK_GRAY + " - ";
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
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length == 0)
			{
				player.sendMessage(prefix + ChatColor.GREEN + "Use " + ChatColor.GOLD + " /marriage help " + ChatColor.GREEN + "to view all commands.");
				player.sendMessage(miniPrefix + ChatColor.GREEN + "To test: here is $10");
				ExpugnExtras.econ.depositPlayer(player, player.getWorld().getName(), 10.0);
			}
			else
			{
				plugin.getConfig().getInt("warps.midnighttime");
			}
			return true;
		}
		else
		{
			sender.sendMessage(ChatColor.RED + "Only players can run this command.");
			return false;
		}
	}
}
