//
// Strategy.java  
// 

package net.ech.casino.reddog;

import net.ech.casino.*;

/**
 * Encapsulation of Red Dog strategy.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class Strategy implements CardConstants
{
	// By default, raise on spread of 7 or more.
	private int raiseSpread = 7;

	/**
	 * Factory.
	 */
	public static Strategy forName (String name)
		throws CasinoException
	{
		return new Strategy ();
	}

	/**
	 * Constructor.
	 */
	public Strategy ()
	{
	}

	/**
	 * Set the minimum spread for raising.
	 */
	public void setRaiseSpread (int raiseSpread)
	{
		this.raiseSpread = raiseSpread;
	}

	/**
	 * Get the minimum spread for raising.
	 */
	public int getRaiseSpread ()
	{
		return raiseSpread;
	}

	/**
	 * How much to raise?
	 */
	public Money getRaise (RedDogTable table, int seatIndex)
	{
		if (table.getSpread() >= raiseSpread)
		{
			return table.getSeat (seatIndex).getAnteBet().getAmount();
		}
		return null;
	}
}
