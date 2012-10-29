//
// Analyzer.java
//

package net.ech.casino.slots;

import net.ech.casino.*;
import java.math.BigDecimal;

/**
 * Slot machine payout analyzer.  Check the machine's data structures for
 * flaws, and print out the expected payout and hit rate.
 *
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class Analyzer implements SlotCore
{
	/**
	 * Main program.  Run Analyzer -class <slot-machine-class>
	 */
	public static void main (String[] args)
	{
		try
		{
			String className = "net.ech.casino.slots.DemoMachine1";
			int bet = 0;
			boolean verbose = false;
			boolean charged = false;

			for (int i = 0; i < args.length; ++i)
			{
				if (args[i].equals ("-class"))
				{
					++i;
					className = args[i];
				}
				else if (args[i].equals ("-bet"))
				{
					++i;
					bet = Integer.parseInt (args[i]);
				}
				else if (args[i].equals ("-v") || args[i].equals("-verbose"))
				{
					verbose = true;
				}
				else if (args[i].startsWith ("-charge"))
				{
					charged = true;
				}
				else
				{
					usage ();
					System.exit (1);
				}
			}

			// Load the slot machine class
			Class clazz = Class.forName (className);
			SlotMachine machine = (SlotMachine) clazz.newInstance ();
			Analyzer analyzer = new Analyzer (machine);
			if (bet != 0)
				analyzer.setBet (bet);
			analyzer.setCharged (charged);
			analyzer.setVerbose (verbose);
			analyzer.analyze ();
			analyzer.report ();
		}
		catch (Exception e)
		{
			e.printStackTrace (System.err);
			System.exit (1);
		}
	}

	private SlotMachine machine;
	private int bet;
	private boolean charged;
	private boolean verbose;
	private long totalBet;
	private long totalReturns;
	private long positionCount;
	private long hitCount;
	private long[] returnsPerWinLevel;
	private long[] hitsPerWinLevel;
	private int multiHitPositions;
	private int jackpotHits;

	/**
	 * Constructor.
	 */
	public Analyzer (SlotMachine machine)
	{
		this.machine = machine;
		this.bet = machine.getMaximumBet();	  // default bet is max
		this.returnsPerWinLevel = new long [100];
		this.hitsPerWinLevel = new long [100];
	}

	/**
	 * Get the machine configuration.
	 */
	public SlotMachine getMachine ()
	{
		return machine;
	}

	/**
	 * Set the bet (fixed for analysis)
	 */
	public void setBet (int bet)
	{
		// Check for bet in range
		if (bet < 1)
		{
			throw new IllegalArgumentException ("Bad bet: " + bet);
		}

		if (bet > machine.getMaximumBet())
		{
			throw new IllegalArgumentException ("Bet of "+bet+" exceeds max bet of "+ machine.getMaximumBet()+".");
		}

		this.bet = bet;
	}

	/**
	 * Get the current bet amount.
	 */
	public int getBet ()
	{
		return bet;
	}

	/**
	 * Set the "charged" indicator.	 (fixed for analysis)
	 */
	public void setCharged (boolean charged)
	{
		this.charged = charged;
	}

	/**
	 * Get the value of the "charged" indicator.
	 */
	public boolean isCharged ()
	{
		return charged;
	}

	/**
	 * Set the "verbose" switch.  If false, analyzer prints nothing during
	 * analysis.
	 */
	public void setVerbose (boolean verbose)
	{
		this.verbose = verbose;
	}

	/**
	 * Add a win.
	 */
	public void addWin (int winLevel, int amountCredits)
	{
		hitCount += 1;
		hitsPerWinLevel[winLevel] += 1;
		totalReturns += amountCredits;
		returnsPerWinLevel[winLevel] += amountCredits;
	}

	/**
	 * Charge up the game.
	 */
	public void addCharge ()
	{
		// No-op.
	}

	/**
	 * Claim the jackpot.
	 */
	public void addJackpot (int winLevel)
	{
		jackpotHits += 1;
	}

	/**
	 * Analyze.
	 */
	public void analyze ()
	{
		if (verbose)
		{
			System.out.print ("Exercising reel positions.");
			System.out.flush ();
		}

		// Create input reel position.
		int reelCount = machine.getReelCount ();
		int[] stops = new int [reelCount];
		int[] symbols = new int [reelCount];

		// Iterate through every reel position.
		do
		{
			totalBet += bet;

			long priorHits = hitCount;

			// Get the number of active pay lines.
			int numActivePayLines = machine.getNumberOfActivePayLines (this);

			// Process each active pay line.
			for (int pl = 0; pl < numActivePayLines; ++pl)
			{
				// Get the symbols that appear on the pay line.
				machine.stopsToSymbols (stops, pl, symbols);

				// Let the machine exercise its pay table.
				machine.rewardMatches (symbols, this);
			}

			if (hitCount - priorHits > 1)
			{
				multiHitPositions += 1;
			}

			if (++positionCount % 1000 == 0 && verbose)
			{
				System.out.print (".");
				System.out.flush ();
			}
		}
		while (advance (stops));

		if (verbose)
		{
			System.out.println ("Finished.");
			System.out.println ();
		}
	}

	private boolean advance (int[] stops)
	{
		for (int i = 0; i < stops.length; ++i)
		{
			if ((stops[i] += 1) < machine.getReelPattern (i).length)
				return true;
			stops[i] = 0;
		}

		return false;
	}

	/**
	 * Print report to standard output.
	 */
	public void report ()
	{
		System.out.println ("================================================");
		System.out.println (machine.getId() + " (" + machine.getClass() + ")");
		System.out.println ("------------------------------------------------");
		System.out.println ("Maximum bet = " + machine.getMaximumBet());
		System.out.println ("Number of reels = " + machine.getReelCount());
		System.out.println ("Pay lines = " + machine.getNumberOfPayLines());
		System.out.println ("Pay per line = " + machine.isPayPerLine());

		System.out.println ("------------------------------------------------");
		System.out.println ("Bet = " + bet);
		System.out.println ("Charged = " + charged);
		System.out.println ("Active pay lines = " +
			machine.getNumberOfActivePayLines (this));

		System.out.println ("------------------------------------------------");
		System.out.println ("Payout percentage = " + payoutPercentage());
		System.out.println ("	 Total bet = " + totalBet);
		System.out.println ("	 Total returns = " + totalReturns);
		System.out.println ("	 Total jackpot returns = " +
			jackpotReturnsDecimal());
		System.out.println ("Hit Rate = " + hitRate());
		System.out.println ("	 Number of positions = " + positionCount);
		System.out.println ("	 Number of hits = " + hitCount);

		if (verbose)
		{
			int maxWinLevel = returnsPerWinLevel.length;
			for (; maxWinLevel > 0; --maxWinLevel)
			{
				if (returnsPerWinLevel[maxWinLevel - 1] > 0 ||
					hitsPerWinLevel[maxWinLevel - 1] > 0)
				{
					break;
				}
			}

			for (int i = 1; i < maxWinLevel; ++i)
			{
				System.out.println ("Win Level " + i + ":");
				System.out.println ("	 Returns = " +
					returnsPerWinLevel[i] + " (" +
					percentage (returnsPerWinLevel[i], totalReturns) + "%)");
				System.out.println ("	 Hits = " +
					hitsPerWinLevel[i] + " (" +
					percentage (hitsPerWinLevel[i], hitCount) + "%)");
			}
		}

		System.out.println ("Multiple hit positions = " + multiHitPositions);
		System.out.println ("Jackpot hits = " + jackpotHits);
	}

	private String payoutPercentage ()
	{
		// A jackpot has the effect of raising the returns by a fraction
		// of the bet.	But only if the jackpot gets hit once in a while.
		//
		return percentage (totalReturns + jackpotReturns(), totalBet);
	}

	private String jackpotReturnsDecimal ()
	{
		return decimal (jackpotReturns(), 4);
	}

	/**
	 * Get the total jackpot returns in credits.
	 */
	private double jackpotReturns ()
	{
		// Assume that all contributions are returned, provided that the
		// jackpot gets hit ever at all!

		if (jackpotHits == 0)
			return 0.0;

		return totalBet * machine.getJackpotContrib (bet);
	}

	private String hitRate ()
	{
		return percentage (hitCount, positionCount);
	}

	private String percentage (double part, double whole)
	{
		return decimal ((part * 100.0) / whole, 4);
	}

	private static String decimal (double n, int scale)
	{
		try
		{
			return new BigDecimal (n).setScale (scale, 0).toString();
		}
		catch (Exception e)
		{
			return "Infinite or Nan";
		}
	}
 
	private static void usage ()
	{
		System.err.println ("usage: java " + Analyzer.class.getName() + " -class classname -bet amount");
	}
}
