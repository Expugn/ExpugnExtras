package io.github.expugn.expugnextras.Configs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'ItemDrop' Configuration File</b>
 * 
 * @version 1.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class ItemDrop extends ConfigurationFile
{
	//-----------------------------------------------------------------------
	/**
	 * Constructor for the ItemDrop class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 * @param configName  The name of the configuration file to be loaded.
	 */
	public ItemDrop(ExpugnExtras plugin)
	{
		super(plugin, "itemdrop");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Creates a new ItemSet and stores it in the configuration file.
	 * 
	 * @param itemSet_Name  Name of the ItemSet.
	 * @param location  Location of where the ItemSet will run.
	 */
	public void createItemSet(String itemSet_Name, Location location)
	{
		List<ItemStack> itemList = Lists.newArrayList();
		
		set("itemsets." + itemSet_Name + ".location", location);
		set("itemsets." + itemSet_Name + ".items", itemList);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Deletes an existing ItemSet from the configuration file.
	 * 
	 * @param itemSet_Name Name of the ItemSet.
	 */
	public void deleteItemSet(String itemSet_Name)
	{
		set("itemsets." + itemSet_Name, null);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Changes the location of where the ItemSet will run.
	 * 
	 * @param itemSet_Name  Name of the ItemSet.
	 * @param location  Location of where the ItemSet will run.
	 */
	public void changeItemSetLocation(String itemSet_Name, Location location)
	{
		set("itemsets." + itemSet_Name + ".location", location);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets a list of Items in a ItemSet.
	 * 
	 * @param itemSet_Name  Name of the ItemSet.
	 * @return  List<ItemStack>, null if empty.
	 */
	@SuppressWarnings("unchecked")
	public List<ItemStack> getItemList(String itemSet_Name)
	{
		return (List<ItemStack>) get("itemsets." + itemSet_Name + ".items");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Adds an item to an ItemSet
	 * 
	 * @param itemSet_Name  Name of the ItemSet
	 * @param item  ItemStack variable to be added to the ItemSet.
	 */
	public void addItem(String itemSet_Name, ItemStack item)
	{
		List<ItemStack> itemList = getItemList(itemSet_Name);
		itemList.add(item);
		
		set("itemsets." + itemSet_Name + ".items", itemList);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Removes an item from an ItemSet.
	 * 
	 * @param itemSet_Name  Name of the ItemSet.
	 * @param index  Index of an item to be removed.
	 */
	public void removeItem(String itemSet_Name, int index)
	{
		List<ItemStack> itemList = getItemList(itemSet_Name);
		itemList.remove(index);
		
		set("itemsets." + itemSet_Name + ".items", itemList);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets an ItemStack from an ItemSet.
	 * 
	 * @param itemSet_Name  Name of the ItemSet.
	 * @param index  Index of the item to be retreived.
	 * @return  ItemStack, null if empty
	 */
	public ItemStack getItem(String itemSet_Name, int index)
	{
		return getItemList(itemSet_Name).get(index);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Gets a Location variable of where the ItemSet runs.
	 * 
	 * @param itemSet_Name  Name of the ItemSet
	 * @return  Location, null if empty.
	 */
	public Location getItemSet_Location(String itemSet_Name)
	{
		return (Location) get("itemsets." + itemSet_Name + ".location");
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Returns a {@code Set<String>} of all ItemSets created.
	 * 
	 * @return  A set of all entries under 'itemsets' in the config.
	 */
	public Set<String> getItemSetList()
	{
		if (isConfigurationSection("itemsets"))
			return getConfigurationSectionKeys("itemsets");
		Set<String> emptySet = new HashSet<String>();
		return emptySet;
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Checks if the ItemSet exists or not.
	 * 
	 * @param name  Player who sent the command.
	 * @return  true if the ItemSet exists, else false.
	 */
	public boolean checkItemSet(String name) 
	{
		if (getItemSetList().contains(name))
			return true;
		return false;
	}
}
