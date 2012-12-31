//
// RedDogSeat.java
//

package net.ech.casino.reddog;

import net.ech.casino.*;

/**
 * RedDogSeat encapsulates state for a seat at the Red Dog table.
 * This is an immutable interface.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public abstract class RedDogSeat
{
	/**
	 * Get the state of this seat.
	 * @return one of the SEAT_* constants
	 * @see Constants
	 */
	public abstract int getState ();

	/**
	 * Get the player's current ante bet.
	 * @return a Bet object, null if the player has not yet ante'ed.
	 */
	public abstract Bet getAnteBet ();

	/**
	 * Get the player's current raise bet.
	 * @return a Bet object, may be null.
	 */
	public abstract Bet getRaiseBet ();

	/**
	 * Get the player's current total bet, equal to the ante bet plus
	 * any raise bet.
	 * @return a Bet object, may be null.
	 */
	public abstract Bet getTotalBet ();

	/**
	 * Get the take amount.	 This is the total of the winnings and returns
	 * resulting from the close of the hand.  It is guaranteed to remain at
	 * the seat only until the state of the game changes again.
	 */
	public abstract Money getTake ();
}
