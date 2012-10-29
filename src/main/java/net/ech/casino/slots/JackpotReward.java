//
// JackpotReward.java  
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * A JackpotReward is a Reward that carries the big pot with it.
 * Assumes at most one jackpot per slot machine.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class JackpotReward extends Reward
{
	private int winLevel;

	/**
	 * Constructor.
	 * @param winLevel	  The win level associated with winning the jackpot.
	 */
	public JackpotReward (int winLevel)
	{
		this.winLevel = winLevel;
	}

	/**
	 * Execute the effect of hitting this payline on the given game.
	 */
	public void reward (SlotCore core)
	{
		core.addJackpot (winLevel);
	}

	/**
	 * For analysis reports.
	 */
	public String toString ()
	{
		return "jackpot";
	}
}
