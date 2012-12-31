//
// Jackpots.java  
// 

package net.ech.casino;

/**
 * The Jackpots interface provides lookup and update of a database
 * of progressive jackpots.	 Jackpots are indexed by name.	Each 
 * jackpot may be owned by any number of games.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public abstract class JackpotsAdapter implements Jackpots
{
	/**
	 * Get an array of all available jackpot names.
	 * @return an array of strings, non-null.
	 */
	public String[] getAllJackpotNames ()
		throws JackpotException
	{
		return new String[0];
	}

	/**
	 * Look up the current amount of the identified jackpot.
	 * @param name		Name of a jackpot
	 * @return the current jackpot amount (money), or null if there is no
	 *		   jackpot of the given name.
	 * @exception JackpotException in casino of data source error.
	 */
	public Money getJackpotAmount (String name)
		throws JackpotException
	{
		return Money.ZERO;
	}

	/**
	 * Increment the identified jackpot by a positive amount and return
	 * the updated amount. 
	 * @param name		Name of a jackpot
	 * @param amount	Amount to add to the jackpot (must be positive)
	 * @return the updated jackpot amount (money).
	 * @exception JackpotException in casino of data source error or
	 *			   if jackpot name is invalid.
	 */
	public Money addToJackpot (String name, double amount)
		throws JackpotException
	{
		throw new JackpotException(name);
	}

	/**
	 * A player has won the identified jackpot.
	 * @param name		unique id of a jackpot
	 * @param amount	fraction of jackpot won [0.0..1.0)
	 * @return the jackpot amount the player receives.
	 * @exception JackpotException in casino of data source error or
	 *			   if jackpot name is invalid.
	 */
	public Money claimJackpot (String name, double amount)
		throws JackpotException
	{
		throw new JackpotException(name);
	}
}
