package io.github.expugn.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;

public class FreeCommand implements CommandExecutor
{
	private final io.github.expugn.functions.ListTitles listtitles;
	public FreeCommand(ExpugnExtras plugin)
	{
		listtitles = new io.github.expugn.functions.ListTitles(plugin);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length == 0)
			{
				player.sendMessage(ChatColor.RED + "Hello World!");
			}
			else
			{
				switch (args[0].toLowerCase())
				{
				case "listtitles":
					listtitles.getTitles(player);
					break;
				default:
					player.sendMessage(ChatColor.RED + "Hello World!");
					break;
				}
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
