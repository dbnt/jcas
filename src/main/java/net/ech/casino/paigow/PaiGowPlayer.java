//
// PaiGowPlayer.java  
// 

package net.ech.casino.paigow;

import net.ech.casino.*;

/**
 * A PaiGowPlayer plays pai gow.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class PaiGowPlayer extends Player
{
	/**
	 * Constructor.
	 */
	public PaiGowPlayer (String accountId, PaiGowGame game)
	{
		super (accountId, game);
	}

	/**
	 * Get my game.
	 */
	public PaiGowGame getPaiGowGame ()
	{
		return (PaiGowGame) getGame ();
	}

	/**
	 * Request the next deal.
	 */
	public void deal(int bet)
		throws CasinoException
	{
		getPaiGowGame ().deal (this, bet);
	}

	/**
	 * The player has finished setting cards.  Resolve the hand.
	 * @param handString		the hand as a string of card encodings,
	 *							separated by spaces
	 */
	public void play(String handString)
		throws CasinoException
	{
		getPaiGowGame ().play (this, handString);
	}

	/**
	 * The player has finished setting cards.  Resolve the hand.
	 * @param hand		the hand as a byte array
	 */
	public void play(byte[] hand)
		throws CasinoException
	{
		getPaiGowGame ().play (this, hand);
	}

	/**
	 * Set the player's hand according to the house strategy.
	 */
	public void houseway ()
		throws CasinoException
	{
		getPaiGowGame ().houseway (this);
	}
}
