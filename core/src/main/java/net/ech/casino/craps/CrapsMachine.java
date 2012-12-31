//
// CrapsMachine.java  
// 

package net.ech.casino.craps;

import java.util.*;
import net.ech.casino.*;

/**
 * A CrapsMachine describes the parameters of a craps table.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class CrapsMachine extends TableMachine implements Constants
{
	// For odds calculations...
	private final static int passOddsMultipliers[] = {2, 3, 6};
	private final static int passOddsDivisors[]	   = {1, 2, 5};
	private final static int placeBetMultipliers[] = {9, 7, 7};
	private final static int placeBetDivisors[]	   = {5, 5, 6};

	// Machine settings...
	private int odds = 1;

	// Chip value is one.  There is no way to modify the chip value currently.

	/**
	 * Constructor.
	 */
	public CrapsMachine ()
	{
	}

	/**
	 * Set the odds (1 = single, 2 = double) on this table.
	 */
	public void setOdds (int odds)
	{
		this.odds = odds;
	}

	/**
	 * Return the odds (1 = single, 2 = double) on this table.
	 */
	public int getOdds ()
	{
		return odds;
	}

	/**
	 * Get the id string for the indexed bet type.
	 */
	public String getBetName (int index)
	{
		try
		{
			return BetNames[index];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			return null;
		}
	}

	/**
	 * Get the maximum odds bet that may be taken on a pass bet of the given
	 * amount.	This is the maximum allowable by the rules of the game.	 The
	 * croupier may enforce a lower maximum in the case that taking the 
	 * maximum allowable by the rules is of no advantage to the player.
	 * @param passLineBet  the amount of the pass bet, in chips
	 * @return the maximum odds bet, in chips
	 */
	public int getMaxPassOddsBet (int passLineBet)
	{
		return passLineBet * odds;
	}

	/**
	 * Get the maximum odds that may be taken on a don't pass bet of the
	 * given amount.  This is the maximum allowable by the rules of the game.
	 * The croupier may enforce a lower maximum in the case that taking the
	 * maximum allowable by the rules is of no advantage to the player. 
	 * @param point		   the point that must be missed to win
	 * @param dontPassBet  the amount of the don't pass bet, in chips
	 * @return the maximum odds bet, in chips
	 */
	public int getMaxDontPassOddsBet (int point, int dontPassBet)
	{
		// Calculate the maximum that may be won on an odds bet.
		int maxWin = dontPassBet * odds;

		// The answer is the maximum value of x, where x times odds,
		// rounded down, yields maxWin!	 odds in this case is the reciprocal
		// of the pass odds for the given point.
		//
		// To get this number, calculate the minimum bet required to 
		// win the maximum plus one, and subtract one from the result.

		int oddsIndex = pointToOddsIndex (point);
		int num = passOddsMultipliers[oddsIndex];
		int denom = passOddsDivisors[oddsIndex];

		// Multiply rounding up to get the minimum.
		return ((((maxWin + 1) * num) + denom - 1) / denom) - 1;
	}

	/**
	 * Get the payout for a winning pass odds bet on the given point.  
	 * @param point	   The point that has been made
	 * @param oddsBet  The amount of the odds bet, in chips
	 * @return the payout, in chips
	 */
	public int getPassOddsWin (int point, int oddsBet)
	{
		int oddsIndex = pointToOddsIndex (point);
		int num = passOddsMultipliers[oddsIndex];
		int denom = passOddsDivisors[oddsIndex];

		// Multiply, rounding down.
		return (oddsBet * num) / denom;
	}

	/**
	 * Get the payout for a winning don't pass odds bet on the given point.	 
	 * @param point	   The point that has been missed!
	 * @param oddsBet  The amount of the odds bet, in chips
	 * @return the payout, in chips
	 */
	public int getDontPassOddsWin (int point, int oddsBet)
	{
		int oddsIndex = pointToOddsIndex (point);
		int num = passOddsDivisors[oddsIndex];
		int denom = passOddsMultipliers[oddsIndex];

		// Multiply, rounding down.
		return (oddsBet * num) / denom;
	}

	/**
	 * Get the payout for a winning place bet.
	 * @param point	   The point on which the place bet was placed
	 * @param placeBet The amount of the place bet, in chips
	 * @return the payout, in chips
	 */
	public int getPlaceWin (int point, int placeBet)
	{
		int oddsIndex = pointToOddsIndex (point);
		int num = placeBetMultipliers[oddsIndex];
		int denom = placeBetDivisors[oddsIndex];

		// Multiply, rounding down.
		return (placeBet * num) / denom;
	}

	/**
	 * Implementation artifact: for the given point value, return the index
	 * into the odds factor tables. 
	 * @throw a runtime exception
	 *		if the point value is invalid (not 4, 5, 6, 8, 9, or 10)
	 */
	private static int pointToOddsIndex (int point)
	{
		switch (point)
		{
		case 4: case 10:
			return 0;
		case 5: case 9:
			return 1;
		case 6: case 8:
			return 2;
		default:
			throw new IllegalArgumentException ("invalid point: " + point);
		}
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public CrapsGame createCrapsGame (Casino casino)
	{
		return new CrapsGame (casino, this);
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame (Casino casino)
	{
		return createCrapsGame (casino);
	}
}
