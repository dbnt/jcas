//
// RidePlayer.java	
// 

package net.ech.casino.poker;

import net.ech.casino.*;

/**
 * A RidePlayer plays Let it Ride.
 * 
 * @version 1.0
 * @author Dave Giese, dgiese@ech.net
 */
public class RidePlayer extends Player
{
	/**
	 * Constructor.
	 */
	public RidePlayer (String accountId, RideGame game)
	{
		super (accountId, game);
	}

	/**
	 * Get my game.
	 */
	public RideGame getRideGame ()
	{
		return (RideGame) getGame ();
	}

	/**
	 * Request the next deal.
	 */
	public void deal (int bet)
		throws CasinoException
	{
		getRideGame ().deal (this, bet);
	}

	/**
	 * Let it Ride.
	 */
	public void letItRide ()
		throws CasinoException
	{
		getRideGame ().letItRide (this);
	}

	/**
	 * Withdraw.
	 */
	public void withdraw ()
		throws CasinoException
	{
		getRideGame ().withdraw (this);
	}
}
