package net.ech.casino.videopoker;

import net.ech.casino.Money;

/**
 * Game state bean.
 */
public class VideoPokerState
	implements Cloneable
{
	public enum Action
	{
		DEAL,
		DRAW,
		ACCEPT_BONUS
	}

	private VideoPokerMachine machine;
	private Money creditValue;
	private int wagerCredits;
	private int winCredits;
	private String cards;
	private String hand;
	private String holds;
	private int grade = -1;
	private boolean charged;
	private Action pendingAction;

	/**
	 * Rules governing hand evaluation and payout.
	 */
	public VideoPokerMachine getMachine ()
	{
		return machine;
	}

	public void setMachine(VideoPokerMachine machine)
	{
		this.machine = machine;
	}

	/**
	 * The value of a credit.
	 */
	public Money getCreditValue()
	{
		return creditValue;
	}

	public void setCreditValue(Money creditValue)
	{
		this.creditValue = creditValue;
	}

	/**
	 * The current wager.
	 */
	public int getWagerCredits()
	{
		return wagerCredits;
	}

	public void setWagerCredits(int creditValue)
	{
		this.wagerCredits = wagerCredits;
	}

	/**
	 * The current "win".
	 */
	public int getWinCredits()
	{
		return wagerCredits;
	}

	public void setWinCredits(int winCredits)
	{
		this.winCredits = winCredits;
	}

	/**
	 * The cards from the last deal.  May include cards the player
	 * has not seen!
	 */
	public String getCards()
	{
		return cards;
	}

	public void setCards(String cards)
	{
		this.cards = cards;
	}

	/**
	 * The cards that were last dealt to the player.
	 */
	public String getHand ()
	{
		return hand;
	}

	public void setHand(String hand)
	{
		this.hand = hand;
	}

	/**
	 * A string of H's and spaces identifying which of the currently displayed
	 * cards the player held.
	 */
	public String holds ()
	{
		return holds;
	}

	public void setHolds(String holds)
	{
		this.holds = holds;
	}

	/**
	 * The grade of the current hand.	The grade is defined as the 
	 * index into the pay table rows, with zero as the top row.	  The 
	 * grade of a losing hand is -1. 
	 */
	public int getGrade ()
	{
		return grade;
	}

	public void setGrade(int grade)
	{
		this.grade = grade;
	}

	/**
	 * True if this game is currently "charged."  (For Shockwave)
	 */
	public boolean isCharged ()
	{
		return charged;
	}

	public void setCharged(boolean charged)
	{
		this.charged = charged;
	}

	/**
	 * The pending action.
	 */
	public Action getPendingAction ()
	{
		return pendingAction;
	}

	public void setPendingAction(Action pendingAction)
	{
		this.pendingAction = pendingAction;
	}

	public VideoPokerState copy()
	{
		try {
			return (VideoPokerState) clone();
		}
		catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
}
