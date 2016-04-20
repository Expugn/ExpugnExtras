package io.github.expugn.functions;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import io.github.expugn.expugnextras.ExpugnExtras;
import io.github.expugn.runnable.ItemDropRunnable;

/**
 * <b>'ItemDrop' Function</b>
 * 
 * @version 1.0
 * @author Expugn <i>(https://github.com/Expugn)</i>
 */
public class ItemDrop 
{
	/* Private variables */
	private File ymlFile;
	private FileConfiguration config;
	private final ExpugnExtras plugin;
	
	//-----------------------------------------------------------------------
	/**
	 * Constructor for the {@code ItemDrop} class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public ItemDrop(ExpugnExtras plugin) 
	{
		ymlFile = new File(plugin.getDataFolder() + "/itemdrop.yml");
		config = YamlConfiguration.loadConfiguration(ymlFile);
		this.plugin = plugin;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code create}: Creates a new ItemSet
	 * 
	 * @param player  The player who sent the command
	 * @param args  Command arguments
	 */
	public void create(Player player, String[] args)
	{
		String itemSetName = args[1];
		
		if (!checkItemSet(itemSetName))
		{
			player.sendMessage("§aCreating new ItemSet: §6" + itemSetName);
			config.set("itemsets." + itemSetName + ".location", player.getLocation());
			List<String> itemList = config.getStringList("itemsets." + itemSetName + ".items");
			config.set("itemsets." + itemSetName + ".items", itemList);
		}
		else
		{
			player.sendMessage("§aItemSet §6" + itemSetName + "§a already exists. Changing location.");
			config.set("itemsets." + itemSetName + ".location", player.getLocation());
		}
		saveConfig();
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code delete}: Deletes an ItemSet
	 * 
	 * @param player  The player who sent the command
	 * @param args  Command arguments
	 */
	public void delete(Player player, String[] args)
	{
		String itemSetName = args[1];
	
		if(checkItemSet(itemSetName))
		{
			player.sendMessage("§cDeleting ItemSet: §6" + itemSetName);
			config.set("itemsets." + itemSetName, null);
			saveConfig();
		}
		else
		{
			player.sendMessage("§cThis ItemSet does not exist.");
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code addItem}: Adds an item to an ItemSet
	 * 
	 * @param player  The player who sent the command
	 * @param args  Command arguments
	 */
	public void addItem(Player player, String[] args)
	{
		String itemSetName = args[1];
		String itemName = args[2].toUpperCase();
		
		if(checkItemSet(itemSetName))
		{
			List<String> itemList = config.getStringList("itemsets." + itemSetName + ".items");
			if (!itemList.contains(itemName))
			{
				player.sendMessage("§aAdding Item §6" + itemName + "§a to ItemSet: §6" + itemSetName);
				itemList.add(itemName);
				config.set("itemsets." + itemSetName + ".items", itemList);
				saveConfig();
			}
			else
			{
				player.sendMessage("§cItem already exists in this ItemSet.");
			}
		}
		else
		{
			player.sendMessage("§cThis ItemSet does not exist.");
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code removeItem}: Removes an item from an ItemSet
	 * 
	 * @param player  The player who sent the command
	 * @param args  Command arguments
	 */
	public void removeItem(Player player, String[] args)
	{
		String itemSetName = args[1];
		String itemName = args[2].toUpperCase();
				
		if(checkItemSet(itemSetName))
		{
			List<String> itemList = config.getStringList("itemsets." + itemSetName + ".items");
			if (itemList.contains(itemName))
			{
				player.sendMessage("§cRemoving Item §6" + itemName + "§c from ItemSet: §6" + itemSetName);
				itemList.remove(itemName);
				config.set("itemsets." + itemSetName + ".items", itemList);
				saveConfig();
			}
			else
			{
				player.sendMessage("§cItem does not exist in this ItemSet.");
			}
		}
		else
		{
			player.sendMessage("§cThis ItemSet does not exist.");
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code list}: Lists all ItemSets
	 * 
	 * @param player  The player who sent the command
	 */
	public void list(Player player)
	{
		player.sendMessage(ChatColor.GOLD + "There are currently §4" + getItemSetList().size() + " §6ItemSets");
		for (String s : getItemSetList()) 
		{
			player.sendMessage("§3- §f" + s);
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code info}: Gets an ItemSet's information
	 * 
	 * @param player  The player who sent the command
	 * @param args  Command arguments
	 */
	public void info(Player player, String[] args)
	{
		String itemSetName = args[1];
		
		if(checkItemSet(itemSetName))
		{
			Location loc = (Location) config.get("itemsets." + itemSetName + ".location");
			List<String> itemList = config.getStringList("itemsets." + itemSetName + ".items");
			int count = 0;
			
			player.sendMessage("§3ItemSet " + ChatColor.GOLD + itemSetName + "\n" + ChatColor.WHITE
					+ "§dWorld: §7" + loc.getWorld() + "\n"
					+ "§dX: §7" + loc.getX() + "\n"
					+ "§dY: §7" + loc.getY() + "\n"
					+ "§dZ: §7" + loc.getZ() + "\n"
					+ ChatColor.DARK_AQUA + "Items:");
			for (String s : itemList)
			{
				player.sendMessage((count + 1) + ") " + ChatColor.GOLD + s);
				count++;
			}
		}
		else
		{
			player.sendMessage("§cThis ItemSet does not exist.");
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code run}: Takes an ItemSet and runs ItemDrop
	 * 
	 * <ul>
	 * <li> Creates a new instance of a class named 'ItemDropRunnable' in the 'runnable' package:
	 * 		{@link io.github.expugn.runnable.ItemDropRunnable}.
	 * <ul>
	 * 
	 * @param args  Command arguments
	 */
	public void run(String args[])
	{
		String itemSetName = args[1];
		int count = Integer.parseInt(args[2]);
		
		Location loc = (Location) config.get("itemsets." + itemSetName + ".location");
		World world = loc.getWorld();
		List<String> itemSet = config.getStringList("itemsets." + itemSetName + ".items");
		
		if(itemSet.isEmpty())
			return;
		
		@SuppressWarnings("unused")
		BukkitTask task = new ItemDropRunnable(plugin, count, world, loc, itemSet).runTaskTimer(plugin, 5, 10);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code getItemSetList}: Returns all list of all item sets created.
	 * 
	 * @return  A set of all entries under 'itemsets' in the config
	 */
	public Set<String> getItemSetList()
	{
		if (config.isConfigurationSection("itemsets"))
		{
			return config.getConfigurationSection("itemsets").getKeys(false);
		}
		Set<String> emptySet = new HashSet<String>();
		return emptySet;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code checkItemSet}: Checks if the supplied item set is supported.
	 * 
	 * <ul>
	 * <li> Links to a method 'getItemSetList' on this class:
	 * 		{@link #getItemSetList}.
	 * </ul>
	 * 
	 * @param name  Player who sent the command
	 * @return 
	 * 		<li> {@code true}  if the item set exists in the file.
	 * 		<li> {@code false}  if the item set does not exist in the file.
	 */
	public boolean checkItemSet(String name) 
	{
		if (getItemSetList().contains(name))
			return true;
		return false;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code saveConfig}: Saves itemdrop.yml.
	 */
	public void saveConfig() 
	{
		try 
		{
			config.save(ymlFile);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
