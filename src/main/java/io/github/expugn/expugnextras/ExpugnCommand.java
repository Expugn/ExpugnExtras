package io.github.expugn.expugnextras;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * 'Expugn' Command
 * 
 * @author Expugn
 * https://github.com/Expugn
 * 
 * @version 2.0
 */
public class ExpugnCommand implements CommandExecutor
{
	private final io.github.expugn.functions.Warps warps;
	private final io.github.expugn.functions.DungeonTimers timers;
	private final io.github.expugn.functions.TimeTrial trials;
	private final io.github.expugn.functions.ListTitles listtitles;
	
	// System Messages
	private static final String OPENING_MESSAGE = ChatColor.GOLD + "Welcome to ExpugnExtras. Use /expugn help for a help menu.";
	
	// Error Messages
	private static final String INVALID_COMMAND_ERROR = ChatColor.RED + "Invalid Command. Use /expugn help for a help menu.";
	private static final String INVALID_PARAMETER_ERROR = ChatColor.RED + "Invalid parameters. Use /expugn help to check if you typed the command correctly.";
	private static final String PLAYERS_ONLY_ERROR = ChatColor.RED + "Only players can run this command.";
	
	// Help Menu
	private static final String HELP_MENU = ChatColor.GOLD +  "ExpugnExtras Help Menu:\n"
			                              +                   "(All commands begin with /expugn)\n"
			                              + ChatColor.GREEN + "- General:\n"
			                              + ChatColor.WHITE + "  - help - Help Menu\n"
			                              + ChatColor.GREEN + "- Warps:\n"
			                              + ChatColor.WHITE + "  - warp [warpname] - Warp to a destination.\n"
			                              +                   "  - warpinfo [warpname] - Get details of a warp.\n"
			                              +                   "  - warplist - Lists warps managed by ExpugnExtras.\n"
			                              +                   "  - setwarp [warpname] - Define a warp location.\n"
			                              +                   "  - delwarp [warpname] - Remove a warp location.\n"
			                              +                   "  - warpsetting [warpname] <cooldown|limit> - Defines a warp to use a cooldown system or a daily limit.\n"
			                              +                   "  - warpsetting [warpname] [number] - Sets the hours for a cooldown or the daily limit.\n"
			                              +                   "  - warpsetting [warpname] [warpname] - Sets an alternate warp to move the player if the player could not warp.\n"
			                              + ChatColor.GREEN + "- Dungeon Cooldown Timer:\n"
			                              + ChatColor.WHITE + "  - dungeonlist - Lists all dungeons.\n"
			                              +                   "  - setdungeon [dungeon] [dungeon loot cooldown in milliseconds] - Set a new 'dungeon' to be recorded.\n"
			                              +                   "  - deldungeon [dungeon] - Removes a dungeon from the configuration file.\n"
			                              +                   "  - settime [dungeon] - Records the current time for the user.\n"
			                              +                   "  - checktime [dungeon] - Displays the time remaining or if it's ready.\n"
			                              + ChatColor.GREEN + "- Time Trials:\n"
			                              + ChatColor.WHITE + "  - setlocation [name] - Creates a new location.\n"
			                              +                   "  - dellocation [name] - Deletes a location.\n"
			                              +                   "  - locationlist - Lists all available locations.\n"
			                              +                   "  - starttrial [name] - Begins a new time trial.\n"
			                              +                   "  - endtrial [name] - Ends a time trial.\n"
			                              +                   "  - getrankings [name] - Gets the rankings of a location.";
	
	// Command Strings
	private static final String HELP_COMMAND = "help";
	private static final String WARP_COMMAND = "warp";
	private static final String WARP_INFO_COMMAND = "warpinfo";
	private static final String WARP_LIST_COMMAND = "warplist";
	private static final String SET_WARP_COMMAND = "setwarp";
	private static final String DELETE_WARP_COMMAND = "delwarp";
	private static final String WARP_SETTING_COMMAND = "warpsetting";
	private static final String DUNGEON_LIST_COMMAND = "dungeonlist";
	private static final String SET_DUNGEON_COMMAND = "setdungeon";
	private static final String DELETE_DUNGEON_COMMAND = "deldungeon";
	private static final String SET_TIME_COMMAND = "settime";
	private static final String CHECK_TIME_COMMAND = "checktime";
	private static final String SET_LOCATION_COMMAND = "setlocation";
	private static final String DELETE_LOCATION_COMMAND = "dellocation";
	private static final String LOCATION_LIST_COMMAND = "locationlist";
	private static final String START_TRIAL_COMMAND = "starttrial";
	private static final String END_TRIAL_COMMAND = "endtrial";
	private static final String GET_RANKINGS_COMMAND = "getrankings";
	private static final String LIST_TITLES_COMMAND = "listtitles";
	
