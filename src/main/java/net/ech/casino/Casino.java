//
// Casino.java	
// 

package net.ech.casino;

/**
 * A Casino provides essential services to Games.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public interface Casino 
{
	/**
	 * Get the default random number generator of this casino.
	 * @return a Randomizer
	 */
	public Randomizer getRandomizer();

	/**
	 * Get the random number generator to use in the given Game.
	 */
	public Randomizer getRandomizer (Game game);

	/**
	 * Create an accounting Session for a new Player.
	 * @param player	the Player
	 * @return a new Session
	 */
	public Session createSession (Player player)
		throws AccountingException;
}
