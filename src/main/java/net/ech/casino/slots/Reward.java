//
// Reward.java	
// 

package net.ech.casino.slots;

/**
 * A Reward is the result of hitting a pay line Pattern in a slots game.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public abstract class Reward
{
	/**
	 * Execute the effect of hitting this payline on the given game.
	 */
	public abstract void reward (SlotCore core);
}
