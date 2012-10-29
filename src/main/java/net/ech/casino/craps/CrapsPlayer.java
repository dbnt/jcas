//
// CrapsPlayer.java	 
// 

package net.ech.casino.craps;

import net.ech.casino.*;

/**
 * A CrapsPlayer plays craps.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class CrapsPlayer extends Player implements Constants
{
	/**
	 * Constructor.
	 */
	public CrapsPlayer (String accountId, CrapsGame game)
	{
		super (accountId, game);
	}

	/**
	 * Get my game.
	 */
	public CrapsGame getCrapsGame ()
	{
		return (CrapsGame) getGame ();
	}

	/**
	 * Bet on the next roll of the dice.
	 * @param newBets	an array of bet values
	 */
	public void bet (int[] newBets)
		throws CasinoException
	{
		getCrapsGame ().play (this, newBets);
	}

	/**
	 * Apply given dice values to the next roll (for testing).
	 * @param newBets	an array of bet values
	 * @param dice		the dice!
	 */
	public void bet (int[] newBets, int[] dice)
		throws CasinoException
	{
		getCrapsGame ().play (this, newBets, dice);
	}
}
