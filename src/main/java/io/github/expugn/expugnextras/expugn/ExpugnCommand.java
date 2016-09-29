package io.github.expugn.expugnextras.expugn;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;
import mkremins.fanciful.FancyMessage;

/**
 * <b>'Expugn' Command</b>
 * 
 * @version 3.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class ExpugnCommand implements CommandExecutor 
{
	/* Private Variables */
	private final io.github.expugn.expugnextras.expugn.Warps.Warps warps;
	private final io.github.expugn.expugnextras.expugn.Timers.DungeonTimers timers;
	private final io.github.expugn.expugnextras.expugn.TimeTrial.TimeTrial trials;
	private final io.github.expugn.expugnextras.expugn.ListTitles.ListTitles listtitles;
	private final io.github.expugn.expugnextras.expugn.ItemDrop.ItemDrop itemdrop;
	
	/* Configuration Files */
	private final io.github.expugn.expugnextras.Configs.Extras extras_config;
	
	private final ExpugnExtras plugin;

	//-----------------------------------------------------------------------
	/**
	 * Constructor for the ExpugnCommand class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public ExpugnCommand(ExpugnExtras plugin) 
	{
		warps = new io.github.expugn.expugnextras.expugn.Warps.Warps(plugin);
		timers = new io.github.expugn.expugnextras.expugn.Timers.DungeonTimers(plugin);
		trials = new io.github.expugn.expugnextras.expugn.TimeTrial.TimeTrial(plugin);
		listtitles = new io.github.expugn.expugnextras.expugn.ListTitles.ListTitles(plugin);
		itemdrop = new io.github.expugn.expugnextras.expugn.ItemDrop.ItemDrop(plugin);
		
		extras_config = new io.github.expugn.expugnextras.Configs.Extras(plugin);
		
		this.plugin = plugin;
	}

	//-----------------------------------------------------------------------
	/**
	 * Reviews the arguments a player inputs and redirects them to the 
	 * appropriate function.
	 * 
	 * @param sender  Whoever sent the command.
	 * @param cmd  Executed command.
	 * @param label  "expugn".
	 * @param args  Arguments after "expugn".
	 * @return  true.
	 */
	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		/* Initialize Variables */
		final boolean hasPermission = sender.hasPermission("expugnextras.admin");
		final String subCommand = (args.length > 0) ? args[0] : "";
		final Player player = sender instanceof Player ? (Player) sender : null;
		
		/* User Type Variables */
		final boolean isPlayerOpOrConsole = (hasPermission) ? true : false;
		final boolean isPlayerOp = (hasPermission && sender instanceof Player) ? true : false;
		final boolean isPlayer = (sender instanceof Player) ? true : false;
		
		/* System Messages */
		final String COMMAND_FAILURE = "§cSomething went wrong. Use §6/expugn help §cfor command help.";
		final String INVALID_COMMAND = "§cInvalid Command. Use §6/expugn help §cfor command help.";
		
		onExecute();
		if (args.length == 0) /* expugn */
		{
			if (isPlayerOpOrConsole)
				sender.sendMessage("§6Welcome to ExpugnExtras. Use §c/expugn help §6for a help menu.");
			else
			{
				new FancyMessage("§c[Click Me.]")
				.tooltip("I dare you.")
				.command("Expugn is a pretty cool guy.")
				.send(sender);
			}
		}
		else
		{
			// General Commands -----------------------------------------------------------------------
			if (subCommand.equalsIgnoreCase("help") && true) /* help */
			{
				if (isPlayerOpOrConsole)
					helpMenu(sender, args);
				else
					fakeHelpMenu(sender, args);
				return true;
			}
			if (subCommand.equalsIgnoreCase("reload") && false) /* reload */
			{
				if (isPlayerOpOrConsole)
					reloadConfigs(sender);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			// Warp Commands -----------------------------------------------------------------------
			if (subCommand.equalsIgnoreCase("warp") && true) /* warp */
			{
				if (isPlayerOp && args.length >= 2)
					warps.warp(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("setwarp") && true) /* setwarp */
			{
				if (isPlayerOp && args.length >= 2)
					warps.setWarp(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("delwarp") && true) /* delwarp */
			{
				if (isPlayerOp && args.length >= 2)
					warps.delWarp(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("warplist") && true) /* warplist */
			{
				if (isPlayerOp && args.length >= 1)
					warps.warpList(player);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("warpinfo") && true) /* warpinfo */
			{
				if (isPlayerOp && args.length >= 2)
					warps.warpInfo(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("warpsetting") && true) /* warpsetting */
			{
				if (isPlayerOp && args.length >= 3)
					warps.warpSetting(player, args[1], args[2]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("checkcanwarp") && true) /* checkcanwarp */
			{
				if (isPlayer && args.length >= 2)
					warps.checkCanWarp(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			// Timer Commands -----------------------------------------------------------------------
			if (subCommand.equalsIgnoreCase("setdungeon") && true) /* setdungeon */
			{
				if (isPlayerOp && args.length >= 3)
					timers.setDungeon(player, args[1], Long.parseLong(args[2]));
				else if (isPlayerOp && args.length >= 2)
					timers.setDungeon(player, args[1], Long.parseLong("72000000"));
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("deldungeon") && true) /* deldungeon */
			{
				if (isPlayerOp && args.length >= 2)
					timers.delDungeon(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("settime") && true) /* settime */
			{
				if (isPlayer && args.length >= 2)
					timers.setTime(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("checktime") && true) /* checktime */
			{
				if (isPlayerOp && args.length >= 2)
					timers.checkTime(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("dungeonlist") && true) /* dungeonlist */
			{
				if (isPlayerOp && args.length >= 1)
					timers.dungeonList(player);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			// Time Trial Commands -----------------------------------------------------------------------
			if (subCommand.equalsIgnoreCase("setlocation") && true) /* setlocation */
			{
				if (isPlayerOp && args.length >= 2)
					trials.setLocation(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("dellocation") && true) /* dellocation */
			{
				if (isPlayerOp && args.length >= 2)
					trials.delLocation(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("locationlist") && true) /* locationlist */
			{
				if (isPlayerOp && args.length >= 1)
					trials.locationList(player);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("starttrial") && true) /* starttrial */
			{
				if (isPlayerOp && args.length >= 2)
					trials.startTrial(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("endtrial") && true) /* endtrial */
			{
				if (isPlayerOp && args.length >= 2)
					trials.endTrial(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("getrankings") && true) /* getrankings */
			{
				if (isPlayer && args.length >= 2)
					trials.getRankings(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("resettimes") && true) /* resettimes */
			{
				if (isPlayerOp && args.length >= 2)
					trials.resetTimes(player, args[1]);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("hallofglory") && true) /* hallofglory */
			{
				if (isPlayer && args.length >= 1)
					trials.warpHallOfGlory(player);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			// ItemDrop Commands -----------------------------------------------------------------------
			if (subCommand.equalsIgnoreCase("createitemset") && true) /* createitemset */
			{
				if (isPlayerOp && args.length >= 2)
					itemdrop.create(player, args);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("deleteitemset") && true) /* deleteitemset */
			{
				if (isPlayerOp && args.length >= 2)
					itemdrop.delete(player, args);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("additem") && true) /* additem */
			{
				if (isPlayerOp && args.length >= 2)
					itemdrop.addItem(player, args);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("removeitem") && true) /* removeitem */
			{
				if (isPlayerOp && args.length >= 3)
					itemdrop.removeItem(player, args);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("giveitem") && true) /* giveitem */
			{
				if (isPlayerOp && args.length >= 3)
					itemdrop.giveItem(player, args);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("listitemset") && true) /* listitemset */
			{
				if (isPlayerOp && args.length >= 1)
					itemdrop.list(player);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("itemsetinfo") && true) /* itemsetinfo */
			{
				if (isPlayerOp && args.length >= 2)
					itemdrop.info(player, args);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			if (subCommand.equalsIgnoreCase("runitemdrop") && true) /* runitemdrop */
			{
				if (isPlayerOpOrConsole && args.length >= 3)
					itemdrop.run(args);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			} 
			// Miscellaneous Commands -----------------------------------------------------------------------
			if (subCommand.equalsIgnoreCase("listtitles") && true) /* listtitles */
			{
				if (isPlayer && args.length >= 1)
					listtitles.getTitles(player, args);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("name") && true) /* name */
			{
				Bukkit.getServer().getOnlinePlayers();
				if (isPlayer && args.length >= 2)
				{
					Collection<? extends Player> onlinePlayers = Bukkit.getServer().getOnlinePlayers();
					
					for (Player currentPlayer : onlinePlayers)
					{
						if (currentPlayer.getName().equals(args[1]) 
								|| args[1].equals("-" + currentPlayer.getName())
								|| args[1].equals("Expugn"))
						{
							defaultMessage(args[1]).send(player);
							return true;
						}
					}
					player.sendMessage("§cThis player is not online at the moment.");
				}
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("rpc") && true) /* rpc */
			{
				if (isPlayer && args.length >= 2)
					rockPaperScissors(player, Integer.parseInt(args[1]));
				else if (isPlayer && args.length >= 1)
					rockPaperScissors(player);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			if (subCommand.equalsIgnoreCase("joke") && true) /* joke */
			{
				if (isPlayer && args.length >= 1)
					joke(player, args);
				else
					sender.sendMessage(COMMAND_FAILURE);
				return true;
			}
			// End of Commands -----------------------------------------------------------------------
			sender.sendMessage(INVALID_COMMAND);
		}
		return true;
	}

	//-----------------------------------------------------------------------
	/**
	 * Displays a helpful guide of the available commands involving /expugn.
	 * 
	 * @param sender  Thing that sent the command.
	 */
	public void helpMenu(CommandSender sender, String[] args) 
	{
		FancyMessage general = new FancyMessage("§2General Commands:\n")
				.then("§6/expugn\n")
				.tooltip("§d/expugn §5<args>\n"
						+ "§7Main command for ExpugnExtras.")
				.suggest("/expugn")
				.then("§6/expugn §7help\n")
				.tooltip("§d/expugn help §5[general | warps | timers | trials | itemdrop | misc | all]\n"
						+ "§7Help menu command. Lists all commands available through /expugn.\n"
						+ "§7More detailed help is in the tooltips.\n"
						+ "§7Click a help entry to auto-type the command out.")
				.suggest("/expugn help")
				.then("§6/expugn §7reload")
				.tooltip("§d/expugn reload\n"
						+ "§7Reload all configuration files.")
				.suggest("/expugn reload");
		//-----------------------------------------------------------------------
		FancyMessage warps = new FancyMessage("§2Warp Commands:\n")
				.then("§6/expugn §7warp\n")
				.tooltip("§d/expugn warp §5[Warp_Name]\n"
						+ "§7Teleport to a warp location.")
				.suggest("/expugn warp")
				.then("§6/expugn §7setwarp\n")
				.tooltip("§d/expugn setwarp §5[Warp_Name]\n"
						+ "§7Creates a new warp.")
				.suggest("/expugn setwarp")
				.then("§6/expugn §7delwarp\n")
				.tooltip("§d/expugn delwarp §5[Warp_Name]\n"
						+ "§7Deletes a warp.")
				.suggest("/expugn delwarp")
				.then("§6/expugn §7warplist\n")
				.tooltip("§d/expugn warplist\n"
						+ "§7Lists all warps available.")
				.suggest("/expugn warplist")
				.then("§6/expugn §7warpinfo\n")
				.tooltip("§d/expugn warpinfo §5[Warp_Name]\n"
						+ "§7Displays information about a warp.")
				.suggest("/expugn warpinfo")
				.then("§6/expugn §7warpsetting\n")
				.tooltip("§d/expugn warpsetting §5[Warp_Name] ['cooldown' | 'limit']\n"
						+ "§7Changes a warp to use cooldowns or limits.\n\n"
						+ "§d/expugn warpsetting §5[Warp_Name] [Value]\n"
						+ "§7Set cooldown hours or daily limit.\n\n"
						+ "§d/expugn warpsetting §5[Warp_Name] [Warp_Name]\n"
						+ "§7Set an warp to send the player to if they can't warp.")
				.suggest("/expugn warpsetting")
				.then("§6/expugn §7checkcanwarp")
				.tooltip("§d/expugn checkcanwarp §5[Warp_Name]\n"
						+ "§7Checks if you can use the warp.")
				.suggest("/expugn checkcanwarp");
		//-----------------------------------------------------------------------
		FancyMessage timers = new FancyMessage("§2Timer Commands:\n")
				.then("§6/expugn §7setdungeon\n")
				.tooltip("§d/expugn setdungeon §5[Dungeon_Name] <Timer_Length_in_Milliseconds>\n"
						+ "§7Create a new timer for a dungeon.\n"
						+ "§7Defaults to 20 hours.")
				.suggest("/expugn setdungeon")
				.then("§6/expugn §7deldungeon\n")
				.tooltip("§d/expugn deldungeon §5[Dungeon_Name]\n"
						+ "§7Delete a timer for a dungeon.")
				.suggest("/expugn deldungeon")
				.then("§6/expugn §7settime\n")
				.tooltip("§d/expugn settime §5[Dungeon_Name]\n"
						+ "§7Start the timer for a dungeon.")
				.suggest("/expugn settime")
				.then("§6/expugn §7checktime\n")
				.tooltip("§d/expugn checktime §5[Dungeon_Name]\n"
						+ "§7Check if the timer for a dungeon has stopped or not.")
				.suggest("/expugn checktime")
				.then("§6/expugn §7dungeonlist")
				.tooltip("§d/expugn dungeonlist\n"
						+ "§7List all the dungeons with timers installed.")
				.suggest("/expugn dungeonlist");
		//-----------------------------------------------------------------------
		FancyMessage trials = new FancyMessage("§2Time Trial Commands:\n")
				.then("§6/expugn §7setlocation\n")
				.tooltip("§d/expugn §5[Location_Name]\n"
						+ "§7Create a new location to do time trials.")
				.suggest("/expugn setlocation")
				.then("§6/expugn §7dellocation\n")
				.tooltip("§d/expugn dellocation §5[Location_Name]\n"
						+ "§7Deletes a time trial location.")
				.suggest("/expugn dellocation")
				.then("§6/expugn §7locationlist\n")
				.tooltip("§d/expugn locationlist\n"
						+ "§7Lists all time trial locations.")
				.suggest("/expugn locationlist")
				.then("§6/expugn §7starttrial\n")
				.tooltip("§d/expugn starttrial §5[Location_Name]\n"
						+ "§7Start a new time trial at a location.")
				.suggest("/expugn starttrial")
				.then("§6/expugn §7endtrial\n")
				.tooltip("§d/expugn endtrial §5[Location_Name]\n"
						+ "§7End a time trial at a location.")
				.suggest("/expugn endtrial")
				.then("§6/expugn §7getrankings\n")
				.tooltip("§d/expugn getrankings §5[Location_Name]\n"
						+ "§7Get the top five time trial times at a location.")
				.suggest("/expugn getrankings")
				.then("§6/expugn §7resettimes\n")
				.tooltip("§d/expugn resettimes §5[Location_Name]\n"
						+ "§7Deletes top five time trial times at a location.")
				.suggest("/expugn resettimes")
				.then("§6/expugn §7hallofglory")
				.tooltip("§d/expugn hallofglory\n"
						+ "§7Teleports the player to a ExpugnExtras warp named 'HallOfGlory'.\n"
						+ "A player can only warp if they are in the top five of any location.")
				.suggest("/expugn hallofglory");
		//-----------------------------------------------------------------------
		FancyMessage itemdrop = new FancyMessage("§2ItemDrop Commands:\n")
				.then("§6/expugn §7createitemset\n")
				.tooltip("§d/expugn createitemset §5[ItemSet_Name]\n"
						+ "§7Creates a new ItemSet.")
				.suggest("/expugn createitemset")
				.then("§6/expugn §7deleteitemset\n")
				.tooltip("§d/expugn deleteitemset §5[ItemSet_Name]\n"
						+ "§7Deletes an ItemSet.")
				.suggest("/expugn deleteitemset")
				.then("§6/expugn §7additem\n")
				.tooltip("§d/expugn additem §5[ItemSet_Name]\n"
						+ "§7Adds the item you are holding to an ItemSet.")
				.suggest("/expugn additem")
				.then("§6/expugn §7removeitem\n")
				.tooltip("§d/expugn removeitem §5[ItemSet_Name] [Index]\n"
						+ "§7Removes the item assigned to the index in an ItemSet.")
				.suggest("/expugn removeitem")
				.then("§6/expugn §7giveitem\n")
				.tooltip("§d/expugn giveitem §5[ItemSet_Name] [Index]\n"
						+ "§7Spawns the item assigned to the index in an ItemSet.\n"
						+ "§7Alternatively, you can click an item in an ItemSet's info.")
				.suggest("/expugn giveitem")
				.then("§6/expugn §7listitemset\n")
				.tooltip("§d/expugn listitemset\n"
						+ "§7Lists all ItemSets created.")
				.suggest("/expugn listitemset")
				.then("§6/expugn §7itemsetinfo\n")
				.tooltip("§d/expugn itemsetinfo §5<Page_Number>\n"
						+ "§7Displays information about an ItemSet.\n"
						+ "§7Alternatively, you can click the ItemSet's name in the ItemSet list.")
				.suggest("/expugn itemsetinfo")
				.then("§6/expugn §7runitemdrop")
				.tooltip("§d/expugn runitemdrop §5[ItemSet_Name] [Number]\n"
						+ "§7Runs ItemDrop using an ItemSet for a set number of times.")
				.suggest("/expugn runitemdrop");
		//-----------------------------------------------------------------------
		FancyMessage misc = new FancyMessage("§2Miscellaneous Commands:\n")
				.then("§6/expugn §7listtitles\n")
				.tooltip("§d/expugn listtitles §5<Page_Number>\n"
						+ "§7Lists every title you own in a fancy format."
						+ "§7Alternatively, you can use §d/titles§7.")
				.suggest("/expugn listtitles")
				.then("§6/expugn §7name\n")
				.tooltip("§d/expugn name §5[PlayerName]\n"
						+ "§7Compliment someone.")
				.suggest("/expugn name")
				.then("§6/expugn §7rpc\n")
				.tooltip("§d/expugn rpc\n"
						+ "§7Fancy a game of Rock, Paper, Scissors?")
				.suggest("/expugn rpc")
				.then("§6/expugn §7joke")
				.tooltip("§d/expugn joke\n"
						+ "§7Want to hear some hilarious jokes?")
				.suggest("/expugn joke");

		if (args.length == 1)
		{
			general.send(sender);
		}
		else if (args.length >= 2)
		{
			switch (args[1].toLowerCase())
			{
			case "general":
				general.send(sender);
				break;
			case "warp":
			case "warps":
				warps.send(sender);
				break;
			case "timer":
			case "timers":
				timers.send(sender);
				break;
			case "trial":
			case "trials":
				trials.send(sender);
				break;
			case "item":
			case "itemdrop":
				itemdrop.send(sender);
				break;
			case "misc":
				misc.send(sender);
				break;
			case "all":
				general.send(sender);
				warps.send(sender);
				timers.send(sender);
				trials.send(sender);
				itemdrop.send(sender);
				misc.send(sender);
				break;
			default:
				general.send(sender);
				break;
			}
		}
	}
	
	public void fakeHelpMenu(CommandSender sender, String[] args)
	{
		if (args.length == 1)
		{
			new FancyMessage("§7Huh? You want help? Well okay...\n"
					+ "§6USA Suicide Hotline: §f1-800-784-2433\n"
					+ "§6SparkNotes: §fwww.sparknotes.com\n"
					+ "§6Google: §fwww.google.com\n"
					+ "§6AllRecipes: §fwww.allrecipes.com\n"
					+ "§6Minecraft Wiki: §fwww.minecraft.gamepedia.com\n"
					+ "§6Server Forums: §fwww.blockworlds.com\n"
					+ "§7If you need more help then look it up on Google or something.\n")
			.then("§3[But... This isn't the type of help I wanted..]")
			.command("/expugn help me")
			.tooltip("Click me.")
			.send(sender);
		}
		else if (args.length >= 2)
		{
			new FancyMessage("§7Okay, okay... Here's a 'real' help menu.\n")
				.then("§2/expugn Help Menu:\n")
				.then("§6/expugn\n")
					.tooltip("§d/expugn §5<args>\n"
						+ "§7Main command.")
					.suggest("/expugn")
				.then("§6/expugn §7help\n")
					.tooltip("§d/expugn help\n"
							+ "§7Help menu command. Lists all commands available through /expugn.\n"
							+ "§7More detailed help is in the tooltips.\n"
							+ "§7Click a help entry to auto-type the command out.")
					.suggest("/expugn help")
				.then("§6/expugn §7name\n")
					.tooltip("§d/expugn name §5[PlayerName]\n"
						+ "§7Compliment someone.")
					.suggest("/expugn name")
					.then("§6/expugn §7listtitles\n")
					.tooltip("§d/expugn listtitles §5<Page #>\n"
						+ "§7Lists all your titles in a fancy manner. "
						+ "§7Alternatively, you can use §d/titles§7.")
					.suggest("/expugn name")
				.then("§6/expugn §7rpc\n")
					.tooltip("§d/expugn rpc\n"
						+ "§7Fancy a game of Rock, Paper, Scissors?")
					.suggest("/expugn rpc")
				.then("§6/expugn §7joke")
					.tooltip("§d/expugn joke\n"
						+ "§7Want to hear some hilarious jokes?")
					.suggest("/expugn joke")
				.send(sender);
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns a JSON based message where it says "[Click Me.]". Clicking on
	 * the text will force the player to say a compliment depending on what
	 * name was given. 
	 * 
	 * <p>
	 * If the player clicks the message they will say "[name] is a [adverb] 
	 * [adjective] [noun]." in game.
	 * 
	 * @param name  Player name
	 * @return  FancyMessage, not null
	 */
	public FancyMessage defaultMessage(String name)
	{
		if (name.equals("Fireteam22"))
		{
			return new FancyMessage(getRandomColor() + "[Click Me.]")
					.tooltip("A secret message?...")
					.command("I love Fire. :I");
		}
		else if (name.equals("GrimmKitty"))
		{
			return new FancyMessage(getRandomColor() + "[Click Me.]")
					.tooltip("A secret message?...")
					.command("GrimmKitty loves breaking things!");
		}
		else if (name.equals("Mirrusky"))
		{
			return new FancyMessage(getRandomColor() + "[Click Me.]")
					.tooltip("A secret message?...")
					.command("Mirr is well liked.");
		}
		else if (name.equals("Joapple"))
		{
			return new FancyMessage(getRandomColor() + "[Click Me.]")
					.tooltip("A secret message?...")
					.command("Joapple is watching you.");
		}
		else
		{
			if (name.substring(0, 1).equals("-"))
				name = name.substring(1, name.length());
			
			return new FancyMessage(getRandomColor() + "[Click Me.]")
					.tooltip("Give it a shot!")
					.command(getMessage(name))
					.then(" | ")
					.then(getRandomColor() + "[Generate another?]")
					.tooltip("Want another message?")
					.command("/expugn name " + name);	
		}
	}
		
	//-----------------------------------------------------------------------
	/**
	 * {@code getMessage}: Returns a string to be used in {@code defaultMessage}
	 * 
	 * @return  String, not null
	 */
	public String getMessage(String name)
	{
		Random randomNum = new Random();

		List<String> adverb = extras_config.getStringList("words.adverbs");
		int adverbNum = randomNum.nextInt(adverb.size());

		List<String> adj = extras_config.getStringList("words.adjectives");
		int adjNum = randomNum.nextInt(adj.size());

		List<String> noun = extras_config.getStringList("words.nouns");
		int nounNum = randomNum.nextInt(noun.size());

		return name + " is a " + adverb.get(adverbNum) + " " + adj.get(adjNum) + " " + noun.get(nounNum) + ".";
	}
		
	//-----------------------------------------------------------------------
	/**
	 * {@code getRandomColor}: Returns a color string to be used in {@code defaultMessage}
	 * 
	 * @return  String, not null.
	 */
	public String getRandomColor()
	{
		Random randomNum = new Random();

		List<String> colors = extras_config.getStringList("words.colors");
		int index = randomNum.nextInt(colors.size());

		return colors.get(index);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * A simple game of Rock, Paper, Scissors. Click on the text boxes in
	 * chat to play.
	 * 
	 * @param player  The player who sent the command.
	 */
	public void rockPaperScissors(Player player)
	{
		new FancyMessage("§7Rock, Paper, or Scissors?\n")
		.then("§8[Rock]")
		.tooltip("Click Me: [Rock]")
		.command("/expugn rpc 0")
		.then("  ")
		.then("§f[Paper]")
		.tooltip("Click Me: [Paper]")
		.command("/expugn rpc 1")
		.then("  ")
		.then("§c[Scissors]")
		.tooltip("Click Me: [Scissors]")
		.command("/expugn rpc 2")
		.send(player);
	}
	
	/**
	 * The game logic under the simple game of Rock, Paper, Scissors. It
	 * will give you a result based on what you clicked and then it will
	 * start up another game.
	 * 
	 * @param player  The player who sent the command
	 * @param command  0, 1, or 2. Rock, Paper, Scissors respectively.
	 */
	public void rockPaperScissors(Player player, int command)
	{
		Random r = new Random();
		int rpc = r.nextInt(3);
		if (command == 0) /* Rock */
		{
			if(rpc == 0)
				player.sendMessage("§8Rock§7!... Looks like it's a tie.");
			else if (rpc == 1)
				player.sendMessage("§fPaper§7!... Better luck next time.");
			else if (rpc == 2)
				player.sendMessage("§cScissors§7!... You win this round.");
		}
		else if (command == 1) /* Paper */
		{
			if(rpc == 0)
				player.sendMessage("§8Rock§7!... You win this round.");
			else if (rpc == 1)
				player.sendMessage("§fPaper§7!... Looks like it's a tie.");
			else if (rpc == 2)
				player.sendMessage("§cScissors§7!... Better luck next time.");
		}
		else if (command == 2) /* Scissors */
		{
			if(rpc == 0)
				player.sendMessage("§8Rock§7!... Better luck next time.");
			else if (rpc == 1)
				player.sendMessage("§fPaper§7!... You win this round.");
			else if (rpc == 2)
				player.sendMessage("§cScissors§7!... Looks like it's a tie.");
		}
		player.sendMessage("§f==============================");
		rockPaperScissors(player);
	}
	
	public void joke(Player player, String[] args)
	{
		if (args.length > 1)
			if (args[1].equals("isTrash"))
			{
				new FancyMessage("§7I know.\n")
					.then("§7Jokes Source: §f§oJokes for Minecrafters: Booby Traps, Bombs, §oBoo-Boos, and More\n")
					.then("§r§6[Amazon.com Link]")
						.tooltip("Click me to visit the book's Amazon page.")
						.link("http://www.amazon.com/Jokes-Minecrafters-Booby-Traps-Boo-Boos/dp/151070633X/ref=pd_bxgy_14_img_2?ie=UTF8&refRID=1M22X8S4P2TPMZ3V30Y9")
					.send(player);
				return;
			}
		Random randomNum = new Random();
		List<String> jokes = extras_config.getStringList("words.jokes");
		int index = randomNum.nextInt(jokes.size());
		
		new FancyMessage("§6Joke #§c" + (index + 1) + "§6 out of §c" + jokes.size() + "§6:\n")
			.then(jokes.get(index) + "\n")
			.then("§7§oThey were destroyed!\n")
			.then("§2[HaHaHa! Hilarious! Give me another!]\n")
				.tooltip("§2Click me.")
				.command("/expugn joke")
			.then("§c[What the heck, man. That joke was garbage.]")
				.tooltip("§cClick me.")
				.command("/expugn joke isTrash")
			.send(player);
			
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Reloads all configuration files.
	 */
	public void reloadConfigs(CommandSender sender)
	{
		new io.github.expugn.expugnextras.Configs.MainConfig(plugin).reloadConfig();
		new io.github.expugn.expugnextras.Configs.Extras(plugin).reloadConfig();
		new io.github.expugn.expugnextras.Configs.Warps(plugin).reloadConfig();
		new io.github.expugn.expugnextras.Configs.Timers(plugin).reloadConfig();
		new io.github.expugn.expugnextras.Configs.TimeTrial(plugin).reloadConfig();
		new io.github.expugn.expugnextras.Configs.ItemDrop(plugin).reloadConfig();
		
		
		sender.sendMessage("§aAll configuration files reloaded.");
	}

	//-----------------------------------------------------------------------
	/**
	 * Runs functions whenever /expugn is used.
	 */
	public void onExecute() 
	{
		new io.github.expugn.expugnextras.Configs.Warps(plugin);
		new io.github.expugn.expugnextras.Configs.TimeTrial(plugin);
		new io.github.expugn.expugnextras.Configs.Timers(plugin);
	}

}
