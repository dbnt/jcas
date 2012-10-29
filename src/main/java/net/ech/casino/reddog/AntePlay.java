//
// AntePlay.java
//

package net.ech.casino.reddog;

import net.ech.casino.*;

/**
 * AntePlay: execute a player ante function.
 */
public class AntePlay extends RedDogPlay
{
	private Bet anteBet;

	/**
	 * Constructor.
	 */
	AntePlay (RedDogGame game, String playerId, int seatIndex)
	{
		super (game, playerId, seatIndex);
	}

	/**
	 * Return true if this Player can ante at this time.
	 */
	public boolean isEnabled ()
	{
		// FIXME: allow ante while seat is in SEAT_OUT state - Game framework
		// currently lacks ability to execute game logic on player join event.
		
		return getTable().getState() != TABLE_WORKING &&
			   getSeat().getState() != SEAT_BLOCKED;
	}

	/**
	 * Set the ante bet.
	 */
	public void setAnteBet (Bet anteBet)
	{
		this.anteBet = anteBet;
	}

	/**
	 * Ante up.
	 */
	protected void execute (RedDogModel model, Transaction trans)
		throws CasinoException
	{
		// Validate ante bet against table limits.
		getGame().validateAnteBet (anteBet);

		// Change model.
		model.showAnte (getSeatIndex(), anteBet);

		trans.addWager ("ante", getPlayerId(), anteBet);
	}

	/**
	 * What's my name?
	 */
	protected String getName ()
	{
		return "ante";
	}
}
