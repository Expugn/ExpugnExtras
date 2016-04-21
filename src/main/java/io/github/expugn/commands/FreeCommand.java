package io.github.expugn.commands;

import java.io.File;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
	private File ymlFile;
	private static FileConfiguration config;
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
		ymlFile = new File(plugin.getDataFolder() + "/extras.yml");
		config = YamlConfiguration.loadConfiguration(ymlFile);
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
				case "realhelp":
					realHelpMenu(player);
					break;
				case "rpc":
					if (args.length == 1)
						rockPaperScissors(player);
					else if (args.length >= 2)
						rockPaperScissors(player, Integer.parseInt(args[1]));
					break;
				case "name":
					if (args.length >= 2)
					{
						Player[] onlinePlayers = Bukkit.getOnlinePlayers();
						for (Player currentPlayer : onlinePlayers) 
						{
							if (currentPlayer.getName().equals(args[1]) || args[1].equals("Expugn"))
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
					player.sendMessage(ChatColor.RED + "Unknown Command. Use '" + ChatColor.GOLD + "/expugnfree help" + ChatColor.RED + "' for a help menu.");
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
		new FancyMessage(ChatColor.GRAY + "Huh? You want help? Well okay...\n"
				+ ChatColor.GOLD + "USA Suicide Hotline: " + ChatColor.WHITE + "1-800-784-2433\n"
				+ ChatColor.GOLD + "SparkNotes: " + ChatColor.WHITE + "www.sparknotes.com\n"
				+ ChatColor.GOLD + "Google: " + ChatColor.WHITE + "www.google.com\n"
				+ ChatColor.GOLD + "AllRecipes: " + ChatColor.WHITE + "www.allrecipes.com\n"
				+ ChatColor.GOLD + "Minecraft Wiki: " + ChatColor.WHITE + "www.minecraft.gamepedia.com\n"
				+ ChatColor.GOLD + "Server Forums: " + ChatColor.WHITE + "www.ultimate-mc.net\n"
				+ ChatColor.GRAY + "If you need more help then look it up on Google or something.\n")
		.then("[But... This isn't the type of help I wanted..]")
		.color(ChatColor.DARK_AQUA)
		.command("/expugnfree realhelp")
		.tooltip("Click me.")
		.send(player);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code realHelpMenu}: Gives real help unlike the other help menu.
	 */
	public void realHelpMenu(Player player)
	{
		new FancyMessage(ChatColor.GRAY + "Okay, okay... Here's a 'real' help menu.\n")
				.then(ChatColor.GOLD + "/expugnfree\n")
				.tooltip("???")
				.suggest("/expugnfree")
				.then(ChatColor.GOLD + "/expugnfree name [playername]\n")
				.tooltip("Compliment someone!")
				.suggest("/expugnfree name [playername]")
				.then(ChatColor.GOLD + "/expugnfree help\n")
				.tooltip("You're looking at the help menu already you silly goose.")
				.suggest("/expugnfree help")
				.then(ChatColor.GOLD + "/expugnfree rpc\n")
				.tooltip("Fancy a game of Rock, Paper, Scissors?")
				.suggest("/expugnfree rpc")
				.then(ChatColor.GRAY + "You can click on a command to have it typed out for ya.")
				.send(player);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code defaultMessage}: Returns a JSON based message where it says "[Click Me.]" 
	 * 
	 * <p>
	 * If the player clicks the message they will say "[name] is a [adverb] [adjective]
	 * [noun]." in game.
	 * 
	 * <ul>
	 * <li> Links to a method 'getMessage' on this class:
	 * 		{@link #getMessage}.
	 * <li> Links to a method 'getRandomColor' on this class:
	 * 		{@link #getRandomColor}.
	 * <ul>
	 * 
	 * @param name  Player name
	 * @return  FancyMessage, not null
	 */
	public static FancyMessage defaultMessage(String name)
	{
		return new FancyMessage(getRandomColor() + "[Click Me.]")
			.tooltip("Give it a shot!")
			.command(getMessage(name))
			.then(" | ")
			.then(getRandomColor() + "[Generate another?]")
			.tooltip("Want another message?")
			.command("/expugnfree name " + name);
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
		
		List<String> adverb = config.getStringList("words.adverbs");
		int adverbNum = randomNum.nextInt(adverb.size());
		
		List<String> adj = config.getStringList("words.adjectives");
		int adjNum = randomNum.nextInt(adj.size());
		
		List<String> noun = config.getStringList("words.nouns");
		int nounNum = randomNum.nextInt(noun.size());
		
		return name + " is a " + adverb.get(adverbNum) + " " + adj.get(adjNum) + " " + noun.get(nounNum) + ".";
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code getRandomColor}: Returns a color string to be used in {@code defaultMessage}
	 * 
	 * @return  String, not null
	 */
	public static String getRandomColor()
	{
		Random randomNum = new Random();
		
		List<String> colors = config.getStringList("words.colors");
		int index = randomNum.nextInt(colors.size());
		
		return colors.get(index);
	}
	
	public void rockPaperScissors(Player player)
	{
		new FancyMessage(ChatColor.GRAY + "Rock, Paper, or Scissors?\n")
			.then(ChatColor.DARK_GRAY + "[Rock]")
				.tooltip("Click Me: [Rock]")
				.command("/expugnfree rpc 0")
			.then("  ")
			.then(ChatColor.WHITE + "[Paper]")
				.tooltip("Click Me: [Paper]")
				.command("/expugnfree rpc 1")
			.then("  ")
			.then(ChatColor.RED + "[Scissors]")
				.tooltip("Click Me: [Scissors]")
				.command("/expugnfree rpc 2")
			.send(player);
	}
	
	public void rockPaperScissors(Player player, int command)
	{
		Random r = new Random();
		int rpc = r.nextInt(3);
		if (command == 0) // Rock
		{
			if(rpc == 0)
				player.sendMessage("§8Rock§7!... Looks like it's a tie.");
			else if (rpc == 1)
				player.sendMessage("§fPaper§7!... Better luck next time.");
			else if (rpc == 2)
				player.sendMessage("§cScissors§7!... You win this round.");
		}
		else if (command == 1) // Paper
		{
			if(rpc == 0)
				player.sendMessage("§8Rock§7!... You win this round.");
			else if (rpc == 1)
				player.sendMessage("§fPaper§7!... Looks like it's a tie.");
			else if (rpc == 2)
				player.sendMessage("§cScissors§7!... Better luck next time.");
		}
		else if (command == 2) // Scissors
		{
			if(rpc == 0)
				player.sendMessage("§8Rock§7!... Better luck next time.");
			else if (rpc == 1)
				player.sendMessage("§fPaper§7!... You win this round.");
			else if (rpc == 2)
				player.sendMessage("§cScissors§7!... Looks like it's a tie.");
		}
		rockPaperScissors(player);
	}
}
