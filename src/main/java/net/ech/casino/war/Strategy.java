//
// Strategy.java  
// 

package net.ech.casino.war;

import net.ech.casino.*;

/**
 * Encapsulation of Casino War strategy.
 *
 * The Casino War bettor may control the following variables:<ul>
 * <li>The amount of the ante bet</li>
 * <li>The amount of the tie bet</li>
 * <li>The choice between surrendering and going to war</li></ul>
 * This class allows the Casino War simulator to do so programmatically.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class Strategy implements CardConstants
{
	private int ante = 1;
	private int tieBet = 0;
	private byte minWarRank = MinRank;	 // always go to war by default

	/**
	 * Factory.
	 */
	public static Strategy forName (String name)
		throws CasinoException
	{
		Strategy strat = new Strategy ();

		if (name == null || name.equals (""))
		{
		}
		else if (name.equalsIgnoreCase ("surrender"))
		{
			strat.setMinimumWarRank ((byte)(MinRank + NumberOfRanks));
		}
		else
		{
			throw new CasinoException ("unknonwn strategy: " + name);
		}

		return strat;
	}

	/**
	 * Constructor.
	 */
	public Strategy ()
	{
	}

	/**
	 * Set the amount to bet on each hand.
	 */
	public void setAnte (int ante)
	{
		this.ante = ante;
	}

	/**
	 * Get the amount to bet on the next hand.
	 */
	public int getAnte ()
	{
		return ante;
	}

	/**
	 * Set the amount to bet on a tie.
	 */
	public void setTieBet (int tieBet)
	{
		this.tieBet = tieBet;
	}

	/**
	 * Get the amount to bet on a tie.
	 */
	public int getTieBet ()
	{
		return tieBet;
	}

	/**
	 * Set the minimum rank at which the player will go to war.	 If the
	 * player and dealer draw a tie with this rank or above, the player
	 * goes to war.	 This allows for a mixture of warring and surrendering.
	 */
	public void setMinimumWarRank (byte minWarRank)
	{
		this.minWarRank = minWarRank;
	}

	/**
	 * Go to war or not?
	 */
	public boolean goToWar (WarGame game)
	{
		return Card.rankOf (game.getTable().getDealerCard(0)) >= minWarRank;
	}
}
