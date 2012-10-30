//
// VideoPokerGame.java
//

package net.ech.casino.videopoker;

import java.util.*;
import net.ech.casino.*;

/**
 * Video poker game servlet.
 *
 * @author James Echmalian, ech@ech.net
 * @version 1.1
 */
public class VideoPokerGame extends CreditsGame implements Constants
{
	// Game state:
	private int state = DealState;
	private byte[] cards;
	private byte[] dealHand;
	private byte[] drawHand;
	private boolean[] holds;
	private int grade = NoGrade;
	private int doubleUpCount;
	private int chargeCount;

	/**
	 * Constructor.
	 */
	public VideoPokerGame (Casino casino, VideoPokerMachine machine)
	{
		super (casino, machine);
	}
	
	//==================================================================
	// PROPERTIES
	//==================================================================

	/**
	 * Get my machine.
	 */
	public VideoPokerMachine getVideoPokerMachine ()
	{
		return (VideoPokerMachine) getMachine ();
	}

	/**
	 * Get the current state of the game, which indicates what plays
	 * are legal.
	 * @return an integer code equal to one of the *State constants.
	 */
	public int getState ()
	{
		return state;
	}

	/**
	 * If the game is in DealState, find out whether the player may
	 * double up.
	 * @return true if double-up is currently offered.
	 */
	public boolean isDoubleUpOffered ()
	{
		return state == DealState && getWin () > 0 &&
			   doubleUpCount < getVideoPokerMachine ().getDoubleUpLimit ();
	}

	/**
	 * Get all the cards from the last deal.  May include more than
	 * the cards the player sees.
	 * @return an array of card values
	 */
	public byte[] getCards ()
	{
		return (byte[]) (cards == null ? null : cards.clone ());
	}

	/**
	 * Get all the cards from the last deal.  May include more than
	 * the cards the player sees.
	 * @return a string of rank/suit codes
	 */
	public String getCardString ()
	{
		return Card.toString (cards);
	}

	/**
	 * Get the five cards that were last dealt to the player in
	 * an initial deal.	 In the case of a regular
	 * hand, there are five cards.	In a double-up hand, there is
	 * one card.
	 * @return an array of card values
	 */
	public byte[] getDealHand ()
	{
		return (byte[]) dealHand.clone ();
	}

	/**
	 * Get the cards that were last dealt to the player in
	 * an initial deal, as a string.  In the case of a regular
	 * hand, there are five cards.	In a double-up hand, there is
	 * one card.
	 * @return an string of rank/suit codes, or null if no cards have been dealt
	 */
	public String getDealHandString ()
	{
		return Card.toString (dealHand);
	}

	/**
	 * Get the five cards that resulted from the player's most recent
	 * draw.
	 * @return an array of card values
	 */
	public byte[] getDrawHand ()
	{
		return (byte[]) (drawHand == null ? null : drawHand.clone ());
	}

	/**
	 * Get the five cards that resulted from the player's most recent draw,
	 * as a string.
	 * @return an string of rank/suit codes, or null if no cards have been dealt
	 */
	public String getDrawHandString ()
	{
		return Card.toString (drawHand);
	}

	/**
	 * Get the cards that the player sees.
	 * @return an array of card values, or null if no cards have been dealt
	 */
	public byte[] getHand ()
	{
		byte[] hand = state == DealState ? drawHand : dealHand;
		return (byte[]) (hand == null ? null : hand.clone ());
	}

	/**
	 * Get the cards the player sees, as a string.
	 * @return an string of rank/suit codes, or null if no cards have been dealt
	 */
	public String getHandString ()
	{
		byte[] hand = state == DealState ? drawHand : dealHand;
		return Card.toString (hand);
	}

	/**
	 * Get an array of booleans indicating which cards the player last
	 * held.
	 */
	public boolean[] getHolds ()
	{
		return (boolean[]) (holds == null ? null : holds.clone ());
	}

	/**
	 * Get a string indicating which cards the player last held.
	 * 'H' indicates a held card, ' ' indicates a drawn card.
	 */
	public String getHoldString ()
	{
		if (holds == null)
			return null;

		StringBuilder buf = new StringBuilder (5);

		for (int i = 0; i < CardsInHand; ++i)
			buf.append (holds[i] ? 'H' : ' ');

		return buf.toString ();
	}

	/**
	 * Get the grade of the last hand.	The grade is defined as the 
	 * index into the pay table rows, with zero as the top row.	  The 
	 * grade of a losing hand is -1. 
	 * @return the grade
	 */
	public int getGrade ()
	{
		return grade;
	}

	/**
	 * Return true if the current results are of a double-up round.
	 */
	public boolean isDoubleUpShowing ()
	{
		return doubleUpCount > 0;
	}

