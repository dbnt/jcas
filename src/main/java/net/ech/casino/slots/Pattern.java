//
// Pattern.java	 
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * A Pattern is that which a slot machine compares a reel configuration
 * and game state against to determine payout.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public abstract class Pattern implements java.io.Serializable
{
	public final static int ANY = ~0;	// extends to ~0L

	/**
	 * Factory method.
	 * @param mask		The mask for all reels.
	 */
	public static Pattern create (long mask)
	{
		return new UniformPatternLong (mask);
	}
	
	/**
	 * Factory method.
	 * @param mask		The mask for all reels.
	 */
	public static Pattern create (int mask)
	{
		return new UniformPattern (mask);
	}

	/**
	 * Factory method.
	 * @param mask		The mask for all reels.
	 * @param number	The number of reels that must match.
	 */
	public static Pattern create (long mask, int number)
	{
		return new UniformPatternLong (mask, number);
	}

	/**
	 * Factory method.
	 * @param mask		The mask for all reels.
	 * @param number	The number of reels that must match.
	 */
	public static Pattern create (int mask, int number)
	{
		return new UniformPattern (mask, number);
	}

	/**
	 * Factory method.
	 * @param masks		The masks per reel.
	 */
	public static Pattern create (long[] masks)
	{
		return new FullPatternLong (masks);
	}

	/**
	 * Factory method.
	 * @param masks		The masks per reel.
	 */
	public static Pattern create (int[] masks)
	{
		return new FullPattern (masks);
	}

	/**
	 * @return true if the given symbols match this payline.
	 */
	abstract public boolean matches (int[] symbols, SlotCore game);

	/**
	 * For testing.	 Create an array of symbols that matches this payline,
	 * ONLY IF there is exactly one such array.	 Otherwise, return null.
	 */
	public int[] helpFabricate ()
	{
		return null;
	}

	//
	// Utility method.
	//
	protected static int bitCount (int n)
	{
		int count = 0;
		for (int i = 0; i < 32; ++i)
		{
			if ((n & (1 << i)) != 0)
				++count;
		}
		return count;
	}

	//
	// Utility method.
	//
	protected static int bitCount (long n)
	{
		int count = 0;
		for (int i = 0; i < 64; ++i)
		{
			if ((n & (1L << i)) != 0)
				++count;
		}
		return count;
	}
	
	//
	// Utility method.
	// converts mask to the first possible matching symbol
	//
	protected int maskToSymbol (long mask)
	{	 
		for (int i = 0; i < 64; ++i)
		{
			if ((mask & (1L << i)) != 0)
				return (i);
		}	 
		// should not get here
		return 0;
	}	 
}
