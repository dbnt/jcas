//
// FlatWin.java	 
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * A FlatWin is a slot machine Reward that pays the player one fixed amount,
 * regardless of the bet.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class FlatWin extends Win
{
	// The number of credits to win:
	private int amount;

	/**
	 * Constructor.
	 * @param amount	Number of credits to win
	 * @param winLevel	The level of user feedback
	 */
	public FlatWin (int winLevel, int amount)
	{
		super (winLevel);
		this.amount = amount;
	}

	/**
	 * Get the number of credits this Winner pays out.
	 */
	public int getAmount ()
	{
		return amount;
	}

	/**
	 * Execute the effect of hitting this payline on the given game.
	 * Implementation of abstract function inherited from Reward.
	 */
	public void reward (SlotCore core)
	{
		core.addWin (getWinLevel(), amount);
	}

	/**
	 * For analysis report.
	 */
	public String toString ()
	{
		return String.valueOf (amount);
	}
}
