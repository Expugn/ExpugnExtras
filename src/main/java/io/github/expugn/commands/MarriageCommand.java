package io.github.expugn.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;

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
	private final io.github.expugn.functions.Marriage marriage;
	private static final String prefix = ChatColor.BLACK + "[" + ChatColor.GOLD + "Marriage" + ChatColor.BLACK + "]" + ChatColor.DARK_GRAY + " - ";
	private static final String miniPrefix = ChatColor.BLACK + "[" + ChatColor.GOLD + "*" + ChatColor.BLACK + "]" + ChatColor.DARK_GRAY + " - ";
	
	// System Messages
	private static final String OPENING_MESSAGE = prefix + ChatColor.GREEN + "Use " + ChatColor.GOLD + " /marriage help " + ChatColor.GREEN + "to view all commands.";
	// Error Messages
	private static final String MARRIAGE_NOT_ENABLED_ERROR = prefix + ChatColor.RED + "Marriage is not enabled.";
	private static final String INVALID_COMMAND_ERROR = prefix + ChatColor.RED + "Invalid Command.\n" + miniPrefix + ChatColor.RED + "Use " + ChatColor.GOLD + "/marriage help" + ChatColor.RED + " for a help menu.";
	private static final String ONLY_PLAYERS_ERROR = ChatColor.RED + "Only players can run this command.";
	private static final String INVALID_PARAMETER_ERROR = prefix + ChatColor.RED + "Invalid parameters.\n"
            + miniPrefix + ChatColor.RED + "Use " + ChatColor.GOLD + "/marriage help" + ChatColor.RED + " to check if you typed the command correctly.";
	private static final String NOT_ENOUGH_PERMISSIONS_ERROR = prefix + ChatColor.RED + "Not enough permissions.";
	// Command Strings
	private static final String HELP_COMMAND = "help";
	private static final String DATE_COMMAND = "date";
	private static final String BREAKUP_COMMAND = "breakup";
	private static final String ACCEPT_COMMAND = "accept";
	private static final String DECLINE_COMMAND = "decline";
	private static final String ADVANCE_COMMAND = "advance";
	private static final String BLOCK_COMMAND = "block";
	private static final String INFO_COMMAND = "info";
	private static final String DEV_COMMAND = "dev";
	// Help Menu
	private static final String HELP_MENU_OPENING = prefix + ChatColor.GREEN + "Help Menu\n"
			+ miniPrefix + ChatColor.GOLD + "/marriage help commands" + ChatColor.DARK_GRAY + " - " + ChatColor.GREEN + "Full command list.\n"
		    + miniPrefix + ChatColor.GOLD + "/marriage help benefits" + ChatColor.DARK_GRAY + " - " + ChatColor.GREEN + "Relationship benefit details.";
	private static final String HELP_MENU_COMMANDS = prefix + ChatColor.GREEN + "Command List\n"
            + miniPrefix + ChatColor.DARK_AQUA + "All commands start with '" + ChatColor.GOLD + "/marriage" + ChatColor.DARK_AQUA + "'.\n"
            + miniPrefix + ChatColor.GOLD + "help" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Help menu alias.\n"
            + miniPrefix + ChatColor.GOLD + "date [playername]" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Request to date a player.\n"
            + miniPrefix + ChatColor.GOLD + "breakup" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Split with your partner.\n"
            + miniPrefix + ChatColor.GOLD + "accept" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Accept a date request.\n"
            + miniPrefix + ChatColor.GOLD + "decline" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Decline a date request.\n"
            + miniPrefix + ChatColor.GOLD + "advance" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Take your relationship a step further.\n"
            + miniPrefix + ChatColor.GOLD + "block" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Allow/Deny yourself from date requests.\n"
            + miniPrefix + ChatColor.GOLD + "info [playername]" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "View the relationship status of a player.\n"
            + miniPrefix + ChatColor.GOLD + "dev" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Administrative commands. Admins only.";
	private static final String HELP_MENU_BENEFITS = prefix + ChatColor.GREEN + "Benefits Menu\n"
            + miniPrefix + ChatColor.DARK_AQUA + "All commands start with '" + ChatColor.GOLD + "/marriage help benefits" + ChatColor.DARK_AQUA + "'.\n"
            + miniPrefix + ChatColor.GOLD + "dating" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "List of dating benefits.\n"
            + miniPrefix + ChatColor.GOLD + "engaged" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "List of engaged benefits.\n"
            + miniPrefix + ChatColor.GOLD + "married" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "List of married benefits.";
	private static final String HELP_MENU_DATING = prefix + ChatColor.GREEN + "Dating Benefits\n"
            + miniPrefix + ChatColor.GOLD + "'Sneak + Right-Click'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a kiss.\n"
            + ChatColor.GRAY + "       Heals your partner.\n"
            + miniPrefix + ChatColor.GOLD + "'Right-Click w/ Mushroom Stew'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Feed your partner.\n"
            + ChatColor.GRAY + "       Fills your partner's hunger bar.";
	private static final String HELP_MENU_ENGAGED = prefix + ChatColor.GREEN + "Engagement Benefits\n"
            + miniPrefix + ChatColor.GOLD + "'Right-Click w/ Gold Pickaxe'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a gift.\n"
            + ChatColor.GRAY + "       Grants 'Haste I' for 3 minutes.\n"
            + miniPrefix + ChatColor.GOLD + "'Right-Click w/ Golden Apple'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a gift.\n"
            + ChatColor.GRAY + "       Grants 'Health Boost I' for 3 minutes.\n"
            + miniPrefix + ChatColor.DARK_AQUA + "Benefits from dating are also available.";
	private static final String HELP_MENU_MARRIED = prefix + ChatColor.GREEN + "Marriage Benefits\n"
            + miniPrefix + ChatColor.GOLD + "'Right-Click w/ Gold Chestplate'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a gift.\n"
            + ChatColor.GRAY + "       Grants 'Resistance I' for 3 minutes.\n"
            + miniPrefix + ChatColor.GOLD + "'Right-Click w/ Cake'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a gift.\n"
            + ChatColor.GRAY + "       Grants 'Speed III' for 3 minutes.\n"
            + miniPrefix + ChatColor.GOLD + "'Right-Click w/ Milk Bucket'" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Give your partner a gift.\n"
            + ChatColor.GRAY + "       Cures all negative potion effects.\n"
            + miniPrefix + ChatColor.DARK_AQUA + "Benefits from dating and engagement are also available.";
	
	
	/**
	 * Constructor for the class
	 * 
	 * @param plugin - Refers back to the main class.
	 */
	public MarriageCommand(ExpugnExtras plugin)
	{
		this.plugin = plugin;
		marriage = new io.github.expugn.functions.Marriage(plugin);
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
		if (plugin.getConfig().getBoolean("marriage") == true)
		{
			sender.sendMessage(MARRIAGE_NOT_ENABLED_ERROR);
			return true;
		}
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			onExecute(player);
			if (args.length == 0)
			{
				player.sendMessage(OPENING_MESSAGE);
			}
			else
			{
				switch (args[0].toLowerCase())
				{
					case HELP_COMMAND:
						helpMenu(player, args);
						break;
					case DATE_COMMAND:
						marriage.date(player, args);
						break;
					case BREAKUP_COMMAND:
						marriage.breakup(player, args);
						break;
					case ACCEPT_COMMAND:
						marriage.acceptRequest(player, args);
						break;
					case DECLINE_COMMAND:
						marriage.declineRequest(player, args);
						break;
					case ADVANCE_COMMAND:
						marriage.advance(player, args);
						break;
					case BLOCK_COMMAND:
						marriage.block(player, args);
						break;
					case INFO_COMMAND:
						marriage.info(player, args);
						break;
					case DEV_COMMAND:
						if (player.hasPermission("marriage.admin"))
							marriage.dev(player, args);
						else
							player.sendMessage(NOT_ENOUGH_PERMISSIONS_ERROR);
						break;
					default:
						player.sendMessage(INVALID_COMMAND_ERROR);			
						break;
				}
			}
			return true;
		}
		else
		{
			sender.sendMessage(ONLY_PLAYERS_ERROR);
			return false;
		}
	}
	/**
	 * HelpMenu - Displays commands and other info
	 * 
	 * @param player
	 * @param args
	 */
	public void helpMenu(Player player, String[] args)
	{
		if (args.length == 1)
			player.sendMessage(HELP_MENU_OPENING);
		else if (args[1].equalsIgnoreCase("commands"))
			player.sendMessage(HELP_MENU_COMMANDS);
		else if (args[1].equalsIgnoreCase("benefits"))
		{
			if(args.length == 2)
				player.sendMessage(HELP_MENU_BENEFITS);
			else
			{
				switch(args[2].toLowerCase())
				{
					case "dating":
						player.sendMessage(HELP_MENU_DATING);
						break;
					case "engaged":
						player.sendMessage(HELP_MENU_ENGAGED);
						break;
					case "married":
						player.sendMessage(HELP_MENU_MARRIED);
						break;
					default:
						player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
				}
			}
		}
		else
			player.sendMessage(INVALID_PARAMETER_ERROR);
	}
	/**
	 * onExecute - Functions that are called whenever the player runs /marriage
	 * 
	 * @param player
	 */
	public void onExecute(Player player)
	{
		marriage.checkCooldown(player);
		marriage.refundIfOffline(player);
	}
}