package io.github.expugn.expugnextras.expugn.Cash;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.expugn.expugnextras.ExpugnExtras;
import io.github.expugn.expugnextras.imports.Fanciful.FancyMessage;

/**
 * <b>'Cash' Function</b>
 * 
 * @version 1.0
 * @author Expugn <i>(https://github.com/Expugn)</i>
 */
public class Cash 
{
	/* Private variables */
	private final io.github.expugn.expugnextras.Configs.Cash config;
	
	private final int DAILY_PURCHASE_LIMIT = 1000; // Default == 50
	private final int DAILY_SELL_LIMIT = 1000; // Default == 100
	
	public Cash(ExpugnExtras plugin)
	{
		config = new io.github.expugn.expugnextras.Configs.Cash(plugin);
	}
	
	public void mainMenu(Player player)
	{
		config.createPlayerData(player);
		
		new FancyMessage("§6Welcome to the §2Expugn Stock Market§6.\n")
			.then("§8- §eCurrent Expugn Balance§7: §2$§6" + config.getCurrentCash() + "\n")
			.then("§8- §ePrevious Expugn Balance§7: §2$§6" + config.getPreviousCash() + "\n")
			.then("§8- §61 §2ExpugnCash §eis worth§7: §2$§6" + String.format("%.2g", config.getValue()) + "\n")
			.then("§8- §eYou currently have§7: §6" + config.getPlayerAmount(player) + " §2ExpugnCash§e.\n")
			.then("§6ExpugnCash Commands:\n")
			.then("§2[Buy ExpugnCash]\n")
				.tooltip("§6/expugn cash §dbuy [amount]\n"
						+ "§7You can buy §6" + (DAILY_PURCHASE_LIMIT - config.getPlayerTodayBought(player)) + " §7more §2ExpugnCash §7today.\n"
						+ "§fClick §2[Buy ExpugnCash] §fto auto-type the command.")
				.suggest("/expugn cash buy ")
			.then("§c[Sell ExpugnCash]\n")
				.tooltip("§6/expugn cash §dsell [amount]\n"
						+ "§7You can sell §6" + (DAILY_SELL_LIMIT - config.getPlayerTodaySold(player)) + " §7more §2ExpugnCash §7today.\n"
						+ "§fClick §c[Sell ExpugnCash] §fto auto-type the command.")
				.suggest("/expugn cash sell ")
			/*
			.then("§d[Buy Items]\n")
				.tooltip("§6/expugn cash §dshop\n"
						+ "§4WORK IN PROGRESS.\n\n"
						+ "§7Buy special items using §2ExpugnCash§7.\n"
						+ "§7There will be §63 §7random items listed daily.\n"
						+ "§7Shop will only be open if §2ExpugnCash §7is worth more than §2$§62§7.\n"
						+ "§fClick §d[Buy Items] §fto auto-type the command.")
				.suggest("/expugn cash shop")
			*/
			.then("§e[Player List]\n")
				.tooltip("§6/expugn cash §dlist\n"
						+ "§7Lists the §2ExpugnCash §7rankings.\n"
						+ "§7Players will be sorted from the highest profits to lowest.\n"
						+ "§fClick §e[Player List] §fto auto-type the command.")
				.suggest("/expugn cash list")
			.then("§6[Player Info]\n")
				.tooltip("§6/expugn cash §dinfo [Player_Name]\n"
						+ "§7Gets information about a player's §2ExpugnCash §7usage.\n"
						+ "§fClick §6[Player Info] §fto auto-type the command.")
				.suggest("/expugn cash info ")
			.then("§3[Help]\n")
				.tooltip("§6/expugn cash §dhelp\n"
						+ "§7Gives information about how §2ExpugnCash §7works.\n"
						+ "§fClick §3[Help] §fto auto-type the command.")
				.suggest("/expugn cash help")
			.then("§7Mouse over the options to get more information in a tooltip.")
			.send(player);
	}
	
