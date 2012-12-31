//
// SlotMachine.java
//

package net.ech.casino.slots;

import net.ech.casino.*;

/**
 * Abstract base class representing a general slot machine.
 * Currently supports:
 *	  - arbitrary number of reels
 *	  - arbitrary number of stops per reel
 *	  - independent reel patterns
 *	  - up to 64 different symbols
 *	  - multiple payout lines
 *	  - pay per line
 *	  - progressive payout
 *	  - progressive win
 *
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
abstract public class SlotMachine extends CreditsMachine
{
	private PayTable payTable;

	// Convenience constant:
	protected final static int ANY = Pattern.ANY;

	//
	// Here is where the mapping of pay line number to pay
	// line position/configuration is defined.	First dimension is 
	// pay line number.	 Second dimension is the reel number.
	// Result is the offset in number of symbols from the
	// center (focus) line.
	//
	// If future machines can't use these mappings, this must be a
	// variable rather than a constant.
	//
	private final static int[][] PayLineConfig =
	{
		{ 0, 0, 0, 0, 0 },
		{ -1, -1, -1, -1, -1 },
		{ 1, 1, 1, 1, 1 },
		{ -1, 0, 1 },
		{ 1, 0, -1 },
	};

	//======================================================================
	// CONSTRUCTORS
	//======================================================================

	/**
	 * Constructor.
	 */
	public SlotMachine ()
	{
	}

	/**
	 * Constructor.
	 * @param id				Unique id for this machine.
	 * @param dollarsPerCredit	Dollars per credit.
	 * @param maxBet			Maximum number of credits per pull.
	 */
	protected SlotMachine (String id, double dollarsPerCredit, int maxBet)
	{
		setId (id);
		setDollarsPerCredit (dollarsPerCredit);
		setMaximumBet (maxBet);
	}

	/**
	 * Here is how subclass constructors set the pay table.
	 */
	protected void setPayTable (PayTable payTable)
	{
		this.payTable = payTable;
	}

	//======================================================================
	// PROPERTIES
	//======================================================================

	/**
	 * @return the number of reels on this machine.
	 */
	abstract public int getReelCount ();

	/**
	 * Get the pattern for the indexed reel.
	 * @return an array of integers, in which each value is an index
	 * into the symbols graphic.
	 */
	abstract public int[] getReelPattern (int index);

	/**
	 * @return the number of pay lines on this slot machine.
	 */
	public int getNumberOfPayLines ()
	{
		return 1;	  // default
	}

	/**
	 * A pay-per-line slot machine activates exactly one pay line per credit
	 * bet.
	 * @return true iff this machine is a pay per-line slot machine.
	 */
	public boolean isPayPerLine ()
	{
		return false;	// default
	}

	/**
	 * Get the pay table.  Should not be modifiable, but it is.
	 */
	public PayTable getPayTable ()
	{
		return payTable;
	}

	//======================================================================
	// METHODS
	//======================================================================

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public SlotsGame createSlotsGame (Casino casino)
		throws MachineException
	{
		return new SlotsGame (casino, this);
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame (Casino casino)
		throws MachineException
	{
		return createSlotsGame (casino);
	}

	/**
	 * Get the symbol values for an array of reel stop values.
	 * @param stops				the reel indexes for the center (#0) pay line
	 * @param symbols			where to deposit the results
	 * @return the symbols array
	 */
	public int[] stopsToSymbols (int[] stops, int[] symbols)
	{
		return stopsToSymbols (stops, 0, symbols);
	}

	/**
	 * Given an array of reel stop values, get the symbols that appear on the
	 * specified pay line.
	 * @param stops				the reel indexes for the center (#0) pay line
	 * @param payLineIndex		the number of the pay line of interest
	 * @param symbols			where to deposit the results
	 * @return the symbols array
	 * @throws IllegalArgumentException if pay line index is bad
	 * @throws ArrayIndexOutOfBoundsException  if 'stops' contains bad reel position
	 */
	public int[] stopsToSymbols (int[] stops, int payLineIndex, int[] symbols)
	{
		validatePayLineIndex (payLineIndex);

		for (int i = 0; i < stops.length; ++i)
		{
			int[] pattern = getReelPattern (i);
			int reelPosition = stops[i];

			// Throw exception if invalid reel position.
			symbols[i] = pattern[reelPosition];

			// Adjust for multiple pay lines.
			if (payLineIndex != 0)
			{
				reelPosition += PayLineConfig[payLineIndex][i];
				reelPosition += pattern.length;
				reelPosition %= pattern.length;
				symbols[i] = pattern[reelPosition];
			}
		}

		return symbols;
	}

	/**
	 * Given the state of a slots game, return the number of active pay lines.
	 */
	public int getNumberOfActivePayLines (SlotCore core)
	{
		int n = getNumberOfPayLines ();
		if (isPayPerLine())
		{
			n = Math.min (n, core.getBet());
		}
		return n;
	}

	/** 
	 * Reward the player for the specified symbols.
	 */
	public void rewardMatches (int[] symbols, SlotCore core)
	{
		payTable.rewardMatches (symbols, core);
	}

	/**
	 * Dial me up a winner.	 Position the reels so that symbols matching
	 * the specified pay table entry appear beneath the specified pay line.
	 * For testing only, obviously.
	 * @throws IllegalArgumentException if pay table index is bad
	 * @throws IllegalArgumentException if pay line index is bad
	 */
	public boolean fabricate (int targetPayTableIndex, 
							  int targetPayLine, 
							  SlotCore core, int[] stops)
	{
		validatePayTableIndex (targetPayTableIndex);
		validatePayLineIndex (targetPayLine);

		// First, look for help from the pay table.	 Otherwise, we're 
		// stuck with the brute force method, which can take a long time.
		// This code can't handle hints in concert with multiple pay
		// lines yet.
		//
		int[] targetSymbols =
			getNumberOfPayLines() == 1
				? payTable.helpFabricate (targetPayTableIndex) : null;

		if (!advanceStops (stops, targetSymbols))
			return false;

		// Remember this point in the cycle, do not revisit it.
		int[] stopStops = (int[]) stops.clone ();

		// Prefer a match that does not introduce another winner on a second
		// pay line.
		int[] inferiorMatch = null;

		// Used in loop, allocated only once:
		int[] symbols = new int [stops.length];

		int numberOfPayLines = getNumberOfPayLines();

		do
		{
			boolean miss = false;

			for (int pl = 0; pl < numberOfPayLines; ++pl)
			{
				stopsToSymbols (stops, pl, symbols);
				int index = payTable.findMatch (symbols, core); 
				if (pl == targetPayLine)
				{
					// We want the target winning to show up under the pay
					// line.
					if (index != targetPayTableIndex)
					{
						miss = true;
						break;		 // Total miss - on to the next.
					}

					// Save this for later, in case it is not a complete match.
					inferiorMatch = (int[]) stops.clone();
				}
				else if (index >= 0)
				{
					// We don't want any other winners showing up elsewhere.
					// This is an inferior match at best.
					miss = true;
				}
			}

			if (!miss)
			{
				// A complete match!
				return true;
			}

			advanceStops (stops, targetSymbols);
		}
		while (!equal (stops, stopStops));

		// If we can't find a preferred match, but we have an inferior one,
		// use that.
		if (inferiorMatch != null)
		{
			System.arraycopy (inferiorMatch, 0, stops, 0, stops.length);
			return true;
		}

		// Hmm.
		return false;
	}

	/**
	 * An ungodly servant of the fabricate() method.
	 */
	private boolean advanceStops (int[] stops, 
								  int[] targetSymbols)
	{
		if (targetSymbols == null)
		{
			// Advance by one.
			for (int reelIndex = 0; reelIndex < stops.length; ++reelIndex)
			{
				if ((stops[reelIndex] += 1) < getReelPattern (reelIndex).length)
					break;
				stops[reelIndex] = 0;
			}
		}
		else
		{
			// Advance to next combination that shows the target symbol.
			for (int reelIndex = 0; reelIndex < stops.length; ++reelIndex)
			{
				int stop = stops[reelIndex];
				boolean rolledOver = false;
				int[] pattern = getReelPattern (reelIndex);
				do
				{
					stop += 1;
					if (stop >= pattern.length)
					{
						if (rolledOver)
							return false;
						stop = 0;
						rolledOver = true;
					}
				}
				while (pattern[stop] != targetSymbols[reelIndex]);
				stops[reelIndex] = stop;
				if (!rolledOver)
					break;
			}
		}

		return true;
	}

	private static boolean equal (int[] a1, int[] a2)
	{
		for (int i = 0; i < a1.length; ++i)
		{
			if (a1[i] != a2[i])
				return false;
		}

		return true;
	}

	/**
	 * Validate a pay table index.
	 * @throws IllegalArgumentException
	 */
	private void validatePayTableIndex (int payTableIndex)
		throws IllegalArgumentException
	{
		if (payTableIndex < 0 || payTableIndex >= payTable.getLength())
		{
			throw new IllegalArgumentException ("payTableIndex=" + payTableIndex);
		}
	}

	/**
	 * Validate a pay line index.
	 * @throws IllegalArgumentException
	 */
	private void validatePayLineIndex (int payLineIndex)
		throws IllegalArgumentException
	{
		if (payLineIndex < 0 || payLineIndex >= getNumberOfPayLines())
		{
			throw new IllegalArgumentException ("payLineIndex=" + payLineIndex);
		}
	}
}
