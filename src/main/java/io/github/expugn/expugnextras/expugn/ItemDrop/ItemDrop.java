package io.github.expugn.expugnextras.expugn.ItemDrop;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import io.github.expugn.expugnextras.ExpugnExtras;
import mkremins.fanciful.FancyMessage;

/**
 * <b>'ItemDrop' Function</b>
 * 
 * @version 1.3.1
 * @author Expugn <i>(https://github.com/Expugn)</i>
 */
public class ItemDrop 
{
	/* Private variables */
	private final ExpugnExtras plugin;
	private final io.github.expugn.expugnextras.Configs.ItemDrop config;
	
	//-----------------------------------------------------------------------
	/**
	 * Constructor for the ItemDrop class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public ItemDrop(ExpugnExtras plugin) 
	{
		config = new io.github.expugn.expugnextras.Configs.ItemDrop(plugin);
		this.plugin = plugin;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Creates a new ItemSet. Creating an existing ItemSet again will
	 * move it's position.
	 * 
	 * @param player  The player who sent the command.
	 * @param args  Command arguments.
	 */
	public void create(Player player, String[] args)
	{
		String itemSetName = args[1];
		
		if (!config.checkItemSet(itemSetName))
		{	
			player.sendMessage("§aCreating new ItemSet: §6" + itemSetName);
			config.createItemSet(itemSetName, player.getLocation());
		}
		else
		{
			player.sendMessage("§aItemSet §6" + itemSetName + "§a already exists. Changing location.");
			config.changeItemSetLocation(itemSetName, player.getLocation());
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Deletes an existing ItemSet.
	 * 
	 * @param player  The player who sent the command.
	 * @param args  Command arguments.
	 */
	public void delete(Player player, String[] args)
	{
		String itemSetName = args[1];
	
		if(config.checkItemSet(itemSetName))
		{
			player.sendMessage("§cDeleting ItemSet: §6" + itemSetName);
			config.deleteItemSet(itemSetName);
		}
		else
			player.sendMessage("§cThis ItemSet does not exist.");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Adds the item in the user's hand to an existing ItemSet. Item
	 * MetaData is supported so the user can choose how many items spawns,
	 * add enchantments, add lore, etc.
	 * 
	 * @param player  The player who sent the command.
	 * @param args  Command arguments.
	 */
	public void addItem(Player player, String[] args)
	{
		String itemSetName = args[1];
		
		if(config.checkItemSet(itemSetName))
		{
			ItemStack item = player.getInventory().getItemInMainHand().clone();
			
			if (item == null || item.getType() == Material.AIR)
				player.sendMessage("§cYour main hand is empty.");
			else
			{
				player.sendMessage("§aAdding Item §6" + item.getType() + "§a to ItemSet: §6" + itemSetName);
				config.addItem(itemSetName, item);
			}
		}
		else
			player.sendMessage("§cThis ItemSet does not exist.");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Removes the item assigned to an index in an existing ItemSet.
	 * 
	 * @param player  The player who sent the command.
	 * @param args  Command arguments.
	 */
	public void removeItem(Player player, String[] args)
	{
		String itemSetName = args[1];
		int value = Integer.parseInt(args[2]);
				
		if (config.checkItemSet(itemSetName))
		{
			if (value <= config.getItemList(itemSetName).size() && value > 0)
			{
				player.sendMessage("§cRemoving Item §6" 
						+ config.getItem(itemSetName, (value - 1)).getType() 
						+ "§c from ItemSet: §6" + itemSetName);
				config.removeItem(itemSetName, (value - 1));
			}
			else
				player.sendMessage("§cUnable to remove item, value was not assigned to any item.");
		}
		else
			player.sendMessage("§cThis ItemSet does not exist.");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Spawns an item from an ItemSet into the user's inventory.
	 * 
	 * @param player  The player who sent the command.
	 * @param args  Command Arguments.
	 */
	public void giveItem(Player player, String[] args)
	{
		String itemSetName = args[1];
		int value = Integer.parseInt(args[2]);
				
		if(config.checkItemSet(itemSetName))
		{
			if (value <= config.getItemList(itemSetName).size() && value > 0)
			{
				ItemStack item = config.getItem(itemSetName, (value - 1));
				
				new FancyMessage("§aSpawning: §d" + item.getAmount() + "x ")
				.then("§6" + item.getType() 
					+ (item.getItemMeta().getDisplayName() != null 
						&& !item.getItemMeta().getDisplayName().isEmpty() 
						? " §8| §f" + item.getItemMeta().getDisplayName() : ""))
				.itemTooltip(item)
				.send(player);
				player.getInventory().addItem(item);
			}
			else
				player.sendMessage("§cUnable to spawn item, value was not assigned to any item.");
		}
		else
			player.sendMessage("§cThis ItemSet does not exist.");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Lists all existing ItemSets. An item in the list can be clicked to
	 * get information about the ItemSet.
	 * 
	 * @param player  The player who sent the command.
	 */
	public void list(Player player)
	{
		Set<String> itemSetList = config.getItemSetList();
		player.sendMessage("§3There are currently §c" + itemSetList.size() + " §3ItemSets");
		for (String s : itemSetList) 
		{
			new FancyMessage("§3- §f" + s)
			.tooltip("Click to see more info about §6" + s)
			.command("/expugn itemsetinfo " + s)
			.send(player);
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets an ItemSet's information. Displays the World, Location, and
	 * Items from the ItemSet. An item from the list can be clicked in chat
	 * to spawn the item in the user's inventory. Mousing over a item in the
	 * list will display a tooltip similar to as if the item was in
	 * the user's inventory.
	 * 
	 * @param player  The player who sent the command.
	 * @param args  Command arguments.
	 */
	public void info(Player player, String[] args)
	{
		String itemSetName = args[1];
		int page = 1;
		int maxPages;
		SortedMap<Integer, FancyMessage> itemSetItems = new TreeMap<Integer, FancyMessage>();
		
		if(config.checkItemSet(itemSetName))
		{
			Location loc = config.getItemSet_Location(itemSetName);
			
			int count = 0;
			
			player.sendMessage("§3ItemSet: §6" + itemSetName + "\n"
					+ "§dWorld: §7" + loc.getWorld().getName() + "\n"
					+ "§dCoordinates: §8[§7" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + "§8]\n");
			for (ItemStack item : config.getItemList(itemSetName))
			{
				FancyMessage message = new FancyMessage((count + 1) + ") §d" + item.getAmount() + "x §6" 
							+ item.getType() 
							+ (item.getItemMeta().getDisplayName() != null 
								&& !item.getItemMeta().getDisplayName().isEmpty() 
								? " §8| §f" + item.getItemMeta().getDisplayName() : ""))
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
			
			if (page <= 1 && 1 != maxPages && (page != 0 && maxPages != 0)) 
			{
				new FancyMessage("§d[Next Page ->]")
				.tooltip("§7Click to go forward one page.")
				.command("/expugn itemsetinfo " + itemSetName + " " + (page + 1))
				.send(player);
			} 
			else if (page < maxPages && 1 != maxPages) 
			{
				new FancyMessage("§d[<- Previous Page]")
				.tooltip("§7Click to go back one page.")
				.command("/expugn itemsetinfo " + itemSetName + " " + (page - 1))
				.then("       ")
				.then("§d[Next Page ->]")
				.tooltip("§7Click to go forward one page.")
				.command("/expugn itemsetinfo " + itemSetName + " " + (page + 1))
				.send(player);
			} 
			else if (page <= maxPages && 1 != maxPages) 
			{
				new FancyMessage("§d[<- Previous Page]")
				.tooltip("§7Click to go back one page.")
				.command("/expugn itemsetinfo " + itemSetName + " " + (page - 1))
				.send(player);
			}
			
			player.sendMessage("§3Click an item to spawn it in your inventory.");
		}
		else
			player.sendMessage("§cThis ItemSet does not exist.");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Takes an ItemSet and calls the ItemDropRunnable class. Randomly
	 * chosen items from the ItemSet will be drop-scattered all over the
	 * place at the location where the ItemSet was created.
	 * 
	 * @param args  Command arguments.
	 */
	public void run(String args[])
	{
		String itemSetName = args[1];
		int count = Integer.parseInt(args[2]);
		
		Location loc = config.getItemSet_Location(itemSetName);
		World world = loc.getWorld();
		
		if(config.getItemList(itemSetName).isEmpty())
			return;
		
		@SuppressWarnings("unused")
		BukkitTask task = new ItemDropRunnable(plugin, count, world, loc, config.getItemList(itemSetName)).runTaskTimer(plugin, 5, 10);	
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

		player.sendMessage("§3Items: §b" + "Page (§3" + String.valueOf(page) + "§b of §3"
				+ (((map.size() % pageLength) == 0) 
						? map.size() / pageLength : (map.size() / pageLength) + 1)
				+ "§b)" + " §e(§c"+ map.size() + "§e items total.)");

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
