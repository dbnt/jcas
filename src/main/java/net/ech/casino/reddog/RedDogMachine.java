//
// RedDogMachine.java  
// 

package net.ech.casino.reddog;

import net.ech.casino.*;

/**
 * Class RedDogMachine defines the house options available in the game of
 * Red Dog.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class RedDogMachine extends TableMachine
	implements Constants
{
	private int numberOfDecks = DEFAULT_NUMBER_OF_DECKS;
	private boolean multipleAppliedToAnte = false;

	/**
	 * Construct a default RedDog machine.
	 */
	public RedDogMachine ()
	{
	}

	//=================================================================
	// Dynamic properties
	//=================================================================

	/**
	 * Set the number of decks in the shoe.
	 */
	public void setNumberOfDecks (int numberOfDecks)
	{
		// Get reasonable.
		if (numberOfDecks < 1 || numberOfDecks > 100)
		{
			throw new IllegalArgumentException (
				"numberOfDecks=" + numberOfDecks);
		}

		this.numberOfDecks = numberOfDecks;
	}

	/**
	 * Get the number of decks in the shoe.
	 */
	public int getNumberOfDecks ()
	{
		return numberOfDecks;
	}

	/**
	 * Set the payout policy on the ante bet - does the payout multiple
	 * apply in the case of a spread win?
	 * @param multipleAppliedToAnte true if the payout multiple applies
	 *								to the ante bet; false if the ante bet
	 *								always pays even money.
	 */
	public void setMultipleAppliedToAnte (boolean multipleAppliedToAnte)
	{
		this.multipleAppliedToAnte = multipleAppliedToAnte;
	}

	/**
	 * Get the payout policy on the ante bet.
	 * @see #setMultipleAppliedToAnte 
	 */
	public boolean isMultipleAppliedToAnte ()
	{
		return multipleAppliedToAnte;
	}

	//=================================================================
	// Game creation
	//=================================================================

	/**
	 * Create a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public RedDogGame createRedDogGame (Casino casino)
	{
		return new RedDogGame (casino, this);
	}

	/**
	 * Create a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame (Casino casino)
	{
		return createRedDogGame (casino);
	}

	//=================================================================
	// Services for RedDogGame.
	//=================================================================

	/**
	 * Create a new Shoe appropriate for a game of this type.
	 */
	public Shoe createNewShoe ()
	{
		return new Shoe (numberOfDecks);
	}

	/**
	 * Get the payout multiple for a given spread.
	 * @param spread   the spread (difference in rank between first two cards)
	 * @return the payout multiple
	 */
	public int getPayMultiple (int spread)
	{
		switch (spread)
		{
		case 1:
			return PAY_FOR_SPREAD_1;
		case 2:
			return PAY_FOR_SPREAD_2;
		case 3:
			return PAY_FOR_SPREAD_3;
		default:
			return spread >= 4 ? PAY_FOR_SPREAD_4_PLUS : 0;
		}
	}
}
