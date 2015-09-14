package io.github.expugn.expugnextras;

import java.util.List;
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
	
	private String playerPartner = null;
	private int playerLevel = 0;
	private long playerCooldown = 0L;
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
		/*
		 * TODO
		 * - listeners
		 */
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length == 0)
			{
				player.sendMessage(prefix + ChatColor.GREEN + "Use " + ChatColor.GOLD + " /marriage help " + ChatColor.GREEN + "to view all commands.");
				player.sendMessage(miniPrefix + ChatColor.GREEN + "To test: here is $10");
				giveMoney(player, 10.0);
			}
			else
			{
				switch (args[0])
				{
					case "help":
						helpMenu(player, args);
						break;
					case "date":
						date(player, args);
						break;
					case "breakup":
						break;
					case "accept":
						break;
					case "decline":
						break;
					case "advance":
						break;
					case "block":
						break;
					case "status":
						break;
					case "dev":
						break;
					default:
						player.sendMessage(prefix + ChatColor.RED + "Invalid Command.");
						player.sendMessage(miniPrefix + ChatColor.RED + "Use " + ChatColor.GOLD + "/marriage help" + ChatColor.RED + "for a help menu.");						
						break;
				}
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
	public void helpMenu(Player player, String[] args)
	{
		if (args.length == 1)
		{
			player.sendMessage(prefix + ChatColor.GREEN + "Help Menu");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "/marriage help commands" + ChatColor.DARK_GRAY + " - " + ChatColor.GREEN + "Full command list.");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "/marriage help benefits" + ChatColor.DARK_GRAY + " - " + ChatColor.GREEN + "Relationship benefit details.");
		}
		else if (args[1].equalsIgnoreCase("commands"))
		{
			player.sendMessage(prefix + ChatColor.GREEN + "Command List");
			player.sendMessage(miniPrefix + ChatColor.DARK_AQUA + "All commands start with '" + ChatColor.GOLD + "/marriage" + ChatColor.DARK_AQUA + "'.");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "date [playername]" + ChatColor.DARK_GRAY + " - " + ChatColor.GREEN + "Request to date a player.");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "breakup" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Split with your partner.");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "accept" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Accept a date request.");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "decline" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Decline a date request.");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "advance" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Take your relationship a step further.");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "block" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Allow/Deny yourself from date requests.");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "status [playername]" + ChatColor.DARK_GRAY + " - " + ChatColor.GREEN + "View the relationship status of a player.");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "help" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Help menu alias.");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "dev" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Administrative commands.");
		}
		else if (args[1].equalsIgnoreCase("benefits"))
		{
			if(args.length == 2)
			{
				player.sendMessage(prefix + ChatColor.GREEN + "Benefits Menu");
				player.sendMessage(miniPrefix + ChatColor.DARK_AQUA + "All commands start with '" + ChatColor.GOLD + "/marriage help benefits" + ChatColor.DARK_AQUA + "'.");
				player.sendMessage(miniPrefix + ChatColor.GOLD + "dating" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "List of dating benefits.");
				player.sendMessage(miniPrefix + ChatColor.GOLD + "engaged" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "List of engaged benefits.");
				player.sendMessage(miniPrefix + ChatColor.GOLD + "married" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "List of married benefits.");
			}
			else
			{
				switch(args[2])
				{
					case "dating":
						player.sendMessage(prefix + ChatColor.GREEN + "Dating Benefits");
						player.sendMessage(miniPrefix + ChatColor.GOLD + "'Sneak + Right-Click'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a kiss.");
						player.sendMessage(ChatColor.GRAY + "       Heals your partner.");
						player.sendMessage(miniPrefix + ChatColor.GOLD + "'Right-Click w/ Mushroom Stew'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Feed your partner.");
						player.sendMessage(ChatColor.GRAY + "       Fills your partner's hunger bar.");
						break;
					case "engaged":
						player.sendMessage(prefix + ChatColor.GREEN + "Engagement Benefits");
						player.sendMessage(miniPrefix + ChatColor.GOLD + "'Right-Click w/ Gold Pickaxe'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a gift.");
						player.sendMessage(ChatColor.GRAY + "       Grants 'Haste I' for 3 minutes.");
						player.sendMessage(miniPrefix + ChatColor.GOLD + "'Right-Click w/ Golden Apple'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a gift.");
						player.sendMessage(ChatColor.GRAY + "       Grants 'Health Boost I' for 3 minutes.");
						player.sendMessage(miniPrefix + ChatColor.DARK_AQUA + "Benefits from dating are also available.");
						break;
					case "married":
						player.sendMessage(prefix + ChatColor.GREEN + "Marriage Benefits");
						player.sendMessage(miniPrefix + ChatColor.GOLD + "'Right-Click w/ Gold Chestplate'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a gift.");
						player.sendMessage(ChatColor.GRAY + "       Grants 'Resistance I' for 3 minutes.");
						player.sendMessage(miniPrefix + ChatColor.GOLD + "'Right-Click w/ Cake'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a gift.");
						player.sendMessage(ChatColor.GRAY + "       Grants 'Speed III' for 3 minutes.");
						player.sendMessage(miniPrefix + ChatColor.GOLD + "'Right-Click w/ Milk Bucket'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a gift.");
						player.sendMessage(ChatColor.GRAY + "       Cures all negative potion effects.");
						player.sendMessage(miniPrefix + ChatColor.DARK_AQUA + "Benefits from dating and engagement are also available.");
						break;
					default:
						invalidParam(player);
						break;
				}
			}
		}
		else
		{
			invalidParam(player);
		}
	}
	public void date(Player player, String[] args)
	{
		getPlayerData(player, args);
		List<String> requestingList = plugin.getConfig().getStringList("marriage.requesting");
		List<String> requestedList = plugin.getConfig().getStringList("marriage.requested");
		
		if (args.length == 2)
		{
			String response = null;
			try 
			{
				response = UUIDFetcher.getUUIDOf("Expugn").toString();
			} 
			catch (Exception e) 
			{
				System.out.println("Exception while running UUIDFetcher");
				e.printStackTrace();
				// FIXME: Remove later
				System.out.println(requestingList);
				System.out.println(requestedList);
				System.out.println(playerLevel);
			}
			String requestedPlayerPartner = plugin.getConfig().getString("marriage.players." + response + ".partner");
			System.out.println("[debug]: done with uuid fetch. " + response + " " + requestedPlayerPartner);
		}
		if (args.length == 1)
		{
			reclaim(player, args);
			return;
		}
		else if (args.length != 2)
		{
			player.sendMessage(prefix + ChatColor.RED + "Invalid Syntax.");
			player.sendMessage(miniPrefix + ChatColor.GOLD + "/marriage date [playername]");
			return;
		}
		else if (playerPartner.equals(null) == false)
		{
			player.sendMessage(prefix + ChatColor.RED + "You are already in a relationship.");
			return;
		}
		else if (System.currentTimeMillis() < playerCooldown)
		{
			player.sendMessage(prefix + ChatColor.RED + "You cannot make a request again just yet.");
			convertMilliseconds(player, playerCooldown - System.currentTimeMillis());
			return;
		}
		else if (args[1].equals(player.getName()))
		{
			player.sendMessage(prefix + ChatColor.RED + "You cannot date yourself.");
			return;
		}
		else
		{
			System.out.println("done.");
		}
	}
	public void convertMilliseconds(Player player, long milliseconds)
	{
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		if (milliseconds >= 3600000)
		{
			hours = (int) milliseconds / 3600000;
			milliseconds = milliseconds % 3600000;
		}
		if (milliseconds >= 60000)
		{
			minutes = (int) milliseconds / 60000;
			milliseconds = milliseconds % 60000;
		}
		if (milliseconds >= 1000)
		{
			seconds = (int) milliseconds / 1000;
			milliseconds = milliseconds % 1000;
		}
		player.sendMessage(miniPrefix + ChatColor.RED + "You can make a request again in: " + hours + " hours " + minutes + " minutes and " + seconds + " seconds.");
	}
	public void reclaim (Player player, String[] args)
	{
		
	}
	public void getPlayerData(Player player, String[] args)
	{
		playerPartner = plugin.getConfig().getString("marriage.player." + player.getUniqueId() + ".partner");
		playerLevel = plugin.getConfig().getInt("marriage.player." + player.getUniqueId() + ".level");
		playerCooldown = plugin.getConfig().getLong("marriage.player." + player.getUniqueId() + ".cooldown");
		
		if (playerCooldown == 0L)
			playerCooldown = 0;
	}
	public void invalidParam(Player player)
	{
		player.sendMessage(prefix + ChatColor.RED + "Invalid parameters.");
		player.sendMessage(miniPrefix + ChatColor.RED + "Use " + ChatColor.GOLD + "/marriage help" + ChatColor.RED + " to check if you typed the command correctly.");
	}
	public void giveMoney(Player player, double amount)
	{
		if (amount >= 0)
			ExpugnExtras.econ.depositPlayer(player, player.getWorld().getName(), amount);
	}
	public void takeMoney(Player player, double amount)
	{
		ExpugnExtras.econ.withdrawPlayer(player, player.getWorld().getName(), amount);
	}
	public boolean checkMoney(Player player, double amount)
	{
		if (amount >= 0)
			return ExpugnExtras.econ.has(player, amount);
		else
			return false;
	}
}
