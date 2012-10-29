//
// PlayerHand.java	
// 

package net.ech.casino.blackjack;

import net.ech.casino.Money;

/**
 * Each player's hand has an associated bet amount and other attributes.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class PlayerHand extends Hand
{
	private BlackjackGame myGame;
	private int id;
	private boolean surrendered;
	private boolean doubled;
	private boolean split;
	private boolean closed;
	private double win;
	private int vsDealer;

	/**
	 * Constructor.
	 */
	public PlayerHand (BlackjackGame myGame)
	{
		this.myGame = myGame;
	}

	/**
	 * Constructor for split hands only.
	 */
	private PlayerHand (BlackjackGame myGame, int id, byte card)
	{
		this (myGame);
		this.id = id;
		split = true;
		hit (card);
	}

	/**
	 * Get the hand id.	 The first hand dealt to the player has id
	 * zero.  If the hand is split, the new hand created gets the
	 * next available id.
	 */
	public int getId ()
	{
		return id;
	}

	/**
	 * Get the amount bet on this hand.
	 */
	public int getBet ()
	{
		int bet = myGame.getOriginalBet ();
		if (doubled)
			bet *= 2;
		return bet;
	}

	/**
	 * Get the score of this hand.
	 * @return a score between 0 and 22.  Zero indicates bust or surrender.
	 * BLACKJACK indicates blackjack.
	 */
	public int getScore ()
	{
		if (surrendered)
			return 0;

		int score = super.getScore ();

		if (split && score == BLACKJACK)
			score = 21;
		return score;
	}

	/**
	 * @return true if this hand was surrendered.
	 */
	public boolean isSurrendered ()
	{
		return surrendered;
	}

	/**
	 * @return true if this hand was doubled.
	 */
	public boolean isDoubled ()
	{
		return doubled;
	}

	/**
	 * @return true if this hand was the result of a split.
	 */
	public boolean isSplit ()
	{
		return split;
	}

	/**
	 * Get a label that describes the score of this hand for the player.
	 */
	public String getScoreLabel ()
	{
		if (surrendered)
			return "";
		return super.getScoreLabel (closed || doubled);
	}

	/**
	 * Get the result of this hand, compared with the dealer's.
	 * @return 1 if player wins, -1 if dealer wins, 0 for push.
	 */
	public int getVersusDealer ()
	{
		return vsDealer;
	}

	/**
	 * Get the win amount in dollars.
	 */
	public double getReturn ()
	{
		return win;
	}

	/**
	 * Get the win amount in Money.
	 */
	public Money getReturnMoney ()
	{
		return new Money (win);
	}

	/**
	 * Get the win amount in Money.
	 */
	public Money getWin ()
	{
		return getReturnMoney ();
	}

	/**
	 * Surrender this hand.
	 */
	void surrender ()
	{
		surrendered = true;
	}

	/**
	 * Double this hand.
	 */
	void doubleDown ()
	{
		doubled = true;
	}

	/**
	 * Split this hand.
	 * @return two new player hands.
	 */
	PlayerHand[] split ()
	{
		int nextId = myGame.getNumberOfPlayerHands ();
		return new PlayerHand[]
		{
			new PlayerHand (myGame, id, getCard (0)),
			new PlayerHand (myGame, nextId, getCard (1))
		};
	}

	/**
	 * Close this hand, storing final results.
	 */
	void close (int vsDealer, double win)
	{
		this.closed = true;
		this.vsDealer = vsDealer;
		this.win = win;
	}
}
