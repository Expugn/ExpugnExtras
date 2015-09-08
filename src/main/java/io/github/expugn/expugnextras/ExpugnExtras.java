package io.github.expugn.expugnextras;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ExpugnExtras 
{
	public final class expugnextras extends JavaPlugin
	{
		@Override
		public void onEnable()
		{
			// Register Events
			// Register Commands
			getCommand("expugn").setExecutor(new ExpugnCommand());
			// Other Code
			PluginDescriptionFile pdfFile = this.getDescription();
			getLogger().info(pdfFile.getName() + " is now loaded. Prepare yourselves!");
		}
		
		@Override
		public void onDisable()
		{
			PluginDescriptionFile pdfFile = this.getDescription();
			getLogger().info(pdfFile.getName() + " is now disabled.");
			getLogger().info("Some features in-game may not work correctly!");
		}
	}
}
