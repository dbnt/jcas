//
// QuitPlay.java
//

package net.ech.casino.war;

import net.ech.casino.*;

/**
 * QuitPlay: quit the game, as a player action.
 */
public class QuitPlay extends WarPlay
{
	/**
	 * Constructor.
	 */
	QuitPlay (WarGame game, String playerId, int seatIndex)
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
	protected void execute (WarModel model, Transaction trans)
		throws CasinoException
	{
		// Cases to worry about:
		// 1 - surrender/war choice required.
		// 2 - clear table with ante already paid.
		// 3 - raise on the table but no war showing yet.

		// Case 1:
		if (model.playerMayGoToWar (getSeatIndex()))
		{
			// A quit amounts to a surrender.
			new SurrenderPlay (getGame(), getPlayerId(),
				getSeatIndex()).execute(model, trans);
		}
		// Case 2:
		else if (model.isClear() && getSeat().getAnte() != null)
		{
			refund ("ante", getSeat(model).getAnte(), trans);
		}
		// Case 3:
		else if (getSeat(model).getRaise() != null)
		{
			refund ("raise", getSeat(model).getRaise(), trans);
		}
	}

	private void refund (String betName, Bet bet, Transaction trans)
	{
		if (!bet.getAmount().equals (Money.ZERO))
		{
			trans.addRefund (betName, bet);
		}
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
