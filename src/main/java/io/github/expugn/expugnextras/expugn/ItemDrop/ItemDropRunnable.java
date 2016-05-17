package io.github.expugn.expugnextras.expugn.ItemDrop;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.inventivetalent.particle.ParticleEffect;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'ItemDrop' Runnable</b>
 * 
 * @version 1.1
 * @author Expugn <i>(https://github.com/Expugn)</i>
 */
public class ItemDropRunnable extends BukkitRunnable
{
	private int counter;
	private World world;
	private Location loc;
	private List<ItemStack> itemSet;
	//private List<Item> spawnedItems;
	
	/**
	 * Constructor for the ItemDropRunnable class.
	 * 
	 * @param plugin  Main Class: {@link ExpugnExtras}
	 * @param counter  The amount of times items should be dropped.
	 * @param world  The world in which the ItemDrop takes place.
	 * @param loc  The location of the ItemDrop.
	 * @param itemSet  The ItemSet holding the items the ItemDrop drops.
	 * @throws IllegalArgumentException  if counter is lower than 1.
	 */
	public ItemDropRunnable(ExpugnExtras plugin, int counter, World world, Location loc, List<ItemStack> itemSet) throws IllegalArgumentException
	{
		this.world = world;
		this.loc = loc;
		this.itemSet = itemSet;
		
		if (counter < 1)
			throw new IllegalArgumentException("Counter must be greater than 1.");
		else
			this.counter = counter;
	}
	
	/**
	 * Drop-scatters items from an ItemSet.
	 */
	@Override
	public void run()
	{
		if (counter > 0)
		{	
			Random r = new Random();
			int itemNum = r.nextInt(itemSet.size());
			Item item = world.dropItem(loc, itemSet.get(itemNum));
			Vector velocity = new Vector(r.nextInt() % 2 == 0 
											? r.nextDouble() * 0.5 : -r.nextDouble() * 0.5
										, 1.0
										, r.nextInt() % 2 == 0 
											? r.nextDouble() * 0.5 : -r.nextDouble() * 0.5);
			item.setVelocity(velocity);
			item.setPickupDelay(50);
			
			ParticleEffect.CLOUD.send(world.getPlayers(), loc, 0, 0, 0, 0.05, 25);
			ParticleEffect.SPELL_MOB.send(world.getPlayers(), loc, 0, 0, 0, 0.05, 50);
			world.playSound(loc, Sound.ENTITY_FIREWORK_LAUNCH, 0.5F, 0.5F);
			
			//spawnedItems.add(item);
			//for(Item itemData : spawnedItems)
			//{
			//	Location itemLoc = itemData.getLocation();
			//	ParticleEffect.CRIT.send(world.getPlayers(), itemLoc, 1, 1, 1, 0F, 6);
			//}

			counter--;
		}
		else
			this.cancel();
	}
}
