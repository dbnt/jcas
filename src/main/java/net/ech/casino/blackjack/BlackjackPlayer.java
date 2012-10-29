//
// BlackjackPlayer.java	 
// 

package net.ech.casino.blackjack;

import net.ech.casino.*;

/**
 * A BlackjackPlayer plays blackjack.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class BlackjackPlayer extends Player
{
	/**
	 * Constructor.
	 */
	public BlackjackPlayer (String accountId, BlackjackGame game)
	{
		super (accountId, game);
	}

	/**
	 * Get my game.
	 */
	public BlackjackGame getBlackjackGame ()
	{
		return (BlackjackGame) getGame ();
	}

	/**
	 * Request a deal.
	 */
	public void deal (int bet)
		throws CasinoException
	{
		getBlackjackGame ().deal (this, bet);
	}

	/**
	 * Take a hit on the current hand.
	 */
	public void hit ()
		throws CasinoException
	{
		getBlackjackGame ().hit (this);
	}

	/**
	 * Stand on the current hand.
	 */
	public void stand ()
		throws CasinoException
	{
		getBlackjackGame ().stand (this);
	}

	/**
	 * Double down on the current hand.
	 */
	public void doubledown ()
		throws CasinoException
	{
		getBlackjackGame ().doubledown (this);
	}

	/**
	 * Surrender.
	 */
	public void surrender ()
		throws CasinoException
	{
		getBlackjackGame ().surrender (this);
	}

	/**
	 * Split the current hand.
	 */
	public void split ()
		throws CasinoException
	{
		getBlackjackGame ().split (this);
	}

	/**
	 * Buy insurance.
	 */
	public void insurance ()
		throws CasinoException
	{
		getBlackjackGame ().insurance (this);
	}
}
