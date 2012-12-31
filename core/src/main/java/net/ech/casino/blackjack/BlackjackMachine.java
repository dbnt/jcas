//
// BlackjackMachine.java  
// 

package net.ech.casino.blackjack;

import net.ech.casino.*;

/**
 * Class BlackjackMachine defines the interface between the general
 * blackjack/21 game player and the type-specific table parameters
 * and functions.
 *
 * All functions have default implementations.	However, this machine
 * as a whole does not correspond to any standard game of blackjack/21.
 * Subclasses do.  Therefore, this class is declared abstract.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public abstract class BlackjackMachine extends TableMachine
	implements Constants
{
	private int maxHands = 4;	// arbitrary default.
	private boolean dealerHitsSoft17 = false;
	private boolean splitAnyTen = false;

	/**
	 * Load a blackjack machine dynamically.
	 */
	public static BlackjackMachine forName (String name)
		throws Exception
	{
		Class mclass;

		try
		{
			mclass = Class.forName (name);
		}
		catch (Exception e)
		{
			try
			{
				mclass = Class.forName ("net.ech.casino.blackjack." + name);
			}
			catch (Exception e2)
			{
				throw e;
			}
		}

		return (BlackjackMachine) mclass.newInstance();
	}

	//=================================================================
	// Dynamic properties
	//=================================================================

	/**
	 * Set the maximum number of hands a player may play by splitting.
	 */
	public void setMaximumHands (int maxHands)
	{
		// BlackjackGame uses maxHands for allocation, so watch out!
		if (maxHands < 1 || maxHands > 100)
			throw new IllegalArgumentException ("maxHands=" + maxHands);
		this.maxHands = maxHands;
	}

	/**
	 * Set whether the table treats all 10s and face cards as
	 * the same denomination for the purpose of splitting hands.
	 * @param splitAnyTen		true if any player may split any
	 *							pair of 10s and/or face cards
	 */
	public void setSplitAnyTen (boolean splitAnyTen)
	{
		this.splitAnyTen = splitAnyTen;
	}

	/**
	 * Set whether the dealer must hit a "soft" 17.
	 * @param hitsSoft17		true if dealer hits soft 17
	 */
	public void setDealerHitsSoft17 (boolean hitsSoft17)
	{
		this.dealerHitsSoft17 = hitsSoft17;
	}

	//=================================================================
	// Game creation
	//=================================================================

	/**
	 * Create a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public BlackjackGame createBlackjackGame (Casino casino)
	{
		return new BlackjackGame (casino, this);
	}

	/**
	 * Create a new game.
	 * @return a new game handler to manage the game in progress.
	 */
	public Game createGame (Casino casino)
	{
		return createBlackjackGame (casino);
	}

	//=================================================================
	// Services for BlackjackGame.
	//=================================================================

	/**
	 * Create a new Shoe appropriate for a game of this type.
	 */
	public Shoe createNewShoe ()
	{
		// Default implementation: single standard deck.
		return new Shoe (1);
	}

	/**
	 * @return the maximum number of hands a player may play (greater
	 * than one if splitting or multiple hands allowed)
	 */
	public int getMaximumHands ()
	{
		return maxHands;
	}

	/**
	 * Is insurance offered?
	 * @return whether insurance is offered
	 */
	public boolean playerMayBuyInsurance ()
	{
		// Default implementation: no insurance offered, ever.
		return false;
	}

	/**
	 * Do the table rules allow the player hand to be hit? 
	 * @param playerHand		the current player hand
	 * @return whether the hand may be hit
	 */
	public boolean playerMayHit (PlayerHand playerHand)
	{
		// Default implementation:
		// No additional hits on a doubled hand.
		//
		return !playerHand.isDoubled ();
	}

	/**
	 * Do the table rules allow the player to surrender the hand?
	 * @param playerHand		the current player hand
	 * @return whether the player may surrender
	 */
	public boolean playerMaySurrender (PlayerHand playerHand)
	{
		// Default implementation: may surrender after initial deal.
		return !playerHand.isSplit () && playerHand.getSize () == 2;
	}

	/**
	 * Assuming that the player may hit the current hand and is not already
	 * playing the maximum number of hands allowed, do the table rules
	 * allow the player to split the given hand?
	 * @param playerHand		the current player hand
	 * @return whether the hand may be split
	 */
	public boolean playerMaySplit (PlayerHand playerHand)
	{
		// Default implementation: may split any pair.
		// Definition of "pair" depends on splitAnyTen property.

		if (playerHand.getSize () != 2)
			return false;

		byte pcard0 = playerHand.getCard (0);
		byte pcard1 = playerHand.getCard (1);

		if (splitAnyTen)
		{
			return Card.faceValueOf (pcard0) == Card.faceValueOf (pcard1);
		}
		else
		{
			return Card.rankOf (pcard0) == Card.rankOf (pcard1);
		}
	}

	/**
	 * Assuming that the player may hit the current hand, do the table
	 * rules allow the player to double down on the given hand?
	 * @param playerHand		the current player hand
	 * @return whether the player may double down
	 */
	public boolean playerMayDoubleDown (PlayerHand playerHand)
	{
		// Default implementation: may double on any two card hand.
		return playerHand.getSize () == 2;
	}

	/**
	 * Must the dealer take a hit, according to the table rules?
	 * @param dealerHand		the current dealer hand.
	 * @return whether dealer takes a hit.
	 */
	public boolean dealerMustHit (Hand dealerHand)
	{
		// Default implementation: hit up to 17.
		// Hit soft 17 or not based on dynamic table setting.

		int highTotal = dealerHand.getHighTotal ();
		if (highTotal > 21)
			return false;

		if (dealerHitsSoft17)
		{
			if (highTotal > 17)
				return false;
			return dealerHand.getLowTotal () < 17;
		}
		else
		{
			return highTotal < 17;
		}
	}

	/**
	 * Compare player hand and dealer score.
	 * @param playerHand		The player hand
	 * @param dealerScore		The dealer hand's score
	 * @return 1 for player win, 0 for push, -1 for dealer win.
	 */
	public int playerVersusDealer (PlayerHand playerHand, int dealerScore)
	{
		//
		// If player busts or surrenders, he loses, regardless of whether
		// the dealer busts.  Mutual bust can happen only in the case of
		// a split in which the player busts some hands and does not bust
		// others.
		//
		int playerScore = playerHand.getScore ();
		if (playerScore == BUST)
			return -1;

		if (dealerScore > playerScore)
			return -1;

		if (dealerScore == playerScore)
			return 0;

		return 1;
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
		// Default implementation: winning player blackjack pays 2.5-1.
		return (isWin && playerHand.getScore () == BLACKJACK) ? 0.5 : 0.0;
	}
}