	public void buy(Player player, String[] args)
	{
		if (args.length < 3)
		{
			player.sendMessage("§cInvalid arguments. §6/expugn cash §dbuy [amount]");
			return;
		}
		
		config.createPlayerData(player);

		int amount = Integer.parseInt(args[2]);
		int today_bought = config.getPlayerTodayBought(player);
		int amount_left = DAILY_PURCHASE_LIMIT - today_bought;
		double expugnCash_value = config.getValue();
		
		/* Check if request is under daily purchase limit */
		if (amount > amount_left)
		{
			player.sendMessage("§cYou cannot purchase §6" + amount + " §2ExpugnCash§c.\n"
					+ "§cYou can purchase §6" + amount_left + " §cmore §2ExpugnCash §ctoday.");
			return;
		}
		
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		
		double cost = expugnCash_value * amount;
		cost = Double.parseDouble(String.format("%.2g", cost));
		
		/* Check if the player has enough money */
		if (!checkMoney(player, cost))
		{
			player.sendMessage("§cYou cannot purchase §6" + amount + " §2ExpugnCash§c.\n"
					+ "§6" + amount_left + " §2ExpugnCash §ccosts §2$§6" + cost + "§c. You do not have enough money.");
			return;
		}
		
		/* Sell player the ExpugnCash, Update values */
		takeMoney(player, cost);
		config.addPlayerAmount(player, amount);
		config.addPlayerProfit(player, -cost);
		config.addPlayerBought(player, amount);
		config.addPlayerTodayBought(player, amount);
		config.addCurrentCash(cost);
		config.addValue(cost * 0.01);
		
		double changedValue = config.getValue() - expugnCash_value;
		changedValue = Double.parseDouble(String.format("%.2g", changedValue));
		
		/* Success Message, tell player changes */
		player.sendMessage("§6" + amount + " §2ExpugnCash §asuccessfully purchased.\n"
				+ "§8- §2$§6" + cost + " §7(§2$§6" + String.format("%.2g", expugnCash_value) + " §7* §6" + amount + "§7) §ehas been taken from your account.\n"
				+ "§8- §eNew §2ExpugnCash §eValue§7: §61 §2ExpugnCash §7= §2$§6" + String.format("%.2g", config.getValue()) + " §a(+§2$§6" + changedValue + "§a)\n"
				+ "§8- §eNew Expugn Balance§7: §6" + String.format("%.2g", config.getValue()) + " §a(+§2$§6" + cost + "§a)");
	}
	
	public void sell(Player player, String[] args)
	{
		if (args.length < 3)
		{
			player.sendMessage("§cInvalid arguments. §6/expugn cash §dsell [amount]");
			return;
		}
		
		config.createPlayerData(player);
		
		int amount = Integer.parseInt(args[2]);
		int today_sold = config.getPlayerTodaySold(player);
		int amount_left = DAILY_SELL_LIMIT - today_sold;
		double expugnCash_value = config.getValue();
		int playerAmount = config.getPlayerAmount(player);
		
		/* Check if player has enough ExpugnCash */
		if (playerAmount < amount)
		{
			player.sendMessage("§cYou cannot sell §6" + amount + " §2ExpugnCash§c.\n"
					+ "§cYou have §6" + playerAmount + " §2ExpugnCash§c.");
			return;
		}
		
		/* Check if request is under daily sell limit */
		if (amount > amount_left)
		{
			player.sendMessage("§cYou cannot sell §6" + amount + " §2ExpugnCash§c.\n"
					+ "§cYou can sell §6" + amount_left + " §cmore §2ExpugnCash §ctoday.");
			return;
		}
		
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		
		double cost = expugnCash_value * amount;
		cost = Double.parseDouble(String.format("%.2g", cost));
		
		/* Check if the stock has enough money */
		if (config.getCurrentCash() < cost)
		{
			player.sendMessage("§cYou cannot sell §6" + amount + " §2ExpugnCash§c.\n"
					+ "§cThe Expugn Balance is too low. It does not have §2$§6" + cost +" §cto give you.");
			return;
		}
		
		/* Give player money, Update values */
		giveMoney(player, cost);
		config.addPlayerAmount(player, -amount);
		config.addPlayerProfit(player, cost);
		config.addPlayerSold(player, amount);
		config.addPlayerTodaySold(player, amount);
		config.addCurrentCash(-cost);
		config.addValue(-cost * 0.01);
		
		double changedValue = expugnCash_value - config.getValue();
		changedValue = Double.parseDouble(String.format("%.2g", changedValue));
		
		/* Success Message, tell player changes */
		player.sendMessage("§6" + amount + " §2ExpugnCash §asuccessfully sold.\n"
				+ "§8- §2$§6" + cost + " §7(§2$§6" + String.format("%.2g", expugnCash_value) + " §7* §6" + amount + "§7) §ehas been added to your account.\n"
				+ "§8- §eNew §2ExpugnCash §eValue§7: §61 §2ExpugnCash §7= §2$§6" + String.format("%.2g", config.getValue()) + " §c(-§2$§6" + changedValue + "§c)\n"
				+ "§8- §eNew Expugn Balance§7: §6" + String.format("%.2g", config.getCurrentCash()) + " §c(-§2$§6" + cost + "§c)");
	}
	
