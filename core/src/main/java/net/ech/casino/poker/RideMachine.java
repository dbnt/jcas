//
// RideMachine.java
//

package net.ech.casino.poker;

import net.ech.casino.*;

/**
 * A RideMachine specifies Let it Ride poker table parameters.
 * Let It Ride is a variation of five-card poker.
 * Let It Ride is a registered trademark of the Shuffle Master
 * Corporation.
 *
 * @version 1.0
 * @author Dave Giese, dgiese@ech.net
 */
public class RideMachine extends TableMachine
{
	private int maximumPayout;

	/**
	 * Constructor.
	 */
	public RideMachine ()
	{
		// Minimum bet is typically $5, though $1 tables exist.
		setMinimumBet (5);
		setMaximumBet (100);
	}

	/**
	 * Get the number of cards in a deck.
	 */
	public int getCardsInDeck ()
	{
		// Deck is standard 52 cards, with no jokers.
		return CardConstants.CardsInStandardDeck;
	}

	/**
	 * Set the maximum payout amount.
	 * @param maximumPayout		the maximum in dollars, or zero for
	 *							unlimited
	 */
	public void setMaximumPayout (int maximumPayout)
	{
		this.maximumPayout = maximumPayout;
	}

	/**
	 * Get the maximum payout amount.
	 * @return the maximum in dollars, or zero for unlimited
	 */
	public int getMaximumPayout ()
	{
		return maximumPayout;
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public RideGame createRideGame(Casino casino)
	{
		return new RideGame (casino, this);
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame(Casino casino)
	{
		return createRideGame (casino);
	}
}
