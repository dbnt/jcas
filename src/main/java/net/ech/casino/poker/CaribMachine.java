//
// CaribMachine.java
//

package net.ech.casino.poker;

import net.ech.casino.*;

/**
 * A CaribMachine specifies Caribbean Stud poker table parameters.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class CaribMachine extends TableMachine implements CaribConstants
{
	/**
	 * Factors used in determining the percent of the jackpot won.
	 */
	public final static double StraightFlushFactor = 0.1;
	public final static double RoyalFlushFactor = 1;

	public final static double BaseJackpotAmount = 10000;
	
	private int maximumBonusPayout;
	private JackpotParameters jackpotParams;

	/**
	 * Constructor.
	 */
	public CaribMachine ()
	{
	}

	/**
	 * Set the maximum bonus payout amount.
	 * @param maximumBonusPayout		the maximum in dollars, or zero for
	 *									unlimited
	 */
	public void setMaximumBonusPayout (int maximumBonusPayout)
	{
		this.maximumBonusPayout = maximumBonusPayout;
	}

	/**
	 * Get the maximum bonus payout amount.
	 * @return the maximum in dollars, or zero for unlimited
	 */
	public int getMaximumBonusPayout ()
	{
		return maximumBonusPayout;
	}

	/**
	 * Set the parameters of this machine's progressive jackpot.
	 * By default, the machine has a jackpot, but it is non-progressive.
	 * @param jackpotName				The id of this machine's jackpot
	 * @param jackpotContrib			The amount contributed to the pot
	 *									per dollar bet on the drop
	 */
	public void setJackpotParameters (String jackpotName,
									  double jackpotContrib)
		throws MachineException
	{
		jackpotParams = new JackpotParameters (jackpotName,
											   new Money (BaseJackpotAmount),
											   jackpotContrib);
	}

	/**
	 * Get jackpot parameters.
	 */
	public JackpotParameters getJackpotParameters ()
	{
		return jackpotParams;
	}

	/**
	 * Get jackpot name.
	 */
	public String getJackpotName ()
	{
		return jackpotParams == null ? null : jackpotParams.getJackpotName ();
	}

	/**
	 * Get the amount contributed to the pot per dollar bet on the drop.
	 */
	public double getJackpotContrib ()
	{
		return jackpotParams == null ? 0 : jackpotParams.getJackpotContrib ();
	}

	/**
	 * Get the current jackpot amount.
	 * @return the most recent jackpot amount as currency
	 */
	public Money getJackpotAmount (Jackpots jackpots)
		throws JackpotException
	{
		String jackpotName = getJackpotName ();
		if (jackpotName == null)
			return null;

		Money pot = jackpots.getJackpotAmount (jackpotName);

		// The amount received from the jackpot source does not include
		// the base payout; add the base to the amount.
		//
		return pot.add (jackpotParams.getJackpotBaseAmount ());
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public CaribGame createCaribGame(Casino casino)
	{
		return new CaribGame (casino, this);
	}

	/**
	 * Start a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame(Casino casino)
	{
		return createCaribGame (casino);
	}
}
