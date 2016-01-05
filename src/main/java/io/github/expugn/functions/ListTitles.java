package io.github.expugn.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import io.github.expugn.expugnextras.ExpugnExtras;
import net.milkbowl.vault.permission.Permission;

/**
 * 'List Title' Function
 * Manages the "listtitles" feature in /expugn
 * 
 * @author Expugn
 * @author Talabrek
 * https://github.com/Expugn
 * 
 * @version 1.0
 */
public class ListTitles 
{
	// Initialize variables
	private String fList;
	private static HashMap <String, String> titles = new HashMap<String, String>();
	private final YamlConfiguration titleFile = new YamlConfiguration();
	public static Permission perms = null;
	private static ExpugnExtras plugin;
	
	/**
	 * Constructor for the ListTitles class
	 * @param plugin - ExpugnExtras
	 */
	public ListTitles(ExpugnExtras plugin)
	{
		ListTitles.plugin = plugin;
		
		// Load title file and setup permissions
		loadTitles();
		setupPermissions();
	}
	/**
	 * GetTitles - Gets a list of titles that the player has and returns it in a more organized fashion compared to the
	 * current /title
	 * 
	 * @param player - The player who sent the command
	 */
	public void getTitles(Player player)
	{
		// Reload title file
		loadTitles();
		
		// Initialize variables
		this.fList = "";
		UUID playerUUID = player.getUniqueId();
		String playerWorld = player.getWorld().getName();
		Iterator<String> titlelist = titles.keySet().iterator();
		List<String> playerTitles = new ArrayList<String>();
		
		player.sendMessage(ChatColor.YELLOW + "You have access to the following titles:");

		// Load up playerTitles with titles the player has
		while(titlelist.hasNext())
		{
			String cTitle = (String)titlelist.next();
			if ((checkPerk(playerUUID, "titlemanager.title." + cTitle, playerWorld)) ||
				(checkPerk(playerUUID, "titlemanager.mod.set", playerWorld)) ||
				(checkPerk(playerUUID, "titlemanager.mod.grant", playerWorld)) ||
				(checkPerk(playerUUID, "titlemanager.admin.add", playerWorld)) ||
				(checkPerk(playerUUID, "titlemanager.admin.remove", playerWorld)))
			{
				playerTitles.add(cTitle);
			}
		}
		
		// Iterate through playerTitles and display titles to player
		Iterator<String> playerTitlesList = playerTitles.iterator();
		while(playerTitlesList.hasNext())
		{
			String cTitle = (String)playerTitlesList.next();
			String cTitle2 = playerTitlesList.hasNext() ? (String)playerTitlesList.next() : "";
			if (cTitle2.equals(""))
			{
				String title1 = "§r" + cTitle + " : " + (String)titles.get(cTitle) + "§f";
				this.fList = this.fList + title1;
			}
			else
			{
				String title1 = "§r" + cTitle + " : " + (String)titles.get(cTitle) + "§f";
				String title2 = "§r" + cTitle2 + " : " + (String)titles.get(cTitle2) + "§f";
				int spacing = 36 - (title1.length() - 4);
				this.fList = String.format("%s%s%" + spacing + "s%s\n", this.fList, title1, "",title2);
			}
		}
		player.sendMessage(this.fList.replace('&', '§'));
		player.sendMessage(ChatColor.YELLOW + "Use /title <titlename> to change your title. Ex: /title member");
	}
	
	/**
	 * LoadTitles - Reloads "plugins/TitleManager/config.yml"
	 */
	public void loadTitles()
	{
		// Try to load file with all the titles
		try
		{
			titleFile.load("plugins/TitleManager/config.yml");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// Load up titles
		Set<String> titleList = titleFile.getKeys(true);
		Iterator<String> ents = titleList.iterator();
		while (ents.hasNext())
		{
			String name = (String)ents.next();
			if (name.length() >= 7)
			{
				String totalprefix = titleFile.getString(name);
				titles.put(name.substring(6), totalprefix);
			}
		}
	}
	/**
	 * SetupPermissions - Loads the permissions from the server
	 * 
	 * @return true if permission loading was success | false if permission loading failed
	 */
	public static boolean setupPermissions()
	{
		RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
		if (rsp.getProvider() != null) 
		{
			perms = (Permission)rsp.getProvider();
		}
		return perms != null;
	}
	
	/**
	 * CheckPerk - Checks if a player has a permission
	 * 
	 * @param player - UUID of the player who sent the command
	 * @param perk - Permission node
	 * @param world - World the player is in
	 * @return true if the player has the perk | false if the player does not have the perk
	 */
	public static boolean checkPerk(UUID player, String perk, String world)
	{
		if(perms.playerHas(Bukkit.getPlayer(player), perk))
		{
			return true;
		}
		return false;
	}
}
