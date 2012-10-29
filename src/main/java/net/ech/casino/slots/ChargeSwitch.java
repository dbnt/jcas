//
// ChargeSwitch.java  
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * A ChargeSwitch is a Reward that selects between two sub-rewards based on
 * whether the slots game is charged up.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class ChargeSwitch extends Reward
{
	private Reward chargedReward;
	private Reward notChargedReward;

	/**
	 * Constructor.
	 * @param chargedReward	   The Reward that applies when the machine
	 *						   is charged up
	 * @param notChargedReward	  The Reward that applies when
	 *							  the machine is not charged up
	 */
	public ChargeSwitch (Reward chargedReward, Reward notChargedReward)
	{
		this.chargedReward = chargedReward;
		this.notChargedReward = notChargedReward;
	}

	/**
	 * Execute the effect of this reward.
	 */
	public void reward (SlotCore core)
	{
		(core.isCharged() ? chargedReward : notChargedReward).reward (core);
	}

	/**
	 * For analysis reports.
	 */
	public String toString ()
	{
		return notChargedReward + "(" + chargedReward + " if charged)";
	}
}
