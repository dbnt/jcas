//
// Payout.java	
// 

package net.ech.casino.videopoker;

/**
 * A Payout represents a row of a video poker payout table.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class Payout implements Constants, java.io.Serializable
{
	private String label;
	private int multiple;
	private int maxMultiple;
	private boolean isJackpot;

	/**
	 * Constructor.
	 */
	public Payout (String label, int multiple)
	{
		this (label, multiple, multiple);
	}

	/**
	 * Constructor.
	 */
	public Payout (String label, int multiple, int maxMultiple)
	{
		this.label = label;
		this.multiple = multiple;
		this.maxMultiple = maxMultiple;
	}

	/**
	 * Get this payout row's label, which (usually) identifies the 
	 * winning poker hand, and primarily serves as a key into the 
	 * Winner lookup table.
	 * @return this payout row's label.
	 */
	public String getLabel ()
	{
		return label;
	}

	/**
	 * Get the multiple of the bet to return to the player for getting
	 * the hand represented by this payout row.
	 * @param isMaxBet			True if the player bet the maximum on this hand.
	 * @return the multiple.
	 */
	public int getMultiple (boolean isMaxBet)
	{
		return isMaxBet ? maxMultiple : multiple;
	}
}
