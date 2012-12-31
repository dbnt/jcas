//
// Win.java	 
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * A Win is a slot machine Reward that pays the player.	 Win is an abstract
 * superclass, having only a win level.	 The Win subclass determines the 
 * amount won.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public abstract class Win extends Reward
{
	// The win level.  Determines level of user feedback:
	private int winLevel;

	/**
	 * Constructor.
	 * @param winLevel	The level of user feedback
	 */
	public Win (int winLevel)
	{
		this.winLevel = winLevel;
	}

	/**
	 * Get the level of user feedback.	Should be a positive number.
	 */
	public int getWinLevel ()
	{
		return winLevel;
	}
}
