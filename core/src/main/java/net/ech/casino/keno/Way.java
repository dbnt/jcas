/**
 * Way.java
 */

package net.ech.casino.keno;

import java.util.*;

/**
 * "Way" is the keno term for a group of marked numbers -- a
 * "way" to win.
 */
public class Way
{
	private String id;
	private int size;
	private BitSet marked;
	private int bet;
	private boolean assignedBet;
	private int ncaught;
	private int win;

	/**
	 * Constructor.
	 */
	public Way (String id, int size, int bet, boolean assignedBet)
	{
		this.id = id;
		this.size = size;
		this.marked = new BitSet (size);
		this.bet = bet;
		this.assignedBet = assignedBet;
	}

	/**
	 * Get the id.
	 */
	public String getId ()
	{
		return id;
	}

	/**
	 * Mark a number.
	 * @exception IndexOutOfBoundsException if bad number
	 */
	public void mark (int number)
	{
		if (number > size)
			number = -1;		// BitSet.set() expands to fit!!!
		marked.set (number - 1);
	}

	/**
	 * Count the number marked.
	 */
	public int getNumberMarked ()
	{
		return countBits (marked);
	}

	/**
	 * Return an array of the numbers marked.
	 */
	public int[] getMarked ()
	{
		int[] markedArray = new int [getNumberMarked ()];
		int count = 0;

		for (int i = 0; i < size; ++i)
		{
			if (marked.get (i))
			{
				markedArray[count++] = i + 1;
			}
		}

		return markedArray;
	}

	/**
	 * Return a formatted string containing the numbers marked.
	 */
	public String getMarkedString ()
	{
		StringBuilder buf = new StringBuilder ();

		for (int i = 0; i < size; ++i)
		{
			if (marked.get (i))
			{
				if (buf.length () > 0)
					buf.append (' ');
				buf.append (i + 1);
			}
		}

		return buf.toString ();
	}

	/**
	 * Get the bet.
	 */
	public int getBet ()
	{
		return bet;
	}

	/**
	 * Tell whether this way had a bet assigned or defaulted.
	 */
	public boolean isAssignedBet ()
	{
		return assignedBet;
	}

	/**
	 * Tally the catches.
	 * @return the number of catches.
	 */
	public int catchNumbers (BitSet numberSet)
	{
		BitSet caught = (BitSet) numberSet.clone ();
		caught.and (marked);
		return ncaught = countBits (caught);
	}

	private int countBits (BitSet bset)
	{
		int count = 0;
		
		for (int b = 0; b < size; ++b)
		{
			if (bset.get (b))
				++count;
		}

		return count;
	}

	/**
	 * Return the number caught.
	 */
	public int getNumberCaught ()
	{
		return ncaught;
	}

	/**
	 * Set the win amount.
	 */
	void setWin (int win)
	{
		this.win = win;
	}

	/**
	 * Get the win amount.
	 */
	public int getWin ()
	{
		return win;
	}
}
