package io.github.expugn.expugnextras;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpugnCommand implements CommandExecutor
{
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length == 0)
			{
				player.sendMessage(ChatColor.GOLD + "Welcome to ExpugnExtras. Use /expugn help for a help menu.");
			}
			else if (args[0] == "help")
			{
				helpMenu(player);
			}
			else
			{
				player.sendMessage(ChatColor.RED + "Invalid Command. Use /expugn help for a help menu.");
			}
			return true;
		}
		else
		{
			sender.sendMessage("Only players can run this command.");
			return false;
		}
	}
	public void helpMenu(Player player)
	{
		player.sendMessage(ChatColor.GOLD + "ExpugnExtras Help Menu:");
		player.sendMessage(ChatColor.GOLD + "(All Commands begin with /expugn)");
		player.sendMessage(ChatColor.GREEN + "- General:");
		player.sendMessage("  - help - Help Menu");
		player.sendMessage(ChatColor.GREEN + "- Warps:");
		player.sendMessage("  - warp [warpname] - Warp to a destination.");
		player.sendMessage("  - warplist - Lists warps managed by ExpugnExtras.");
		player.sendMessage("  - warp [warpname] - Warp to a destination.");
		player.sendMessage("  - setwarp [warpname] - Define a warp location.");
		player.sendMessage("  - warpsetting [warpname] <cooldown|limit> - Defines a warp to use a cooldown system or a daily limit.");
		player.sendMessage("  - warpsetting [warpname] [number] - Sets the hours for a cooldown or the daily limit.");
	}
}
