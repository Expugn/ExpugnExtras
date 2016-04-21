package io.github.expugn.expugnextras.gollem;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import io.github.expugn.expugnextras.ExpugnExtras;

/**
 * <b>'Gollem'</b>
 * 
 * @version 1.0
 * @author Expugn  <i>(https://github.com/Expugn)</i>
 */
public class Gollem
{
	/* Gollem Stats ----------------------------------
	 * > Gollem Health: 100
	 * > Player Group Lives: 3
	 * 
	 * Gollem Trials ---------------------------------
	 * 1) Arrow Barrage
	 *      > Fire a barrage of fire arrows onto the field of players
	 *      * "Fufufu, let's see if you can dodge this!"
	 * 2) 'Don't Kill the Pig'
	 *      > Spawns one pig on the field.
	 *      * "Do you trust me? Don't kill the pig if you want to live."
	 *      > After about 30 seconds: 
	 *          - if the pig is killed: Continue
	 *          * "Clever..."
	 *          - if the pig is not killed: Smite pig and spawn swarm of zombie pigmen
	 *          * "Foolish players! Now you will experience pain!"
	 * 3) Crush (1 Person)
	 *      > Teleport one Player to enclosed blocks. Player will take suffocating damage for 5 seconds
	 *      * "Gollem snatched up <name> and is crushing them with his hand!"
	 *      > After 5 seconds:
	 *      * "<name> was thrown back onto the field."
	 * 4) Swallow (1 Person)
	 *      > Teleport one Player to a pool of lava for 5 seconds
	 *      * "Gollem swallowed <name>!"
	 *      > After about 5 seconds
	 *      * "<name> was spit out back on the field."
	 * 5) Earthquake
	 * 		> Force an upwards velocity on all players so that they take fall damage.
	 *      * "Gollem smashed his fists on the field, knocking up all players."
	 * 6) Mob Swarm
	 *      > Spawn a swarm of zombies (10?) w/ stone block heads onto the field
	 *      * "Arise my minions! Take my life force and attack!"
	 *      > When one zombie dies, Gollem takes 1 damage
	 *      > When all zombies die
	 *      * "Argh... You players are tough."
	 * Gollem Psuedo-Code -------------------------------------
	 * > Start Battle
	 * > VT_Timer enabled
	 * > Minute passes: Get trial 1-5 at random and run it's runnable class (Attack();)
	 * > When 3 trials 1-5 have been called, Run Mob Swarm Trial next
	 * > trial_runnable_class stops timer from calling another attack until it finishes.
	 * > trial happens, when it ends re-enable timer
	 * > Check() is called, check if Gollem is dead or players are all dead
	 * > if gollem and players still alive: Call Attack() and continue battle
	 * > if gollem is dead: Teleport players to a room with "dead" gollem and spawn reward. End Battle.
	 * > if players are dead: End Battle
	 */
	
	/* Private variables */
	private File ymlFile;
	private FileConfiguration config;
	private final ExpugnExtras plugin;
	
	public Gollem(ExpugnExtras plugin)
	{
		ymlFile = new File(plugin.getDataFolder() + "/gollem.yml");
		config = YamlConfiguration.loadConfiguration(ymlFile);
		this.plugin = plugin;
	}
	
	public void debug()
	{
		
	}
	
	public void attack()
	{
		
	}
	
	public void check()
	{
		
	}
	
	public int getHealth()
	{
		return config.getInt("data.health");
	}
	
	public void setHealth(int value) throws IllegalArgumentException
	{
		if (value >= 0)
		{
			config.set("data.health", value);
			saveConfig();
		}
		else
			throw new IllegalArgumentException("Health value must be greater than or equal to 0");
	}
	
	public void subtractHealth(int value) throws IllegalArgumentException
	{
		if (value > 0)
		{
			int health = getHealth();
			health = (health - value) > 0 ? (health - value) : 0; 
			setHealth(health);
		}
		else
			throw new IllegalArgumentException("Health value must be greater than 0");
	}
	
	public void setPlayerLives()
	{
		
	}
	
	public void subtractPlayerLives()
	{
		
	}
	
	public void getPlayerLives()
	{
		
	}
	
	public void addParty()
	{
		
	}
	
	public void removeParty()
	{
		
	}
	
	public void clearParty()
	{
		
	}
	
	public void getParty()
	{
		
	}
	
	public void reset()
	{
		
	}
	
	public void run_ArrowBarrage()
	{
		
	}
	
	public void run_PigTrial()
	{
		
	}
	
	public void run_Crush()
	{
		
	}
	
	public void run_Swallow()
	{
		
	}
	
	public void run_Earthquake()
	{
		
	}
	
	public void run_MobSwarm()
	{
		
	}
	
	public void run_ItemDrop(Player player)
	{
		@SuppressWarnings("unused")
		BukkitTask task = new ItemDrop_Runnable(plugin, 32, player).runTaskTimer(plugin, 5, 10);
	}
	
	//-----------------------------------------------------------------------
	/**
	 * {@code saveConfig}: Saves gollem.yml.
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
