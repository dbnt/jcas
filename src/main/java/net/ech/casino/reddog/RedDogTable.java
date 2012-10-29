//
// RedDogTable.java
//

package net.ech.casino.reddog;

import net.ech.casino.*;

/**
 * RedDogTable exposes the state of a game of Red Dog.
 * This is a read-only interface.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public abstract class RedDogTable implements Constants
{
	/**
	 * Return the current table state.
	 * @return one of the TABLE_* constants.
	 * @see Constants
	 */
	public abstract int getState ();

	/**
	 * Get a card.	There are two or three dealt per hand.
	 * @param index a number 0..2
	 * @return the card value, or NilCard if there is no card in that position
	 */
	public abstract byte getCard (int index);

	/**
	 * Get the current spread.	Valid only if there are cards on the table.
	 * @return the number of card values between the first two cards
	 * (non-inclusive) or -1 if the card ranks are equal.
	 */
	public abstract int getSpread ();

	/**
	 * Return true if there are three cards on the table and, in the case of
	 * a positive spread, the center card lies between the left and right
	 * cards in rank, or in the case of a tie, all three cards are equal in
	 * rank.
	 */
	public abstract boolean isWin ();

	/**
	 * Get the number of seats.
	 */
	public abstract int getNumberOfSeats ();

	/**
	 * Get the model for the indexed seat.
	 */
	public abstract RedDogSeat getSeat (int index);
}
