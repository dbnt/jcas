//
// MultWin.java	 
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * A MultWin is a slot machine Reward that pays the player a multiple
 * of the bet.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class MultWin extends Win
{
	private int multiple;

	/**
	 * Constructor.
	 * @param multiple	  Multiply the bet by this number to get the win
	 */
	public MultWin (int multiple)
	{
		super (multiple);
		this.multiple = multiple;
	}

	/**
	 * Constructor.
	 * @param winLevel	The level of user feedback
	 * @param multiple	  Multiply the bet by this number to get the win
	 */
	public MultWin (int winLevel, int multiple)
	{
		super (winLevel);
		this.multiple = multiple;
	}

	/**
	 * Get the multiple.
	 */
	public int getMultiple ()
	{
		return multiple;
	}

	/**
	 * Execute the effect of hitting this payline on the given game.
	 * Implementation of abstract function inherited from Reward.
	 */
	public void reward (SlotCore core)
	{
		core.addWin (getWinLevel(), multiple * core.getBet());
	}

	/**
	 * For analysis report.
	 */
	public String toString ()
	{
		return String.valueOf ("x" + multiple);
	}
}
