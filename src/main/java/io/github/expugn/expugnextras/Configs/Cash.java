package io.github.expugn.expugnextras.Configs;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import io.github.expugn.expugnextras.ExpugnExtras;
import io.github.expugn.expugnextras.expugn.Cash.ShopItem;

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
			
			set("cash.data.config_created", true);
			set("cash.data.midnighttime", 0L);
			set("cash.data.current_cash", 1000.0);
			set("cash.data.previous_cash", 1000.0);
			set("cash.data.value", 1.0);
			set("cash.data.daily_items", emptyList);
			set("cash.data.items", emptyList);
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
			//refreshShop();
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
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getShopItems()
	{
		//SortedMap<Integer, ItemStack> shopItems = (SortedMap<Integer, ItemStack>) get("cash.data.daily_items");
		List<Map<String, Object>> shopItems = (List<Map<String, Object>>) get("cash.data.daily_items");
		
		if (shopItems != null && !shopItems.isEmpty())
			return shopItems;
		else
		{
			List<Map<String, Object>> emptyList = Lists.newArrayList();
			return emptyList;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getItems()
	{
		List<Map<String, Object>> items = (List<Map<String, Object>>) get("cash.data.items");

		if(items != null && !items.isEmpty())
			return items;
		else
		{
			List<Map<String, Object>> emptyList = Lists.newArrayList();
			return emptyList;
		}
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
	
	public void addItem(ShopItem entry)
	{
		//List<ShopItem> items = getItems();
		List<Map<String, Object>> items = getItems();

		if (items != null && !items.isEmpty())
		{
			items.add(entry.serialize());
			set("cash.data.items", items);
		}
		else
		{
			List<Map<String, Object>> emptyList = Lists.newArrayList();
			emptyList.add(entry.serialize());
			set("cash.data.items", items);
		}
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
}
