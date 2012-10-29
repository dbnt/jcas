//
// PayTable.java  
// 

package net.ech.casino.slots;

import java.util.*;

/**
 * PayTable is a functional slot machine payout table.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class PayTable implements java.io.Serializable
{
	private final static int DEFAULT_WIN_LEVEL = 2;

	private Entry[] entries = new Entry [0];
	private boolean firstMatchOnly = false;

	public static class Entry implements java.io.Serializable
	{
		private String notation;
		private Pattern pattern;
		private Reward reward;

		public Entry (String notation, Pattern pattern, Reward reward)
		{
			this.notation = notation;
			this.pattern = pattern;
			this.reward = reward;
		}

		/**
		 * Get the pattern.
		 */
		public Pattern getPattern ()
		{
			return pattern;
		}

		/**
		 * Get the reward.
		 */
		public Reward getReward ()
		{
			return reward;
		}
	}

	/**
	 * Set the firstMatchOnly property, which affects the behavior of this
	 * PayTable.  If firstMatchOnly is true, then this pay table will only
	 * ever reward the player for the first matching pattern in a set of
	 * symbol values.  Otherwise, this pay table will reward the player for
	 * each matching pattern.
	 */
	public void setFirstMatchOnly (boolean firstMatchOnly)
	{
		this.firstMatchOnly = firstMatchOnly;
	}

	/**
	 * Add an entry.
	 * @param notation	The label of the row (for analysis only)
	 * @param pattern	The pattern to match.
	 * @param multiple	The multiple used in calculation of win per bet.
	 */
	public void add (String notation, Pattern pattern, int multiple)
	{
		add (notation, pattern, new MultWin (DEFAULT_WIN_LEVEL, multiple));
	}

	/**
	 * Add an entry.
	 * @param notation	The label of the row (for analysis only)
	 * @param pattern	The pattern to match.
	 * @param amounts	The payout amounts per bet amount.
	 */
	public void add (String notation, Pattern pattern, int[] amounts)
	{
		add (notation, pattern, new WinTable (DEFAULT_WIN_LEVEL, amounts));
	}

	/**
	 * Constructor.
	 * @param notation	The label of the row (for analysis only)
	 * @param pattern	The pattern to match.
	 * @param reward	The reward for matching the pattern.
	 */
	public void add (String notation, Pattern pattern, Reward reward)
	{
		ArrayList arrayList = new ArrayList (Arrays.asList (entries));
		arrayList.add (new Entry (notation, pattern, reward));
		entries = (Entry[]) arrayList.toArray (entries);
	}

	/**
	 * Get the number of entries in this pay table.
	 */
	public int getLength ()
	{
		return entries.length;
	}

	/** 
	 * Reward the player for the given symbols.
	 *
	 * The default behavior of this method is to exercise every pay table
	 * entry with a matching pattern.  But this behavior may be modified
	 * by subclassing.
	 */
	public void rewardMatches (int[] symbols, SlotCore core)
	{
		// Run through the pay table, looking for matches.
		for (int i = 0; i < entries.length; ++i)
		{
			 // Exercise a matching pay table entry by claiming its rewards.
			if (entries[i].getPattern().matches (symbols, core))
			{
				entries[i].getReward().reward (core);
				if (firstMatchOnly)
					break;
			}
		}
	}

	/** 
	 * Return the index of the first matching pay table entry.
	 */
	public int findMatch (int[] symbols, SlotCore core)
	{
		// Run through the pay table, looking for matches.
		for (int i = 0; i < entries.length; ++i)
		{
			if (entries[i].getPattern().matches (symbols, core))
				return i;
		}

		return -1;
	}

	/**
	 * Help the fabricator.
	 */
	public int[] helpFabricate (int targetPayTableIndex)
	{
		return entries[targetPayTableIndex].getPattern().helpFabricate();
	}
}
