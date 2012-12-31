//
// Casino.java	
// 

package net.ech.casino;

/**
 * A Casino provides essential services to Games, including randomization and accounting.
 * 
 * @version 1.2
 * @author James Echmalian, ech@ech.net
 */
public interface Casino 
{
	/**
	 * Get the random number generator to use in the given Game.
	 * @return a Randomizer
	 */
	public Randomizer getRandomizer (Game game);

	/**
	 * A game transaction has occurred in the Java casino.	Execute the
	 * transaction in the accounting back end.	
	 * @see net.ech.casino.Transaction
	 * @see net.ech.casino.JackpotTransaction
	 * @exception InsufficientFundsException if the player's balance does not
	 *									cover the bet amount
	 * @exception AccountingException	there was an error accessing the
	 *									accounting back end
	 */
	public abstract void executeTransaction (Transaction trans)
		throws CasinoException;
}
