package io.github.expugn.expugnextras;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DisabledCommand implements CommandExecutor
{
	public DisabledCommand()
	{
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) 
	{
		sender.sendMessage("§cThis command is disabled.");
		return true;
	}
}
