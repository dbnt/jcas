//
// MaxBetBonus.java	 
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * A MaxBetBonus is a Reward that applies only when the user bets the
 * maximum amount.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class MaxBetBonus extends Reward
{
	private Reward bonus;

	/**
	 * Constructor.
	 * @param bonus	   The Reward that applies when the user bets the max
	 */
	public MaxBetBonus (Reward bonus)
	{
		this.bonus = bonus;
	}

	/**
	 * Execute the effect of this reward.
	 */
	public void reward (SlotCore core)
	{
		if (core.getBet() == core.getMachine().getMaximumBet())
		{
			bonus.reward (core);
		}
	}

	/**
	 * For analysis reports.
	 */
	public String toString ()
	{
		return bonus + "(max bet only)";
	}
}
