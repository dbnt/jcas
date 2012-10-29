//
// CreditsMachine.java
//

package net.ech.casino;

/**
 * Base class for a type of machine that runs on credits rather
 * than dollar-value chips.	 Examples: slots, video poker, video
 * keno.  These types of machine also can have a progressive 
 * jackpot that builds with every play.
 *
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public abstract class CreditsMachine extends Machine
{
	private double dollarsPerCredit = 1.0;
	private String variant;
	private JackpotParameters jackpotParams;

	/**
	 * Constructor.	 
	 */
	public CreditsMachine ()
	{
	}

	/**
	 * Constructor.	 
	 * @param id		an id string for this machine
	 */
	public CreditsMachine (String id)
	{
		super (id);
	}

	//====================================================================
	// Properties
	//====================================================================

	/**
	 * Set dollars per credit.
	 */
	public void setDollarsPerCredit (double dollarsPerCredit)
	{
		this.dollarsPerCredit = dollarsPerCredit;
	}

	/**
	 * Get dollars per credit.
	 */
	public double getDollarsPerCredit ()
	{
		return dollarsPerCredit;
	}

	/**
	 * Set the variant string for this machine.	 The variant string
	 * is the "name of the game" (e.g., "Jacks or Better," "Instant
	 * Keno," "Gator Lake Slots").	This differs from the machine
	 * id in that many different machines can share the same
	 * variant.
	 */
	public void setVariant (String variant)
	{
		this.variant = variant;
	}

	/**
	 * Get the variant string for this machine.
	 */
	public String getVariant ()
	{
		return variant;
	}

	/**
	 * Set the parameters of this machine's progressive jackpot.
	 * @param jackpotName				The id of this machine's jackpot
	 * @param jackpotContrib			The amount per credit of a normal
	 *									bet contributed to the pot
	 * @param jackpotContribMaxBet		The amount per credit of a maximum
	 *									credit bet contributed to the pot
	 * @param jackpotBaseAmount			The constant base jackpot amount
	 */
	public void setJackpotParameters (String jackpotName,
									  double jackpotContrib,
									  double jackpotContribMaxBet,
									  Money jackpotBaseAmount)
		throws MachineException
	{
		jackpotParams = new JackpotParameters (jackpotName,
											   jackpotBaseAmount,
											   jackpotContrib,
											   jackpotContribMaxBet);
	}

	/**
	 * Set jackpot parameters.
	 */
	public void setJackpotParameters (JackpotParameters jp)
	{
		this.jackpotParams = jp;
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
		return jackpotParams == null ? null
									 : jackpotParams.getJackpotName ();
	}

	/**
	 * Get jackpot contribution factor for the given bet.
	 */
	public double getJackpotContrib (int bet)
	{
		if (jackpotParams == null)
			return 0;
		return bet == getMaximumBet () ? jackpotParams.getJackpotContribMaxBet ()
									   : jackpotParams.getJackpotContrib ();
	}

	//====================================================================
	// Methods
	//====================================================================

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
}
