//
// RouletteGame.java  
// 

package net.ech.casino.roulette;

import java.util.*;
import net.ech.casino.*;

/**
 * A RouletteGame is a servelet for single-player roulette.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class RouletteGame extends TableGame implements Constants
{
	private Bet[] bets;
	private int number = NoNumber;

	/**
	 * Constructor.
	 */
	public RouletteGame (Casino casino, RouletteMachine machine)
	{
		super (casino, machine);
	}

	//====================================================================
	// PROPERTIES
	//====================================================================

	/**
	 * Get my machine.
	 */
	public RouletteMachine getRouletteMachine ()
	{
		return (RouletteMachine) getMachine ();
	}

	/**
	 * Get the last number.
	 * @return the last number rolled, or NoNumber if there has been no 
	 * play yet.
	 */
	public int getNumber ()
	{
		return number;
	}

	/**
	 * Get the last number as a String.
	 * @return a String containing the last number rolled, or null if there
	 * has been no play yet.
	 */
	public String getNumberString ()
	{
		switch (number)
		{
		case NoNumber:
			return null;
		case DoubleZero:
			return "00";
		default:
			return Integer.toString (number);
		}
	}

	/**
	 * Get the total amount bet.
	 */
	public int getTotalBet ()
	{
		int total = 0;

		if (bets != null)
		{
			for (int i = 0; i < bets.length; ++i)
				total += bets[i].getBet ();
		}

		return total;
	}

	/**
	 * Get the total amount bet.
	 */
	public int getBet ()
	{
		return getTotalBet ();
	}

	/**
	 * Get the total amount bet.
	 * @return the total bet as a Money value.
	 */
	public Money getTotalBetMoney ()
	{
		return new Money (getTotalBet ());
	}

	/**
	 * Get the total amount returned.
	 */
	public double getTotalReturn ()
	{
		double total = 0;

		if (bets != null)
		{
			for (int i = 0; i < bets.length; ++i)
				total += bets[i].getReturn ();
		}

		return total;
	}

	/**
	 * Get the total amount returned as a currency value.
	 */
	public Money getTotalReturnMoney ()
	{
		return new Money (getTotalReturn ());
	}

	/**
	 * Get the total amount returned as a currency value (alias for
	 * getTotalReturnMoney)
	 */
	public Money getWin ()
	{
		return getTotalReturnMoney ();
	}

	/**
	 * Get the number of bets on the table.
	 */
	public int getNumberOfBets ()
	{
		return bets == null ? 0 : bets.length;
	}

	/**
	 * Get the array of bet objects.
	 */
	public Bet[] getBets ()
	{
		return bets == null ? new Bet [0] : (Bet[]) bets.clone ();
	}

	/**
	 * Get the position of bet #index as a bit mask.
	 */
	public long getBetPoint (int index)
	{
		return bets[index].getPoint ();
	}

	/**
	 * Get the position of bet #index as a comma-separated String.
	 */
	public String getBetString (int index)
	{
		return Point.toString (bets[index].getPoint (), false);
	}

	/**
	 * Get the official name of the betting position of bet #index.
	 */
	public String getBetName (int index)
	{
		return bets[index].getName ();
	}

	/**
	 * Get the amount of bet #index.
	 */
	public int getBetAmount (int index)
	{
		return bets[index].getBet ();
	}

	/**
	 * Get the return on bet #index.
	 */
	public double getBetReturn (int index)
	{
		return bets[index].getReturn ();
	}

	/**
	 * Get the return on bet #index as a currency value.
	 */
	public Money getBetReturnMoney (int index)
	{
		return bets[index].getReturnMoney ();
	}

	//====================================================================
	// METHODS
	//====================================================================

	/**
	 * Spin the wheel.
	 * @param player		The player.
	 * @param amounts		 Bet amounts.
	 * @param points		Bet position masks.
	 */
	public synchronized void spin (Player player, int[] amounts, long[] points)
		throws CasinoException
	{
		// Validate.
		if (amounts == null || points == null)
			throw new GameException ("No bets?", this);
		if (amounts.length != points.length)
			throw new GameException ("amounts.length != points.length", this);

		RouletteMachine machine = getRouletteMachine ();

		Bet[] newBets = new Bet [amounts.length];
		int totalBet = 0;
		for (int i = 0; i < newBets.length; ++i)
		{
			int amount = amounts[i];
			long point = points[i];

			// Validate bet.
			newBets[i] = new Bet (amount, point);
			machine.validateBet (newBets[i], this);

			totalBet += amount;
		}

		// Save state.
		int oldNumber = number;
		Bet[] oldBets = bets;

		try
		{
			// Randomize, get results.
			bets = newBets;
			number = machine.rollNumber (getRandomizer ());
			for (int i = 0; i < bets.length; ++i)
				machine.computeWin (number, bets[i]);

			Transaction trans = new Transaction (this);
			double totalReturn = getTotalReturn ();
			trans.setWagerAmount (totalBet);
			trans.setReturnAmount (Math.min (totalBet, totalReturn));
			trans.setWinAmount (Math.max (totalReturn - totalBet, 0));
			getCasino().executeTransaction (trans);

		}
		catch (Exception e)
		{
			// Restore state.
			number = oldNumber;
			bets = oldBets;
			throw (e instanceof CasinoException) ? (CasinoException) e
												 : new CasinoException (e);
		}
	}
}
