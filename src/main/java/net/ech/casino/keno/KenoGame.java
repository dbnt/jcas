/**
 * KenoGame.java
 */

package net.ech.casino.keno;

import java.util.*;
import net.ech.casino.*;

/**
 * Dynamic state of keno game.
 */
public class KenoGame extends CreditsGame
{
	private int size;
	private int[] numbers;
	private Way[] ways;
	private boolean jackpotWin;

	/**
	 * Constructor.
	 */
	public KenoGame (Casino casino, KenoMachine machine)
	{
		super (casino, machine);
		this.size = machine.getSize ();
	}

	//==================================================================
	// Properties
	//==================================================================

	/**
	 * Get my machine.
	 */
	public KenoMachine getKenoMachine ()
	{
		return (KenoMachine) getMachine ();
	}

	public boolean isPlayed ()
	{
		return ways != null;
	}

	public int getTotalBet ()
	{
		return getBet ();
	}

	public int getTotalWin ()
	{
		return getWin ();
	}

	public int[] getNumbers ()
	{
		return numbers == null ? null : (int[]) numbers.clone ();
	}

	public String getNumberString ()
	{
		StringBuilder buf = new StringBuilder ();

		if (numbers != null)
		{
			for (int i = 0; i < numbers.length; ++i)
			{
				if (buf.length () > 0)
					buf.append (' ');
				buf.append (numbers[i]);
			}
		}

		return buf.toString ();
	}

	public Way[] getWays ()
	{
		return ways;
	}

	//==================================================================
	// Methods
	//==================================================================

	/**
	 * Play the game.  
	 * (Insert description of parameter handling here.)
	 * @param player		The player.
	 * @param params		A parameter lookup table.
	 */
	public void play (Player player, Map params)
		throws CasinoException
	{
		// Get all way strings and b-way (way with specific bet amount) strings.
		Object[] wayStrings = (Object[]) params.get ("way");
		Object[] bwayStrings = (Object[]) params.get ("bway");
		int nways = wayStrings.length + bwayStrings.length;
		if (nways == 0)
			throw new GameException ("No ways?", this);

		// Way strings require default bet param.
		Integer defaultBet = (Integer) params.get ("bet");
		if (defaultBet == null && wayStrings.length > 0)
			throw new GameException ("missing bet amount", this);

		// To enforce uniqueness of way ids:
		Hashtable idLookup = new Hashtable ();

		// Parse way strings.
		Way[] ways = new Way [nways];
		for (int w = 0; w < 2; ++w)
		{
			Object[] strings = w == 0 ? wayStrings : bwayStrings;
			for (int i = 0; i < strings.length; ++i)
			{
				String str = strings[i].toString ();
				StringTokenizer toks = new StringTokenizer (str);
				try
				{
					// First token is the id.
					String id = toks.nextToken ();
					if (idLookup.get (id) != null)
						throw new Exception ();		   // id must be unique
					idLookup.put (id, id);

					// Second token of a b-way string is the bet amount. 
					int bet = w == 1 ? Integer.parseInt (toks.nextToken ())
									 : defaultBet.intValue ();
					if (bet <= 0 || bet > getMaximumBet ())
						throw new GameException ("Invalid bet amount " + bet, this);

					// Construct a way.
					Way way = new Way (id, size, bet, w == 1);

					// Remaining tokens are marked numbers.
					// There must be at least one.
					do
					{
						way.mark (Integer.parseInt (toks.nextToken ()));
					}
					while (toks.hasMoreTokens ());

					// Save the way.
					ways[w * wayStrings.length + i] = way;
				}
				catch (GameException e)
				{
					throw e;
				}
				catch (Exception e)
				{
					throw new GameException ("Bad way: " + str, this);
				}
			}
		}

		play (player, ways);
	}

	/**
	 * Play the game.
	 * @param player		The player.
	 * @param newWays		 Array of ways.
	 */
	public synchronized void play (Player player, Way[] newWays)
		throws CasinoException
	{
		// Save state.
		int oldBet = getBet ();
		int oldWin = getWin ();
		boolean oldJackpotWin = jackpotWin;
		int[] oldNumbers = numbers;
		Way[] oldWays = ways;

		try
		{
			ways = newWays;

			// Set the total bet.
			int totalBet = 0;
			for (int i = 0; i < ways.length; ++i)
				totalBet += ways[i].getBet ();
			setBet (totalBet);

			// Randomize.
			numbers = getKenoMachine ().roll (getRandomizer ());

			// Get returns.
			grade ();

			executeTransaction();
		}
		catch (Exception e)
		{
			// Restore state.
			setBet (oldBet);
			setWin (oldWin);
			numbers = oldNumbers;
			ways = oldWays;
			jackpotWin = oldJackpotWin;

			throw (e instanceof CasinoException) ? (CasinoException) e
												 : new CasinoException (e);
		}
	}

	// 
	// Calculate returns by grading each Way.
	//
	private void grade ()
	{
		jackpotWin = false;

		KenoMachine machine = getKenoMachine ();
		BitSet numberSet = toBitSet (numbers);

		int totalWin = 0;
		for (int i = 0; i < ways.length; ++i)
		{
			Way way = ways[i];
			int nmarked = way.getNumberMarked ();
			int ncaught = way.catchNumbers (numberSet);

			// Look up return.
			int bet = way.getBet ();
			int win = bet * machine.getPayMultiple (nmarked, ncaught);
			totalWin += win;
			way.setWin (win);

			// Check for jackpot hit.
			int jackpotCatches = machine.getJackpotCatches ();
			if (jackpotCatches > 0 && ncaught >= jackpotCatches)
				jackpotWin = true;
		}

		setWin (Math.min (totalWin, machine.getMaximumWin ()));
	}

	private static BitSet toBitSet (int[] iarray)
	{
		BitSet bits = new BitSet ();

		for (int i = 0; i < iarray.length; ++i)
		{
			bits.set (iarray[i] - 1);
		}

		return bits;
	}

	private void executeTransaction ()
		throws CasinoException
	{
		Transaction trans = new Transaction (this);
		trans.setWagerAmount (getBetMoney ());
		if (getWin () > 0)
		{
			trans.setReturnAmount (getBetMoney ());
			trans.setWinAmount (getWinMoney ().subtract (getBetMoney ()));
		}

		// Jackpot.
		JackpotParameters jackpotParams =
			getKenoMachine().getJackpotParameters();
		if (jackpotParams != null && jackpotParams.getJackpotName() != null)
		{
			JackpotTransaction jtrans = new JackpotTransaction ();
			jtrans.setJackpotName (jackpotParams.getJackpotName ());

			double betDollars = getBet() * getDollarsPerCredit();

			double factor =
				getBet() == getMaximumBet()
					? jackpotParams.getJackpotContribMaxBet ()
					: jackpotParams.getJackpotContrib ();


			// Contribute to pot.
			jtrans.setContributionAmount (betDollars * factor);

			if (jackpotWin)
			{
				// Claim the whole pot.
				jtrans.setClaimFactor (1.0);
			}

			trans.addJackpotTransaction (jtrans);
		}

		// Commit to database.
		getCasino().executeTransaction (trans);
	}
}
