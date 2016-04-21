package io.github.expugn.expugnextras.functions;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Lists;

import io.github.expugn.expugnextras.ExpugnExtras;
import io.github.expugn.expugnextras.runnable.ItemDropRunnable;
import io.github.expugn.fanciful.FancyMessage;

/**
 * <b>'ItemDrop' Function</b>
 * 
 * @version 1.2
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
			List<ItemStack> itemList = Lists.newArrayList();
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
		
		if(checkItemSet(itemSetName))
		{
			@SuppressWarnings("unchecked")
			List<ItemStack> itemList = (List<ItemStack>) config.get("itemsets." + itemSetName + ".items");
			ItemStack item = player.getItemInHand().clone();
			if (item == null || item.getType() == Material.AIR)
			{
				player.sendMessage("§cYour hand is empty.");
			}
			else
			{
				player.sendMessage("§aAdding Item §6" + item.getType() + "§a to ItemSet: §6" + itemSetName);
				itemList.add(item);
				config.set("itemsets." + itemSetName + ".items", itemList);
				saveConfig();
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
		int value = Integer.parseInt(args[2]);
				
		if(checkItemSet(itemSetName))
		{
			@SuppressWarnings("unchecked")
			List<ItemStack> itemList = (List<ItemStack>) config.get("itemsets." + itemSetName + ".items");
			if (value <= itemList.size() && value > 0)
			{
				player.sendMessage("§cRemoving Item §6" + itemList.get(value - 1).getType() + "§c from ItemSet: §6" + itemSetName);
				itemList.remove(value - 1);
				config.set("itemsets." + itemSetName + ".items", itemList);
				saveConfig();
			}
			else
			{
				player.sendMessage("§cUnable to remove item, value was not assigned to any item.");
			}
		}
		else
		{
			player.sendMessage("§cThis ItemSet does not exist.");
		}
	}
	
	public void giveItem(Player player, String[] args)
	{
		String itemSetName = args[1];
		int value = Integer.parseInt(args[2]);
				
		if(checkItemSet(itemSetName))
		{
			@SuppressWarnings("unchecked")
			List<ItemStack> itemList = (List<ItemStack>) config.get("itemsets." + itemSetName + ".items");
			if (value <= itemList.size() && value > 0)
			{
				ItemStack item = itemList.get(value - 1);
				new FancyMessage("§aSpawning: §d" + item.getAmount() + "x ")
					.then("§6" + item.getType() + (item.getItemMeta().getDisplayName() != null && !item.getItemMeta().getDisplayName().isEmpty() ? " §8| §f" + item.getItemMeta().getDisplayName() : ""))
						.itemTooltip(item)
					.send(player);
				player.getInventory().addItem(item);
			}
			else
			{
				player.sendMessage("§cUnable to spawn item, value was not assigned to any item.");
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
		player.sendMessage(ChatColor.DARK_AQUA + "There are currently §c" + getItemSetList().size() + " §3ItemSets");
		for (String s : getItemSetList()) 
		{
			new FancyMessage("§3- §f" + s)
				.tooltip("Click to see more info about " + ChatColor.GOLD + s)
				.command("/expugn itemsetinfo " + s)
				.send(player);
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
		int page = 1;
		int maxPages;
		SortedMap<Integer, FancyMessage> itemSetItems = new TreeMap<Integer, FancyMessage>();
		
		if(checkItemSet(itemSetName))
		{
			Location loc = (Location) config.get("itemsets." + itemSetName + ".location");
			@SuppressWarnings("unchecked")
			List<ItemStack> itemList = (List<ItemStack>) config.get("itemsets." + itemSetName + ".items");
			int count = 0;
			
			player.sendMessage("§3ItemSet: " + ChatColor.GOLD + itemSetName + "\n"
					+ "§dWorld: §7" + loc.getWorld().getName() + "\n"
					+ "§dCoordinates: §8(§7" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "§8)\n");
			for (ItemStack item : itemList)
			{
				FancyMessage message = new FancyMessage((count + 1) + ") §d" + item.getAmount() + "x §6" + item.getType() + (item.getItemMeta().getDisplayName() != null && !item.getItemMeta().getDisplayName().isEmpty() ? " §8| §f" + item.getItemMeta().getDisplayName() : ""))
					.itemTooltip(item)
					.command("/expugn giveitem " + itemSetName + " " + (count + 1));
				itemSetItems.put(count, message);
				count++;
			}
			if (args.length == 3)
			{
				try 
				{
					page = Integer.parseInt(args[2]);
					if (page < 1) 
						page = 1;
				} 
				catch (NumberFormatException e) 
				{
					page = 1;
				}
				maxPages = paginate(player, itemSetItems, page, 5);
				if (page > maxPages)
					page = maxPages;
			}
			else
			{
				maxPages = paginate(player, itemSetItems, page, 5);
			}
			
			if (page <= 1 && 1 != maxPages) 
			{
				new FancyMessage(ChatColor.LIGHT_PURPLE + "[Next Page ->]")
						.tooltip(ChatColor.GRAY + "Click to go forward one page.")
						.command("/expugn itemsetinfo " + itemSetName + " " + (page + 1))
						.send(player);
			} 
			else if (page < maxPages && 1 != maxPages) 
			{
				new FancyMessage(ChatColor.LIGHT_PURPLE + "[<- Previous Page]")
						.tooltip(ChatColor.GRAY + "Click to go back one page.")
						.command("/expugn itemsetinfo " + itemSetName + " " + (page - 1))
						.then("       ")
						.then(ChatColor.LIGHT_PURPLE + "[Next Page ->]")
						.tooltip(ChatColor.GRAY + "Click to go forward one page.")
						.command("/expugn itemsetinfo " + itemSetName + " " + (page + 1))
						.send(player);
			} 
			else if (page <= maxPages && 1 != maxPages) 
			{
				new FancyMessage(ChatColor.LIGHT_PURPLE + "[<- Previous Page]")
						.tooltip(ChatColor.GRAY + "Click to go back one page.")
						.command("/expugn itemsetinfo " + itemSetName + " " + (page - 1))
						.send(player);
			}
			player.sendMessage("§3Click an item to spawn it in your inventory.");
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
	 * 		{@link io.github.expugn.expugnextras.runnable.ItemDropRunnable}.
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
		@SuppressWarnings("unchecked")
		List<ItemStack> itemSet = (List<ItemStack>) config.get("itemsets." + itemSetName + ".items");
		
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
	 * {@code paginate}: Takes a sorted map and makes it into a neat page system.
	 * 
	 * @param player  Player who sent the command
	 * @param map  SortedMap with all the data to be displayed
	 * @param page  Requested page
	 * @param pageLength  How many entries per page
	 * @return  integer of the max length of pages, not null
	 */
	public int paginate(Player player, SortedMap<Integer, FancyMessage> map, int page, int pageLength) 
	{
		if (page > (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1)) 
			page = (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1);

		player.sendMessage(ChatColor.DARK_AQUA + "Items: " + ChatColor.AQUA + "Page (" + ChatColor.DARK_AQUA
				+ String.valueOf(page) + ChatColor.AQUA + " of " + ChatColor.DARK_AQUA
				+ (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1)
				+ ChatColor.AQUA + ")" + ChatColor.AQUA + " (" + ChatColor.RED + map.size() + ChatColor.AQUA
				+ " items total.)");

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
