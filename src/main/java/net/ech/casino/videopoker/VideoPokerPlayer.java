//
// VideoPokerPlayer.java  
// 

package net.ech.casino.videopoker;

import net.ech.casino.*;

/**
 * A VideoPokerPlayer plays video poker.
 * 
 * @version 1.1
 * @author Dave Giese, dgiese@ech.net
 */
public class VideoPokerPlayer extends Player
{
	/**
	 * Constructor.
	 */
	public VideoPokerPlayer (String accountId, VideoPokerGame game)
	{
		super (accountId, game);
	}

	/**
	 * Get my game.
	 */
	public VideoPokerGame getVideoPokerGame ()
	{
		return (VideoPokerGame) getGame ();
	}

	/**
	 * Request a deal.
	 */
	public void deal (int bet)
		throws CasinoException
	{
		getVideoPokerGame ().deal (this, bet);
	}

	/**
	 * Draw some cards
	 */
	public void draw (int hold)
		throws CasinoException
	{
		getVideoPokerGame ().draw (this, hold);
	}

	/**
	 * Play doubleup
	 */
	public void doubleUp (int hold)
		throws CasinoException
	{
		getVideoPokerGame ().doubleUp (this);
	}

	/**
	 * Pick a card for double-up.
	 */
	public void pick (int pick)
		throws CasinoException
	{
		getVideoPokerGame ().pick (this, pick);
	}
}
