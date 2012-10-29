//
// SuperFun21.java	
// 

package net.ech.casino.blackjack;

import java.util.*;
import net.ech.casino.*;

/**
 * Super Fun 21 is a blackjack variation found at lots of casinos in Las
 * Vegas.  There are lots of liberal rules offset by most blackjacks
 * paying only even money.
 *
 * Blackjack rules are followed with these specifics and changes:
 * 1.  The game is played with a single deck of cards.
 * 2.  Dealer hits a soft 17.
 * 3.  Player may double after a split.
 * 4.  Player may resplit up to four hands, including aces.
 * 5.  Player may hit and double down to split aces.
 * 6.  Player may double on any number of cards.
 * 7.  Player may take late surrender on any number of cards.
 * 8.  Player may surrender half of total bet after doubling.
 * 9.  A player hand totaling 20 or less, consisting of six cards or more,
 *	   except after doubling, automatically wins.
 * 10. A player hand of 21 points, consisting of 5 cards or more, except
 *	   after doubling, plays 2 to 1 instantly.
 * 11. A player blackjack always wins.
 * 12. A player blackjack in diamonds pays 2 to 1; all other blackjacks
 *	   pay even money.
 *
 * -The Wizard of Odds
 *
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class SuperFun21 extends BlackjackMachine
{
	/**
	 * Constructor.
	 */
	public SuperFun21 ()
	{
		setDealerHitsSoft17 (true);
	}

	/**
	 * Is insurance offered?
	 * @return whether insurance is offered
	 */
	public boolean playerMayBuyInsurance ()
	{
		// Oh, alright.
		return true;
	}

	/**
	 * Do the table rules allow the player to surrender the hand?
	 * @param playerHand		the current player hand
	 * @return whether the player may surrender
	 */
	public boolean playerMaySurrender (PlayerHand playerHand)
	{
		// Late surrender is allowed.
		return true;
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
	 * Compare player hand to dealer score.
	 * @param playerHand		The player hand
	 * @param dealerScore		The dealer hand's score
	 * @return 1 for player win, 0 for push, -1 for dealer win.
	 */
	public int playerVersusDealer (PlayerHand playerHand, int dealerScore)
	{
		switch (playerHand.getScore ())
		{
		case BUST:
			break;

		case 21:
			// A player hand of 21 points, consisting of 5 cards or more,
			// except after doubling, pays 2 to 1 instantly.
			//
			if (playerHand.getSize () >= 5 && !playerHand.isDoubled ())
			{
				return 1;
			}
			break;

		case BLACKJACK:
			// A player blackjack always wins.
			return 1;

		default:
			// A player hand totaling 20 or less, consisting of six cards 
			// or more, except after doubling, automatically wins.
			//
			if (playerHand.getSize () >= 6 && !playerHand.isDoubled ())
			{
				return 1;
			}
		}

		// Otherwise, default rules apply.
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
		switch (playerHand.getScore ())
		{
		case 21:
			// A player hand of 21 points, consisting of 5 cards or more,
			// except after doubling, pays 2 to 1 instantly.
			//
			if (playerHand.getSize () >= 5 && !playerHand.isDoubled ())
			{
				return 1.0;
			}
			break;

		case BLACKJACK:
			// A player blackjack in diamonds pays 2 to 1; all
			// other blackjacks pay even money.
			if (playerHand.getCardSuit (0) == Diamonds &&
				playerHand.getCardSuit (1) == Diamonds)
			{
				return 1.0;
			}
		}

		return 0;
	}
}
