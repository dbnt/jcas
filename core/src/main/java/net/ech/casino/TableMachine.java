//
// TableMachine.java
//

package net.ech.casino;

/**
 * Base class for a table game (machine).  These types of machine 
 * run on chips with dollar values.	 Extended property minimumBet
 * represents the minimum amount that may be placed on a basic bet,
 * excluding side bets.
 *
 * Examples: blackjack, baccarat, craps, roulette.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public abstract class TableMachine extends Machine
{
	private int numSeats = 1;
	private int minBet = 1;

	/**
	 * Constructor.	 
	 */
	public TableMachine ()
	{
	}

	/**
	 * Constructor.	 
	 * @param id		an id string for this machine
	 */
	public TableMachine (String id)
	{
		super (id);
	}

	//====================================================================
	// Properties
	//====================================================================

	/**
	 * Set the number of seats at the table.
	 */
	public void setNumberOfSeats (int numSeats)
	{
		this.numSeats = numSeats;
	}

	/**
	 * Get the number of seats at the table.
	 */
	public int getNumberOfSeats ()
	{
		return numSeats;
	}

	/**
	 * Set the minimum bet.
	 */
	public void setMinimumBet (int minBet)
	{
		this.minBet = minBet;
	}

	/**
	 * Get the minimum bet.
	 */
	public int getMinimumBet ()
	{
		return minBet;
	}
}
