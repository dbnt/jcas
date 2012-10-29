//
// Lucky777.java  
// 

package net.ech.casino.blackjack;

import net.ech.casino.*;

/**
 * Lucky 7's blackjack.
 *
 * Casino-style blackjack, plus a bonus payout for a hand consisting 
 * of three sevens.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class Lucky777 extends CasinoBlackjack
{
	private int bonus = 5;

	//=================================================================
	// INITIALIZATION
	//=================================================================

	/**
	 * Constructor.	 Single-deck blackjack.
	 */
	public Lucky777 ()
	{
	}

	/**
	 * Constructor.
	 * @param ndecks	Number of decks in a shoe.
	 */
	public Lucky777 (int ndecks)
	{
		super (ndecks);
	}

	/**
	 * Set the bonus factor for a triple 7.
	 */
	public void setBonusFactor (int bonus)
	{
		this.bonus = bonus;
	}

	/**
	 * Assuming that the given player hand is a winning hand, 
	 * calculate the bonus awarded the hand.  Base payout for a winning
	 * hand is 1-1.	 The bonus is any amount above and beyond that.
	 * Lucky 7's bonus: receive the bonus factor on a triple-7 hand.
	 * Receive the bonus factor plus one on a suited triple-7 hand.
	 * @param	playerHand		The player's hand
	 * @return the bonus, as a multiple of the bet on the hand
	 */
	public double getBonus (PlayerHand playerHand, boolean isWin)
	{
		return super.getBonus (playerHand, isWin) +
					getLucky777Bonus (playerHand); 
	}

	private double getLucky777Bonus (PlayerHand hand)
	{
		if (hand.getSize () == 3 && hand.getCardRank (0) == 7 &&
			hand.getCardRank (1) == 7 && hand.getCardRank (2) == 7)
		{
			int suit0 = hand.getCardSuit (0);
			boolean suited = hand.getCardSuit (1) == suit0 &&
							 hand.getCardSuit (2) == suit0;
			return bonus + (suited ? 1 : 0);
		}

		return 0;
	}
}
