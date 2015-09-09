package io.github.expugn.expugnextras;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ExpugnCommand implements CommandExecutor
{
	private final ExpugnExtras plugin;
	public ExpugnCommand(ExpugnExtras plugin)
	{
		this.plugin = plugin;
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			Player player = (Player) sender;
			if (args.length == 0)
			{
				player.sendMessage(ChatColor.GOLD + "Welcome to ExpugnExtras. Use /expugn help for a help menu.");
			}
			else if (args[0].equals("help"))
			{
				helpMenu(player);
			}
			else if (args[0].equals("reload"))
			{
				plugin.reloadConfig();
				player.sendMessage("Reloaded Configuration.");
			}
			else if (args[0].equals("warplist"))
			{
				warpList(player);
			}
			else if (args[0].equals("warpinfo"))
			{
				warpInfo(player, args[1]);
			}
			else if (args[0].equals("warp"))
			{
				warp(player, args[1]);
			}
			else if (args[0].equals("setwarp"))
			{
				setWarp(player, args[1]);
			}
			else if (args[0].equals("delwarp"))
			{
				delWarp(player, args[1]);
			}
			else
			{
				player.sendMessage(ChatColor.RED + "Invalid Command. Use /expugn help for a help menu.");
			}
			return true;
		}
		else
		{
			sender.sendMessage("Only players can run this command.");
			return false;
		}
	}
	public void helpMenu(Player player)
	{
		player.sendMessage(ChatColor.GOLD + "ExpugnExtras Help Menu:");
		player.sendMessage(ChatColor.GOLD + "(All Commands begin with /expugn)");
		player.sendMessage(ChatColor.GREEN + "- General:");
		player.sendMessage("  - help - Help Menu");
		player.sendMessage("  - reload - Reload configuration.");
		player.sendMessage(ChatColor.GREEN + "- Warps:");
		player.sendMessage("  - warplist - Lists warps managed by ExpugnExtras.");
		player.sendMessage("  - warpinfo [warpname] - Get details of a warp.");
		player.sendMessage("  - warp [warpname] - Warp to a destination.");
		player.sendMessage("  - setwarp [warpname] - Define a warp location.");
		player.sendMessage("  - delwarp [warpname] - Remove a warp location.");
		player.sendMessage("  - warpsetting [warpname] <cooldown|limit> - Defines a warp to use a cooldown system or a daily limit.");
		player.sendMessage("  - warpsetting [warpname] [number] - Sets the hours for a cooldown or the daily limit.");
	}
	public void warpList(Player player)
	{
		List<String> warpList = plugin.getConfig().getStringList("warps");
		player.sendMessage("There are currently " + warpList.size() + " warps");
		for (String s : warpList)
		{
			player.sendMessage("- " + s);
		}
		player.sendMessage("For more info on a warp: use /expugn warpinfo [warpname].");
	}
	public void warpInfo(Player player, String name)
	{
		if (plugin.getConfig().getString(name + ".type") == null)
		{
			player.sendMessage("Invalid warp. Use /expugn warplist for a list of warps.");
		}
		else
		{
			player.sendMessage("Information for warp " + name + ":");
			player.sendMessage("x: " + plugin.getConfig().getInt(name + ".x"));
			player.sendMessage("y: " + plugin.getConfig().getInt(name + ".y"));
			player.sendMessage("z: " + plugin.getConfig().getInt(name + ".z"));
			player.sendMessage("world: " + plugin.getConfig().getString(name + ".world"));
			player.sendMessage("type: " + plugin.getConfig().getString(name + ".type"));
			player.sendMessage("value: " + plugin.getConfig().getInt(name + ".value") + " hours/daily entry limit");
		}
	}
	public void warp(Player player, String name)
	{
		World warpWorld = Bukkit.getWorld(plugin.getConfig().getString(name + ".world"));
		int warpX = plugin.getConfig().getInt(name + ".x");
		int warpY = plugin.getConfig().getInt(name + ".y");
		int warpZ = plugin.getConfig().getInt(name + ".z");
		Location loc = new Location(warpWorld, warpX, warpY, warpZ);
		player.teleport(loc);
	}
	public void setWarp(Player player, String name)
	{
		if (this.checkWarp(name) == false)
		{
			Location loc = player.getLocation();
			player.sendMessage("Warp does not exist. Creating a new warp.");
			
			// Setup a new path in the configuration.
			plugin.getConfig().addDefault(name + ".x", loc.getX());
			plugin.getConfig().addDefault(name + ".y", loc.getY());
			plugin.getConfig().addDefault(name + ".z", loc.getZ());
			plugin.getConfig().addDefault(name + ".world", player.getWorld().getName());
			plugin.getConfig().addDefault(name + ".type", "cooldown");
			plugin.getConfig().addDefault(name + ".value", 0);
			
			// Add warp name to the list of warps
			List<String> warpList = plugin.getConfig().getStringList("warps");
			warpList.add(name);
			plugin.getConfig().set("warps", warpList);
			
			// Save and inform player
			this.saveConfig();
			player.sendMessage("- Use /expugn warpsetting " + name + " <cooldown|limit> to modify the type of warp.");
			player.sendMessage("- Use /expugn warpsetting " + name + " [number] to modify the cooldown/daily limit of the warp.");
		}
		else
		{
			Location loc = player.getLocation();
			player.sendMessage("There is an existing warp. Defining new position.");
		
			plugin.getConfig().addDefault(name + ".x", loc.getX());
			plugin.getConfig().addDefault(name + ".y", loc.getY());
			plugin.getConfig().addDefault(name + ".z", loc.getZ());
			plugin.getConfig().addDefault(name + ".world", player.getWorld());
			
			this.saveConfig();
		}
	}
	public void delWarp(Player player, String name)
	{
		if (this.checkWarp(name) == false)
		{
			player.sendMessage("This warp does not exist. Use /expugn warplist for a list of warps.");
		}
		else
		{
			plugin.getConfig().set(name, null);
			
			List<String> warpList = plugin.getConfig().getStringList("warps");
			warpList.remove(name);
			plugin.getConfig().set("warps", warpList);
			
			this.saveConfig();
			
			player.sendMessage("Warp " + name + " has been removed.");
		}
	}
	public void warpSetting(Player player, String name, String param)
	{
		// TODO
	}
	public boolean checkWarp(String name)
	{
		List<String> warpList = plugin.getConfig().getStringList("warps");
		if (warpList.contains(name))
			return true;
		return false;
	}
	public void saveConfig()
	{
		plugin.getConfig().options().copyDefaults(true);
		plugin.saveConfig();
	}
}
