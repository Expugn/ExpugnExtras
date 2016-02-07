package io.github.expugn.functions;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import io.github.expugn.expugnextras.ExpugnExtras;
import io.github.expugn.fetchers.UUIDFetcher;

/**
 * 'Marriage' Function
 * Manages the "marriage" feature in /marriage
 * 
 * @author Expugn
 * https://github.com/Expugn
 * 
 * @version 1.0
 */
public class Marriage 
{
	/* example marriage.yml
	 * players:
	 *   <uuid>:
	 *     partner: <uuid>
	 *     level: <int>
	 *     effectcooldown: <long>
	 *     cooldown: <long>
	 *     requestexpire: <long>
	 *     requesting: <uuid>
	 *     requested: <uuid>
	 *     daterefund: <boolean>
	 * banlist:
	 *   - <uuid>
	 *   - <uuid2>
	 *   - <uuid3>
	 */
	
	// System Variables
	private File ymlFile;
	private FileConfiguration config;
	private boolean enabled;
	
	// Player Variables
	private String playerPartner = "";
	private long playerCooldown = 0L;
	
	// System Messages
	private static final String prefix = ChatColor.BLACK + "[" + ChatColor.GOLD + "Marriage" + ChatColor.BLACK + "]" + ChatColor.DARK_GRAY + " - ";
	private static final String miniPrefix = ChatColor.BLACK + "[" + ChatColor.GOLD + "*" + ChatColor.BLACK + "]" + ChatColor.DARK_GRAY + " - ";
	
	// Error Messages
	private static final String DATE_INVALID_SYNTAX_ERROR = prefix + ChatColor.RED + "Invalid Syntax.\n"
			+ miniPrefix + ChatColor.GOLD + "/marriage date [playername]";
	private static final String IN_RELATIONSHIP_ERROR = prefix + ChatColor.RED + "You are already in a relationship.";
	private static final String REQUEST_ON_COOLDOWN_ERROR = prefix + ChatColor.RED + "You cannot make a request again just yet.";
	private static final String CANT_DATE_YOURSELF_ERROR = prefix + ChatColor.RED + "You cannot date yourself.";
	private static final String OTHER_IN_RELATIONSHIP_ERROR = prefix + ChatColor.RED + "That player is already in a relationship.";
	private static final String OTHER_BLOCKED_REQUESTS_ERROR = prefix + ChatColor.RED + "This player has blocked requests.";
	private static final String OTHER_SENT_REQUEST_ALREADY_ERROR = prefix + ChatColor.RED + "This player has sent a request to someone else.";
	private static final String OTHER_HAS_REQUEST_ALREADY_ERROR = prefix + ChatColor.RED + "This player has a pending request already.";
	private static final String OTHER_IS_NOT_ONLINE_ERROR = prefix + ChatColor.RED + "This player is offline.";
	private static final String REQUEST_HAS_NOT_EXPIRED_ERROR = prefix + ChatColor.RED + "Your request has not expired yet.";
	private static final String NO_REQUEST_ERROR = prefix + ChatColor.RED + "You do not have a pending request.";
	
	// Item Lists
	private static final ItemStack[] dateItems = {new ItemStack(Material.IRON_BLOCK), new ItemStack(Material.RED_ROSE)};
	private static final ItemStack[] engagementItems = {new ItemStack(Material.EMERALD_BLOCK), new ItemStack(Material.RED_ROSE)};
	private static final ItemStack[] marriageItems = {new ItemStack(Material.GOLD_BLOCK, 2), new ItemStack(Material.DIAMOND, 2), new ItemStack(Material.RED_ROSE)};
	
	/**
	 * Constructor for the class
	 * 
	 * @param plugin
	 */
	public Marriage(ExpugnExtras plugin)
	{
		enabled = !plugin.getConfig().getBoolean("marriage");
		ymlFile = new File(plugin.getDataFolder() + "/marriage.yml");
		config = YamlConfiguration.loadConfiguration(ymlFile);
	}
	
	/**
	 * Date - Request to start a new relationship with someone.
	 * Syntax: /marriage date [playername]
	 * 
	 * @param player
	 * @param args
	 */
	public void date(Player player, String[] args)
	{
		String requestedPlayerPartner = null;
		Player requestedPlayer = null;
		
		playerPartner = config.getString("players." + player.getUniqueId() + ".partner");
		playerCooldown = config.getLong("players." + player.getUniqueId() + ".cooldown");
		
		if (playerCooldown == 0L)
			playerCooldown = 0;
		
		List<String> banList = config.getStringList("banlist");
		
		// /marriage date
		if (args.length == 1)
		{
			if (null != config.getString("players." + player.getUniqueId() + ".requesting"))
			{
				if (System.currentTimeMillis() > config.getLong("players." + config.getString("players." + player.getUniqueId() + ".requesting") + ".requestexpire"))
				{
					refund(player, args);
					return;
				}
				else
				{
					player.sendMessage(REQUEST_HAS_NOT_EXPIRED_ERROR);
					return;
				}
			}
			else
			{
				player.sendMessage(NO_REQUEST_ERROR);
				return;
			}
		}
		// /marriage date <string>
		else if (args.length == 2)
		{
			if (!checkPlayerOnline(args[1]))
			{
				player.sendMessage(OTHER_IS_NOT_ONLINE_ERROR);
				return;
			}
			else
			{
				requestedPlayer = this.getPlayer(args[1]);
				requestedPlayerPartner = config.getString("players." + requestedPlayer.getUniqueId() + ".partner");
			}
		}
		// /marriage date <arg> <arg>
		else if (args.length != 2)
			player.sendMessage(DATE_INVALID_SYNTAX_ERROR);
		
		// Check if the user is in a relationship
		if (playerPartner != null && !playerPartner.isEmpty())
		{
			player.sendMessage(IN_RELATIONSHIP_ERROR);
		}
		// Check if the user is on cooldown
		else if (System.currentTimeMillis() < playerCooldown)
		{
			player.sendMessage(REQUEST_ON_COOLDOWN_ERROR);
			convertMilliseconds(player, (playerCooldown - System.currentTimeMillis()));
		}
		// Check if the user is trying to request themself
		else if (args[1].equals(player.getName()))
		{
			player.sendMessage(CANT_DATE_YOURSELF_ERROR);
		}
		// Check if the requested player is in a relationship
		else if (requestedPlayerPartner != null && !requestedPlayerPartner.isEmpty())
		{
			player.sendMessage(OTHER_IN_RELATIONSHIP_ERROR);
		}
		// Check if the requested player blocked requests
		else if (banList.contains(requestedPlayer.getUniqueId().toString()))
		{
			player.sendMessage(OTHER_BLOCKED_REQUESTS_ERROR);
		}
		// Check if the requested player is already requesting someone else
		else if (config.getString("players." + requestedPlayer.getUniqueId() + ".requesting") != null && !config.getString("marriage.players." + requestedPlayer.getUniqueId() + ".requesting").isEmpty())
		{
			player.sendMessage(OTHER_SENT_REQUEST_ALREADY_ERROR);
		}
		// Check if the requested player already has a request
		else if (config.getString("players." + requestedPlayer.getUniqueId() + ".requested") != null && !config.getString("players." + requestedPlayer.getUniqueId() + ".requested").isEmpty())
		{
			player.sendMessage(OTHER_HAS_REQUEST_ALREADY_ERROR);
		}
		// Check if the user already sent a request
		else if (config.getString("players." + player.getUniqueId() + ".requesting") != null && !config.getString("players." + player.getUniqueId() + ".requesting").isEmpty())
		{
			player.sendMessage(prefix + ChatColor.RED + "You are already requesting another player.\n" +
					miniPrefix + ChatColor.RED + "You sent a request to: " + ChatColor.GOLD + config.getString("players." + player.getUniqueId() + ".requesting") + ".\n" +
					miniPrefix + ChatColor.RED + "You can remove a expired request with " + ChatColor.GOLD + "/marriage date" + ChatColor.RED + ".");
		}
		// Check if the user has a pending request
		else if (config.getString("players." + player.getUniqueId() + ".requested") != null && !config.getString("players." + player.getUniqueId() + ".requested").isEmpty())
		{
			player.sendMessage(prefix + ChatColor.RED + "You have a pending request from another player.\n" +
					miniPrefix + ChatColor.RED + "The following player has requested to take you on a date: " + ChatColor.GOLD + config.getString("players." + player.getUniqueId() + ".requested") + ".\n" +
					miniPrefix + ChatColor.RED + "Accept their request with " + ChatColor.GOLD + "/marriage accept" + ChatColor.RED + ".\n" + 
					miniPrefix + ChatColor.RED + "Decline their request with " + ChatColor.GOLD + "/marriage decline" + ChatColor.RED + ".");
		}
		// Everything looks good. Send request.
		else
		{
			sendDateRequest(player, requestedPlayer, args, player.getUniqueId().toString(), requestedPlayer.getUniqueId().toString());
		}
	}
	
