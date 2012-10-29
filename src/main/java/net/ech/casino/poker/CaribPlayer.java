//
// CaribPlayer.java	 
// 

package net.ech.casino.poker;

import net.ech.casino.*;

/**
 * A CaribPlayer plays Caribbean Stud poker.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class CaribPlayer extends Player
{
	/**
	 * Constructor.
	 */
	public CaribPlayer (String accountId, CaribGame game)
	{
		super (accountId, game);
	}

	/**
	 * Get my game.
	 */
	public CaribGame getCaribGame ()
	{
		return (CaribGame) getGame ();
	}

	/**
	 * Request the next deal.
	 */
	public void deal (int ante, int drop)
		throws CasinoException
	{
		getCaribGame ().deal (this, ante, drop);
	}

	/**
	 * Call.
	 */
	public void call ()
		throws CasinoException
	{
		getCaribGame ().call (this);
	}

	/**
	 * Fold.
	 */
	public void fold ()
		throws CasinoException
	{
		getCaribGame ().fold (this);
	}
}
