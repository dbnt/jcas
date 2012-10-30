//
// Bet.java	 
// 

package net.ech.casino.roulette;

import net.ech.casino.Money;

/**
 * A Bet is an object to hold per-bet information.
 */
public class Bet
{
	private int amount;
	private long point;
	private double win;

	/**
	 * Constructor.
	 */
	public Bet (int amount, long point)
	{
		this.amount = amount;
		this.point = point;
		this.win = 0;
	}

	/**
	 * Get the bitmask that represents the selected betting position.
	 */
	public long getPoint ()
	{
		return point;
	}

	/**
	 * Get the official name of the selected betting position.
	 */
	public String getName ()
	{
		return Point.toString (point);
	}

	/**
	 * Get the amount of this bet.
	 */
	public int getBet ()
	{
		return amount;
	}

	/**
	 * Set the return on this bet.
	 */
	void setReturn (double win)
	{
		this.win = win;
	}

	/**
	 * Get the return this bet.
	 */
	public double getReturn ()
	{
		return win;
	}

	/**
	 * Get the return on this bet as a currency value.
	 */
	public Money getReturnMoney ()
	{
		return new Money (win);
	}

	/**
	 * Get the amount returned on this bet.
	 */
	public Money getWin ()
	{
		return getReturnMoney ();
	}

	/** 
	 * Format as a string.
	 */
	public String toString ()
	{
		return Point.toString (point) + ":" + amount;
	}
}