	/**
	 * Return true if this game is currently "charged."
	 */
	public boolean isCharged ()
	{
		return chargeCount > 0;
	}
	
	//==================================================================
	// METHODS
	//==================================================================

	/**
	 * Deal.
	 * @param player	the Player
	 * @param bet		bet amount in credits
	 */
	public synchronized void deal (Player player, int bet)
		throws CasinoException
	{
		new Deal (bet).play (player);
	}

	/**
	 * Draw.
	 * @param mask		a bit mask specifying the cards to
	 *					<strong>draw.</strong>	Low bit corresponds to
	 *					leftmost card.
	 * @exception IllegalPlayException if it is not player's turn to draw 
	 * @exception GameException if mask is invalid
	 * @exception AccountingException error accessing accounting database
	 */
	public void draw (Player player, int mask)
		throws CasinoException
	{
		if (mask < 0 || mask > 0x1f)
			throw new GameException ("invalid draw mask", this);

		// Convert draw mask to hold array
		boolean[] holds = new boolean [CardsInHand];
		for (int i = 0; i < CardsInHand; ++i)
			holds[i] = (mask & (1 << i)) == 0;

		new Draw (holds).play (player);
	}

	/**
	 * Draw.
	 * @param holds		an array of booleans specifying which cards to hold.
	 * @exception IllegalPlayException if it is not player's turn to draw 
	 * @exception GameException if holds array is invalid
	 * @exception AccountingException error accessing accounting database
	 */
	private synchronized void draw (Player player, boolean[] holds)
		throws CasinoException
	{
		new Draw ((boolean[]) holds.clone ()).play (player);
	}

	/**
	 * Let's play double-up!
	 */
	public synchronized void doubleUp (Player player)
		throws CasinoException
	{
		new DoubleUp ().play (player);
	}

	/**
	 * Finish a round of double-up.
	 */
	public synchronized void pick (Player player, int pick)
		throws CasinoException
	{
		new Pick (pick).play (player);
	}

	/**
	 * Return true if the indicated Player can legally quit the game.
	 */
	public synchronized boolean isQuitLegal (Player player)
	{
		return state == DealState;
	}

	/**
	 * Return the amount that the indicated Player should be credited
	 * if it leaves the game now.
	 */
	public synchronized Money getRedemptionAmount (Player player)
	{
		//
		// If game is closed in the middle of a winning hand, give the
		// player the winnings.	 If game is closed in the middle of 
		// a non-winning hand, refund the bet.
		//
		switch (state)
		{
		case DrawState:
			return creditsToMoney (Math.max (getBet (), computeWin ()));
		case PickState:
			if (Card.rankOf (cards[0]) != Ace)
				return getBetMoney ();
		}

		return Money.ZERO;
	}

	//==================================================================
	// Implementation
	//==================================================================

	//
	// Implement save/restore state methods common to all plays...
	//
	private class VideoPokerPlay extends CreditsGamePlay
	{
		private int oldState;
		private byte[] oldCards;
		private byte[] oldDealHand;
		private byte[] oldDrawHand;
		private boolean[] oldHolds;
		private int oldGrade;
		private int oldDoubleUpCount;
		private int oldChargeCount;

		protected void saveState ()
		{
			super.saveState ();
			oldState = state;
			oldCards = cards;
			oldDealHand = dealHand;
			oldDrawHand = drawHand;
			oldHolds = holds;
			oldGrade = grade;
			oldDoubleUpCount = doubleUpCount;
			oldChargeCount = chargeCount;
		}

		protected void restoreState ()
		{
			super.restoreState ();
			state = oldState;
			cards = oldCards;
			dealHand = oldDealHand;
			drawHand = oldDrawHand;
			holds = oldHolds;
			grade = oldGrade;
			doubleUpCount = oldDoubleUpCount;
			chargeCount = oldChargeCount;
		}
	}

	/**
	 * How to deal...
	 */
	private class Deal extends VideoPokerPlay
	{
		int bet;

		Deal (int bet)
		{
			this.bet = bet;
		}

		protected void validate ()
			throws GameException
		{
			if (state != DealState)
				throw new IllegalPlayException (VideoPokerGame.this);
			if (bet <= 0 || bet > getMaximumBet ())
				throw new GameException ("invalid bet amount", VideoPokerGame.this);
		}

		protected void computePlay ()
		{
			setBet (bet);
			setWin (0);

			// Deal new cards.
			cards = deal ();
			dealHand = copyCards (cards, CardsInHand);
			drawHand = null;
			holds = null;

			// Compute initial grade for player reference.
			grade = getVideoPokerMachine ().grade (dealHand,
												   VideoPokerGame.this);

			state = DrawState;
			doubleUpCount = 0;
		}

		protected void transact ()
			throws CasinoException
		{
			Transaction trans = new Transaction (VideoPokerGame.this);
			trans.setWagerAmount (getBetMoney ());
			getCasino().executeTransaction (trans);
		}
	}

