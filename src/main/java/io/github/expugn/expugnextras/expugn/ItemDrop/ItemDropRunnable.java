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

import io.github.expugn.expugnextras.ExpugnExtras;
import io.github.expugn.expugnextras.imports.ParticleEffect.ParticleEffect;

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
			
			ParticleEffect.CLOUD.display(0.0F, 0.0F, 0.0F, 0.1F, 25, loc, 40.0D);
			ParticleEffect.SPELL_MOB.display(0.0F, 0.0F, 0.0F, 0.3F, 50, loc, 40.0D);
			world.playSound(loc, Sound.FIREWORK_LAUNCH, 0.5F, 0.5F);
			
			counter--;
		}
		else
			this.cancel();
	}
}
