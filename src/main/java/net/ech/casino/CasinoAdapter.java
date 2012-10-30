//
// CasinoAdapter.java	
// 

package net.ech.casino;

/**
 * A Casino is the source of casino-wide services.	
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public class CasinoAdapter implements Casino
{
	private Randomizer randomizer;

	/**
	 * Constructor.
	 */
	public CasinoAdapter ()
	{
		randomizer = new Randomizer();
	}

	/**
	 * Get the default random number generator of this casino.
	 * @return a Randomizer
	 */
	public Randomizer getRandomizer()
	{
		return randomizer;
	}

	/**
	 * Get the random number generator to use in the given Game.
	 */
	public Randomizer getRandomizer (Game game)
	{
		return getRandomizer();
	}

	/**
	 * @inheritDoc
	 */
	public void executeTransaction (Transaction trans)
		throws CasinoException
	{
	}

	/**
	 * Apply the latest jackpot amount to the Game's properties.
	 * executeTransaction must call this method whenever the player
	 * contributes to a jackpot or wins a jackpot.
	 */
	protected void applyJackpotAmount (Game game, String jackpotName, Money jackpotAmount)
	{
		game.applyJackpotAmount (jackpotName, jackpotAmount);
	}
}
