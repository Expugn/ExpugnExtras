package io.github.expugn.expugnextras.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * 'PlayerInteract' Listener: Listens for player interaction.
 * 
 * @author Expugn <i>(https://github.com/Expugn)</i>
 * @version 1.0
 */
public class PlayerInteractListener implements Listener 
{
	private final io.github.expugn.expugnextras.functions.Marriage marriage;

	//-----------------------------------------------------------------------
	/**
	 * Constructor for the {@code PlayerInteractListener} class
	 * 
	 * @param plugin  {@link ExpugnExtras}
	 */
	public PlayerInteractListener(ExpugnExtras plugin) 
	{
		marriage = new io.github.expugn.expugnextras.functions.Marriage(plugin);
	}

	//-----------------------------------------------------------------------
	/**
	 * {@code onPlayerInteract}: Gets called whenever somebody right clicks an entity.
	 * 
	 * @param event  PlayerInteractEntityEvent
	 */
	@EventHandler
	public void onPlayerInteract(PlayerInteractEntityEvent event) 
	{
		if (event.getRightClicked() instanceof Player) 
		{
			marriage.onPlayerInteract(event);
		}
	}
}
