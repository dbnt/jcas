//
// ContinuePlay.java
//

package net.ech.casino.reddog;

import net.ech.casino.*;

/**
 * ContinuePlay: play controller a player must activate to finish a 
 * hand.  Includes an optional raise.
 */
public class ContinuePlay extends RedDogPlay
{
	private Money raise;

	/**
	 * Constructor.
	 */
	ContinuePlay (RedDogGame game, String playerId, int seatIndex)
	{
		super (game, playerId, seatIndex);
	}

	/**
	 * Return true if this player can continue at this time.
	 */
	public boolean isEnabled ()
	{
		return getTable().getState() == TABLE_WORKING &&
			   getSeat().getState() == SEAT_READY;
	}

	/**
	 * Set the raise amount.  May be null.
	 */
	public void setRaise (Money raise)
	{
		this.raise = raise;
	}

	/**
	 * Continue the hand.
	 */
	protected void execute (RedDogModel model, Transaction trans)
		throws CasinoException
	{
		// Validate the raise.
		getGame().validateRaise (getSeatIndex(), raise);

		// Indicate that the player has continued.
		model.showContinue (getSeatIndex(), raise);

		// Financials.
		if (raise != null)
		{
			Bet raiseBet = new Bet (raise, getSeat().getAnteBet().getPurse());
			trans.addWager ("raise", getPlayerId(), raiseBet);
		}
	}

	/**
	 * What's my name?
	 */
	protected String getName ()
	{
		return "continue";
	}
}
