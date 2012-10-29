//
// ChargeUpReward.java	
// 

package net.ech.casino.slots;

/**
 * A ChargeUpReward is a slot machine Reward that "charges up" the game.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class ChargeUpReward extends Reward
{
	/**
	 * Execute the effect of hitting this payline on the given game.
	 * Implementation of abstract function inherited from Reward.
	 */
	public void reward (SlotCore core)
	{
		core.addCharge ();
	}

	/**
	 * For analysis report.
	 */
	public String toString ()
	{
		return "chargeup";
	}
}
