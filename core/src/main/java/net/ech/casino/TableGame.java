//
// TableGame.java
//

package net.ech.casino;

/**
 * Base class for a table game.	 These types of machine 
 * run on chips with dollar values.	 Extended property minimumBet
 * represents the minimum amount that may be placed on a basic bet,
 * excluding side bets.
 *
 * Examples: blackjack, baccarat, craps, roulette.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public abstract class TableGame extends Game
{
	private int minBet;

	/**
	 * Constructor.	 
	 */
	public TableGame (Casino casino, TableMachine machine)
	{
		super (casino, machine);
		this.minBet = machine.getMinimumBet ();
	}

	//====================================================================
	// PROPERTIES
	//====================================================================

	/**
	 * Get the machine associated with this game.  
	 * @return the machine
	 */
	public TableMachine getTableMachine ()
	{
		return (TableMachine) getMachine ();
	}

	/**
	 * Get the minimum bet at this game.
	 * @return the minimum bet
	 */
	public int getMinimumBet ()
	{
		return minBet;
	}

	//====================================================================
	// METHODS
	//====================================================================

	/**
	 * Test the amount of a single bet against the allowable betting
	 * range.
	 */
	protected void testBet (int bet) throws GameException
	{
		if (bet < minBet || bet > getMaximumBet ())
			throw new GameException ("Bet is out of range: " + bet, this);
	}
}

