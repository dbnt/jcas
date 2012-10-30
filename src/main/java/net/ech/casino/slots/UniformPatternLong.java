//
// UniformPatternLong.java	
// 

package net.ech.casino.slots;

/**
 * A UniformPatternLong is a Pattern that matches "ANY 3 X", "ANY 1 X", etc.,
 * with allowance for more than 32 different symbols.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class UniformPatternLong extends Pattern
{
	private long mask;
	private int number;
	
	/**
	 * Constructor.
	 * @param mask		The mask for all reels.
	 */
	public UniformPatternLong (long mask)
	{
		this.mask = mask;
	}

	/**
	 * Constructor.
	 * @param mask		The mask for all reels.
	 * @param number	The number of reels that must match.
	 */
	public UniformPatternLong (long mask, int number)
	{
		this.mask = mask;
		this.number = number;
	}

	/**
	 * @return true if the given symbols match this payline.
	 */
	public boolean matches (int[] symbols, SlotCore game)
	{
		try
		{
			int maxMisses = number == 0 ? 0 : (symbols.length - number);
			int missCount = 0;

			for (int i = 0; i < symbols.length; ++i)
			{
				int symbol = symbols[i];
				if ((mask & (1L << symbol)) == 0)
				{
					if (++missCount > maxMisses)
						return false;
				}
			}

			return true;
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return false;
		}
	}

	public String toString ()
	{
		StringBuilder buf = new StringBuilder ();
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
		buf.append (Long.toString (mask, 16));

		return buf.toString ();
	}
}
