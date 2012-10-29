//
// JackpotParameters.java
//

package net.ech.casino;

/**
 * JackpotParameters is a group of parameters describing a
 * progressive jackpot as would be associated with a slot
 * machine or similar game.	 A JackpotParameters object is
 * immutable.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class JackpotParameters implements java.io.Serializable
{
	private String jackpotName;
	private Money jackpotBaseAmount;
	private double jackpotContrib;
	private double jackpotContribMaxBet;

	/**
	 * Constructor.
	 * @param jackpotName				The id of this machine's jackpot
	 * @param jackpotBaseAmount			The constant base jackpot amount
	 * @param jackpotContrib			The amount per credit of a 
	 *									bet contributed to the pot
	 */
	public JackpotParameters (String jackpotName, 
							  Money jackpotBaseAmount,
							  double jackpotContrib)
		throws MachineException
	{
		this (jackpotName, jackpotBaseAmount, jackpotContrib, jackpotContrib);
	}

	/**
	 * Constructor.
	 * @param jackpotName				The id of this machine's jackpot
	 * @param jackpotContrib			The amount per credit of a normal
	 * @param jackpotBaseAmount			The constant base jackpot amount
	 *									bet contributed to the pot
	 * @param jackpotContribMaxBet		The amount per credit of a maximum
	 *									credit bet contributed to the pot
	 */
	public JackpotParameters (String jackpotName, 
							  Money jackpotBaseAmount,
							  double jackpotContrib,
							  double jackpotContribMaxBet)
		throws MachineException
	{
		if (jackpotName == null || jackpotName.length () == 0)
			throw new MachineException ("empty jackpot name");

		if (jackpotContrib < 0.0 || jackpotContrib > 1.0 ||
			jackpotContribMaxBet < 0.0 || jackpotContribMaxBet > 1.0)
		{
			throw new MachineException ("invalid jackpot contrib");
		}

		this.jackpotName = jackpotName;
		this.jackpotBaseAmount = jackpotBaseAmount;
		this.jackpotContrib = jackpotContrib;
		this.jackpotContribMaxBet = jackpotContribMaxBet;
	}

	/**
	 * Get the jackpot name.
	 */
	public String getJackpotName ()
	{
		return jackpotName;
	}

	/**
	 * Return the base jackpot amount.
	 */
	public Money getJackpotBaseAmount ()
	{
		return jackpotBaseAmount == null ? new Money (0) : jackpotBaseAmount;
	}

	/**
	 * Return the amount contributed to the jackpot per credit bet.
	 */
	public double getJackpotContrib ()
	{
		return jackpotContrib;
	}

	/**
	 * Return the amount contributed to the jackpot per credit bet on
	 * a maximum bet.
	 */
	public double getJackpotContribMaxBet ()
	{
		return jackpotContribMaxBet;
	}

	/**
	 * Format as string.
	 */
	public String toString ()
	{
		return jackpotName + " " + jackpotBaseAmount + " " +
			   jackpotContrib + " " + jackpotContribMaxBet;
	}
}
