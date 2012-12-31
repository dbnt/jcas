//
// Spanish21.java  
// 

package net.ech.casino.blackjack;

import java.util.*;
import net.ech.casino.*;

/**
 * Spanish 21: the FUN is back
 * <br>
 * More ways to win
 * <br>
 * Same old Game - Great new Rules
 * <ul>
 * <li>
 * PLAYER BLACKJACK
 * <br>Always BEATS dealer's Blackjack - pays 3/2.
 * </li>
 * <li>
 * PLAYER TOTAL OF 21
 * <br>Always BEATS dealer's total of 21.
 * </li>
 * <li>
 * PAIR SPLITTING
 * <br>Player may split cards of equal value including ACES up to 4 hands -
 * hitting and doubling of split hands, including ACES is allowed.
 * </li>
 * <li>
 * DOUBLE DOWN
 * <br>On 2, 3, 4 or more cards, on any total - including after splitting.
 * <small>No bonuses on doubled hands.</small>
 * </li>
 * <li>
 * DOUBLE DOWN RESCUE
 * <br>After doubling if a player is dissatisfied with his non-busted hand,
 * he may rescue (take back) the DOUBLED portion of the bet, and forfeit the
 * original wager.
 * </li>
 * <li>
 * SURRENDER ALLOWED
 * </li>
 * </ul>
 * <p>
 * BONUS 21 PAYOFFS
 * <ul>
 * <li>5 - card 21 Pays 3/2</li>
 * <li>6 - card 21 Pays 2/1</li>
 * <li>7+ card 21 Pays 3/1</li>
 * <li>6-7-8 Mixed Pays 3/2</li>
 * <li>6-7-8 Suited Pays 2/1</li>
 * <li>6-7-8 Spaded Pays 3/1</li>
 * <li>7-7-7 Mixed Pays 3/2</li>
 * <li>7-7-7 Suited Pays 2/1</li>
 * <li>7-7-7 Spaded Pays 3/1</li>
 * </ul>
 * <p>Played with 6 Spanish Packs 2-9, J, Q, K, A
 *
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class Spanish21 extends BlackjackMachine
{
	/**
	 * Constructor.
	 */
	public Spanish21 ()
	{
		setDealerHitsSoft17 (true);
	}

	/**
	 * Create a new Shoe appropriate for a game of this type.
	 */
	public Shoe createNewShoe ()
	{
		// Shoe is composed of 6 "Spanish" decks.
		Shoe shoe = new Shoe (6, new SpanishDeck ());
		shoe.setCutRange (0.3, 0.7);
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
	 * Do the table rules allow the player to surrender the hand?
	 * @param playerHand		the current player hand
	 * @return whether the player may surrender
	 */
	public boolean playerMaySurrender (PlayerHand playerHand)
	{
		// May surrender after initial deal.
		if (super.playerMaySurrender (playerHand))
			return true;

		// May also surrender immediately after doubling down.
		// This is a "double down rescue."
		return playerHand.isDoubled ();
	}

	/**
	 * Assuming that the player may hit the current hand, do the table
	 * rules allow the player to double down on the given hand?
	 * @param playerHand		the current player hand
	 * @return whether the player may double down
	 */
	public boolean playerMayDoubleDown (PlayerHand playerHand)
	{
		return true;
	}

	/**
	 * Compare player hand and dealer score.
	 * @param playerHand		The player hand
	 * @param dealerScore		The dealer hand's score
	 * @return 1 for player win, 0 for push, -1 for dealer win.
	 */
	public int playerVersusDealer (PlayerHand playerHand, int dealerScore)
	{
		// PLAYER BLACKJACK always BEATS dealer's Blackjack.
		// PLAYER TOTAL OF 21 always BEATS dealer's total of 21.
		//
		int playerScore = playerHand.getScore ();
		switch (playerScore)
		{
		case 21:
		case BLACKJACK:
			if (playerScore == dealerScore)
				return 1;
		}

		return super.playerVersusDealer (playerHand, dealerScore);
	}

	/**
	 * Assuming that the given player hand is a winning hand, 
	 * calculate the bonus awarded the hand.  Base payout for a winning
	 * hand is 1-1.	 The bonus is any amount above and beyond that.
	 * @param	playerHand		The player's hand
	 * @return the bonus, as a multiple of the bet on the hand
	 */
	public double getBonus (PlayerHand playerHand, boolean isWin)
	{
		return super.getBonus (playerHand, isWin) +
			getSpanish21Bonus (playerHand); 
	}

	private double getSpanish21Bonus (PlayerHand playerHand)
	{
		if (playerHand.getScore () != 21)
			return 0;
		if (playerHand.isDoubled ())
			return 0;

		int size = playerHand.getSize ();
		if (size == 5)
			return 0.5;
		if (size == 6)
			return 1;
		if (size >= 7)
			return 2;

		if (size == 3)
		{
			int count6 = 0;
			int count7 = 0;

			for (int i = 0; i < 3; ++i)
			{
				switch (playerHand.getCardRank (i))
				{
				case 6: ++count6; break;
				case 7: ++count7; break;
				}
			}

			if ((count6 == 1 && count7 == 1) || count7 == 3)
			{
				int suit0 = playerHand.getCardSuit (0);
				boolean suited = playerHand.getCardSuit (1) == suit0 &&
								 playerHand.getCardSuit (2) == suit0;
				boolean spaded = suited && suit0 == Spades;
				if (spaded)
					return 2;
				if (suited)
					return 1;
				return 0.5;
			}
		}

		return 0;
	}
}
