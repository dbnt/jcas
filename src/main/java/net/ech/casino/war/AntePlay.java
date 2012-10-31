//
// AntePlay.java
//

package net.ech.casino.war;

import net.ech.casino.*;

/**
 * AntePlay: execute a player ante function.
 */
public class AntePlay extends WarPlay
{
	// Two input properties: the ante bet and the tie bet.	
	private Bet ante;
	private Bet tieBet;

	/**
	 * Constructor.
	 */
	AntePlay (WarGame game, String playerId, int seatIndex)
	{
		super (game, playerId, seatIndex);
	}

	/**
	 * Return true if this player can ante at this time.
	 */
	public boolean isEnabled ()
	{
		return getTable().playerMayAnte (getSeatIndex());
	}

	/**
	 * Set the ante bet.
	 */
	public void setAnte (Bet ante)
	{
		this.ante = ante;
	}

	/**
	 * Set the tie bet.
	 */
	public void setTieBet (Bet tieBet)
	{
		this.tieBet = tieBet;
	}

	/**
	 * Ante up.
	 */
	protected void execute (WarModel model, Transaction trans)
		throws CasinoException
	{
		// Validate input bets against table limits.
		getMachine().validateAnte (ante, tieBet);

		// Change model.
		model.showAnte (getSeatIndex(), ante, tieBet);

		trans.addWager ("ante", getPlayerId(), ante);
		if (tieBet != null)
		{
			trans.addWager ("tie", getPlayerId(), tieBet); 
		}
	}

	/**
	 * What's my name?
	 */
	protected String getName ()
	{
		return "ante";
	}
}
