//
// WinTable.java  
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * A WinTable is a slot machine Reward that pays the player an amount
 * determined by some function of the bet.	It's not really a table, it
 * just uses one to look up the win.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class WinTable extends Win
{
	// The lookup table:
	private int[] table;

	/**
	 * Constructor.
	 * @param winLevel	The level of user feedback
	 * @param table		The lookup table.  Index zero corresponds to a bet of 1
	 */
	public WinTable (int winLevel, int[] table)
	{
		super (winLevel);
		this.table = table;
	}

	/**
	 * Get the win table.
	 */
	public int[] getTable ()
	{
		return table;
	}

	/**
	 * Execute the effect of hitting this payline on the given game.
	 * Implementation of abstract function inherited from Reward.
	 */
	public void reward (SlotCore rewards)
	{
		int bet = rewards.getBet();
		int win = table[bet - 1];
		rewards.addWin (getWinLevel(), win);
	}

	/**
	 * For analysis report.
	 */
	public String toString ()
	{
		StringBuffer buf = new StringBuffer ();

		for (int i = 0; i < table.length; ++i)
		{
			if (i > 0)
				buf.append (',');
			buf.append (table[i]);
		}

		return buf.toString();
	}
}
