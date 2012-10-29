//
// RoulettePlayer.java	
// 

package net.ech.casino.roulette;

import net.ech.casino.*;

/**
 * A RoulettePlayer plays roulette.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class RoulettePlayer extends Player implements Constants
{
	/**
	 * Constructor.
	 */
	public RoulettePlayer (String accountId, RouletteGame game)
	{
		super (accountId, game);
	}

	/**
	 * Get my game.
	 */
	public RouletteGame getRouletteGame ()
	{
		return (RouletteGame) getGame ();
	}

	/**
	 * Bet on the next spin of the wheel.
	 * @param amounts	Bet amounts.
	 * @param points	Bet position masks.
	 */
	public void spin (int[] amounts, long[] points)
		throws CasinoException
	{
		getRouletteGame ().spin (this, amounts, points);
	}
}
