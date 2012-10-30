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
	 * Create an accounting Session for a new Player.
	 * @param player	the Player
	 * @return a new Session
	 */
	public Session createSession (Player player)
		throws AccountingException
	{
		return null;
	}
}
