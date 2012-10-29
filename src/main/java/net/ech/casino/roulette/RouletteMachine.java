//
// RouletteMachine.java	 
// 

package net.ech.casino.roulette;

import java.util.*;
import net.ech.casino.*;

/**
 * A RouletteMachine describes the parameters of a roulette table.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class RouletteMachine extends TableMachine implements Constants
{
	public final static int SideBetCover = 18;
	public final static int PayoffNumerator = 36;

	// Machine settings...
	private int maxInsideBet = -1;
	private boolean hasDoubleZero = true;
	private double outsideBetReturnOnZero = 0.0;
	private boolean loosePointChecking = false;

	//====================================================================
	// STATIC INITIALIZATION
	//====================================================================

	private static Hashtable AmericanPoints = makePoints (true);
	private static Hashtable EuroPoints = makePoints (false);

	private static Hashtable makePoints (boolean hasDoubleZero)
	{
		Hashtable ptab = new Hashtable ();

		insert (ptab, Red);
		insert (ptab, Black);
		insert (ptab, Odd);
		insert (ptab, Even);
		insert (ptab, Twelve1);
		insert (ptab, Twelve2);
		insert (ptab, Twelve3);
		insert (ptab, Col1);
		insert (ptab, Col2);
		insert (ptab, Col3);
		insert (ptab, Low);
		insert (ptab, High);

		for (int i = 0; i <= HighNumber; ++i)
			insert (ptab, Integer.toString (i));

		for (int i = 1; i < HighNumber; i += 3)
		{
			int j = i + 1;
			int k = i + 2;
			int l = i + 3;
			int m = i + 4;
			int n = i + 5;
			insert (ptab, i + "," + j);
			insert (ptab, j + "," + k);
			insert (ptab, i + "," + j + "," + k);
			if (n <= HighNumber)
			{
				insert (ptab, i + "," + j + "," + l + "," + m);
				insert (ptab, j + "," + k + "," + m + "," + n);
				insert (ptab, i + "," + j + "," + k + "," + l + "," +
							  m + "," + n);
			}
		}

		for (int i = 1; i <= HighNumber - 3; ++i)
		{
			insert (ptab, i + "," + (i + 3));
		}

		for (int i = 1; i < HighNumber; i += 6)
		{
			insert (ptab, i + "," + (i + 3));
		}

		if (hasDoubleZero)
		{
			insert (ptab, "00");
			insert (ptab, "00,0");
			insert (ptab, "00,0,1,2,3");
			insert (ptab, "00,0,2");
		}
		else
		{
			insert (ptab, "0,1,2,3");
			insert (ptab, "0,2");
		}

		return ptab;
	}

	private static void insert (Hashtable ptab, String pointString)
	{
		ptab.put (new Long (Point.parse (pointString)), pointString);
	}

	//====================================================================
	// OBJECT INITIALIZATION
	//====================================================================

	/**
	 * Constructor.
	 */
	public RouletteMachine ()
	{
	}

	/**
	 * Constructor.
	 */
	public RouletteMachine (int maxBet, int maxInsideBet,
							double outsideBetReturnOnZero)
	{
		setMaximumBet (maxBet);
		this.maxInsideBet = maxInsideBet;
		this.outsideBetReturnOnZero = outsideBetReturnOnZero;
	}

	//====================================================================
	// PROPERTIES
	//====================================================================

	/**
	 * Set European style (one 0) vs. American style (0 and 00)
	 */
	public void setEuropean (boolean european)
	{
		this.hasDoubleZero = !european;
	}
	
	/**
	 * Get European style (one 0) vs. American style (0 and 00)
	 */
	public boolean isEuropean ()
	{
		return !hasDoubleZero;
	}

	/**
	 * Set the maximum inside bet.	The maximum inside bet is intended
	 * to be less than the maximum general bet, but this method does
	 * not enforce that.
	 */
	public void setMaximumInsideBet (int maxInsideBet)
	{
		this.maxInsideBet = maxInsideBet;
	}

	/**
	 * Get the maximum inside bet.
	 */
	public int getMaximumInsideBet ()
	{
		return maxInsideBet <= 0 ? getMaximumBet () : maxInsideBet;
	}

	/**
	 * Turn loose point checking on/off.
	 * Loose point checking allows the player to bet on combinations that
	 * do not appear on a real roulette table.
	 */
	public void setLoosePointChecking (boolean on)
	{
		this.loosePointChecking = on;
	}

	//====================================================================
	// METHODS
	//====================================================================

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public RouletteGame createRouletteGame (Casino casino)
	{
		return new RouletteGame (casino, this);
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame (Casino casino)
	{
		return createRouletteGame (casino);
	}

	/**
	 * Throw an exception if the given Bet is illegal.
	 * @see setLoosePointChecking
	 */
	public void validateBet (Bet bet, RouletteGame game) 
		throws GameException
	{
		long point = bet.getPoint ();
		int amount = bet.getBet ();

		// Validate bet point.
		if (!loosePointChecking)
		{
			Hashtable ptab = hasDoubleZero ? AmericanPoints : EuroPoints;
			if (ptab.get (new Long (point)) == null)
			{
				throw new GameException ("Illegal betting point: " +
											Point.toString (point), game);
			}
		}

		// Validate bet amount.
		if (amount < getMinimumBet () || amount > pointToMaximumBet (point))
		{
			throw new GameException ("Invalid bet amount " + amount, game);
		}
	}

	/**
	 * Get the maximum on the indicated type of bet.
	 */
	public int pointToMaximumBet (long point)
	{
		return (maxInsideBet <= 0 || Point.count (point) >= 12)
					? getMaximumBet () : getMaximumInsideBet ();
	}

	/**
	 * Use the randomizer to get the next number.
	 */
	public int rollNumber (Randomizer random)
	{
		return random.roll (hasDoubleZero ? DoubleZero : Zero, HighNumber);
	}

	/**
	 * Calculate the winnings for a given number and bet.
	 */
	public void computeWin (int number, Bet bet)
	{
		long point = bet.getPoint ();

		double winFactor = 0;
		int cover = Point.count (point);
		int shift = number - DoubleZero;
		if ((point & (1L << shift)) != 0)
		{
			winFactor = PayoffNumerator / cover;
		}
		else if (cover == SideBetCover &&
				 (number == Zero || number == DoubleZero))
		{
			winFactor = outsideBetReturnOnZero;
		}

		bet.setReturn (bet.getBet () * winFactor);
	}
}
