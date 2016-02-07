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
	
	private static boolean titleManager = true;
	
	/**
	 * Constructor for the ListTitles class
	 * @param plugin - ExpugnExtras
	 */
	public ListTitles(ExpugnExtras plugin)
	{
		if (plugin.getServer().getPluginManager().getPlugin("TitleManager") == null)
		{
			plugin.getLogger().info("The dependency TitleManager is not found. ListTitles will not work.");
			titleManager = false;
		}
		else
		{
			ListTitles.plugin = plugin;
			
			loadTitles();
			setupPermissions();
		}
	}
	/**
	 * GetTitles - Gets a list of titles that the player has and returns it in a more organized fashion compared to the
	 * current /title
	 * 
	 * @param player - The player who sent the command
	 */
	public void getTitles(Player player)
	{
		if (!titleManager)
			return;
		
		// Reload title file
		loadTitles();
		
		// Initialize variables
		this.fList = "";
		UUID playerUUID = player.getUniqueId();
		String playerWorld = player.getWorld().getName();
		Iterator<String> titlelist = titles.keySet().iterator();
		List<String> playerTitles = new ArrayList<String>();
		int titleCount = 0;
		
		if (perms.playerInGroup(player, "admin") || perms.playerInGroup(player, "moderator"))
		{
			boolean gaveOp = false;
			if (!player.isOp())
			{
				player.setOp(true);
				gaveOp = true;
			}
			try
			{
				player.sendMessage(ChatColor.YELLOW + "- Title listing is broken for anyone with 'titlemanager.*' and hard for me to figure out how to do nicely lol.\n" +
						"- Blame PermissionsEx for not working well with Vault. You'll have to live with this cheap title list instead." + 
						ChatColor.RED + " - Expugn");
			    Bukkit.getServer().dispatchCommand(player, "pex user " + player.getName() + " remove titlemanager.title.");
			    player.sendMessage(ChatColor.YELLOW + "- Use /title <titlename> to change your title. Ex: /title member\n" +
			    		ChatColor.YELLOW + "- The title name is after 'titlemanager.title.'.");
			}
			catch(Exception e)
			{
			    e.printStackTrace();
			}
			
			if (gaveOp)
				player.setOp(false);
			return;
		}
		
		// Load up playerTitles with titles the player has
		while(titlelist.hasNext())
		{
			String cTitle = (String)titlelist.next();
			if ((checkPerk(playerUUID, "titlemanager.title." + cTitle, playerWorld)))
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
				String title1 = ChatColor.ITALIC + cTitle + ChatColor.RESET + " : " + (String)titles.get(cTitle) + ChatColor.RESET;
				this.fList = this.fList + title1;
				titleCount += 1;
			}
			else
			{
				String title1 = ChatColor.ITALIC + cTitle + ChatColor.RESET + " : " + (String)titles.get(cTitle) + ChatColor.WHITE;
				String title2 = ChatColor.ITALIC + cTitle2 + ChatColor.RESET + " : " + (String)titles.get(cTitle2) + ChatColor.WHITE;
				int spacing = 36 - (title1.length() - 4);
				this.fList = String.format("%s%s%" + spacing + "s%s\n", this.fList, title1, "",title2);
				titleCount += 2;
			}
		}
		player.sendMessage(ChatColor.YELLOW + "You have access to the following " + ChatColor.RED + titleCount + ChatColor.YELLOW + " titles:");
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
