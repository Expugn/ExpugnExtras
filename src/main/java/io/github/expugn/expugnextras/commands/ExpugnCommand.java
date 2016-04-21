package io.github.expugn.expugnextras.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'Expugn' Command</b>
 * 
 * @version 2.2
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class ExpugnCommand implements CommandExecutor 
{
	/* Private variables */
	private final io.github.expugn.expugnextras.functions.Warps warps;
	private final io.github.expugn.expugnextras.functions.DungeonTimers timers;
	private final io.github.expugn.expugnextras.functions.TimeTrial trials;
	private final io.github.expugn.expugnextras.functions.ListTitles listtitles;
	private final io.github.expugn.expugnextras.functions.ItemDrop itemdrop;

	/* System messages and errors */
	private static final String OPENING_MESSAGE = ChatColor.GOLD
			+ "Welcome to ExpugnExtras. Use /expugn help for a help menu.";
	private static final String INVALID_COMMAND_ERROR = ChatColor.RED
			+ "Invalid Command. Use /expugn help for a help menu.";
	private static final String INVALID_PARAMETER_ERROR = ChatColor.RED
			+ "Invalid parameters. Use /expugn help to check if you typed the command correctly.";
	private static final String PLAYERS_ONLY_ERROR = ChatColor.RED + "Only players can run this command.";

	/* Help menu */
	private static final String HELP_MENU = ChatColor.GOLD + "ExpugnExtras Help Menu:\n"
			+ "(All commands begin with /expugn)\n" + ChatColor.GREEN + "- General:\n" + ChatColor.WHITE
			+ "  - help - Help Menu\n" + ChatColor.GREEN + "- Warps:\n" + ChatColor.WHITE
			+ "  - warp [warpname] - Warp to a destination.\n" + "  - warpinfo [warpname] - Get details of a warp.\n"
			+ "  - warplist - Lists warps managed by ExpugnExtras.\n"
			+ "  - setwarp [warpname] - Define a warp location.\n"
			+ "  - delwarp [warpname] - Remove a warp location.\n"
			+ "  - warpsetting [warpname] <cooldown|limit> - Defines a warp to use a cooldown system or a daily limit.\n"
			+ "  - warpsetting [warpname] [number] - Sets the hours for a cooldown or the daily limit.\n"
			+ "  - warpsetting [warpname] [warpname] - Sets an alternate warp to move the player if the player could not warp.\n"
			+ "  - checkcanwarp [warpname] - Check if you can warp to a warp.\n" + ChatColor.GREEN
			+ "- Dungeon Cooldown Timer:\n" + ChatColor.WHITE + "  - dungeonlist - Lists all dungeons.\n"
			+ "  - setdungeon [dungeon] [dungeon loot cooldown in milliseconds] - Set a new 'dungeon' to be recorded.\n"
			+ "  - deldungeon [dungeon] - Removes a dungeon from the configuration file.\n"
			+ "  - settime [dungeon] - Records the current time for the user.\n"
			+ "  - checktime [dungeon] - Displays the time remaining or if it's ready.\n" 
			+ ChatColor.GREEN + "- Time Trials:\n" 
			+ ChatColor.WHITE + "  - setlocation [name] - Creates a new location.\n"
			+ "  - dellocation [name] - Deletes a location.\n" + "  - locationlist - Lists all available locations.\n"
			+ "  - starttrial [name] - Begins a new time trial.\n" + "  - endtrial [name] - Ends a time trial.\n"
			+ "  - getrankings [name] - Gets the rankings of a location.\n"
			+ "  - resettimes [name] - Resets the times of a location.\n"
			+ "  - hallofglory - Teleports the player to a ExpugnExtras warp named 'HallOfGlory'.\n" 
			+ ChatColor.GREEN + "- Item Drop:\n"
			+ ChatColor.WHITE + "  - createitemset [name] - Creates a new ItemSet.\n"
			+ "  - deleteitemset [name] - Deletes an ItemSet.\n"
			+ "  - additem [name] - Adds the item in your hand to an ItemSet.\n"
			+ "  - removeitem [name] [index] - Removes the item assigned to the index from an ItemSet.\n"
			+ "  - giveitem [name] [index] - Spawns the item assigned to the index from an ItemSet.\n"
			+ "  - listitemset - Lists all ItemSets created.\n"
			+ "  - itemsetinfo [name] <Page_#> - Gets the info of an ItemSet.\n"
			+ "  - runitemdrop [name] [item_count] - Runs ItemDrop with an ItemSet.\n"
			+ ChatColor.GREEN + "- Miscellaneous:\n" 
			+ ChatColor.WHITE + "  - listtitles - Displays all titles a player owns.";

	/* Command strings */
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
	private static final String CHECK_CAN_WARP_COMMAND = "checkcanwarp";
	private static final String SET_TIME_COMMAND = "settime";
	private static final String CHECK_TIME_COMMAND = "checktime";
	private static final String SET_LOCATION_COMMAND = "setlocation";
	private static final String DELETE_LOCATION_COMMAND = "dellocation";
	private static final String LOCATION_LIST_COMMAND = "locationlist";
	private static final String START_TRIAL_COMMAND = "starttrial";
	private static final String END_TRIAL_COMMAND = "endtrial";
	private static final String GET_RANKINGS_COMMAND = "getrankings";
	private static final String RESET_TIMES_COMMAND = "resettimes";
	private static final String HALL_OF_GLORY_COMMAND = "hallofglory";
	private static final String LIST_TITLES_COMMAND = "listtitles";
	private static final String ITEMDROP_CREATE_COMMAND = "createitemset";
	private static final String ITEMDROP_DELETE_COMMAND = "deleteitemset";
	private static final String ITEMDROP_ADDITEM_COMMAND = "additem";
	private static final String ITEMDROP_REMOVEITEM_COMMAND = "removeitem";
	private static final String ITEMDROP_GIVEITEM_COMMAND = "giveitem";
	private static final String ITEMDROP_LIST_COMMAND = "listitemset";
	private static final String ITEMDROP_INFO_COMMAND = "itemsetinfo";
	private static final String ITEMDROP_RUN_COMMAND = "runitemdrop";

	//-----------------------------------------------------------------------
	/**
	 * Constructor for the {@code ExpugnCommand} class.
	 * 
	 * <ul>
	 * <li> Links to a class named 'Warps' in the 'functions' package: 
	 * 		{@link io.github.expugn.expugnextras.functions.Warps}.
	 * <li> Links to a class named 'DungeonTimers' in the 'functions' package: 
	 * 		{@link io.github.expugn.expugnextras.functions.DungeonTimers}.
	 * <li> Links to a class named 'TimeTrial' in the 'functions' package: 
	 * 		{@link io.github.expugn.expugnextras.functions.TimeTrial}.
	 * <li> Links to a class named 'ListTitles' in the 'functions' package: 
	 * 		{@link io.github.expugn.expugnextras.functions.ListTitles}.
	 * </ul>
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public ExpugnCommand(ExpugnExtras plugin) 
	{
		warps = new io.github.expugn.expugnextras.functions.Warps(plugin);
		timers = new io.github.expugn.expugnextras.functions.DungeonTimers(plugin);
		trials = new io.github.expugn.expugnextras.functions.TimeTrial(plugin);
		listtitles = new io.github.expugn.expugnextras.functions.ListTitles(plugin);
		itemdrop = new io.github.expugn.expugnextras.functions.ItemDrop(plugin);
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code onCommand}: Reviews the arguments a player inputs and redirects them
	 * to the appropriate function.
	 * 
	 * <ul>
	 * <li> Links to a method 'warpInfo' on this class:
	 * 		{@link #helpMenu} 
	 * <li> Links to a method 'warpList' on a class named 'Warps' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.Warps#warpList}.
	 * <li> Links to a method 'warpInfo' on a class named 'Warps' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.Warps#warpInfo}.
	 * <li> Links to a method 'warp' on a class named 'Warps' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.Warps#warp}.
	 * <li> Links to a method 'setWarp' on a class named 'Warps' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.Warps#setWarp}. 
	 * <li> Links to a method 'delWarp' on a class named 'Warps' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.Warps#delWarp}.
	 * <li> Links to a method 'warpSetting' on a class named 'Warps' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.Warps#warpSetting}. 
	 * <li> Links to a method 'checkCanWarp' on a class named 'Warps' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.Warps#checkCanWarp}. 
	 * <li> Links to a method 'setDungeon' on a class named 'DungeonTimers' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.DungeonTimers#setDungeon}. 
	 * <li> Links to a method 'setTime' on a class named 'DungeonTimers' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.DungeonTimers#setTime}. 
	 * <li> Links to a method 'delDungeon' on a class named 'DungeonTimers' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.DungeonTimers#delDungeon}. 
	 * <li> Links to a method 'checkTime' on a class named 'DungeonTimers' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.DungeonTimers#checkTime}. 
	 * <li> Links to a method 'dungeonList' on a class named 'DungeonTimers' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.DungeonTimers#dungeonList}.
	 * <li> Links to a method 'setLocation' on a class named 'TimeTrial' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.TimeTrial#setLocation}. 
	 * <li> Links to a method 'delLocation' on a class named 'TimeTrial' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.TimeTrial#delLocation}. 
	 * <li> Links to a method 'locationList' on a class named 'TimeTrial' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.TimeTrial#locationList}. 
	 * <li> Links to a method 'startTrial' on a class named 'TimeTrial' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.TimeTrial#startTrial}. 
	 * <li> Links to a method 'endTrial' on a class named 'TimeTrial' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.TimeTrial#endTrial}. 
	 * <li> Links to a method 'getRankings' on a class named 'TimeTrial' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.TimeTrial#getRankings}. 
	 * <li> Links to a method 'resetTimes' on a class named 'TimeTrial' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.TimeTrial#resetTimes}. 
	 * <li> Links to a method 'warpHallOfGlory' on a class named 'TimeTrial' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.TimeTrial#warpHallOfGlory}. 
	 * <li> Links to a method 'getTitles' on a class named 'ListTitles' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.ListTitles#getTitles}.
	 * <li> Links to a method 'create' on a class named 'ItemDrop' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.ItemDrop#create}.
	 * <li> Links to a method 'delete' on a class named 'ItemDrop' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.ItemDrop#delete}.
	 * <li> Links to a method 'addItem' on a class named 'ItemDrop' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.ItemDrop#addItem}.
	 * <li> Links to a method 'removeItem' on a class named 'ItemDrop' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.ItemDrop#removeItem}.
	 * <li> Links to a method 'giveItem' on a class named 'ItemDrop' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.ItemDrop#giveItem}.
	 * <li> Links to a method 'list' on a class named 'ItemDrop' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.ItemDrop#list}.
	 * <li> Links to a method 'info' on a class named 'ItemDrop' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.ItemDrop#info}.
	 * <li> Links to a method 'run' on a class named 'ItemDrop' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.ItemDrop#run}.
	 * </ul>
	 * 
	 * @param sender  Whoever sent the command
	 * @param cmd  Executed command
	 * @param label  "expugn"
	 * @param args  Arguments after "expugn"
	 * @return  true
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (sender instanceof Player) 
		{
			Player player = (Player) sender;
			onExecute();
			if (args.length == 0) 
				player.sendMessage(OPENING_MESSAGE);
			else 
			{
				switch (args[0].toLowerCase()) 
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
				case CHECK_CAN_WARP_COMMAND:
					if (args.length >= 2)
						warps.checkCanWarp(player, args[1]);
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
				case RESET_TIMES_COMMAND:
					if (args.length >= 2)
						trials.resetTimes(player, args[1]);
					else
						player.sendMessage(INVALID_PARAMETER_ERROR);
					break;
				case HALL_OF_GLORY_COMMAND:
					trials.warpHallOfGlory(player);
					break;
				case LIST_TITLES_COMMAND:
					listtitles.getTitles(player, args);
					break;
				case ITEMDROP_CREATE_COMMAND:
					if (args.length >= 2)
						itemdrop.create(player, args);
					else
						player.sendMessage(INVALID_PARAMETER_ERROR);
					break;
				case ITEMDROP_DELETE_COMMAND:
					if (args.length >= 2)
						itemdrop.delete(player, args);
					else
						player.sendMessage(INVALID_PARAMETER_ERROR);
					break;
				case ITEMDROP_ADDITEM_COMMAND:
					if (args.length >= 2)
						itemdrop.addItem(player, args);
					else
						player.sendMessage(INVALID_PARAMETER_ERROR);
					break;
				case ITEMDROP_REMOVEITEM_COMMAND:
					if (args.length >= 3)
						itemdrop.removeItem(player, args);
					else
						player.sendMessage(INVALID_PARAMETER_ERROR);
					break;
				case ITEMDROP_GIVEITEM_COMMAND:
					if (args.length >= 3)
						itemdrop.giveItem(player, args);
					else
						player.sendMessage(INVALID_PARAMETER_ERROR);
					break;
				case ITEMDROP_LIST_COMMAND:
					itemdrop.list(player);
					break;
				case ITEMDROP_INFO_COMMAND:
					if (args.length >= 2)
						itemdrop.info(player, args);
					else
						player.sendMessage(INVALID_PARAMETER_ERROR);
					break;
				case ITEMDROP_RUN_COMMAND:
					if (args.length >= 3)
						itemdrop.run(args);
					else
						player.sendMessage(INVALID_PARAMETER_ERROR);
					break;
				default:
					player.sendMessage(INVALID_COMMAND_ERROR);
					break;
				}
			}
		} 
		else 
			sender.sendMessage(PLAYERS_ONLY_ERROR);
		return true;
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code helpMenu}: Displays a helpful guide of the available commands involving
	 * /expugn
	 * 
	 * @param player  Player who sent the command
	 */
	public void helpMenu(Player player) 
	{
		player.sendMessage(HELP_MENU);
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code onExecute}: Runs functions whenever /expugn is used.
	 * 
	 * <ul>
	 * <li> Links to a method 'checkMidnight' on a class named 'Warps' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.Warps#checkMidnight}.
	 * <li> Links to a method 'checkProgress' on a class named 'TimeTrial' in the 'functions' package:
	 * 		{@link io.github.expugn.expugnextras.functions.TimeTrial#checkProgress}.
	 * <ul>
	 */
	public void onExecute() 
	{
		warps.checkMidnight();
		trials.checkProgress();
		timers.cleanConfig();
	}

}