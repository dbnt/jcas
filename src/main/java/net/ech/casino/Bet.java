//
// Bet.java	 
// 

package net.ech.casino;

/**
 * A general description of a bet.
 * A Bet is immutable.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class Bet
	implements java.io.Serializable, java.lang.Cloneable
{
	private Money amount;
	private String purseId;

	/**
	 * Constructor.
	 */
	public Bet (double amount, String purseId)
	{
		this (new Money (amount), purseId);
	}

	/**
	 * Constructor.
	 */
	public Bet (Money amount, String purseId)
	{
		if (purseId == null)
		{
			throw new IllegalArgumentException ("null");
		}

		this.amount = Money.materialize (amount);
		this.purseId = purseId;
	}

	/**
	 * Get the amount.
	 * @return a Money object, never null.
	 */
	public Money getAmount ()
	{
		return amount;
	}

	/**
	 * Get the purse id.
	 * @return a purse id string, never null
	 */
	public String getPurse ()
	{
		return purseId;
	}

	/**
	 * Format as String.
	 */
	public String toString ()
	{
		StringBuilder buf = new StringBuilder ();
		buf.append ("Bet(");
		buf.append (amount);
		if (purseId.length() > 0)
		{
			buf.append (",");
			buf.append (purseId);
		}
		buf.append (")");
		return buf.toString ();
	}

	/**
	 * Return true if that is a Bet with value equals to this one.
	 */
	public boolean equals (Object that)
	{
		if (that == null)
			return false;
		if (!(that instanceof Bet))
			return false;
		Bet thatBet = (Bet) that;
		return amount.equals (thatBet.amount) &&
			purseId.equals (thatBet.purseId);
	}

	/**
	 * Convenience method.	Checks that the Bet is null and contains a
	 * positive amount.
	 * @return true if the Bet is null or does not contain a postivie amount.
	 */
	public static boolean isEmpty (Bet bet)
	{
		return bet == null || bet.getAmount() == null ||
			   bet.getAmount().signum() <= 0;
	}
}
