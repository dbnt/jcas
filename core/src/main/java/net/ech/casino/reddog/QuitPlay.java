//
// QuitPlay.java
//

package net.ech.casino.reddog;

import net.ech.casino.*;

/**
 * QuitPlay: quit the game, as a player action.
 */
public class QuitPlay extends RedDogPlay
{
	/**
	 * Constructor.
	 */
	QuitPlay (RedDogGame game, String playerId, int seatIndex)
	{
		super (game, playerId, seatIndex);
	}

	/**
	 * Return true if this player can quit at this time.
	 */
	public boolean isEnabled ()
	{
		// Quit is always enabled.	Easier that way.
		return true;
	}

	/**
	 * Quit.
	 */
	protected void execute (RedDogModel model, Transaction trans)
		throws CasinoException
	{
	}

	/**
	 * What's my name?
	 */
	protected String getName ()
	{
		return "quit";
	}

	/**
	 * Clean up by removing the player from the game.
	 */
	protected void cleanup ()
	{
		getGame().removePlayerAt (getSeatIndex());
	}
}
