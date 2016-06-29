package io.github.expugn.expugnextras.Configs;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'Cash' Configuration File</b>
 * 
 * @version 1.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class Cash extends ConfigurationFile
{
	private Calendar midnight;
	
	//-----------------------------------------------------------------------
	/**
	 * Constructor for the Extras class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 */
	public Cash(ExpugnExtras plugin) 
	{
		super(plugin, "cash");
		
		if (!getBoolean("cash.data.config_created"))
		{
			List<Map<String, Object>> emptyList = Lists.newArrayList();
			int[] emptyIntArray = null;
			ItemStack[] emptyItemStackArray = null;
			
			set("cash.data.config_created", true);
			set("cash.data.midnighttime", 0L);
			set("cash.data.current_cash", 1000.0);
			set("cash.data.previous_cash", 1000.0);
			set("cash.data.value", 1.0);
			set("cash.data.daily_items", emptyIntArray);
			set("cash.data.items", emptyItemStackArray);
			set("cash.data.prices", emptyIntArray);
			set("cash.players", emptyList);
		}
		checkMidnight();
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Determines if midnight has passed. Does the daily update if true.
	 */
	public void checkMidnight()
	{
		if (getMidnightTime() <= System.currentTimeMillis() - 1) 
		{	
			midnight = Calendar.getInstance();
			midnight.set(Calendar.HOUR_OF_DAY, 0);
			midnight.set(Calendar.MINUTE, 0);
			midnight.set(Calendar.SECOND, 0);
			midnight.set(Calendar.MILLISECOND, 0);
			midnight.set(Calendar.DAY_OF_YEAR, midnight.get(Calendar.DAY_OF_YEAR) + 1);
			set("cash.data.midnighttime", midnight.getTimeInMillis());

			/* Update Data */
			set("cash.data.previous_cash", getCurrentCash());
			// TODO refreshShop();
			resetDailyLimit();
		}
	}

	//-----------------------------------------------------------------------
	/**
	 * Returns the value of midnighttime in the configuration file.
	 * 
	 * @return  long, 0L if empty.
	 */
	public long getMidnightTime()
	{
		return getLong("cash.data.midnighttime");
	}
	
	@SuppressWarnings("unchecked")
	public void refreshShop()
	{
		//SortedMap<Integer, ItemStack> items = (SortedMap<Integer, ItemStack>) get("cash.data.items");
		List<Map<String, Object>> items = (List<Map<String, Object>>) get("cash.data.items");
		
		if (items != null && !items.isEmpty())
		{
			//SortedMap<Integer, ItemStack> shopItems = new TreeMap<Integer, ItemStack>(Collections.reverseOrder());
			List<Map<String, Object>> shopItems = Lists.newArrayList();
			
			while (shopItems.size() < 3)
			{
				if (items != null && !items.isEmpty())
				{
					Random r = new Random();
					int itemNum = r.nextInt(items.size());
					
					//int itemPrice = items.get(itemNum).getPrice();
					//ItemStack item = items.get(itemNum).getItem();
					
					shopItems.add(items.get(itemNum));
					
					//shopItems.put(itemPrice, item);
					//items.remove(itemPrice, item);
				}
				else
					break;
			}
			
			set("cash.data.daily_items", shopItems);
			return;
		}
		return;
	}
	
	public int[] getShopItems()
	{
		//SortedMap<Integer, ItemStack> shopItems = (SortedMap<Integer, ItemStack>) get("cash.data.daily_items");
		//List<Map<String, Object>> shopItems = (List<Map<String, Object>>) get("cash.data.daily_items");
		int[] shopItems = (int[]) get("cash.data.daily_items");
		
		//ItemStack[] items = getItems();
		//int[] prices = getPrices();
		
		if (shopItems != null && shopItems.length > 0)
			return shopItems;
		else
		{
			int[] emptyArray = null;
			return emptyArray;
		}
	}
	
	@SuppressWarnings("unchecked")
	public ItemStack[] getItems()
	{
		List<HashMap<Map<String, Object>, Map<String, Object>>> items = (List<HashMap<Map<String, Object>, Map<String, Object>>>) get("cash.data.items");
		return deserializeItemStackList(items);
	}
	
	public int[] getPrices()
	{
		return (int[]) get("items.data.prices");
	}
	
	public void resetDailyLimit()
	{
		if (getConfigurationSection("cash.players") != null)
		{
			for (String s : getConfigurationSectionKeys("cash.players"))
			{
				set("cash.players." + s + ".today_bought", 0);
				set("cash.players." + s + ".today_sold", 0);
			}
		}
	}
	
	public double getCurrentCash()
	{
		return getDouble("cash.data.current_cash");
	}
	
	public double getPreviousCash()
	{
		return getDouble("cash.data.previous_cash");
	}
	
	public double getValue()
	{
		return getDouble("cash.data.value");
	}
	
	public void addItem(ItemStack item, int itemPrice)
	{
		ItemStack[] items = getItems();
		int[] prices = getPrices();
		//List<ShopItem> items = getItems();
		//List<Map<String, Object>> items = getItems();
		
		for (int i = 0 ; i < items.length ; i++)
		{
			if (items[i] == null)
			{
				items[i] = item;
				prices[i] = itemPrice;
				break;
			}
		}
		set("cash.data.items", items);
		set("cash.data.prices", prices);

		/*
		if (items != null && items.length > 0)
		{
			for (int i = 0 ; i < items.length ; i++)
			{
				if (items[i] == null)
				{
					items[i] = item;
					break;
				}
			}
			set("cash.data.items", items);
		}
		else
		{
			set("cash.data.items", items);
		}
		*/
	}
	
	public void addCurrentCash(double amount)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		set("cash.data.current_cash", (getCurrentCash() + Double.parseDouble(df.format(amount))));
	}
	
	public void addPreviousCash(double amount)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		set("cash.data.previous_cash", (getPreviousCash() + Double.parseDouble(df.format(amount))));
	}
	
	public void addValue(double amount)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		set("cash.data.value", (getValue() + Double.parseDouble(df.format(amount))));
		
		if (getValue() < 0.01)
			set("cash.data.previous_cash", 0.01);
	}
	
	public int getPlayerAmount(Player player)
	{
		return getInt("cash.players." + player.getUniqueId() + ".amount");
	}
	
	public int getPlayerBought(Player player)
	{
		return getInt("cash.players." + player.getUniqueId() + ".bought");
	}
	
	public int getPlayerSold(Player player)
	{
		return getInt("cash.players." + player.getUniqueId() + ".sold");
	}
	
	public double getPlayerProfit(Player player)
	{
		return getDouble("cash.players." + player.getUniqueId() + ".profit");
	}
	
	public int getPlayerTodayBought(Player player)
	{
		return getInt("cash.players." + player.getUniqueId() + ".today_bought");
	}
	
	public int getPlayerTodaySold(Player player)
	{
		return getInt("cash.players." + player.getUniqueId() + ".today_sold");
	}
	
	public void addPlayerAmount(Player player, int amount)
	{
		set("cash.players." + player.getUniqueId() + ".amount", (getPlayerAmount(player) + amount));
	}
	
	public void addPlayerBought(Player player, int amount)
	{
		set("cash.players." + player.getUniqueId() + ".bought", (getPlayerBought(player) + amount));
	}
	
	public void addPlayerSold(Player player, int amount)
	{
		set("cash.players." + player.getUniqueId() + ".sold", (getPlayerSold(player) + amount));
	}
	
	public void addPlayerProfit(Player player, double amount)
	{
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		set("cash.players." + player.getUniqueId() + ".profit", (getPlayerProfit(player) + Double.parseDouble(df.format(amount))));
	}
	
	public void addPlayerTodayBought(Player player, int amount)
	{
		set("cash.players." + player.getUniqueId() + ".today_bought", (getPlayerTodayBought(player) + amount));
	}
	
	public void addPlayerTodaySold(Player player, int amount)
	{
		set("cash.players." + player.getUniqueId() + ".today_sold", (getPlayerTodaySold(player) + amount));
	}
	
	public boolean checkPlayerExists(Player player)
	{
		if (getConfigurationSection("cash.players." + player.getUniqueId()) != null)
			return true;
		return false;
	}
	
	public void createPlayerData(Player player)
	{
		if (checkPlayerExists(player))
			return;
		
		String playerDataPath = "cash.players." + player.getUniqueId();
		set(playerDataPath + ".amount", 0);
		set(playerDataPath + ".bought", 0);
		set(playerDataPath + ".sold", 0);
		set(playerDataPath + ".profit", 0.0);
		set(playerDataPath + ".today_bought", 0);
		set(playerDataPath + ".today_sold", 0);
	}
	
	public Set<String> getPlayerList()
	{
		return getConfigurationSectionKeys("cash.players");
	}
	
	public final static List<HashMap<Map<String, Object>, Map<String, Object>>> serializeItemStackList(final ItemStack[] itemStackList)
	{
		final List <HashMap<Map<String, Object>, Map<String, Object>>> serializedItemStackList = new ArrayList<HashMap<Map<String, Object>, Map<String, Object>>>();
		
		for (ItemStack itemStack : itemStackList)
		{
			Map<String, Object> serializedItemStack, serializedItemMeta;
			HashMap<Map<String, Object>, Map<String, Object>> serializedMap = new HashMap<Map<String, Object>, Map<String, Object>>();
			
			if (itemStack == null)
				itemStack = new ItemStack(Material.AIR);
			serializedItemMeta = (itemStack.hasItemMeta()) ? itemStack.getItemMeta().serialize() : null;
			serializedItemStack = itemStack.serialize();
			
			serializedMap.put(serializedItemStack, serializedItemMeta);
			serializedItemStackList.add(serializedMap);
		}
		return serializedItemStackList;
		
	}
	
	public final static ItemStack[] deserializeItemStackList(final List<HashMap<Map<String, Object>, Map<String, Object>>> serializedItemStackList) 
	{
		final ItemStack[] itemStackList = new ItemStack[serializedItemStackList.size()];
		
		int i = 0;
		for (HashMap<Map<String, Object>, Map<String, Object>> serializedItemStackMap : serializedItemStackList) 
		{
			Entry<Map<String, Object>, Map<String, Object>> serializedItemStack = serializedItemStackMap.entrySet().iterator().next();
			
			ItemStack itemStack = ItemStack.deserialize(serializedItemStack.getKey());
			if (serializedItemStack.getValue() != null) 
			{
				ItemMeta itemMeta = (ItemMeta)ConfigurationSerialization.deserializeObject(serializedItemStack.getValue(), ConfigurationSerialization.getClassByAlias("ItemMeta"));
				itemStack.setItemMeta(itemMeta);
			}
			
			itemStackList[i++] = itemStack;
		}
		return itemStackList;
	}
}
