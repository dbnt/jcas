//
// KenoPlayer.java	
// 

package net.ech.casino.keno;

import net.ech.casino.*;

/**
 * A KenoPlayer plays keno.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class KenoPlayer extends Player
{
	/**
	 * Constructor.
	 */
	public KenoPlayer (String accountId, KenoGame game)
	{
		super (accountId, game);
	}

	/**
	 * Get my game.
	 */
	public KenoGame getKenoGame ()
	{
		return (KenoGame) getGame ();
	}

	/**
	 * Play the game.
	 * @param ways		The "way" (bet) list.
	 */
	public void play (Way[] ways)
		throws CasinoException
	{
		getKenoGame ().play (this, ways);
	}
}
