//
// JackpotTransaction.java	
// 

package net.ech.casino;

/**
 * A JackpotTransaction represents a change to a shared jackpot
 * value.  The transaction may entail either a "contribution", a 
 * "claim", or both.  The amount of a contribution is a non-negative 
 * dollar amount to be added to the identified jackpot.	 The
 * amount of a claim is the non-negative fraction of the identified
 * jackpot to be awarded to the player.	 (This value is usually
 * 1.0).
 *
 * @see net.ech.casino.Transaction
 * @see net.ech.casino.Session
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class JackpotTransaction implements java.io.Serializable
{
	private String jackpotName;
	private double contributionAmount;
	private double claimFactor;

	private final static double EPSILON = 0.0000001;

	/**
	 * Default constructor.
	 */
	public JackpotTransaction ()
	{
	}

	/**
	 * Constructor.	 Takes name of jackpot.
	 * @param jackpotName	the jackpot name
	 */
	public JackpotTransaction (String jackpotName)
	{
		this.jackpotName = jackpotName;
	}

	/**
	 * Set the jackpot name.
	 * @param jackpotName	the jackpot name
	 */
	public void setJackpotName (String jackpotName)
	{
		this.jackpotName = jackpotName;
	}

	/**
	 * Get the jackpot name.
	 * @return the jackpot name
	 */
	public String getJackpotName ()
	{
		return jackpotName;
	}

	/**
	 * Specify the amount to contribute to the jackpot in this 
	 * transaction.
	 * @param contributionAmount   a money value with arbitrary precision
	 */
	public void setContributionAmount (double contributionAmount)
	{
		this.contributionAmount = contributionAmount;
	}

	/**
	 * Return true if there is any jackpot contribution in this transaction.
	 * @return true if the contributionAmount is significantly greater than zero
	 */
	public boolean isContribution ()
	{
		return contributionAmount > EPSILON;
	}

	/**
	 * Get the amount to contribute to the jackpot in this transaction.
	 * @return	 a money value with arbitrary precision
	 */
	public double getContributionAmount ()
	{
		return contributionAmount;
	}

	/**
	 * Set the claim factor.  This value is usually either zero, indicating
	 * that no jackpot is claimed, or one, indicating that the jackpot is
	 * claimed.	 Some games, such as Caribbean Stud Poker, may request
	 * partial jackpots, in which case the claim factor is between zero and
	 * one.
	 * @param claimFactor the claim factor, a number between zero and one
	 */
	public void setClaimFactor (double claimFactor)
	{
		if (claimFactor > 1.0)
		{
			throw new IllegalArgumentException("claimFactor=" + claimFactor);
		}
		this.claimFactor = claimFactor;
	}

	/**
	 * Return true if there is any jackpot claim in this transaction.
	 * @return true if claimFactor is significantly greater than zero.
	 */
	public boolean isClaim()
	{
		return claimFactor > EPSILON;
	}

	/**
	 * Return the jackpot claim factor.
	 * @return a number between zero and one
	 */
	public double getClaimFactor ()
	{
		return claimFactor;
	}
}
