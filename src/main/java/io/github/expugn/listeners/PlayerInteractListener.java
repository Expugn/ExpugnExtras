package io.github.expugn.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * 'PlayerInteract' Listener
 * Listens for player interaction.
 * 
 * @author Expugn
 * https://github.com/Expugn
 * 
 * @version 1.0
 */
public class PlayerInteractListener implements Listener
{
	private final io.github.expugn.functions.Marriage marriage;
	
	/**
	 * Constructor for the class
	 * 
	 * @param plugin - ExpugnExtras
	 */
	public PlayerInteractListener(ExpugnExtras plugin)
	{
		marriage = new io.github.expugn.functions.Marriage(plugin);
	}
	/**
	 * onPlayerInteract - Gets called whenever somebody right clicks an entity.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent event)
	{
		marriage.onPlayerInteract(event);
	}
}
