/**
 * KenoMachine.java
 */

package net.ech.casino.keno;

import java.io.*;
import java.util.*;
import net.ech.casino.*;

/**
 * Static definition of keno game.
 */
public class KenoMachine extends CreditsMachine
{
	// Game parameters and their defaults.
	private int size = 80;
	private int nrolled = 20;
	private int minPicked = 1;
	private int maxPicked = 10;
	private int[][] payTable =
	{				   // A default pay table.
		 {	0, 0 },
		 {	0, 0, 155 },
		 {	0, 0,  30, 372 },
		 {	0, 0,  10, 120, 660 },
		 { 10, 0,	0,	30, 280, 1800 },
		 { 10, 0,	0,	10, 140,  625, 3400 },
		 { 10, 0,	0,	10,	 40,  330, 1825, 4000 },
		 { 10, 0,	0,	 0,	 10,  300,	700, 2950, 4000 },
		 { 10, 0,	0,	 0,	 10,   80,	650, 1875, 3600, 4000 },
		 { 10, 0,	0,	 0,	 10,   20,	360, 1150, 2750, 3500, 4000 }
	};
	private int maxWin = Integer.MAX_VALUE;
	private int jackpotCatches = -1;

	/**
	 * Default constructor.
	 */
	public KenoMachine ()
	{
		 setMaximumBet (10);
	}

	/**
	 * Here's one way to create a pay table.  Load it from a file.
	 * Format is line-based.  Each line is:
	 * [number-of-picks] [[number-of-hits]:[pay-multiple]]*
	 */
	public void loadPayTable (String payTableName)
		 throws MachineException
	{
		 FileReader fin = null;
		 try
		 {
			 fin = new FileReader (payTableName);
			 loadPayTable (new BufferedReader (fin));
		 }
		 catch (IOException e)
		 {
			 throw new MachineException ("cannot open " + payTableName);
		 }
		 finally
		 {
			 if (fin != null)
				  try { fin.close (); } catch (IOException e) {}
		 }
	}

	/**
	 * Load pay table from character input stream.
	 */
	public void loadPayTable (BufferedReader in)
		 throws MachineException
	{
		 try
		 {
			 // To accumulate results...
			 Vector v = new Vector ();
			 int minPicked = size;
			 int maxPicked = 0;

			 // Read line by line.
			 String line;
			 for (int ln = 1; (line = in.readLine ()) != null; ++ln)
			 {
				  // Strip comments.
				  int comment = line.indexOf ('#');
				  if (comment >= 0)
					  line = line.substring (0, comment);

				  // Skip blank lines.
				  line = line.trim ();
				  if (line.length () == 0)
					  continue;

				  // Each line of text represents the payouts for a certain
				  // number of picks.
				  try
				  {
					  // Tokenize line into words.
					  StringTokenizer words = new StringTokenizer (line);

					  // First word is number of picks for this pay line.
					  int numberPicked = Integer.parseInt (words.nextToken ());
					  if (numberPicked < 1 || numberPicked > nrolled)
						   throw new IllegalArgumentException ();
					  int[] payLine = new int [numberPicked + 1];

					  // Remaining words are hits:payback
					  while (words.hasMoreTokens ())
					  {
						   String word = words.nextToken ();
						   int colon = word.indexOf (':');
						   int payIndex =
							   Integer.parseInt (word.substring (0, colon));
						   int payAmount = 
							   Integer.parseInt (word.substring (colon + 1));
						   payLine[payIndex] = payAmount;
					  }

					  if (payLine != null)
					  {
						   while (v.size () < numberPicked)
							   v.addElement (null);
						   v.setElementAt (payLine, numberPicked - 1);
						   minPicked = Math.min (minPicked, numberPicked);
						   maxPicked = Math.max (maxPicked, numberPicked);
					  }
				  }
				  catch (Exception e)
				  {
					  throw new MachineException ("config error, line " + ln +
													  ": " + e);
				  }
			 }

			 // Save data from vector into array.
			 int[][] payTable = new int [v.size ()][];
			 v.copyInto (payTable);

			 // It's a wrap.
			 this.minPicked = minPicked;
			 this.maxPicked = maxPicked;
			 this.payTable = payTable;
		 }
		 catch (IOException e)
		 {
			 throw new MachineException ("error reading config file");
		 }
	}