	/**
	 * Breakup - Split from an existing relationship
	 * Syntax: /marriage breakup
	 * 
	 * @param player
	 * @param args
	 */
	public void breakup(Player player, String[] args)
	{
		String playerPartnerUUID = null;
		Player playerPartner = null;
		OfflinePlayer offlinePlayerPartner = null;
		
		if(config.getString("players." + player.getUniqueId() + ".partner") != null && !config.getString("players." + player.getUniqueId() + ".partner").isEmpty())
		{
			playerPartnerUUID = config.getString("players." + player.getUniqueId() + ".partner");
			playerPartner = getPlayerFromUUIDString(playerPartnerUUID);
			offlinePlayerPartner = getOfflinePlayerFromUUIDString(playerPartnerUUID);
			
			if (playerPartner != null)
			{
				player.sendMessage(prefix + ChatColor.GREEN + "You broke up with " + ChatColor.GOLD + playerPartner.getName() + ChatColor.GREEN + "...");
				playerPartner.sendMessage(prefix + ChatColor.GREEN + "You broke up with " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + "...");
				deleteConfigData(playerPartner, "partner");
				deleteConfigData(playerPartner, "level");
				deleteConfigData(playerPartner, "couplesince");
			}
			else
			{
				player.sendMessage(prefix + ChatColor.GREEN + "You broke up with " + ChatColor.GOLD + offlinePlayerPartner.getName() + ChatColor.GREEN + "...");
				deleteConfigData(offlinePlayerPartner, "partner");
				deleteConfigData(offlinePlayerPartner, "level");
				deleteConfigData(offlinePlayerPartner, "couplesince");
			}
			
			deleteConfigData(player, "partner");
			deleteConfigData(player, "level");
			deleteConfigData(player, "couplesince");
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "You are not in a relationship.");
		}
	}
	
	/**
	 * acceptRequest - Accept a player's request to start a new relationship.
	 * Syntax: /marriage accept
	 * 
	 * @param player
	 * @param args
	 */
	public void acceptRequest(Player player, String[] args)
	{
		Long playerRequestExpire = config.getLong("players." + player.getUniqueId() + ".requestexpire");
		
		if (playerRequestExpire == 0L)
		{
			player.sendMessage(prefix + ChatColor.RED + "You don't have a pending request.");
			return;
		}
		
		Long currentTime = System.currentTimeMillis();
		Player originPlayer = getPlayerFromUUIDString(config.getString("players." + player.getUniqueId() + ".requested"));
		OfflinePlayer offlineOriginPlayer = getOfflinePlayerFromUUIDString(config.getString("players." + player.getUniqueId() + ".requested"));
		
		if (System.currentTimeMillis() > playerRequestExpire)
		{
			player.sendMessage(prefix + ChatColor.RED + "The request has expired.");
			if (originPlayer != null)
			{
				originPlayer.sendMessage(prefix + ChatColor.GOLD + player.getName() + ChatColor.RED + " has responded to your request but it expired! Your items will be refunded.");
				
				originPlayer.getInventory().addItem(dateItems);
				giveMoney(originPlayer, 250);
				
				deleteConfigData(originPlayer, "requesting");
			}
			else
			{
				setConfigData(offlineOriginPlayer, "daterefund", true);
				deleteConfigData(offlineOriginPlayer, "requesting");
			}
			deleteConfigData(player, "requested");
			deleteConfigData(player, "requestexpire");
		}
		else
		{
			String playerUUID = player.getUniqueId().toString();
			String originPlayerUUID = null;
			String originPlayerName = null;
					
			if (originPlayer != null)
			{
				originPlayerUUID = originPlayer.getUniqueId().toString();
				originPlayerName = originPlayer.getName();
			}
			else
			{
				originPlayerUUID = offlineOriginPlayer.getUniqueId().toString();
				originPlayerName = offlineOriginPlayer.getName();
			}
			
			player.sendMessage(prefix + ChatColor.GREEN + "You are now dating " + ChatColor.GOLD + originPlayerName + ChatColor.GREEN + ". Congratulations!");
			
			if (originPlayer != null)
			{
				originPlayer.sendMessage(prefix + ChatColor.GREEN + "You are now dating " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + ". Congratulations!");
				deleteConfigData(originPlayer, "requesting");
				deleteConfigData(originPlayer, "cooldown");
				setConfigData(originPlayer, "partner", playerUUID);
				setConfigData(originPlayer, "level", 1);
				setConfigData(originPlayer, "couplesince", currentTime);
			}
			else
			{
				deleteConfigData(offlineOriginPlayer, "requesting");
				deleteConfigData(offlineOriginPlayer, "cooldown");
				setConfigData(offlineOriginPlayer, "partner", playerUUID);
				setConfigData(offlineOriginPlayer, "level", 1);
				setConfigData(offlineOriginPlayer, "couplesince", currentTime);
			}
			
			deleteConfigData(player, "requested");
			deleteConfigData(player, "requestexpire");
			
			setConfigData(player, "partner", originPlayerUUID);
			setConfigData(player, "level", 1);
			setConfigData(player, "couplesince", currentTime);
		}
	}
	
	/**
	 * declineRequest - Deny a player's request to start a new relationship.
	 * Syntax: /marriage decline
	 * 
	 * @param player
	 * @param args
	 */
	public void declineRequest(Player player, String[] args)
	{
		Long playerRequestExpire = config.getLong("players." + player.getUniqueId() + ".requestexpire");
		
		if (playerRequestExpire == 0L)
		{
			player.sendMessage(prefix + ChatColor.RED + "You don't have a pending request.");
			return;
		}
		
		Player originPlayer = getPlayerFromUUIDString(config.getString("players." + player.getUniqueId() + ".requested"));
		OfflinePlayer offlineOriginPlayer = getOfflinePlayerFromUUIDString(config.getString("players." + player.getUniqueId() + ".requested"));
		
		if (System.currentTimeMillis() > playerRequestExpire)
		{
			player.sendMessage(prefix + ChatColor.RED + "The request has expired.");
			if (originPlayer != null)
			{
				originPlayer.sendMessage(prefix + ChatColor.GOLD + player.getName() + ChatColor.RED + " has responded to your request but it expired! Your items will be refunded.");
				
				originPlayer.getInventory().addItem(dateItems);
				giveMoney(originPlayer, 250);
				
				deleteConfigData(originPlayer, "requesting");
			}
			else
			{
				setConfigData(offlineOriginPlayer, "daterefund", true);
				deleteConfigData(offlineOriginPlayer, "requesting");
			}
			deleteConfigData(player, "requested");
			deleteConfigData(player, "requestexpire");
		}
		else
		{
			if (originPlayer != null)
			{
				player.sendMessage(prefix + ChatColor.GREEN + "You have denied " + ChatColor.GOLD + originPlayer.getName() + ChatColor.GREEN + "'s  request.");
				originPlayer.sendMessage(prefix + ChatColor.GOLD + player.getName() + ChatColor.GREEN + " turned down your date request.");
				deleteConfigData(originPlayer, "requesting");
			}
			else
			{
				player.sendMessage(prefix + ChatColor.GREEN + "You have denied " + ChatColor.GOLD + offlineOriginPlayer.getName() + ChatColor.GREEN + "'s  request.");
				deleteConfigData(offlineOriginPlayer, "requesting");
			}
			deleteConfigData(player, "requested");
			deleteConfigData(player, "requestexpire");
		}
	}
	
	/**
	 * Advance - Evolve an existing relationship with the cost of money and a few items.
	 * Syntax: /marriage advance
	 * 
	 * @param player
	 * @param args
	 */
	public void advance(Player player, String[] args)
	{
		int playerCoupleLevel = config.getInt("players." + player.getUniqueId() + ".level");
		
		switch(playerCoupleLevel)
		{
			case 0:
				player.sendMessage(prefix + ChatColor.RED + "You are not in a relationship.");
				break;
			case 1:
				levelOneAdvance(player);
				break;
			case 2:
				levelTwoAdvance(player);
				break;
			case 3:	
				player.sendMessage(prefix + ChatColor.RED + "You cannot advance any further in your relationship.");
				break;
			default:
				break;
		}
	}
	
	/**
	 * Block - Block yourself from receiving date requests
	 * Syntax: /marriage block
	 * 
	 * @param player
	 * @param args
	 */
	public void block(Player player, String[] args)
	{
		List<String> banList = config.getStringList("banlist");
		
		if (banList.contains(player.getUniqueId().toString()))
		{
			banList.remove(player.getUniqueId().toString());
			player.sendMessage(prefix + ChatColor.GREEN + "Players can now send requests to you.");
		}
		else
		{
			banList.add(player.getUniqueId().toString());
			player.sendMessage(prefix + ChatColor.GREEN + "You have blocked yourself from recieving requests.");
		}
		
		config.set("banlist", banList);
		saveConfig();
	}
	
	/**
	 * Info - Fetch relationship information and display it to the player.
	 * Syntax: /marriage info <playername>
	 * 
	 * @param player
	 * @param args
	 */
	public void info(Player player, String[] args)
	{
		String playerName = null;
		if (args.length != 2)
		{
			if (args.length == 1)
			{
				playerName = player.getName();
			}
			else
			{
				player.sendMessage(prefix + ChatColor.RED + "Invalid syntax. " + ChatColor.GOLD + "/marriage info <playername>");
				return;
			}
		}	
		
		if (playerName == null)
			playerName = args[1];
		String requestedPlayerName = null;
		String requestedPlayerUUID = null;
		List<String> banList = config.getStringList("banlist");

		try
		{
			requestedPlayerUUID = UUIDFetcher.getUUIDOf(playerName).toString();
		}
		catch (Exception e)
		{
			player.sendMessage(prefix + ChatColor.GOLD + playerName +ChatColor.RED + " does not exist.");
			return;
		}
		if (checkPlayerOnline(playerName))
		{
			Player requestedPlayer = getPlayerFromUUIDString(requestedPlayerUUID);
			requestedPlayerName = requestedPlayer.getName();
		}
		else
		{
			OfflinePlayer requestedPlayer = getOfflinePlayerFromUUIDString(requestedPlayerUUID);
			requestedPlayerName = requestedPlayer.getName();
		}
		if (requestedPlayerName == null)
		{
			player.sendMessage(prefix + ChatColor.GOLD + playerName + ChatColor.RED + " has never joined the server.");
			return;
		}
		
		boolean inBanList = banList.contains(requestedPlayerUUID);
		String inBanListString = null;
		int level = config.getInt("players." + requestedPlayerUUID + ".level");
		String levelString = null;
		String partner = null;
		String partnerPlayerName = null;
		if (level > 0)
		{
			partner = config.getString("players." + requestedPlayerUUID + ".partner");
			if (checkPlayerOnline(partner))
			{
				partnerPlayerName = getPlayerFromUUIDString(partner).getName();
			}
			else
			{
				partnerPlayerName = getOfflinePlayerFromUUIDString(partner).getName();
			}
		}
		
		int years = 0;
		int months = 0;
		int weeks = 0;
		int days = 0;
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		long milliseconds = System.currentTimeMillis() - config.getLong("players." + requestedPlayerUUID + ".couplesince");

		if (level == 1)
			levelString = ChatColor.DARK_RED + "Dating!";
		else if (level == 2)
			levelString = ChatColor.DARK_RED + "Engaged!";
		else if (level == 3)
			levelString = ChatColor.DARK_RED + "Married!";
		else
			levelString = ChatColor.DARK_GREEN + "Single!";

		if (inBanList)
			inBanListString = ChatColor.DARK_RED + "True";
		else
			inBanListString = ChatColor.DARK_GREEN + "False";

		// 1 Year
		if (milliseconds >= 31540000000L)
		{
			years = (int)(milliseconds / 31540000000L);
			milliseconds = milliseconds % 31540000000L;
		}
		// 1 Month
		if (milliseconds >= 2628000000L)
		{
			months = (int)(milliseconds / 2628000000L);
			milliseconds = milliseconds % 2628000000L;
		}
		// 1 Week
		if (milliseconds >= 604800000L)
		{
			weeks = (int)(milliseconds / 604800000L);
			milliseconds = milliseconds % 604800000L;
		}
		// 1 Day
		if (milliseconds >= 86400000L)
		{
			days = (int)(milliseconds / 86400000L);
			milliseconds = milliseconds % 86400000L;
		}
		// 1 Hour
		if (milliseconds >= 3600000L)
		{
			hours = (int)(milliseconds / 3600000L);
			milliseconds = milliseconds % 3600000L;
		}
		// 1 Minute
		if (milliseconds >= 60000L)
		{
			minutes = (int)(milliseconds / 60000L);
			milliseconds = milliseconds % 60000L;
		}
		// 1 Second
		if (milliseconds >= 1000L)
		{
			seconds = (int)(milliseconds / 1000L);
			milliseconds = milliseconds % 1000L;
		}

		player.sendMessage(prefix + ChatColor.GREEN + "Relationship status for " + ChatColor.GOLD + requestedPlayerName + ChatColor.GREEN + ":");
		player.sendMessage(miniPrefix + ChatColor.GREEN + "Status: " + levelString);
		if (level > 0)
			player.sendMessage(miniPrefix + ChatColor.GREEN + "Partner: " + ChatColor.GOLD + partnerPlayerName);
		player.sendMessage(miniPrefix + ChatColor.GREEN + "Blocked from Requests: " + inBanListString);
		if (level > 0)
			player.sendMessage(miniPrefix + ChatColor.GREEN + "In a relationship for: " + 
					ChatColor.GOLD + years + ChatColor.GREEN + " yrs, " +
					ChatColor.GOLD + months + ChatColor.GREEN + " mo, " +
					ChatColor.GOLD + weeks + ChatColor.GREEN + " wks, " +
					ChatColor.GOLD + days + ChatColor.GREEN + " d, and " +
					ChatColor.GOLD + hours + ChatColor.GREEN + ":" +
					ChatColor.GOLD + minutes + ChatColor.GREEN + ":" +
					ChatColor.GOLD + seconds + ChatColor.GREEN + ".");
	}
	
	/**
	 * Dev - Administrative management of relationships
	 * Syntax: /marriage dev <args>
	 * 
	 * @param player
	 * @param args
	 */
	public void dev(Player player, String[] args)
	{
		if (args.length == 1)
		{
			player.sendMessage(prefix + ChatColor.GREEN + "Dev Command List\n"
		            + miniPrefix + ChatColor.DARK_AQUA + "All commands start with '" + ChatColor.GOLD + "/marriage dev" + ChatColor.DARK_AQUA + "'.\n"
		            + miniPrefix + ChatColor.GOLD + "heartbreak" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Force a breakup\n"
		            + miniPrefix + ChatColor.GOLD + "forcelove" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Force a relationship\n"
		            + miniPrefix + ChatColor.GOLD + "forceadvance" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Force a advancement\n"
		            + miniPrefix + ChatColor.GOLD + "warmup" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Remove cooldown for a player\n"
		            + miniPrefix + ChatColor.GOLD + "give" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Gives items for advancements\n"
		            + miniPrefix + ChatColor.GOLD + "timemachine" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Add time to relationship status\n"
		            + miniPrefix + ChatColor.GOLD + "banlist" + ChatColor.DARK_GRAY + ": " + ChatColor.GREEN + "Toggle blocked requests for a player\n");
		}
		else
		{
			switch(args[1].toLowerCase())
			{
				case "heartbreak":
					devHeartbreak(player, args);
					break;
				case "forcelove":
					devForceLove(player, args);
					break;
				case "forceadvance":
					devForceAdvance(player, args);
					break;
				case "warmup":
					devWarmup(player, args);
					break;
				case "give":
					devGive(player, args);
					break;
				case "timemachine":
					devTimeMachine(player, args);
					break;
				case "banlist":
					devBanlist(player, args);
					break;
				default:
					player.sendMessage(prefix + ChatColor.RED + "Invalid Command.\n" + miniPrefix + ChatColor.RED + "Use " + ChatColor.GOLD + "/marriage dev" + ChatColor.RED + " for a help menu.");
			}
		}
	}
	
	/**
	 * onPlayerInteract - Is called whenever a player right clicks an entity.
	 * 
	 * @param event
	 */
	public void onPlayerInteract(PlayerInteractEntityEvent event)
	{
		if (enabled)
		{
			config = YamlConfiguration.loadConfiguration(ymlFile);
			Player player = event.getPlayer();
			String playerUUID = player.getUniqueId().toString();
			
			Player playerInteracted = (Player)event.getRightClicked();
			String playerInteractedUUID = playerInteracted.getUniqueId().toString();
			
			String playerPartnerUUID = null;
			int playerLevel = 0;
			long effectCooldown = 0L;
			
			if (config.getString("players." + playerUUID + ".partner") != null && !config.getString("players." + playerUUID + ".partner").isEmpty())
			{
				// TODO
				playerPartnerUUID = config.getString("players." + playerUUID + ".partner");
				playerLevel = config.getInt("players." + playerUUID + ".level");
				effectCooldown = config.getLong("players." + playerUUID + ".effectcooldown");
				
				if (playerLevel != 0 && playerPartnerUUID.equals(playerInteractedUUID))
				{
					if (System.currentTimeMillis() < effectCooldown)
					{
						int seconds = (int) (effectCooldown - System.currentTimeMillis()) / 1000;
						player.sendMessage(miniPrefix + ChatColor.LIGHT_PURPLE + "Please wait " + ChatColor.GOLD + seconds + ChatColor.LIGHT_PURPLE + " second(s) before interacting with your partner.");
						return;
					}
					if (player.isSneaking() && playerLevel > 0)
					{
						player.sendMessage(miniPrefix + ChatColor.LIGHT_PURPLE + "You gave " + ChatColor.GOLD + playerInteracted.getName() + ChatColor.LIGHT_PURPLE + " a kiss! " + ChatColor.DARK_RED + "❤");
						playerInteracted.sendMessage(miniPrefix + ChatColor.GOLD + player.getName() + ChatColor.LIGHT_PURPLE + " gave you a kiss! " + ChatColor.DARK_RED + "❤");
						
						playerInteracted.setHealth(20.0);
						
						setConfigData(player, "effectcooldown", (System.currentTimeMillis() + 60000L));
					}
					else
					{
						if (player.getItemInHand().equals(new ItemStack(Material.MUSHROOM_SOUP)) &&
							playerLevel > 0)
						{
							player.sendMessage(miniPrefix + ChatColor.LIGHT_PURPLE + "You fed " + ChatColor.GOLD + playerInteracted.getName() + ChatColor.LIGHT_PURPLE + " some mushroom stew! " + ChatColor.DARK_RED + "❤");
							playerInteracted.sendMessage(miniPrefix + ChatColor.GOLD + player.getName() + ChatColor.LIGHT_PURPLE + " fed you some mushroom stew! " + ChatColor.DARK_RED + "❤");
							
							playerInteracted.setFoodLevel(20);
							playerInteracted.setSaturation(1.2F);
							player.getInventory().removeItem(new ItemStack(Material.MUSHROOM_SOUP, 1));
							player.getInventory().addItem(new ItemStack(Material.BOWL, 1));
							
							setConfigData(player, "effectcooldown", (System.currentTimeMillis() + 60000L));
						}
						else if (player.getItemInHand().equals(new ItemStack(Material.GOLD_PICKAXE)) &&
								 playerLevel > 1)
						{
							player.sendMessage(miniPrefix + ChatColor.LIGHT_PURPLE + "You gave " + ChatColor.GOLD + playerInteracted.getName() + ChatColor.LIGHT_PURPLE + " a gold pickaxe! " + ChatColor.DARK_RED + "❤");
							playerInteracted.sendMessage(miniPrefix + ChatColor.GOLD + player.getName() + ChatColor.LIGHT_PURPLE + " gave you a gold pickaxe! " + ChatColor.DARK_RED + "❤");
							
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 3600, 0));
							player.getInventory().removeItem(new ItemStack(Material.GOLD_PICKAXE, 1));
							
							setConfigData(player, "effectcooldown", (System.currentTimeMillis() + 60000L));
						}
						else if (player.getItemInHand().equals(new ItemStack(Material.GOLDEN_APPLE)) &&
								 playerLevel > 1)
						{
							player.sendMessage(miniPrefix + ChatColor.LIGHT_PURPLE + "You gave " + ChatColor.GOLD + playerInteracted.getName() + ChatColor.LIGHT_PURPLE + " a golden apple! " + ChatColor.DARK_RED + "❤");
							playerInteracted.sendMessage(miniPrefix + ChatColor.GOLD + player.getName() + ChatColor.LIGHT_PURPLE + " gave you a golden apple! " + ChatColor.DARK_RED + "❤");
							
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 3600, 0));
							player.getInventory().removeItem(new ItemStack(Material.GOLDEN_APPLE, 1));
							
							setConfigData(player, "effectcooldown", (System.currentTimeMillis() + 60000L));
						}
						else if (player.getItemInHand().equals(new ItemStack(Material.GOLD_CHESTPLATE)) &&
								 playerLevel > 2)
						{
							player.sendMessage(miniPrefix + ChatColor.LIGHT_PURPLE + "You gave " + ChatColor.GOLD + playerInteracted.getName() + ChatColor.LIGHT_PURPLE + " a gold chestplate! " + ChatColor.DARK_RED + "❤");
							playerInteracted.sendMessage(miniPrefix + ChatColor.GOLD + player.getName() + ChatColor.LIGHT_PURPLE + " gave you a gold chestplate! " + ChatColor.DARK_RED + "❤");
							
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 3600, 0));
							player.getInventory().removeItem(new ItemStack(Material.GOLD_CHESTPLATE, 1));
							
							setConfigData(player, "effectcooldown", (System.currentTimeMillis() + 60000L));
						}
						else if (player.getItemInHand().equals(new ItemStack(Material.CAKE)) &&
								 playerLevel > 2)
						{
							player.sendMessage(miniPrefix + ChatColor.LIGHT_PURPLE + "You gave " + ChatColor.GOLD + playerInteracted.getName() + ChatColor.LIGHT_PURPLE + " a cake! " + ChatColor.DARK_RED + "❤");
							playerInteracted.sendMessage(miniPrefix + ChatColor.GOLD + player.getName() + ChatColor.LIGHT_PURPLE + " gave you a cake! " + ChatColor.DARK_RED + "❤");
							
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 2));
							player.getInventory().removeItem(new ItemStack(Material.CAKE, 1));
							
							setConfigData(player, "effectcooldown", (System.currentTimeMillis() + 60000L));
						}
						else if (player.getItemInHand().equals(new ItemStack(Material.MILK_BUCKET)) &&
								 playerLevel > 2)
						{
							player.sendMessage(miniPrefix + ChatColor.LIGHT_PURPLE + "You gave " + ChatColor.GOLD + playerInteracted.getName() + ChatColor.LIGHT_PURPLE + " some milk to drink! " + ChatColor.DARK_RED + "❤");
							playerInteracted.sendMessage(miniPrefix + ChatColor.GOLD + player.getName() + ChatColor.LIGHT_PURPLE + " gave you some milk to drink! " + ChatColor.DARK_RED + "❤");
							
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 0, 0));
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 0, 0));
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 0, 0));
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 0, 0));
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 0, 0));
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 0, 0));
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 0, 0));
							playerInteracted.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 0, 0));
							player.getInventory().removeItem(new ItemStack(Material.MILK_BUCKET, 1));
							player.getInventory().addItem(new ItemStack(Material.BUCKET, 1));
							
							setConfigData(player, "effectcooldown", (System.currentTimeMillis() + 60000L));
						}
					}
				}
			}
		}
	}
	
	/**
	 * sendDateRequest - Called from the Date function, sends a date request to another player.
	 * 
	 * @param player
	 * @param requestedPlayer
	 * @param args
	 * @param playerUUID
	 * @param requestedPlayerUUID
	 */
	public void sendDateRequest(Player player, Player requestedPlayer, String[] args, String playerUUID, String requestedPlayerUUID)
	{
		if (!hasDateItems(player))
		{
			player.sendMessage(prefix + ChatColor.GREEN + "You do not have all the items you need to\n request a date!\n" +
					miniPrefix + ChatColor.GREEN + "You need:\n" +
					miniPrefix + ChatColor.GOLD + "1 " + ChatColor.GREEN + "Iron Block\n" +
					miniPrefix + ChatColor.GOLD + "1 " + ChatColor.GREEN + "Poppy Flower\n" +
					miniPrefix + ChatColor.GREEN + "$" + ChatColor.GOLD + "250");
		}
		else
		{
			player.getInventory().removeItem(dateItems);
			takeMoney(player, 250);
			
			setConfigData(player, "requesting", requestedPlayerUUID);
			setConfigData(player, "cooldown", (System.currentTimeMillis() + 60000));
			setConfigData(requestedPlayer, "requested", playerUUID);
			setConfigData(requestedPlayer, "requestexpire", (System.currentTimeMillis() + 60000));

			requestedPlayer.sendMessage(prefix + ChatColor.GOLD + player.getName() + ChatColor.GREEN + " has requested to take you out on a date!\n" +
					miniPrefix + ChatColor.DARK_GREEN + "/marriage accept " + ChatColor.GREEN + " to accept the request.\n" +
					miniPrefix + ChatColor.DARK_RED + "/marriage decline " + ChatColor.GREEN + " to decline the request.\n" +
					miniPrefix + ChatColor.GRAY + "(This request will expire in one minute.)");
			player.sendMessage(prefix + ChatColor.GREEN + "The request has been sent! Good luck!\n" +
						miniPrefix + ChatColor.GREEN + "If the request expired and you wish to reclaim your items, use " +
						ChatColor.GOLD + "/marriage date" + ChatColor.GREEN + ".");
		}
	}
	
	/**
	 * levelOneAdvance - Called from the Advance function, advances a relationship from dating to engagement
	 * 
	 * @param player
	 */
	public void levelOneAdvance(Player player)
	{
		Player playerPartner = getPlayerFromUUIDString(config.getString("players." + player.getUniqueId() + ".partner"));
		OfflinePlayer offlinePlayerPartner = getOfflinePlayerFromUUIDString(config.getString("players." + player.getUniqueId() + ".partner"));
		long playerCoupleSince = config.getLong("players." + player.getUniqueId() + ".couplesince");
		long coupleFor = System.currentTimeMillis() - playerCoupleSince;
		
		if (coupleFor > 604800000)
		{
			if (hasEngagementItems(player))
			{
				player.getInventory().removeItem(engagementItems);
				takeMoney(player, 500);

				if (playerPartner != null)
				{
					player.sendMessage(prefix + ChatColor.GREEN + "You are now engaged with " + ChatColor.GOLD + playerPartner.getName() + ChatColor.GREEN + "!");
					playerPartner.sendMessage(prefix + ChatColor.GREEN + "You are now engaged with " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + "!");
					setConfigData(playerPartner, "level", 2);
				}
				else
				{
					player.sendMessage(prefix + ChatColor.GREEN + "You are now engaged with " + ChatColor.GOLD + offlinePlayerPartner.getName() + ChatColor.GREEN + "!");
					setConfigData(offlinePlayerPartner, "level", 2);
				}
				setConfigData(player, "level", 2);
			}
			else
			{
				player.sendMessage(prefix + ChatColor.GREEN + "You do not have all the items you need to advance your relationship!\n" +
						miniPrefix + ChatColor.GREEN + "You need:\n" +
						miniPrefix + ChatColor.GOLD + "1 " + ChatColor.GREEN + "Emerald Block\n" +
						miniPrefix + ChatColor.GOLD + "1 " + ChatColor.GREEN + "Poppy Flower\n" +
						miniPrefix + ChatColor.GREEN + "$" + ChatColor.GOLD + "500");
			}
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Sorry! You need to be in a relationship for a week to be engaged.");
		}
	}
	
	/**
	 * levelTwoAdvance - Called from the Advance function, advances a relationship from engagement to marriage.
	 * @param player
	 */
	public void levelTwoAdvance(Player player)
	{
		Player playerPartner = getPlayerFromUUIDString(config.getString("players." + player.getUniqueId() + ".partner"));
		OfflinePlayer offlinePlayerPartner = getPlayerFromUUIDString(config.getString("players." + player.getUniqueId() + ".partner"));
		long playerCoupleSince = config.getLong("players." + player.getUniqueId() + ".couplesince");
		long coupleFor = System.currentTimeMillis() - playerCoupleSince;
		
		if (coupleFor > 1209600000)
		{
			if (hasMarriageItems(player))
			{
				player.getInventory().removeItem(marriageItems);
				takeMoney(player, 1000);
				
				if (playerPartner != null)
				{
					player.sendMessage(prefix + ChatColor.GREEN + "You are now married with " + ChatColor.GOLD + playerPartner.getName() + ChatColor.GREEN + "!");
					playerPartner.sendMessage(prefix + ChatColor.GREEN + "You are now married with " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + "!");
					setConfigData(playerPartner, "level", 3);
				}
				else
				{
					player.sendMessage(prefix + ChatColor.GREEN + "You are now engaged with " + ChatColor.GOLD + offlinePlayerPartner.getName() + ChatColor.GREEN + "!");
					setConfigData(offlinePlayerPartner, "level", 3);
				}
				setConfigData(player, "level", 3);
			}
			else
			{
				player.sendMessage(prefix + ChatColor.GREEN + "You do not have all the items you need to advance your relationship!\n" +
						miniPrefix + ChatColor.GREEN + "You need:\n" +
						miniPrefix + ChatColor.GOLD + "2 " + ChatColor.GREEN + "Gold Blocks\n" +
						miniPrefix + ChatColor.GOLD + "2 " + ChatColor.GREEN + "Diamonds\n" +
						miniPrefix + ChatColor.GOLD + "1 " + ChatColor.GREEN + "Poppy Flower\n" +
						miniPrefix + ChatColor.GREEN + "$" + ChatColor.GOLD + "1000");
			}
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Sorry! You need to be in a relationship for two weeks to be married.");
		}
	}
	
	/**
	 * devHeartBreak - Called from the Dev function, forces a breakup in a relationship.
	 * Syntax: /marriage dev heartbreak [playername]
	 * 
	 * @param player
	 * @param args
	 */
	public void devHeartbreak(Player player, String[] args)
	{
		if (args.length == 3)
		{
			String playerOneName = args[2];
			String playerOneUUID = null;
			Player playerOne = null;
			OfflinePlayer offlinePlayerOne = null;
			String playerOnePartnerUUID = null;
			Player playerOnePartner = null;
			OfflinePlayer offlinePlayerOnePartner = null;
			
			try
			{
				playerOneUUID = UUIDFetcher.getUUIDOf(playerOneName).toString();
			}
			catch (Exception e)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " does not exist.");
				return;
			}
			playerOne = getPlayerFromUUIDString(playerOneUUID);
			offlinePlayerOne = getOfflinePlayerFromUUIDString(playerOneUUID);
			
			if (config.getString("players." + playerOneUUID + ".partner") != null && !config.getString("players." + playerOneUUID + ".partner").isEmpty())
			{
				playerOnePartnerUUID = config.getString("players." + playerOneUUID + ".partner");
				playerOnePartner = getPlayerFromUUIDString(playerOnePartnerUUID);
				offlinePlayerOnePartner = getOfflinePlayerFromUUIDString(playerOnePartnerUUID);
				
				player.sendMessage(prefix + ChatColor.GREEN + "Breaking up " + ChatColor.GOLD + playerOneName + ChatColor.GREEN + "'s relationship.");
				if (playerOne != null)
				{
					deleteConfigData(playerOne, "partner");
					deleteConfigData(playerOne, "level");
					deleteConfigData(playerOne, "couplesince");
				}
				else
				{
					deleteConfigData(offlinePlayerOne, "partner");
					deleteConfigData(offlinePlayerOne, "level");
					deleteConfigData(offlinePlayerOne, "couplesince");
				}
				if (playerOnePartner != null)
				{
					deleteConfigData(playerOnePartner, "partner");
					deleteConfigData(playerOnePartner, "level");
					deleteConfigData(playerOnePartner, "couplesince");
				}
				else
				{
					deleteConfigData(offlinePlayerOnePartner, "partner");
					deleteConfigData(offlinePlayerOnePartner, "level");
					deleteConfigData(offlinePlayerOnePartner, "couplesince");
				}
			}
			else
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " is not in a relationship.");
			}
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Incorrect syntax. " + ChatColor.GOLD + "/marriage dev heartbreak [playername]");
		}
	}
	
	/**
	 * devForceLove - Called from the Dev function, forces a relationship between two players.
	 * Syntax: /marriage dev forcelove [playername] [playername]
	 * 
	 * @param player
	 * @param args
	 */
	public void devForceLove(Player player, String[] args)
	{
		if (args.length == 4)
		{
			String playerOneName = args[2];
			String playerOneUUID = null;
			Player playerOne = null;
			OfflinePlayer offlinePlayerOne = null;
			String playerTwoName = args[3];
			String playerTwoUUID = null;
			Player playerTwo = null;
			OfflinePlayer offlinePlayerTwo = null;
			Long currentTime = System.currentTimeMillis();
			
			try
			{
				playerOneUUID = UUIDFetcher.getUUIDOf(playerOneName).toString();
			}
			catch (Exception e)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " does not exist.");
				return;
			}
			playerOne = getPlayerFromUUIDString(playerOneUUID);
			offlinePlayerOne = getOfflinePlayerFromUUIDString(playerOneUUID);
			try
			{
				playerTwoUUID = UUIDFetcher.getUUIDOf(playerTwoName).toString();
			}
			catch (Exception e)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerTwoName + ChatColor.RED + " does not exist.");
				return;
			}
			playerTwo = getPlayerFromUUIDString(playerTwoUUID);
			offlinePlayerTwo = getOfflinePlayerFromUUIDString(playerTwoUUID);
			
			if (playerOneName.equals(playerTwoName))
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " cannot have a relationship with themself.");
				return;
			}
			if (config.getString("players." + playerOneUUID + ".partner") != null && !config.getString("players." + playerOneUUID + ".partner").isEmpty())
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " has an existing relationship.");
				return;
			}
			if (config.getString("players." + playerTwoUUID + ".partner") != null && !config.getString("players." + playerTwoUUID + ".partner").isEmpty())
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerTwoName + ChatColor.RED + " has an existing relationship.");
				return;
			}
			if (config.getString("players." + playerOneUUID + ".requesting") != null && !config.getString("players." + playerOneUUID + ".requesting").isEmpty())
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " is being requested by a player currently.");
				return;
			}
			if (config.getString("players." + playerTwoUUID + ".requesting") != null && !config.getString("players." + playerTwoUUID + ".requesting").isEmpty())
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerTwoName + ChatColor.RED + " is being requested by a player currently.");
				return;
			}
			if (config.getString("players." + playerOneUUID + ".requested") != null && !config.getString("players." + playerOneUUID + ".requested").isEmpty())
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " is requesting a player currently.");
				return;
			}
			if (config.getString("players." + playerTwoUUID + ".requested") != null && !config.getString("players." + playerTwoUUID + ".requested").isEmpty())
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerTwoName + ChatColor.RED + " is requesting a player currently.");
				return;
			}
			if (playerOne == null && offlinePlayerOne.getName() == null)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " has never joined the server.");
				return;
			}
			if (playerTwo == null && offlinePlayerTwo.getName() == null)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerTwoName + ChatColor.RED + " has never joined the server.");
				return;
			}
			
			player.sendMessage(prefix + ChatColor.GREEN + "Forcing " + ChatColor.GOLD + playerOneName + ChatColor.GREEN + " and " + ChatColor.GOLD + playerTwoName + ChatColor.GREEN + " to be in a relationship.");
			if(playerOne != null)
			{
				deleteConfigData(playerOne, "cooldown");
				setConfigData(playerOne, "partner", playerTwoUUID);
				setConfigData(playerOne, "level", 1);
				setConfigData(playerOne, "couplesince", currentTime);
			}
			else
			{
				deleteConfigData(offlinePlayerOne, "cooldown");
				setConfigData(offlinePlayerOne, "partner", playerTwoUUID);
				setConfigData(offlinePlayerOne, "level", 1);
				setConfigData(offlinePlayerOne, "couplesince", currentTime);
			}
			if(playerTwo != null)
			{
				deleteConfigData(playerTwo, "cooldown");
				setConfigData(playerTwo, "partner", playerOneUUID);
				setConfigData(playerTwo, "level", 1);
				setConfigData(playerTwo, "couplesince", currentTime);
			}
			else
			{
				deleteConfigData(offlinePlayerTwo, "cooldown");
				setConfigData(offlinePlayerTwo, "partner", playerOneUUID);
				setConfigData(offlinePlayerTwo, "level", 1);
				setConfigData(offlinePlayerTwo, "couplesince", currentTime);
			}
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Incorrect syntax. " + ChatColor.GOLD + "/marriage dev forcelove [playername] [playername]");
		}
	}
	
	/**
	 * devForceAdvance - Called from the Dev function, forces a relationship to advance to the next stage.
	 * Syntax: /marriage dev forceadvance [playername]
	 * 
	 * @param player
	 * @param args
	 */
	public void devForceAdvance(Player player, String[] args)
	{
		// marriage dev forceadvance player
		if (args.length == 3)
		{
			String playerOneName = args[2];
			String playerOneUUID = null;
			Player playerOne = null;
			OfflinePlayer offlinePlayerOne = null;
			String playerOnePartnerUUID = null;
			Player playerOnePartner = null;
			OfflinePlayer offlinePlayerOnePartner = null;
			int coupleLevel = 0;
			
			try
			{
				playerOneUUID = UUIDFetcher.getUUIDOf(playerOneName).toString();
			}
			catch (Exception e)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " does not exist.");
				return;
			}
			playerOne = getPlayerFromUUIDString(playerOneUUID);
			offlinePlayerOne = getOfflinePlayerFromUUIDString(playerOneUUID);
			
			if (playerOne == null && offlinePlayerOne.getName() == null)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " has never joined the server.");
				return;
			}
			
			if (config.getString("players." + playerOneUUID + ".partner") != null && !config.getString("players." + playerOneUUID + ".partner").isEmpty())
			{
				playerOnePartnerUUID = config.getString("players." + playerOneUUID + ".partner");
				playerOnePartner = getPlayerFromUUIDString(playerOnePartnerUUID);
				offlinePlayerOnePartner = getOfflinePlayerFromUUIDString(playerOnePartnerUUID);
				coupleLevel = config.getInt("players." + playerOneUUID + ".level");
				
				switch(coupleLevel)
				{
					case 1:
						player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.GREEN + " is now engaged with their partner.");
						if (playerOne != null)
							setConfigData(playerOne, "level", 2);
						else
							setConfigData(offlinePlayerOne, "level", 2);
						if (playerOnePartner != null)
							setConfigData(playerOnePartner, "level", 2);
						else
							setConfigData(offlinePlayerOnePartner, "level", 2);
						break;
					case 2:
						player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.GREEN + " is now married with their partner.");
						if (playerOne != null)
							setConfigData(playerOne, "level", 3);
						else
							setConfigData(offlinePlayerOne, "level", 3);
						if (playerOnePartner != null)
							setConfigData(playerOnePartner, "level", 3);
						else
							setConfigData(offlinePlayerOnePartner, "level", 3);
						break;
					case 3:	
						player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + "'s relationship cannot be advanced any further.");
						break;
				}
				
				
			}
			else
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " is not in a relationship.");
			}
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Incorrect syntax. " + ChatColor.GOLD + "/marriage dev forceadvance [playername]");
		}
	}
	
	/**
	 * devWarmup - Called from the Dev function, removes all cooldowns from a player.
	 * Syntax: /marriage dev warmup [playername]
	 * 
	 * @param player
	 * @param args
	 */
	public void devWarmup(Player player, String[] args)
	{
		if (args.length == 3)
		{
			String playerOneName = args[2];
			String playerOneUUID = null;
			Player playerOne = null;
			OfflinePlayer offlinePlayerOne = null;
			try
			{
				playerOneUUID = UUIDFetcher.getUUIDOf(playerOneName).toString();
			}
			catch (Exception e)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " does not exist.");
				return;
			}
			playerOne = getPlayerFromUUIDString(playerOneUUID);
			offlinePlayerOne = getOfflinePlayerFromUUIDString(playerOneUUID);
			
			if (playerOne == null && offlinePlayerOne.getName() == null)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " has never joined the server.");
				return;
			}
			
			player.sendMessage(prefix + ChatColor.GREEN + "Removing " + ChatColor.GOLD + playerOneName + ChatColor.GREEN + "'s cooldown timer.");
			if (playerOne != null)
			{
				deleteConfigData(playerOne, "cooldown");
				deleteConfigData(playerOne, "effectcooldown");
			}
			else
			{
				deleteConfigData(offlinePlayerOne, "cooldown");
				deleteConfigData(offlinePlayerOne, "effectcooldown");
			}
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Incorrect syntax. " + ChatColor.GOLD + "/marriage dev warmup [playername]");
		}
	}
	
	/**
	 * devGive - Called from the Dev function, gives a player the items and money needed to date/advance a relationship.
	 * Syntax: /marriage dev give [type] [playername]
	 * 
	 * @param player
	 * @param args
	 */
	public void devGive(Player player, String[] args)
	{
		if (args.length == 4)
		{
			String playerOneName = args[3];
			String playerOneUUID = null;
			Player playerOne = null;
			try
			{
				playerOneUUID = UUIDFetcher.getUUIDOf(playerOneName).toString();
			}
			catch (Exception e)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " does not exist.");
				return;
			}
			playerOne = getPlayerFromUUIDString(playerOneUUID);
			
			if (playerOne != null)
			{
				switch (args[2].toLowerCase())
				{
					case "dating":
						player.sendMessage(prefix + ChatColor.GREEN + "Giving " + ChatColor.GOLD + playerOneName + ChatColor.GREEN + " dating items");
						playerOne.getInventory().addItem(dateItems);
						giveMoney(playerOne, 250);
						break;
					case "engagement":
						player.sendMessage(prefix + ChatColor.GREEN + "Giving " + ChatColor.GOLD + playerOneName + ChatColor.GREEN + " engagement items");
						playerOne.getInventory().addItem(engagementItems);
						giveMoney(playerOne, 500);
						break;
					case "marriage":
						player.sendMessage(prefix + ChatColor.GREEN + "Giving " + ChatColor.GOLD + playerOneName + ChatColor.GREEN + " marriage items");
						playerOne.getInventory().addItem(marriageItems);
						giveMoney(playerOne, 1000);
						break;
					default:
						player.sendMessage(prefix + ChatColor.RED + "Unknown type.\n" +
								miniPrefix + ChatColor.DARK_AQUA + "Types: " + ChatColor.GOLD + "dating" +
								ChatColor.DARK_AQUA + " | " + ChatColor.GOLD + "engagement" +
								ChatColor.DARK_AQUA + " | " + ChatColor.GOLD + "marriage");
						break;
				}
			}
			else
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " is offline.");
			}
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Incorrect syntax. " + ChatColor.GOLD + "/marriage dev give [type] [playername]\n" +
					miniPrefix + ChatColor.DARK_AQUA + "Types: " + ChatColor.GOLD + "dating" +
					ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "engagement" +
					ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "marriage");
		}
	}
	
	/**
	 * devTimeMachine - Called from the Dev function, increases the time of a relationship.
	 * Syntax: /dev marriage timemachine [playername] [amount] [type]
	 * 
	 * @param player
	 * @param args
	 */
	public void devTimeMachine(Player player, String[] args)
	{
		if (args.length == 5)
		{
			String playerOneName = args[2];
			String playerOneUUID = null;
			Player playerOne = null;
			OfflinePlayer offlinePlayerOne = null;
			String playerOnePartnerUUID = null;
			Player playerOnePartner = null;
			OfflinePlayer offlinePlayerOnePartner = null;
			long coupleSince = 0L;
			int amount;
			String type = "";
			try
			{
				amount = Integer.parseInt(args[3]);
			}
			catch(NumberFormatException e)
			{
				amount = 0;
			}
			
			try
			{
				playerOneUUID = UUIDFetcher.getUUIDOf(playerOneName).toString();
			}
			catch (Exception e)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " does not exist.");
				return;
			}
			playerOne = getPlayerFromUUIDString(playerOneUUID);
			offlinePlayerOne = getOfflinePlayerFromUUIDString(playerOneUUID);
			
			if (playerOne == null && offlinePlayerOne.getName() == null)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " has never joined the server.");
				return;
			}
			
			if (config.getString("players." + playerOneUUID + ".partner") != null && !config.getString("players." + playerOneUUID + ".partner").isEmpty())
			{
				playerOnePartnerUUID = config.getString("players." + playerOneUUID + ".partner");
				playerOnePartner = getPlayerFromUUIDString(playerOnePartnerUUID);
				offlinePlayerOnePartner = getOfflinePlayerFromUUIDString(playerOnePartnerUUID);
				coupleSince = config.getLong("players." + playerOneUUID + ".couplesince");
				
				switch(args[4].toLowerCase())
				{
					case "year":
						coupleSince -= (31540000000L * amount);
						type = "year(s)";
						break;
					case "month":
						coupleSince -= (2628000000L * amount);
						type = "month(s)";
						break;
					case "week":
						coupleSince -= (604800000L * amount);
						type = "week(s)";
						break;
					case "day":
						coupleSince -= (86400000L * amount);
						type = "day(s)";
						break;
					case "hour":
						coupleSince -= (3600000L * amount);
						type = "hour(s)";
						break;
					case "minute":
						coupleSince -= (60000L * amount);
						type = "minute(s)";
						break;
					case "second":
						coupleSince -= (1000L * amount);
						type = "second(s)";
						break;
					case "millisecond":
						coupleSince = coupleSince - amount;
						type = "millisecond(s)";
						break;
				}
				
				if (coupleSince == config.getLong("players." + playerOneUUID + ".couplesince"))
				{
					player.sendMessage(prefix + ChatColor.DARK_AQUA + "Types: " + ChatColor.GOLD + "year" +
							ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "month" +
							ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "week" +
							ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "day" +
							ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "hour" +
							ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "minute" +
							ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "second" +
							ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "millisecond");
				}
				else
				{
					player.sendMessage(prefix + ChatColor.GREEN + "Adding " + ChatColor.GOLD + amount + " " + type + ChatColor.GREEN + " to " + ChatColor.GOLD + playerOneName + ChatColor.GREEN + "'s relationship.");
					if (playerOne != null)
						setConfigData(playerOne, "couplesince", coupleSince);
					else
						setConfigData(offlinePlayerOne, "couplesince", coupleSince);
					
					if (playerOnePartner != null)
						setConfigData(playerOnePartner, "couplesince", coupleSince);
					else
						setConfigData(offlinePlayerOnePartner, "couplesince", coupleSince);
				}
			}
			else
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " is not in a relationship.");
			}
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Incorrect syntax. " + ChatColor.GOLD + "/marriage dev timemachine [playername] [amount] [type]\n" +
					miniPrefix + ChatColor.DARK_AQUA + "Types: " + ChatColor.GOLD + "year" +
					ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "month" +
					ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "week" +
					ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "day" +
					ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "hour" +
					ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "minute" +
					ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "second" +
					ChatColor.DARK_AQUA + "|" + ChatColor.GOLD + "millisecond");
		}
	}
	
	/**
	 * devBanList - Called from the Dev function, forces a player into or out of the banlist.
	 * Syntax: /marriage dev banlist [playername]
	 * 
	 * @param player
	 * @param args
	 */
	public void devBanlist(Player player, String[] args)
	{
		// marriage dev banlist player
		if (args.length == 3)
		{
			String playerOneName = args[2];
			String playerOneUUID = null;
			List<String> banList = config.getStringList("banlist");
			try
			{
				playerOneUUID = UUIDFetcher.getUUIDOf(playerOneName).toString();
			}
			catch (Exception e)
			{
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.RED + " does not exist.");
				return;
			}
			
			if (banList.contains(playerOneUUID))
			{
				banList.remove(playerOneUUID);
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.GREEN + " has been removed from the banlist.");
			}
			else
			{
				banList.add(playerOneUUID);
				player.sendMessage(prefix + ChatColor.GOLD + playerOneName + ChatColor.GREEN + " has been added to the banlist.");
			}
			
			config.set("banlist", banList);
			saveConfig();
		}
		else
		{
			player.sendMessage(prefix + ChatColor.RED + "Incorrect syntax. " + ChatColor.GOLD + "/marriage dev banlist [playername]");
		}
	}
	
	/**
	 * Refund - Called from the Date function, returns a player's date items
	 * Syntax: /marriage date
	 * 
	 * @param player
	 * @param args
	 */
	public void refund(Player player, String[] args)
	{
		String playerRequesting = config.getString("players." + player.getUniqueId() + ".requesting");
		Long playerRequestExpire = config.getLong("players." + playerRequesting + ".requestexpire");
		Player requestedPlayer = getPlayerFromUUIDString(playerRequesting);
		
		if (playerRequesting != null && !playerRequesting.isEmpty())
		{
			if (System.currentTimeMillis() > playerRequestExpire)
			{
				player.sendMessage(prefix + ChatColor.GREEN + "Your reclaimed your payment from your request to " + ChatColor.GOLD + requestedPlayer.getName() + ChatColor.GREEN + ".");
				
				player.getInventory().addItem(dateItems);
				giveMoney(player, 250);

				deleteConfigData(player, "requesting");
				deleteConfigData(requestedPlayer, "requested");
				deleteConfigData(requestedPlayer, "requestexpire");
			}
		}
	}
	/**
	 * refundIfOffline - Called from the acceptRequest and declineRequest functions, gives a player their items back if they were offline.
	 * 
	 * @param player
	 */
	public void refundIfOffline(Player player)
	{
		boolean dateRefund = config.getBoolean("players." + player.getUniqueId() + ".daterefund");
		
		if (dateRefund)
		{
			player.sendMessage(prefix + ChatColor.GREEN + "Your date request has either been accepted or declined but it expired. Your items will be refunded.");
			
			player.getInventory().addItem(dateItems);
			giveMoney(player, 250);
			
			deleteConfigData(player, "daterefund");
		}
	}
	
	/**
	 * hasDateItems - Check if the player has the items required to date in their inventory
	 * 
	 * Requires:
	 *   - 1 Iron Block
	 *   - 1 Poppy Flower
	 *   - $250
	 *   
	 * @param player - The player who sent the command
	 * @return - True if player has all items | False if player does not have all items
	 */
	public boolean hasDateItems(Player player)
	{
		ItemStack ironBlock = new ItemStack(Material.IRON_BLOCK);
		ItemStack poppyFlower = new ItemStack(Material.RED_ROSE);
		int moneyAmount = 250;
		
		if (player.getInventory().containsAtLeast(ironBlock, 1) &&
			player.getInventory().containsAtLeast(poppyFlower, 1) &&
			checkMoney(player, moneyAmount))
			return true;
		return false;
	}
	
	/**
	 * hasEngagementItems - Check if the player has the items required to be engaged in their inventory
	 * 
	 * Requires:
	 *   - 1 Emerald Block
	 *   - 1 Poppy Flower
	 *   - $500
	 *   
	 * @param player - The player who sent the command
	 * @return - True if player has all items | False if player does not have all items
	 */
	public boolean hasEngagementItems(Player player)
	{
		ItemStack emeraldBlock = new ItemStack(Material.EMERALD_BLOCK);
		ItemStack poppyFlower = new ItemStack(Material.RED_ROSE);
		int moneyAmount = 500;
		
		if (player.getInventory().containsAtLeast(emeraldBlock, 1) &&
			player.getInventory().containsAtLeast(poppyFlower, 1) &&
			checkMoney(player, moneyAmount))
			return true;
		return false;
	}
	
	/**
	 * hasMarriageItems - Check if the player has the items required to be married in their inventory
	 * 
	 * Requires:
	 *   - 2 Gold Blocks
	 *   - 2 Diamonds
	 *   - 1 Poppy Flower
	 *   - $1000
	 *   
	 * @param player - The player who sent the command
	 * @return - True if player has all items | False if player does not have all items
	 */
	public boolean hasMarriageItems(Player player)
	{
		ItemStack goldBlock = new ItemStack(Material.GOLD_BLOCK);
		ItemStack diamond = new ItemStack(Material.DIAMOND);
		ItemStack poppyFlower = new ItemStack(Material.RED_ROSE);
		int moneyAmount = 1000;
		
		if (player.getInventory().containsAtLeast(goldBlock, 2) &&
			player.getInventory().containsAtLeast(diamond, 2) &&
			player.getInventory().containsAtLeast(poppyFlower, 1) &&
			checkMoney(player, moneyAmount))
			return true;
		return false;
	}
	
	/**
	 * convertMilliseconds - Called from the Date function, converts milliseconds and tells the player when they can make another date request.
	 * 
	 * @param player
	 * @param milliseconds
	 */
	public void convertMilliseconds(Player player, long milliseconds)
	{
		int hours = 0;
		int minutes = 0;
		int seconds = 0;
		if (milliseconds >= 3600000)
		{
			hours = (int) milliseconds / 3600000;
			milliseconds = milliseconds % 3600000;
		}
		if (milliseconds >= 60000)
		{
			minutes = (int) milliseconds / 60000;
			milliseconds = milliseconds % 60000;
		}
		if (milliseconds >= 1000)
		{
			seconds = (int) milliseconds / 1000;
			milliseconds = milliseconds % 1000;
		}
		player.sendMessage(miniPrefix + ChatColor.RED + "You can make a request again in: " + hours + " hours " + minutes + " minutes and " + seconds + " seconds.");
	}
	
	/**
	 * giveMoney - Give a player money.
	 * 
	 * @param player
	 * @param amount
	 */
	public void giveMoney(Player player, double amount)
	{
		if (amount >= 0)
			ExpugnExtras.econ.depositPlayer(player, player.getWorld().getName(), amount);
	}
	
	/**
	 * takeMoney - Take money from a player.
	 * 
	 * @param player
	 * @param amount
	 */
	public void takeMoney(Player player, double amount)
	{
		ExpugnExtras.econ.withdrawPlayer(player, player.getWorld().getName(), amount);
	}
	
	/**
	 * checkMoney - Check if the player has enough money
	 * 
	 * @param player
	 * @param amount
	 * @return
	 */
	public boolean checkMoney(Player player, double amount)
	{
		if (amount > 0)
			return ExpugnExtras.econ.has(player, amount);
		else
			return false;
	}
	
	/**
	 * checkCooldown - Checks the player's cooldowns and deletes them if they're past due.
	 * 
	 * @param player
	 */
	public void checkCooldown(Player player)
	{
		if (System.currentTimeMillis() > config.getLong("players." + player.getUniqueId() + ".cooldown"))
			deleteConfigData(player, "cooldown");
		if (System.currentTimeMillis() > config.getLong("players." + player.getUniqueId() + ".effectcooldown"))
			deleteConfigData(player, "effectcooldown");
	}
	
	/**
	 * checkPlayerOnline - Check if the player is currently online the server.
	 * 
	 * @param playerName
	 * @return
	 */
	public boolean checkPlayerOnline(String playerName)
	{
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
		
		for (Player currentPlayer : onlinePlayers)
		{
			if (currentPlayer.getName().equals(playerName))
				return true;
		}
		return false;
	}
	
	/**
	 * getPlayer - Finds the player from a list of online players and returns their data.
	 * @param playerName
	 * @return
	 */
	public Player getPlayer(String playerName)
	{
		Player[] onlinePlayers = Bukkit.getOnlinePlayers();
		
		for (Player currentPlayer : onlinePlayers)
		{
			if (currentPlayer.getName().equals(playerName))
				return currentPlayer;
		}
		return null;
	}
	
	/**
	 * getPlayerFromUUIDString - Gets player data from a UUID in String format
	 * 
	 * @param playerUUID
	 * @return
	 */
	public Player getPlayerFromUUIDString(String playerUUID)
	{
		return Bukkit.getPlayer(UUID.fromString(playerUUID));
	}
	
	/**
	 * getOfflinePlayerFromUUIDString - Gets offlineplayer data from a UUID in String format
	 * @param playerUUID
	 * @return
	 */
	public OfflinePlayer getOfflinePlayerFromUUIDString(String playerUUID)
	{
		return Bukkit.getOfflinePlayer(UUID.fromString(playerUUID));
	}
	
	/**
	 * setConfigData - Sets config data under a player's name with a provided key and object.
	 * 
	 * @param player
	 * @param key
	 * @param obj
	 */
	public void setConfigData(Player player, String key, Object obj)
	{
		String playerDataPath = "players." + player.getUniqueId() + ".";
		config.set(playerDataPath + key, obj);
		saveConfig();
	}
	
	/**
	 * setConfigData - Sets config data under a player's name with a provided key and object.
	 * 
	 * @param player
	 * @param key
	 * @param obj
	 */
	public void setConfigData(OfflinePlayer player, String key, Object obj)
	{
		String playerDataPath = "players." + player.getUniqueId() + ".";
		config.set(playerDataPath + key, obj);
		saveConfig();
	}
	
	/**
	 * deleteConfigData - Deletes config data under a player's name with a provided key and object.
	 * 
	 * @param player
	 * @param key
	 */
	public void deleteConfigData(Player player, String key)
	{
		String playerDataPath = "players." + player.getUniqueId() + ".";
		config.set(playerDataPath + key, null);
		saveConfig();
	}
	
	/**
	 * deleteConfigData - Deletes config data under a player's name with a provided key and object.
	 * @param player
	 * @param key
	 */
	public void deleteConfigData(OfflinePlayer player, String key)
	{
		String playerDataPath = "players." + player.getUniqueId() + ".";
		config.set(playerDataPath + key, null);
		saveConfig();
	}
	
	/**
	 * Save Config: Saves the configuration file.
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
