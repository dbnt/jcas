//
// StraightPattern.java	 
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * This class represents a pattern of a type similar to a straight in Poker.   
 * 
 * @version 1.1
 * @author Dave Giese, dgiese@ech.net
 */
public class StraightPattern extends Pattern
{
	private long mask;
	private int bitoffset;
	private long  highMask;
	private int	 symbolCount;
	private int numOccurences;
	private boolean aceLow;
	/**
	 * Constructor.	 This constructor is useful for pattern matching where the order
	 * that symbols appear on the reels is not important and where we are looking for 
	 * a range of exclusive symbols (ie. a straight in poker).	The constraint put on the
	 * symbols is that symbols must be ordered consecutively, or with a fixed offset between
	 * consectutive symbols.
	 * @param mask	Starting point of range
	 * @param bitoffset	   Array of bit offsets to be used in finding the range. i.e. 0,13
	 * @param numOccurences Number of reels that must match the range.
	 * @param highMask Ending point of the range of symbols to match.
	 */
	public StraightPattern (long mask, int bitoffset, int numOccurences, long highMask, int symbolCount)
	{
		this (mask, bitoffset, numOccurences, highMask, symbolCount, false);
	}

	public StraightPattern (long mask, int bitoffset, int numOccurences, long highMask, int symbolCount, boolean aceLow)
	{
		this.mask = mask;
		this.bitoffset = bitoffset;
		this.numOccurences = numOccurences;
		this.highMask = highMask;
		this.symbolCount = symbolCount;
		this.aceLow = aceLow;
	}
	
	/**
	 * @return true if the given symbols match this payline.
	 */
	public boolean matches (int[] symbols, SlotCore game)
	{
		// First convert the symbols to masks, taking into account any bitoffset supplied
		int i;
		long [] symbolMasks = new long[symbols.length];
		for (i=0;i<symbols.length;i++)
		{
			if (bitoffset > 0)
				symbolMasks[i] = 1L<<(symbols[i] % bitoffset);
			else		
				symbolMasks[i] = 1L<<symbols[i];
		}	 
			
		// Sort the symbols 
		Vector vec = new Vector (symbolMasks.length);
		vec.addElement (new Long(symbolMasks[0]));
		for (i=1;i<symbolMasks.length;i++)
		{
			for (int j=0;j<vec.size();j++)
			{
				long currentElement = ((Long) vec.elementAt(j)).longValue();
				if (symbolMasks[i] <= currentElement)
				{	 // found the place in the vector, insert
					vec.insertElementAt(new Long(symbolMasks[i]), j);
					break;	
				}
				else if (j==vec.size()-1)
				{	// last time thru the loop add it at the end 
					vec.addElement (new Long (symbolMasks[i]));
					break;
				}	 
			}
		}				 

		// Now, check to see if the potential straight can be kicked out because it
		// starts lower than the lowest possible (given in the constructor)
		// or ends higher than the highest possible.
		if ( ((Long) vec.firstElement()).longValue() < mask ||
			  ((Long) vec.lastElement()).longValue() > highMask )
			return (false);
		
		//	OK, check to see if the symbols are in order
		int start = 0; 
		int end = vec.size()-1;
		if (aceLow)
		{
			// check last value against first
			long firstVal = ((Long) vec.firstElement ()).longValue();
			long lastVal = ((Long) vec.lastElement ()).longValue();
			if (firstVal != (lastVal >> (bitoffset-1)))
				return (false);
			end--;		// do not check last value in loop
		}	 

		for (i=0; i<end; i++)
		{
			long currVal = ((Long) vec.elementAt(i)).longValue();
			long nextVal = ((Long) vec.elementAt(i+1)).longValue();
			if (currVal != (nextVal >> 1))
				return (false);
		}			 
		// Made it this far, must be a straight
		return (true);
	   
	}
}
