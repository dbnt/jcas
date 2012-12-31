//
// CasinoBlackjack.java	 
// 

package net.ech.casino.blackjack;

import net.ech.casino.*;

/**
 * Class CasinoBlackjack expresses the rules of standard Nevada
 * casino-style blackjack.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class CasinoBlackjack extends BlackjackMachine
{
	private int howManyDecks = 1;
	private double minCut = 0.3;
	private double maxCut = 0.7;
	private boolean doubleAfterSplit = true;
	private int doubleRestrictMask = -1;

	//=================================================================
	// INITIALIZATION
	//=================================================================

	/**
	 * Constructor.	 Single-deck blackjack.
	 */
	public CasinoBlackjack ()
	{
	}

	/**
	 * Constructor.
	 * @param ndecks	Number of decks in a shoe.
	 */
	public CasinoBlackjack (int ndecks)
	{
		setNumberOfDecks (ndecks);
	}

	//=================================================================
	// Dynamic properties
	//=================================================================

	/**
	 * Get the number of decks in a shoe.
	 * @return	the number of decks
	 */
	public int getNumberOfDecks ()
	{
		return this.howManyDecks;
	}

	/**
	 * Set the minimum cut point.
	 */
	public void setMinimumCutFactor (double minCut)
	{
		if (minCut < 0 || minCut > 1)
			throw new IllegalArgumentException ("minCut=" + minCut);
		this.minCut = minCut;
	}

	/**
	 * Set the maximum cut point.
	 */
	public void setMaximumCutFactor (double maxCut)
	{
		if (maxCut < 0 || maxCut > 1)
			throw new IllegalArgumentException ("maxCut=" + maxCut);
		this.maxCut = maxCut;
	}

	/**
	 * Set the number of decks in a shoe.
	 * @param ndecks	the number of decks
	 */
	public void setNumberOfDecks (int ndecks)
	{
		if (ndecks < 1)
			throw new IllegalArgumentException ("ndecks=" + ndecks);
		this.howManyDecks = ndecks;
	}

	/**
	 * Control the ability of the player to double down after a split.
	 */
	public void setDoubleAfterSplit (boolean doubleAfterSplit)
	{
		this.doubleAfterSplit = doubleAfterSplit;
	}

	/**
	 * Set the rule for doubling down.
	 * @param when		one of the Constants.Double... values.
	 * @deprecated	use setTotalsEnablingDouble instead
	 */
	public void setDoubleDownWhen (int when)
	{
		// Backward compatibility:
		if (when == DoubleTwo9_11)
		{
			setTotalsEnablingDouble (new int[] { 9, 10, 11 });
		}
	}

	/**
	 * Restrict the player from doubling down unless the low total
	 * for the hand matches one of the listed values.
	 * @param totals   an array of enabling total values
	 * @see Hand#getLowTotal
	 */
	public void setTotalsEnablingDouble (int[] totals)
	{
		doubleRestrictMask = 0;

		// Convert to bit mask.
		for (int i = 0; i < totals.length; ++i)
		{
			doubleRestrictMask |= (1 << totals[i]);
		}
	}

	//=================================================================
	// Services for BlackjackGame.
	//=================================================================

	/**
	 * Create a new Shoe appropriate for a game of this type.
	 */
	public Shoe createNewShoe ()
	{
		Shoe shoe = new Shoe (howManyDecks);
		if (howManyDecks > 1)
		{
			shoe.setCutRange (minCut, maxCut);
		}
		return shoe;
	}

	/**
	 * Is insurance offered?
	 * @return whether insurance is offered
	 */
	public boolean playerMayBuyInsurance ()
	{
		return true;
	}

	/**
	 * Do the table rules allow the player hand to be hit? 
	 * @param playerHand		the current player hand
	 * @return whether the hand may be hit
	 */
	public boolean playerMayHit (PlayerHand playerHand)
	{
		// Extended implementation: player may not hit a split ace hand.
		return super.playerMayHit (playerHand) &&
			!(playerHand.isSplit() && playerHand.getCardRank (0) == Ace);
	}

	/**
	 * Assuming that the player may hit the current hand, do the table
	 * rules allow the player to double down on the given hand?
	 * @param playerHand		the current player hand
	 * @return whether the player may double down
	 */
	public boolean playerMayDoubleDown (PlayerHand playerHand)
	{
		return
			// Must have two cards in hand.
			(playerHand.getSize () == 2) &&

			// May double after split only if configured to allow it.
			(doubleAfterSplit || !playerHand.isSplit()) &&

			// Some restrictions based on score may apply!
			(doubleRestrictMask == -1 ||
			  (doubleRestrictMask & (1<<playerHand.getLowTotal())) != 0);
	}
}
