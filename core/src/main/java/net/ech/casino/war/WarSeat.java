//
// WarSeat.java
//

package net.ech.casino.war;

import net.ech.casino.*;

/**
 * WarSeat encapsulates state for a seat at the Casino War table.
 * This is an immutable interface.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public abstract class WarSeat
{
	//======================================================================
	// Properties
	//======================================================================

	/**
	 * Get the player's card by index.	Index 0 refers to the first card
	 * dealt to the player.	 Index 1 is valid only if there was a war.
	 * @return a card value, or NilCard if the index is invalid.
	 */
	public abstract byte getPlayerCard (int index);

	/**
	 * Return the player's current ante bet.
	 * @return a Bet object, null if the player has not yet ante'ed.
	 */
	public abstract Bet getAnte();

	/**
	 * Return the player's current tie bet.
	 * @return a Bet object, may be null.
	 */
	public abstract Bet getTieBet();

	/**
	 * Return the player's current raise bet.
	 * The bet has zero value if the player has surrendered.
	 * @return a Bet object, may be null
	 */
	public abstract Bet getRaise();

	/**
	 * Get the take amount.	 This is the total of the winnings and returns
	 * resulting from a recent game event.	It is guaranteed to remain at
	 * the seat only until the state of the game changes again.
	 */
	public abstract Money getTake();

	/**
	 * Return true if the player has surrendered.  This is equivalent to
	 * a raise bet of zero.
	 */
	public boolean isSurrendered ()
	{
		return getRaise() != null && getRaise().getAmount().signum() == 0;
	}
}