	/**
	 * Set the parameters of this machine's progressive jackpot.
	 * @param jackpotName				   The id of this machine's jackpot
	 * @param jackpotContrib				  The amount per credit of a normal
	 *											   bet contributed to the pot
	 * @param jackpotContribMaxBet		   The amount per credit of a maximum
	 *											   credit bet contributed to the pot
	 * @param jackpotBaseAmount					 The constant base jackpot amount
	 */
	public void setJackpotParameters (String jackpotName,
										  double jackpotContrib,
										  double jackpotContribMaxBet,
										  int jackpotCatches)
		 throws MachineException
	{
		 if (jackpotCatches <= minPicked || jackpotCatches > maxPicked)
			 throw new MachineException ("bad number of catches for jackpot");

		 super.setJackpotParameters (jackpotName, jackpotContrib, 
										jackpotContribMaxBet, Money.ZERO);
		 this.jackpotCatches = jackpotCatches;
	}

	/**
	 * Return whether this machine reports balances in terms of credits,
	 * versus dollars and cents.
	 */
	public boolean usesCredits ()
	{
		 return true;
	}

	/**
	 * Set the size of this game (the number of numbers to pick from).
	 */
	public void setSize (int size)
	{
		 this.size = size;
	}

	/**
	 * Get the size of this game (the number of numbers to pick from).
	 */
	public int getSize ()
	{
		 return this.size;
	}

	/**
	 * Set the number of numbers rolled.
	 */
	public void setNumberRolled (int nrolled)
	{
		 this.nrolled = nrolled;
	}

	/**
	 * Get the number of numbers rolled.
	 */
	public int getNumberRolled ()
	{
		 return this.nrolled;
	}

	/**
	 * Get the minimum number of picks allowable.
	 */
	public int getMinPicked ()
	{
		 return this.minPicked;
	}

	/**
	 * Get the maximum number of picks allowable.
	 */
	public int getMaxPicked ()
	{
		 return this.maxPicked;
	}

	/**
	 * Set the maximum win per card in credits.
	 */
	public void setMaximumWin (int maxWin)
	{
		 this.maxWin = maxWin;
	}

	/**
	 * Get the maximum win per card in credits.
	 */
	public int getMaximumWin ()
	{
		 return this.maxWin;
	}

	/**
	 * Get the number of catches required to win the jackpot.
	 * @return the number of catches required, or zero if no jackpot.
	 */
	public int getJackpotCatches ()
	{
		 return this.jackpotCatches;
	}

	/**
	 * Dump the whole darn thing to standard output for debugging.
	 */
	public void dump ()
	{
		 System.out.println ("id=" + getId ());
		 System.out.println ("dollarsPerCredit=" + getDollarsPerCredit ());
		 System.out.println ("maxBet=" + getMaximumBet ());
		 System.out.println ("size=" + size);
		 System.out.println ("nrolled=" + nrolled);
		 System.out.println ("minPicked=" + minPicked);
		 System.out.println ("maxPicked=" + maxPicked);
		 System.out.println ("maxWin=" + maxWin);
		 System.out.println ("jackpotCatches=" + jackpotCatches);
		 for (int i = 0; i < payTable.length; ++i)
		 {
			 boolean prefix = false;
			 for (int j = 0; j < payTable[i].length; ++j)
			 {
				  int pay = payTable[i][j];
				  if (pay > 0)
				  {
					  if (!prefix)
					  {
						   System.out.print ((i + 1) + " picks:");
						   prefix = true;
					  }
					  System.out.print (" " + j + ":" + pay);
				  }
			 }
			 if (prefix)
				  System.out.println ();
		 }
	}

	/**
	 * Randomize the winning numbers.  Result is an int array.
	 */
	public int[] roll (Randomizer random)
	{
		 // Create an index array and shuffle it.
		 byte[] temp = new byte [size];
		 for (byte i = 1; i <= size; ++i)
			 temp[i - 1] = i;
		 random.shuffle (temp);

		 // Take the first nrolled numbers.
		 int[] result = new int [nrolled];
		 for (int i = 0; i < nrolled; ++i)
			 result[i] = temp[i];

		 return result;
	}

	/**
	 * Get the payoff (as a multiple of the bet) given the number
	 * of picks and the number of hits.
	 */
	public int getPayMultiple (int npicks, int nhits)
	{
		 try
		 {
			 return payTable[npicks - 1][nhits];
		 }
		 catch (Exception e)
		 {
			 return 0;
		 }
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public KenoGame createKenoGame (Casino casino)
	{
		 return new KenoGame (casino, this);
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame (Casino casino)
	{
		 return createKenoGame (casino);
	}
}
