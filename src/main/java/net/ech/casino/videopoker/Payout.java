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
public class Payout implements Constants
{
	private String winnerId;
	private String label;
	private int multiple;
	private int maxMultiple;
	private boolean isJackpot;

	/**
	 * Constructor.
	 * @param winnerId	  key to the win detection logic
	 * @param multiple	  payout multiple 
	 */
	public Payout (String winnerId, int multiple)
	{
		this (winnerId, multiple, multiple);
	}

	/**
	 * Constructor.
	 * @param winnerId	  key to the win detection logic
	 * @param multiple	  payout multiple for other than maximum bet
	 * @param maxMultiple payout multiple for maximum bet only
	 */
	public Payout (String winnerId, int multiple, int maxMultiple)
	{
		this.winnerId = winnerId;
		this.label = winnerId;
		this.multiple = multiple;
		this.maxMultiple = maxMultiple;
	}

	/**
	 * Get the key to this payout row's win detection logic.
	 * @see net.ech.casino.videopoker.Winner
	 */
	public String getWinnerId ()
	{
		return winnerId;
	}

	/**
	 * Get this payout row's label.	 Default is the winner id.
	 * @return this payout row's label.
	 */
	public String getLabel ()
	{
		return label;
	}

	/**
	 * Set this payout row's label.
	 */
	public void setLabel (String label)
	{
		this.label = label;
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
