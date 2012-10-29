//
// WarTable.java
//

package net.ech.casino.war;

import net.ech.casino.*;

/**
 * WarTable exposes the state of a game of Casino War.
 * This is a read-only interface.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public abstract class WarTable implements Constants
{
	/**
	 * Get a number that identifies the round, or hand, of this Game in 
	 * play.  This number begins at zero and increments with the start of
	 * each new round.	A round of zero indicates that no plays have yet
	 * been made.  The meaning of "round" depends on the type of Game.
	 * For a card game, a round is usually a hand.	For many games, a
	 * round amounts to a single play.
	 * @return the round counter
	 */
	public abstract int getRoundCount();

	/**
	 * Get a dealer card.
	 * @param index one of the DEALER_CARD_ constants.
	 * @return the card value, or NilCard if index is invalid.
	 * @see net.ech.casino.war.Constants
	 */
	public abstract byte getDealerCard (int index);

	/**
	 * Get the number of seats.
	 */
	public abstract int getSeatCount ();

	/**
	 * Get the model for the indexed seat.
	 */
	public abstract WarSeat getSeat (int index);

	/**
	 * Shortcut method.	 Returns true if the the indexed player's initial
	 * card and the dealer's are tied
	 */
	public abstract boolean isTieAt (int seatIndex);

	/**
	 * Return true if there are no cards on the table.
	 */
	public abstract boolean isClear ();

	/**
	 * Return true if this table is showing a finished hand.
	 */
	public abstract boolean isEndOfHand ();

	/**
	 * Return true if the player at the indexed seat may ante up.
	 */
	public abstract boolean playerMayAnte (int seatIndex);

	/**
	 * Return true if the player at the indexed seat may surrender or
	 * go to war.
	 */
	public abstract boolean playerMayGoToWar (int seatIndex);
}
