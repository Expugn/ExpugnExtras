package io.github.expugn.expugnextras.gollem;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import io.github.expugn.expugnextras.ExpugnExtras;
import io.github.expugn.particleeffect.ParticleEffect;

public class ItemDrop_Runnable extends BukkitRunnable
{
	//private final ExpugnExtras plugin;
	private int counter;
	private World world;
	private Location loc;
	
	public ItemDrop_Runnable(ExpugnExtras plugin, int counter, Player player) throws IllegalArgumentException
	{
		//this.plugin = plugin;
		this.world = player.getWorld();
		this.loc = player.getLocation();
		if (counter < 1)
		{
			throw new IllegalArgumentException("Counter must be greater than 1.");
		}
		else
		{
			this.counter = counter;
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void run()
	{
		if (counter > 0)
		{
			Random r = new Random();
			Item item = world.dropItem(loc, new ItemStack(Material.getMaterial(264), 1));
			Vector velocity = new Vector(r.nextInt() % 2 == 0 ? r.nextDouble() * 0.5 : -r.nextDouble() * 0.5,
										 1.0,
										 r.nextInt() % 2 == 0 ? r.nextDouble() * 0.5 : -r.nextDouble() * 0.5);
			item.setVelocity(velocity);
			ParticleEffect.CLOUD.display(0.0F, 0.0F, 0.0F, 0.1F, 25, loc, 25.0D);
			ParticleEffect.CRIT.display(0.0F, 0.0F, 0.0F, 1.0F, 25, loc, 50.0D);
			ParticleEffect.CRIT_MAGIC.display(0.0F, 0.0F, 0.0F, 1.0F, 30, loc, 50.0D);
			counter--;
		}
		else
		{
			this.cancel();
		}
	}
}
