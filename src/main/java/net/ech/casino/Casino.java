//
// Casino.java	
// 

package net.ech.casino;

/**
 * A Casino is the source of casino-wide services.	
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
	 * Get the machine uniquely identified by the given key.
	 * @return the Machine, or null if the key is invalid.
	 */
	public Machine getMachine (String machineId);

	/**
	 * Find a game in progress with the given unique machine id and 
	 * player id.
	 */
	public Game findGame (String machineId, String playerId)
		throws CasinoException;

	/**
	 * Create an accounting Session for a new Player.
	 * @param player	the Player
	 * @return a new Session
	 */
	public Session createSession (Player player)
		throws AccountingException;
}