	public void itemShop(Player player, String[] args)
	{
		double value = config.getValue();
		List<Map<String, Object>> shopItems = config.getShopItems();
		int i = 0;
		
		if (value < 2.0)
		{
			player.sendMessage("§cThe shop is closed. §2ExpugnCash §cis worth less than §2$§62§c.");
			return;
		}
		if (shopItems != null && !shopItems.isEmpty())
		{
			if (args.length > 2)
			{
				buyItem(player, Integer.parseInt(args[2]));
				return;
			}
			player.sendMessage("§2ExpugnCash §6Shop§7:\n"
					+ "§eItems reset everyday midnight time!");
			for (Map<String, Object> entry : shopItems)
			{
				//int itemPrice = e.getKey();
				//ItemStack item = e.getValue();
				
				int itemPrice = (int) entry.get("price");
				ItemStack itemData = (ItemStack) entry.get("item");
				
				new FancyMessage("- ")
					.then("§6" + itemPrice + " §2ExpugnCash §8| ")
					.then("§6" + itemData.getAmount() + "§ex ")
					.then(itemData.getItemMeta().getDisplayName())
						.itemTooltip(itemData)
					.then(" §8| ")
					.then("§d[Buy Item]")
						.tooltip("§7Buy this " + itemData.getItemMeta().getDisplayName() + "§7!")
						.command("/expugn cash shop " + i)
					.send(player);
				i++;
			}
			player.sendMessage("§7Make sure your inventory has enough space before buying items.");
		}
		else
		{
			player.sendMessage("§7There are no items to sell.");
			return;
		}
		
	}
	
	public void buyItem(Player player, int itemIndex)
	{
		int playerBalance = config.getPlayerAmount(player);
		//SortedMap<Integer, ItemStack> shopItems = config.getShopItems();
		List<Map<String, Object>> shopItems = config.getShopItems();
		Map<String, Object> item = shopItems.get(itemIndex);
		
		//int itemPrice = (int) shopItems.keySet().toArray()[itemIndex];
		//ItemStack item = (ItemStack) shopItems.values().toArray()[itemIndex];
		
		int itemPrice = (int) item.get("price");
		ItemStack itemData = (ItemStack) item.get("item");
		
		if(itemPrice > playerBalance)
		{
			player.sendMessage("§cYou do not have enough §2ExpugnCash §cto buy this item.");
			return;
		}
		
		config.addPlayerAmount(player, -itemPrice);
		player.getInventory().addItem(itemData);
		
		double moneyValue = Double.parseDouble(String.format("%.2g", (itemPrice * config.getValue())));
		
		player.sendMessage("§eYou have successfully bought a " + itemData.getItemMeta().getDisplayName() + "§e.\n"
				+ "§6" + itemPrice + " §2ExpugnCash §ehas been removed from your account.\n"
				+ "§6" + itemPrice + " §2ExpugnCash §eis worth §2$6" + moneyValue + "\n"
				+ "§eYou have §6" + config.getPlayerAmount(player) + " §2ExpugnCash §eleft.");
	}
	
	public void list(Player player, String[] args)
	{
		Player currentPlayer;
		OfflinePlayer currentOfflinePlayer;
		String playerName;
		SortedMap<Double, String> playerListData = new TreeMap<Double, String>(Collections.reverseOrder());
		
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
		
		for (String s : config.getPlayerList()) 
		{
			currentPlayer = Bukkit.getPlayer(UUID.fromString(s));
			currentOfflinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(s));
			
			if (currentPlayer != null)
				playerName = currentPlayer.getName();
			else
				playerName = currentOfflinePlayer.getName();
			
			playerListData.put(config.getDouble("cash.players." + s + ".profit"), playerName);
		}
		int maxPages = paginate(player, playerListData, page, 10);
		if (page > maxPages)
			page = maxPages;
		
