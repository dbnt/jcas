//
// ChargeBonus.java	 
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * A ChargeBonus is a Reward that applies only while the slot machine
 * is charged up.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class ChargeBonus extends Reward
{
	private Reward bonus;

	/**
	 * Constructor.
	 * @param bonus	   The Reward that applies when the machine is charged up
	 */
	public ChargeBonus (Reward bonus)
	{
		this.bonus = bonus;
	}

	/**
	 * Execute the effect of this reward.
	 */
	public void reward (SlotCore core)
	{
		if (core.isCharged())
		{
			bonus.reward (core);
		}
	}

	/**
	 * For analysis reports.
	 */
	public String toString ()
	{
		return bonus + "(if charged)";
	}
}
