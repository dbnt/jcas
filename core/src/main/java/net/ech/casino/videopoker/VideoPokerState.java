package net.ech.casino.videopoker;

/**
 * Game state bean.
 */
public class VideoPokerState
	implement Cloneable
{
	public enum Action
	{
		DEAL = 0,
		DRAW = 1,
		ACCEPT_BONUS = 2
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

	public void setHand()
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
}