		if (page <= 1 && 1 != maxPages) 
		{
			new FancyMessage("§d[Next Page ->]")
					.tooltip("§7Click to go forward one page.")
					.command("/expugn cash list " + (page + 1)).send(player);
		} 
		else if (page < maxPages && 1 != maxPages) 
		{
			new FancyMessage("§d[<- Previous Page]")
					.tooltip("§7Click to go back one page.")
					.command("/expugn cash list " + (page - 1))
					.then("       ")
					.then("§d[Next Page ->]")
					.tooltip("§7Click to go forward one page.")
					.command("/expugn cash list " + (page + 1))
					.send(player);
		} 
		else if (page <= maxPages && 1 != maxPages) 
		{
			new FancyMessage("§d[<- Previous Page]")
					.tooltip("§7Click to go back one page.")
					.command("/expugn cash list " + (page - 1)).send(player);
		}
	}
	
	public void info(Player player, String[] args)
	{
		if (args.length < 3)
		{
			player.sendMessage("§cInvalid arguments. §6/expugn cash §dinfo [Player_Name]");
			return;
		}
		
		Player currentPlayer;
		OfflinePlayer currentOfflinePlayer;
		String playerName = null;
		String wantedPlayerName = args[2];
		String playerUUID = null;
		boolean playerFound = false;
		
		for (String s : config.getPlayerList()) 
		{
			currentPlayer = Bukkit.getPlayer(UUID.fromString(s));
			currentOfflinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(s));
			
			if (currentPlayer != null)
				playerName = currentPlayer.getName();
			else
				playerName = currentOfflinePlayer.getName();
			
			if (wantedPlayerName.equalsIgnoreCase(playerName))
			{
				playerFound = true;
				playerUUID = s;
				break;
			}
		}
		if (!playerFound)
		{
			player.sendMessage("§cPlayer §6" + wantedPlayerName + " §cnot found. Have they used §2ExpugnCash§c?");
			return;
		}
			
		
		int playerBalance = config.getInt("cash.players." + playerUUID + ".amount");
		int playerBought = config.getInt("cash.players." + playerUUID + ".bought");
		int playerSold = config.getInt("cash.players." + playerUUID + ".sold");
		double playerProfit = config.getDouble("cash.players." + playerUUID + ".profit");
		double playerBalanceValue = (playerBalance * config.getValue());
		
		player.sendMessage("§ePlayer info for §6" + playerName + "\n"
				+ "§8- §2ExpugnCash §eBalance§7: §6" + playerBalance + " §e(§2$§6" + playerBalanceValue + "§e value)\n"
				+ "§8- §eTotal Bought§7: §6" + playerBought + "\n"
				+ "§8- §eTotal Sold§7: §6" + playerSold + "\n"
				+ "§8- §eTotal Profits§7: §2$§6" + playerProfit);
	}
	
	public void help(Player player, String[] args)
	{
		FancyMessage main = new FancyMessage("§2ExpugnCash §eHelp Menu§7:\n")
				.then("§e[What is ExpugnCash?]\n")
					.tooltip("§7Get help on what ExpugnCash is.")
					.command("/expugn cash help info")
				.then("§a[Buying and Selling]\n")
					.tooltip("§7Get help on what buying and selling is.")
					.command("/expugn cash help buyandsell")
				/*
				.then("§b[Item Shop]")
					.tooltip("§7Get help on what the item shop is.")
					.command("/expugn cash help itemshop")
				*/
					;
		FancyMessage info = new FancyMessage("§7'What is §2ExpugnCash§7?'\n")
				.then("§8- §2ExpugnCash §fis a marketing simulator of sorts.\n")
				.then("§8- §fUnlike the server currency, §2ExpugnCash §ffluxuates in it's value.\n")
				.then("§8- §fIf many players buy §2ExpugnCash§f, the value of §2ExpugnCash §fwill rise.\n")
				.then("§8- §fIf many players sell §2ExpugnCash§f, the value of §2ExpugnCash §fwill decrease.\n")
				.then("§8- §fIf you play your cards right, you can make a large profit off §2ExpugnCash§f.\n")
				//.then("§8- §fThere is a special item shop that takes §2ExpugnCash §ffor payment. Click the §b[Item Shop] §fhelp option for more info.\n")
				.then("§2ExpugnCash §6Terms:\n")
				.then("§8- §6'ExpugnCash'§8: §fThe currency name.\n")
				.then("§8- §6'Expugn Balance'§8: §fThe amount of money the system has. Determines §2ExpugnCash§f value.\n")
				.then("§8- §6'Current Balance'§8: §fThe amount of money the system currently has.\n")
				.then("§8- §6'Previous Balance'§8: §fThe amount of money the system had in the last update.\n")
				.then("§8- §6'Profits'§8: §fHow much money a player has made via §2ExpugnCash§f.\n")
				.then("§8- §6'Value'§8: §fHow much money one §2ExpugnCash §fis worth.\n");
		FancyMessage buyandsell = new FancyMessage("§7'What is Buying and Selling?'\n")
				.then("§8- §fBuying and Selling is the exchange of §2ExpugnCash §fbetween you and the system.\n")
				.then("§8- §fPlayers can buy §650 §2ExpugnCash §fand sell §6100 §2ExpugnCash §fdaily. This is to prevent crazy changes in price.\n")
				.then("§8- §fWith every transaction, the amount of money is multiplied by §60.01§f. The result is added or subtracted to or from the §2ExpugnCash §fvalue.\n")
				.then("§8- §2ExpugnCash §fcannot be sold if the cost is above the Expugn Balance.\n");
		FancyMessage itemshop = new FancyMessage("§7'What is the item shop?'\n")
				.then("§8- §fThe item shop is a shop where §2ExpugnCash §fis used.\n")
				.then("§8- §fYou can buy exclusive items here.\n")
				.then("§8- §fThese items are either very rare or unobtainable through dungeons/events/gameplay.\n")
				.then("§8- §fThe shop can only be open when the §2ExpugnCash §fvalue is above §2$§62§f. This is to prevent easy access to items.\n");
		
		if (args.length == 2)
		{
			main.send(player);
		}
		else if (args.length >= 3)
		{
			switch (args[2].toLowerCase())
			{
				case "info":
					info.send(player);
					break;
				case "buyandsell":
					buyandsell.send(player);
					break;
				case "itemshop":
					itemshop.send(player);
					break;
				default:
					main.send(player);
			}
		}
	}
	
	public void addItem(Player player, String[] args)
	{
		if (args.length != 3)
		{
			player.sendMessage("Invalid arguments. /expugn cash additem [price]");
			return;
		}
		
		int itemPrice = Integer.parseInt(args[2]);
		ItemStack item = player.getInventory().getItemInMainHand().clone();
		
		ShopItem entry = new ShopItem(itemPrice, item);

		if (item == null || item.getType() == Material.AIR)
			player.sendMessage("§cYour main hand is empty.");
		else
		{
			player.sendMessage("§aAdding Item §6" + item.getType() + "§a to shopItems.");
			config.addItem(entry);
		}
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Give a player money.
	 * 
	 * @param player  The player who sent the command
	 * @param amount  Amount of money to be given.
	 */
	public void giveMoney(Player player, double amount) 
	{
		if (amount >= 0)
			ExpugnExtras.econ.depositPlayer(player, player.getWorld().getName(), amount);
	}

	//-----------------------------------------------------------------------
	/**
	 * Take money from a player.
	 * 
	 * @param player  The player who sent the command
	 * @param amount  Amount of money to be taken.
	 */
	public void takeMoney(Player player, double amount) 
	{
		ExpugnExtras.econ.withdrawPlayer(player, player.getWorld().getName(), amount);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * Check if the player has enough money
	 * 
	 * @param player  The player who sent the command
	 * @param amount  Amount of money to be checked.
	 * @return  true if player has enough money, else false.
	 */
	public boolean checkMoney(Player player, double amount) 
	{
		if (amount > 0)
			return ExpugnExtras.econ.has(player, amount);
		else
			return false;
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
	public int paginate(Player player, SortedMap<Double, String> map, int page, int pageLength) 
	{
		if (page > (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1)) 
			page = (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1);
		
		player.sendMessage("§6Player List: " + "§ePage (§6" + String.valueOf(page) + " §eof §6" + (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1) + "§e)" 
				+ " §e(§c" + map.size() + "§e players total.)");
		
		int i = 0, k = 0;
		page--;
		
		for (final Entry<Double, String> e : map.entrySet()) 
		{
			k++;
			if ((((page * pageLength) + i + 1) == k) && (k != ((page * pageLength) + pageLength + 1))) 
			{
				i++;
				
				String profitString;
				if (e.getKey() < 0)
					profitString = "§2$§c" + String.format("%.2g", e.getKey());
				else
					profitString = "§2$§a" + String.format("%.2g", e.getKey());
				
				player.sendMessage("§6" + i + "§8) §7" + e.getValue() + " §8- " + profitString);
			}
		}
		return (((map.size() % pageLength) == 0) ? map.size() / pageLength : (map.size() / pageLength) + 1);
	}
}