	/**
	 * Constructor for the class
	 * 
	 * @param plugin - Refers back to main class.
	 */
	public ExpugnCommand(ExpugnExtras plugin)
	{
		warps = new io.github.expugn.functions.Warps(plugin);
		timers = new io.github.expugn.functions.DungeonTimers(plugin);
		trials = new io.github.expugn.functions.TimeTrial(plugin);
		listtitles = new io.github.expugn.functions.ListTitles(plugin);
	}
	/**
	 * Command Manager:
	 * Reviews the arguments a player inputs and redirects them to the appropriate function.
	 * 
	 * @param sender - Whoever sent the command. (player/console)
	 * @param cmd - "expugn"
	 * @param label - null
	 * @param args - Arguments following the command "expugn"
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			onExecute();
			if (args.length == 0)
			{
				player.sendMessage(OPENING_MESSAGE);
			}
			else
			{
				switch(args[0].toLowerCase())
				{ 
					case HELP_COMMAND: 
						helpMenu(player);
						break;
					case WARP_LIST_COMMAND:
						warps.warpList(player);
						break;
					case WARP_INFO_COMMAND:
						if (args.length >= 2)
							warps.warpInfo(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case WARP_COMMAND:
						if (args.length >= 2)
							warps.warp(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case SET_WARP_COMMAND:
						if (args.length >= 2)
							warps.setWarp(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case DELETE_WARP_COMMAND:
						if (args.length >= 2)
							warps.delWarp(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case WARP_SETTING_COMMAND:
						if (args.length >= 3)
							warps.warpSetting(player, args[1], args[2]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case SET_DUNGEON_COMMAND:
						if (args.length >= 3)
							timers.setDungeon(player, args[1], Long.parseLong(args[2]));
						else
						{
							if (args.length >= 2)
								timers.setDungeon(player, args[1], Long.parseLong("72000000"));
							else
								player.sendMessage(INVALID_PARAMETER_ERROR);
						}
						break;
					case SET_TIME_COMMAND:
						if (args.length >= 2)
							timers.setTime(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case DELETE_DUNGEON_COMMAND:
						if (args.length >= 2)
							timers.delDungeon(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case CHECK_TIME_COMMAND:
						if (args.length >= 2)
							timers.checkTime(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case DUNGEON_LIST_COMMAND:
							timers.dungeonList(player);
						break;
					case SET_LOCATION_COMMAND:
						if (args.length >= 2)
							trials.setLocation(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case DELETE_LOCATION_COMMAND:
						if (args.length >= 2)
							trials.delLocation(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case LOCATION_LIST_COMMAND:
						trials.locationList(player);
						break;
					case START_TRIAL_COMMAND:
						if (args.length >= 2)
							trials.startTrial(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case END_TRIAL_COMMAND:
						if (args.length >= 2)
							trials.endTrial(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case GET_RANKINGS_COMMAND:
						if (args.length >= 2)
							trials.getRankings(player, args[1]);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
						break;
					case LIST_TITLES_COMMAND:
						if (args.length == 1)
							listtitles.getTitles(player);
						else
							player.sendMessage(INVALID_PARAMETER_ERROR);
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
			sender.sendMessage(PLAYERS_ONLY_ERROR);
			return false;
		}
	}
	/**
	 * Help Menu:
	 * Displays a helpful guide of the available commands involving /expugn
	 * 
	 * @param player - The player who sent the command
	 */
	public void helpMenu(Player player)
	{
		player.sendMessage(HELP_MENU);
	}
	
	/**
	 * On Execute:
	 * Runs these functions whenever /expugn is used.
	 */
	public void onExecute()
	{
		warps.checkMidnight();
		trials.checkProgress();
	}
	
}