	/**
	 * How to draw...
	 */
	private class Draw extends VideoPokerPlay
	{
		boolean[] newHolds;

		Draw (boolean[] newHolds)
		{
			this.newHolds = newHolds;
		}

		protected void validate ()
			throws GameException
		{
			if (state != DrawState)
				throw new IllegalPlayException (VideoPokerGame.this);
			if (newHolds == null || newHolds.length != CardsInHand)
				throw new GameException ("invalid hold array", VideoPokerGame.this);
		}

		protected void computePlay ()
		{
			// Draw the cards, grade them.
			holds = newHolds;
			draw ();

			VideoPokerMachine machine = getVideoPokerMachine ();
			grade = machine.grade (drawHand, VideoPokerGame.this);

			// Handle charged machines here
			if (machine.isChargedPayLine (grade))
				chargeCount = 0;
			else if (machine.isChargeablePayLine (grade))
				chargeCount = machine.getMaxCharge ();
			else if (chargeCount > 0)
				chargeCount--;	// charge leaking away

			setWin (computeWin ());
			state = DealState;
		}

		protected void transact ()
			throws CasinoException
		{
			Transaction trans = new Transaction (VideoPokerGame.this);
			if (getWin () > 0)
			{
				trans.setReturnAmount (getBetMoney ());
				trans.setWinAmount (getWinMoney ().subtract (getBetMoney ()));
			}

			getCasino().executeTransaction (trans);
		}
	}

	/**
	 * How to initiate double-up...
	 */
	private class DoubleUp extends VideoPokerPlay
	{
		protected void validate ()
			throws GameException
		{
			if (!isDoubleUpOffered ())
				throw new IllegalPlayException (VideoPokerGame.this);
		}

		protected void computePlay ()
		{
			setBet (getWin ());
			setWin (0);

			cards = deal (CardsInHand);

			// Show only the house's card.
			dealHand = new byte [] { cards[0] };
			drawHand = null;
			holds = null;

			// Update state to "waiting for pick".
			state = PickState;
			grade = NoGrade;
			++doubleUpCount;
		}

		protected void transact ()
			throws CasinoException
		{
			Transaction trans = new Transaction (VideoPokerGame.this);
			trans.setWagerAmount (getBetMoney ());
			getCasino().executeTransaction (trans);
		}
	}

	/**
	 * How to resolve double-up...
	 */
	private class Pick extends VideoPokerPlay
	{
		private int pick;

		Pick (int pick)
		{
			this.pick = pick;
		}

		protected void validate ()
			throws GameException
		{
			if (state != PickState)
				throw new IllegalPlayException (VideoPokerGame.this);
			if (pick <= 0 || pick >= CardsInHand)
				throw new GameException ("Invalid pick", VideoPokerGame.this);
		}

		protected void computePlay ()
		{
			// Show all 5 cards
			drawHand = cards;

			// Record the player's pick as a hold.
			holds = new boolean [CardsInHand];
			holds[pick] = true;

			// Figure the results of the hi/lo
			int win = 0;
			if (Card.rankOf (cards[pick]) > Card.rankOf (cards[0]))
				win = getBet () * 2;
			setWin (win);

			// Onward.
			state = DealState;
		}

		protected void transact ()
			throws CasinoException
		{
			Transaction trans = new Transaction (VideoPokerGame.this);
			if (getWin () > 0)
			{
				trans.setReturnAmount (getBetMoney ());
				trans.setWinAmount (getBetMoney ());
			}
			getCasino().executeTransaction (trans);
		}
	}

	/**
	 * Deal a new set of cards from the deck.
	 */
	protected byte[] deal ()
	{
		return deal (CardsInHand * 2);
	}

	private byte[] deal (int n)
	{
		int nCardsInDeck = getVideoPokerMachine ().getCardsInDeck ();
		Deck deck = new Deck (nCardsInDeck - CardsInStandardDeck);
		return deck.deal (n, getRandomizer ());
	}

	private static byte[] copyCards (byte[] cards, int n)
	{
		byte[] copy = new byte [n];

		for (int i = 0; i < n; ++i)
			copy[i] = cards[i];

		return copy;
	}

	private void draw ()
	{
		drawHand = new byte [CardsInHand];

		int cardIndex = CardsInHand;
		for (int i = 0; i < CardsInHand; ++i)
			drawHand[i] = holds[i] ? dealHand[i] : cards[cardIndex++];
	}

	private int computeWin ()
	{
		int win = 0;
		Payout payout = getVideoPokerMachine ().getPayout (grade);
		if (payout != null)
		{
			int bet = getBet ();
			int multiple = payout.getMultiple (bet == getMaximumBet ());
			win = bet * multiple;
		}
		return win;
	}
}
