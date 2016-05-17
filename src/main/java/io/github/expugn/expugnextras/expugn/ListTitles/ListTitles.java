package io.github.expugn.expugnextras.expugn.ListTitles;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import io.github.expugn.expugnextras.ExpugnExtras;
import io.github.expugn.expugnextras.imports.Fanciful.FancyMessage;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

/**
 * <b>'ListTitles' Function</b>
 * 
 * <p>
 * <b>Special Thanks</b>:
 * <ul>
 * <li> @<b>Talabrek</b>  (Provided base title displayer and made title system)
 * <li> @<b>Rescudo</b>  (Provided an example title page determiner)
 * <li> @<b>mkremins</b>  (Created the 'Fanciful' library)
 * <li> @<b>gomeow</b>  (Wrote the base {@code paginate} method: {@link #paginate})
 * </ul>
 * 
 * @version 2.1.2
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class ListTitles implements CommandExecutor
{
	/* Private variables */
	private static HashMap<String, String> titles = new HashMap<String, String>();
	private final YamlConfiguration titleFile = new YamlConfiguration();
	private static boolean enabled = true;

	//-----------------------------------------------------------------------
	/**
	 * Constructor for the ListTitles class
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public ListTitles(ExpugnExtras plugin) 
	{
		boolean titlePlugin = plugin.getServer().getPluginManager().getPlugin("Titles") == null ? true : false;
		boolean permissionsEx = plugin.getServer().getPluginManager().getPlugin("PermissionsEx") == null ? true : false;
		
		if (titlePlugin && permissionsEx) 
		{
			plugin.getLogger().info("Dependencies are missing. ListTitles will not work.");
			enabled = false;
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Calls getTitles.
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			getTitles(player, args);
		}
		else
			sender.sendMessage("§cOnly players can use this command. Sorry!");
		return true;
	}

	//-----------------------------------------------------------------------
	/**
	 * Display all titles a player has to the player.
	 * 
	 * @param player  Player who sent the command.
	 * @param args  Arguments following 'listtitles'.
	 */
	public void getTitles(Player player, String[] args) 
	{
		if (!enabled) 
		{
			player.sendMessage("§cListTitles is not enabled.");
			return;
		}

		if (args.length > 2) 
			player.sendMessage("§cInvalid parameters. §6/title [page #]");
		int page = 1;
		if (args.length > 0) 
		{
			try 
			{
				page = Integer.parseInt(args[(args.length - 1)]);
				if (page < 1) 
					page = 1;
			} 
			catch (NumberFormatException e) 
			{
				page = 1;
			}
		}

		loadTitles();

		Iterator<String> titlelist = titles.keySet().iterator();
		PermissionUser user = PermissionsEx.getUser(player);
		List<String> ownPermissions = user.getPermissions(player.getWorld().getName());
		SortedMap<Integer, FancyMessage> playerTitles = new TreeMap<Integer, FancyMessage>(Collections.reverseOrder());
		int titleCount = 1;
		
		while (titlelist.hasNext()) 
		{
			String title = (String) titlelist.next();
			if (ownPermissions.contains("titlemanager.title." + title)) 
			{
				FancyMessage fancyMess = new FancyMessage("§o" + title + "§r" + " : " + (String) titles.get(title).replace('&', '§'))
								.tooltip("§cSample:\n" 
										+ ((String)titles.get(title).replace('&', '§')) + "§f§r" + player.getName() + " > Hello World!\n\n" 
										+ "§7§o" + "Click to apply this title.")
								.command("/title " + title);

				playerTitles.put(titleCount, fancyMess);
				titleCount++;
			}
		}
		int maxPages = paginate(player, playerTitles, page, 10);
		if (page > maxPages)
			page = maxPages;
		
		if (page <= 1 && 1 != maxPages) 
		{
			new FancyMessage("§d[Next Page ->]")
					.tooltip("§7Click to go forward one page.")
					.command("/expugn listtitles " + (page + 1)).send(player);
		} 
		else if (page < maxPages && 1 != maxPages) 
		{
			new FancyMessage("§d[<- Previous Page]")
					.tooltip("§7Click to go back one page.")
					.command("/expugn listtitles " + (page - 1))
					.then("       ")
					.then("§d[Next Page ->]")
					.tooltip("§7Click to go forward one page.")
					.command("/expugn listtitles " + (page + 1))
					.send(player);
		} 
		else if (page <= maxPages && 1 != maxPages) 
		{
			new FancyMessage("§d[<- Previous Page]")
					.tooltip("§7Click to go back one page.")
					.command("/expugn listtitles " + (page - 1)).send(player);
		}
		player.sendMessage("§eUse §6/title <titlename> §eto change your title. Ex: §6/title member\n" 
				+ "§eYou may also click the title to apply it.");
	}

	//-----------------------------------------------------------------------
	/**
	 * Reloads "/plugins/TitleManager/config.yml"
	 */
	public void loadTitles() 
	{
		try 
		{
			titleFile.load("plugins/Titles/config.yml");
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

		Set<String> titleList = titleFile.getKeys(true);
		Iterator<String> ents = titleList.iterator();
		while (ents.hasNext()) 
		{
			String name = (String) ents.next();
			if (name.length() >= 7) 
			{
				String totalprefix = titleFile.getString(name);
				titles.put(name.substring(6), totalprefix);
			}
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Takes a sorted map and makes it into a neat page system.
	 * 
	 * @param player  Player who sent the command.
	 * @param map  SortedMap with all the data to be displayed.
	 * @param page  Requested page.
	 * @param pageLength  How many entries per page.
	 * @return  integer of the max length of pages, not null.
	 */
	public int paginate(Player player, SortedMap<Integer, FancyMessage> map, int page, int pageLength) 
	{
		if (page > (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1)) 
			page = (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1);
		
		player.sendMessage("§6Title List: " + "§ePage (§6" + String.valueOf(page) + " §eof §6" + (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1) + "§e)" 
				+ " §e(§c" + map.size() + "§e titles total.)");
		
		int i = 0, k = 0;
		page--;
		
		for (final Entry<Integer, FancyMessage> e : map.entrySet()) 
		{
			k++;
			if ((((page * pageLength) + i + 1) == k) && (k != ((page * pageLength) + pageLength + 1))) 
			{
				i++;
				e.getValue().send(player);
			}
		}
		return (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1);
	}
}
