package io.github.expugn.commands;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
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
 * @version 2.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class FreeCommand implements CommandExecutor 
{
	private final io.github.expugn.functions.ListTitles listtitles;
	private final io.github.expugn.functions.DungeonTimers timers;

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
		timers = new io.github.expugn.functions.DungeonTimers(plugin);
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
	 * <li> Links to a method 'helpMenu' on this class:
	 * 		{@link #helpMenu}.
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
				defaultMessage("Expugn").send(player);
			else 
			{
				switch (args[0].toLowerCase()) 
				{
				case "listtitles":
					listtitles.getTitles(player, args);
					break;
				case "settime":
					timers.setTime(player, args[1]);
					break;
				case "help":
					helpMenu(player);
					break;
				case "name":
					if (args.length >= 2)
					{
						Player[] onlinePlayers = Bukkit.getOnlinePlayers();
						for (Player currentPlayer : onlinePlayers) 
						{
							if (currentPlayer.getName().equals(args[1]))
							{
								defaultMessage(args[1]).send(player);
								return true;
							}
						}
						player.sendMessage(ChatColor.RED + "This player is not online on the server.");
					}
					else
						player.sendMessage(ChatColor.RED + "Invalid Arguments. /expugnfree name [name]");
					break;
				default:
					defaultMessage("Expugn").send(player);
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
	 * {@code helpMenu}: Gives help but probably not the help the player was expecting
	 */
	public void helpMenu(Player player)
	{
		player.sendMessage(ChatColor.GRAY + "Huh? You want help? Well okay...\n"
				+ ChatColor.GOLD + "USA Suicide Hotline: " + ChatColor.WHITE + "1-800-784-2433\n"
				+ ChatColor.GOLD + "SparkNotes: " + ChatColor.WHITE + "www.sparknotes.com\n"
				+ ChatColor.GOLD + "Google: " + ChatColor.WHITE + "www.google.com\n"
				+ ChatColor.GOLD + "AllRecipes: " + ChatColor.WHITE + "www.allrecipes.com\n"
				+ ChatColor.GOLD + "Minecraft Wiki: " + ChatColor.WHITE + "www.minecraft.gamepedia.com\n"
				+ ChatColor.GOLD + "Server Forums: " + ChatColor.WHITE + "www.ultimate-mc.net\n"
				+ ChatColor.GRAY + "If you need more help then look it up on Google or something.");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code defaultMessage}: Returns a JSON based message where it says "[Click Me.]" 
	 * 
	 * <p>
	 * If the player clicks the message they will say "<name> is a pretty <adj>
	 * <noun>." in game.
	 * 
	 * <ul>
	 * <li> Links to a method 'getMessage' on this class:
	 * 		{@link #getMessage}.
	 * <li> Links to a method 'getRandomColor' on this class:
	 * 		{@link #getRandomColor}.
	 * <ul>
	 * 
	 * @return  FancyMessage, not null
	 */
	public static FancyMessage defaultMessage(String name)
	{
		return new FancyMessage(getRandomColor() + "[Click Me.]")
			.tooltip("C'mon, I double dog dare you to click this.")
			.command(getMessage(name));
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code getMessage}: Returns a string to be used in {@code defaultMessage}
	 * 
	 * @return  String, not null
	 */
	public static String getMessage(String name)
	{
		Random randomNum = new Random();
		
		ArrayList<String> adj = new ArrayList<String>();
		adj.add("cool");
		adj.add("alright");
		adj.add("satisfactory");
		adj.add("acceptable");
		adj.add("adequate");
		adj.add("good");
		adj.add("passable");
		adj.add("OK");
		adj.add("funky");
		adj.add("groovy");
		adj.add("swanky");
		adj.add("fine");
		adj.add("decent");
		adj.add("average");
		adj.add("remarkable");
		adj.add("average");
		adj.add("swell");
		adj.add("trendy");
		adj.add("fantastic");
		adj.add("great");
		adj.add("rad");
		adj.add("nifty");
		adj.add("excellent");
		int adjNum = randomNum.nextInt(adj.size());
		
		ArrayList<String> noun = new ArrayList<String>();
		noun.add("guy");
		noun.add("lad");
		noun.add("person");
		noun.add("dude");
		noun.add("fellow");
		noun.add("chap");
		noun.add("individual");
		noun.add("creature");
		noun.add("being");
		noun.add("whippersnapper");
		noun.add("brat");
		noun.add("character");
		int nounNum = randomNum.nextInt(noun.size());
		
		return name + " is a pretty " + adj.get(adjNum) + " " + noun.get(nounNum) + ".";
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code getRandomColor}: Returns a color string to be used in {@code defaultMessage}
	 * 
	 * @return  String, not null
	 */
	public static String getRandomColor()
	{
		ArrayList<String> colors = new ArrayList<String>();
		Random randomNum = new Random();
		
		colors.add("§2");
		colors.add("§3");
		colors.add("§4");
		colors.add("§5");
		colors.add("§6");
		colors.add("§7");
		colors.add("§8");
		colors.add("§9");
		colors.add("§a");
		colors.add("§b");
		colors.add("§c");
		colors.add("§d");
		colors.add("§e");
		colors.add("§f");		
		
		int index = randomNum.nextInt(colors.size());
		
		return colors.get(index);
	}
}
