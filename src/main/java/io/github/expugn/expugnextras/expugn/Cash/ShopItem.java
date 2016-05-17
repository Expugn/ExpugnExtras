package io.github.expugn.expugnextras.expugn.Cash;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class ShopItem implements ConfigurationSerializable
{
	private int price;
	private ItemStack item;
	
	public ShopItem(int price, ItemStack item)
	{
		this.price = price;
		this.item = item;
	}
	
	public int getPrice()
	{
		return price;
	}
	
	public ItemStack getItem()
	{
		return item;
	}

	@Override
	public Map<String, Object> serialize() 
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("price", price);
		map.put("item", item);
	
		return map;
	}
}
