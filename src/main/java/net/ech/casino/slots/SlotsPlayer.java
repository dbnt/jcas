//
// SlotsPlayer.java	 
// 

package net.ech.casino.slots;

import net.ech.casino.*;

/**
 * A slot machine player.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class SlotsPlayer extends Player
{
	/**
	 * Constructor.
	 */
	public SlotsPlayer (String accountId, SlotsGame game)
	{
		super (accountId, game);
	}

	/**
	 * Get my game.
	 */
	public SlotsGame getSlotsGame ()
	{
		return (SlotsGame) getGame ();
	}

	/**
	 * Play the game.
	 */
	public void play (int bet)
		throws CasinoException
	{
		getSlotsGame ().pull (bet);
	}

	/**
	 * Play the game.
	 */
	public void play (int bet, boolean charged)
		throws CasinoException
	{
		getSlotsGame ().pull (bet, charged);
	}
}
