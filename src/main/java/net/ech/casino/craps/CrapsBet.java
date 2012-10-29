//
// CrapsBet.java  
// 

package net.ech.casino.craps;

/**
 * A CrapsBet represents a bet on a single position on the craps table.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class CrapsBet implements Constants
{
	private int type;
	private int amountBet;
	private int amountReturned;

	/**
	 * Constructor.
	 */
	public CrapsBet (int type, int amountBet, int amountReturned)
	{
		if (type < 0 || type >= NUM_BETS)
			throw new IllegalArgumentException ("type=" + type);

		this.type = type;
		this.amountBet = amountBet;
		this.amountReturned = amountReturned;
	}

	/**
	 * Get the type code of this bet
	 * @see net.ech.casino.craps.Constants
	 */
	public int getType ()
	{
		return type;
	}

	/**
	 * Get the name of this bet.
	 */
	public String getName ()
	{
		return BetNames[type];
	}

	/**
	 * Get the amount of this bet.
	 */
	public int getAmountBet ()
	{
		return amountBet;
	}

	/** 
	 * Get the amount returned (still on the table) for this bet.
	 */
	public int getAmountReturned ()
	{
		return amountReturned;
	}
}
