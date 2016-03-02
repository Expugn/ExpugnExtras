package io.github.expugn.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;
import io.github.expugn.fanciful.FancyMessage;

/**
 * <b>'Free' Command</b>
 * 
 * @version 1.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class FreeCommand implements CommandExecutor 
{
	private final io.github.expugn.functions.ListTitles listtitles;

	//-----------------------------------------------------------------------
	/**
	 * Constructor for the {@code FreeCommand} class.
	 * 
	 * <ul>
	 * <li> Links to a class named 'ListTitles' in the 'functions' package: 
	 * 		{@link io.github.expugn.functions.ListTitles}.
	 * </ul>
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public FreeCommand(ExpugnExtras plugin) 
	{
		listtitles = new io.github.expugn.functions.ListTitles(plugin);
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code onCommand}: Reviews the arguments a player inputs and redirects them
	 * to the appropriate function.
	 * 
	 * <ul>
	 * <li> Links to a method 'getTitles' on a class named 'ListTitles' in the 'functions' package:
	 * 		{@link io.github.expugn.functions.ListTitles#getTitles}.
	 * <li> Links to a method 'defaultMessage' on this class:
	 * 		{@link #defaultMessage}.
	 * <ul>
	 * 
	 * @param sender  Whoever sent the command
	 * @param cmd  Executed command
	 * @param label  "expugnfree"
	 * @param args  Arguments after "expugnfree"
	 * @return  true
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (sender instanceof Player) 
		{
			Player player = (Player) sender;
			if (args.length == 0) 
				defaultMessage().send(player);
			else 
			{
				switch (args[0].toLowerCase()) 
				{
				case "listtitles":
					listtitles.getTitles(player, args);
					break;
				default:
					defaultMessage().send(player);
					break;
				}
			}
		} 
		else 
			sender.sendMessage(ChatColor.RED + "Only players can run this command.");
		return true;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code defaultMessage}: Returns a JSON based message where it says "[Click Me.]" 
	 * <p>
	 * If the player clicks the message they will say "Expugn is a pretty cool
	 * guy." in game.
	 * 
	 * @return  FancyMessage, not null
	 */
	public static FancyMessage defaultMessage()
	{
		return new FancyMessage(ChatColor.RED + "[Click Me.]")
			.tooltip("You know you want to click that.")
			.command("Expugn is a pretty cool guy.");
	}
}
