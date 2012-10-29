//
// UniformPattern.java	
// 

package net.ech.casino.slots;

/**
 * A UniformPattern is a Pattern that matches "ANY 3 X", "ANY 1 X", etc.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class UniformPattern extends Pattern
{
	private int mask;
	private int number;
	
	/**
	 * Constructor.
	 * @param mask		The mask for all reels.
	 */
	public UniformPattern (int mask)
	{
		this.mask = mask;
	}

	/**
	 * Constructor.
	 * @param mask		The mask for all reels.
	 * @param number	The number of reels that must match.
	 */
	public UniformPattern (int mask, int number)
	{
		this.mask = mask;
		this.number = number;
	}

	/**
	 * @return true if the given symbols match this payline.
	 */
	public boolean matches (int[] symbols, SlotCore game)
	{
		int targetHitCount = number == 0 ? symbols.length : number;
		int hitCount = 0;

		for (int i = 0; i < symbols.length; ++i)
		{
			int symbol = symbols[i];
			if ((mask & (1 << symbol)) != 0)
			{
				++hitCount;
			}
		}

		return hitCount == targetHitCount;
	}

	public String toString ()
	{
		StringBuffer buf = new StringBuffer ();
		if (number > 0)
		{
			buf.append ("any ");
			buf.append (number);
			buf.append (" ");
		}
		else
		{
			buf.append ("all ");
		}
		buf.append ("0x");
		buf.append (Integer.toString (mask, 16));

		return buf.toString ();
	}
}
