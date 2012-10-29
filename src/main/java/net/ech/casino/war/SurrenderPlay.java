//
// SurrenderPlay.java
//

package net.ech.casino.war;

import net.ech.casino.*;

/**
 * SurrenderPlay: don't go to war.
 */
public class SurrenderPlay extends WarPlay
{
	/**
	 * Constructor.
	 */
	SurrenderPlay (WarGame game, String playerId, int seatIndex)
	{
		super (game, playerId, seatIndex);
	}

	/**
	 * Return true if the player can surrender at this time.
	 */
	public boolean isEnabled ()
	{
		return getTable().playerMayGoToWar (getSeatIndex());
	}

	/**
	 * Surrender and get half of the ante back.
	 */
	protected void execute (WarModel model, Transaction trans)
		throws CasinoException
	{
		// Calculate the amount of the player refund.
		Bet ante = getSeat(model).getAnte();
		Money refund = ante.getAmount().divide (SURRENDER_DIVISOR);

		// Show the surrender.
		model.showSurrender (getSeatIndex(), refund);

		// Financials.
		trans.addRefund ("surrender", ante.getPurse(), refund);
	}

	/**
	 * What's my name?
	 */
	protected String getName ()
	{
		return "surrender";
	}
}
