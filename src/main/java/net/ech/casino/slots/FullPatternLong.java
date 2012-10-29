//
// FullPatternLong.java	 
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * A FullPatternLong is a Pattern composed of one bitmask for each reel.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class FullPatternLong extends Pattern
{
	private long[] masks;

	/**
	 * Constructor.
	 * @param masks		The masks per reel.
	 */
	public FullPatternLong (long[] masks)
	{
		this.masks = (long[]) masks.clone ();
	}

	/**
	 * @return true if the given symbols match this payline.
	 */
	public boolean matches (int[] symbols, SlotCore game)
	{
		try
		{
			for (int i = 0; i < symbols.length; ++i)
			{
				if ((masks[i] & (1L << symbols[i])) == 0)
					return false;
			}

			return true;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
	}

	/**
	 * For testing.
	 */
	public int[] helpFabricate ()
	{
		// Return a non-null array of symbols only in the case that this
		// pattern matches a unique array.
		//
		for (int i = 0; i < masks.length; ++i)
		{
			long mask = masks[i];
			if (bitCount (mask) != 1)
				return null;
		}

		int[] symbols = new int [masks.length];
		for (int i = 0; i < symbols.length; ++i)
		{
			for (int bit = 0; bit < 64; ++bit)
			{
				if ((masks[i] & (1 << bit)) != 0)
				{
					symbols[i] = bit;
					break;
				}
			}
		}

		return symbols;
	}

	public String toString ()
	{
		StringBuffer buf = new StringBuffer ();

		for (int i = 0; i < masks.length; ++i)
		{
			if (i > 0)
				buf.append (" ");
			buf.append ("0x");
			buf.append (Long.toString (masks[i], 16));
		}

		return buf.toString ();
	}
}
