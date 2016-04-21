package io.github.expugn.expugnextras.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'Gollem' Command</b>
 * 
 * @version 1.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class GollemCommand implements CommandExecutor
{
	private final io.github.expugn.expugnextras.gollem.Gollem gollem;
	public GollemCommand(ExpugnExtras plugin)
	{
		gollem = new io.github.expugn.expugnextras.gollem.Gollem(plugin);
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		Player player = null;
		if (args.length == 0) 
			sender.sendMessage(ChatColor.GOLD + "Gollem Boss Battle: by " + ChatColor.RED + "Expugn\n"
					+ ChatColor.DARK_PURPLE + "Do not use this command unless you know what you are doing.");
		else 
		{
			if (sender instanceof Player)
				player = (Player) sender;
			
			switch (args[0].toLowerCase()) 
			{
			case "itemdrop":
				if (sender instanceof Player)
					gollem.run_ItemDrop(player);
				break;
			}
		}
		return true;
	}
}
