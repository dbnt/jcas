//
// GoToWarPlay.java
//

package net.ech.casino.war;

import net.ech.casino.*;

/**
 * GoToWarPlay: activate one of these to go to war.
 */
public class GoToWarPlay extends WarPlay
{
	/**
	 * Constructor.
	 */
	GoToWarPlay (WarGame game, String playerId, int seatIndex)
	{
		super (game, playerId, seatIndex);
	}

	/**
	 * Return true if this player can go to war at this time.
	 */
	public boolean isEnabled ()
	{
		return getTable().playerMayGoToWar (getSeatIndex());
	}

	/**
	 * Go to war.
	 */
	protected void execute (WarModel model, Transaction trans)
		throws CasinoException
	{
		// Show the raise.
		model.showRaise (getSeatIndex());

		// Financials.
		trans.addWager ("war", getPlayerId(), getSeat(model).getAnte());
	}

	/**
	 * What's my name?
	 */
	protected String getName ()
	{
		return "war";
	}
}
