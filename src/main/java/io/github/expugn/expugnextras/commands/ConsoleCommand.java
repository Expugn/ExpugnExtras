package io.github.expugn.expugnextras.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'Console' Command</b>
 * 
 * @version 1.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class ConsoleCommand implements CommandExecutor 
{
	private final ExpugnExtras plugin;
	
	//-----------------------------------------------------------------------
	/**
	 * Constructor for the {@code ConsoleCommand} class.
	 * 
	 * <ul>
	 * <li> Links to a class named 'ItemDrop' in the 'functions' package: 
	 * 		{@link io.github.expugn.expugnextras.functions.ItemDrop}.
	 * </ul>
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public ConsoleCommand(ExpugnExtras plugin) 
	{
		this.plugin = plugin;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code onCommand}: Reviews the arguments a player inputs and redirects them
	 * to the appropriate function.
	 * 
	 * <ul>
	 * <li> Links to a method 'run' on a class named 'ItemDrop' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.ListTitles#getTitles}.
	 * <ul>
	 * 
	 * @param sender  Whoever sent the command
	 * @param cmd  Executed command
	 * @param label  "expugnfree"
	 * @param args  Arguments after "expugnconsole"
	 * @return  true
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (sender instanceof ConsoleCommandSender) 
		{
			switch (args[0].toLowerCase())
			{
				case "runitemdrop":
					io.github.expugn.expugnextras.functions.ItemDrop itemdrop = new io.github.expugn.expugnextras.functions.ItemDrop(plugin);
					if (args.length >= 3)
						itemdrop.run(args);
					else
						sender.sendMessage("Invalid parameters. /expugnconsole runitemdrop [itemset_name] [count]");
					break;
				default:
					sender.sendMessage("hello. You can use these commands: "
							+ "runitemdrop, ");
					break;
			}
		}
		else 
			sender.sendMessage(ChatColor.RED + "Only the console can run this command.");
		return true;
	}
}
