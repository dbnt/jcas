//
// RedDogModel.java
//

package net.ech.casino.reddog;

import net.ech.casino.*;

/**
 * RedDogModel encapsulates the state of a game of Red Dog.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class RedDogModel extends RedDogTable
{
	// Warning: copy method is not based on clone()!  Must copy 
	// individual instance variables.

	// Current state:
	private int state = TABLE_CLEAR;

	// The shoe:
	private Shoe shoe;
	
	// The cards.
	private byte[] cards;

	// The current pay factor.
	private int payFactor;

	// The player seats.
	private SeatImpl[] seats;
	
	/**
	 * Constructor.
	 */
	public RedDogModel (RedDogMachine machine)
	{
		// Initialize shoe.
		shoe = machine.createNewShoe();

		cards = new byte [MAXIMUM_CARDS_PER_HAND];

		// Initialize seats.
		seats = new SeatImpl [machine.getNumberOfSeats()];
		for (int i = 0; i < seats.length; ++i)
		{
			seats[i] = new SeatImpl ();
		}
	}

	/**
	 * Set the shoe (for testing).
	 */
	void setShoe (Shoe shoe)
	{
		this.shoe = shoe;
	}

	//=======================================================================
	// RedDogTable interface
	//=======================================================================

	/**
	 * Return the current table state.
	 * @return one of the TABLE_* constants.
	 * @see Constants
	 */
	public int getState ()
	{
		return state;
	}

	/**
	 * Get a card.	There are two or three dealt per hand.
	 * @param index one of the CARD_* constants
	 * @return the card value, or NilCard if there is no card in that position
	 */
	public byte getCard (int index)
	{
		return cards[index];
	}

	/**
	 * Get the current spread.	Valid only if there are cards on the table.
	 * @return the number of card values between the first two cards
	 * (non-inclusive) or -1 if the card ranks are equal.
	 */
	public int getSpread ()
	{
		return Math.abs (Card.rankOf (cards[CARD_LEFT]) -
						 Card.rankOf (cards[CARD_RIGHT])) - 1;
	}

	/**
	 * Return true if there are three cards on the table and, in the case of
	 * a positive spread, the center card lies between the left and right
	 * cards in rank, or in the case of a tie, all three cards are equal in
	 * rank.
	 */
	public boolean isWin ()
	{
		return is3WayTie() || isCatch();
	}

	/**
	 * Get the number of seats.
	 */
	public int getNumberOfSeats ()
	{
		return seats.length;
	}

	/**
	 * Get the model for the indexed seat.
	 */
	public RedDogSeat getSeat (int index)
	{
		return seats[index];
	}

	//=======================================================================
	// Copying
	//=======================================================================

	/**
	 * The copy method.
	 */
	public RedDogModel copy ()
	{
		return new RedDogModel (this);
	}
	
	/**
	 * Constructor.
	 */
	private RedDogModel (RedDogModel that)
	{
		this.state = that.state;
		this.shoe = that.shoe.copy();
		this.cards = (byte[]) that.cards.clone();
		this.payFactor = that.payFactor;
		this.seats = that.copySeats ();
	}

	private SeatImpl[] copySeats ()
	{
		SeatImpl[] newSeats = new SeatImpl [seats.length];

		for (int i = 0; i < newSeats.length; ++i)
		{
			newSeats[i] = new SeatImpl (seats[i]);
		}

		return newSeats;
	}

	//=======================================================================
	// Dealer methods. 
	//=======================================================================

	/**
	 * Remove all of the cards from the table.
	 */
	void sweepCards ()
	{
		for (int i = 0; i < cards.length; ++i)
		{
			cards[i] = NilCard;
		}
	}

	/**
	 * Deal one card to each player, and one to the dealer.
	 */
	void dealInitial (Randomizer randomizer)
	{
		// Shuffle the shoe if necessary.
		shoe.shuffleIfPending (randomizer);

		cards[CARD_LEFT] = shoe.draw();
		cards[CARD_RIGHT] = shoe.draw();
	}

	/**
	 * Deal the third card.
	 */
	void dealThirdCard ()
	{
		// This is the one that counts.
		cards[2] = shoe.draw();
	}

	/**
	 * Return true if all three cards are dealt and there is a 3-way
	 * tie.
	 */
	boolean is3WayTie ()
	{
		boolean tie = false;
		if (getCard (CARD_CENTER) != NilCard)
		{
			int rankLeft = Card.rankOf (getCard (CARD_LEFT));
			int rankRight = Card.rankOf (getCard (CARD_RIGHT));
			int rankCenter = Card.rankOf (getCard (CARD_CENTER));
			return rankCenter == rankLeft && rankCenter == rankRight;
		}

		return tie;
	}

	/**
	 * Return true if all three cards are dealt and the outer cards
	 * "catch" the center card.
	 */
	boolean isCatch ()
	{
		int rankLeft = Card.rankOf (getCard (CARD_LEFT));
		int rankRight = Card.rankOf (getCard (CARD_RIGHT));
		int rankCenter = Card.rankOf (getCard (CARD_CENTER));

		// Check for a catch.
		int minRank = Math.min (rankLeft, rankRight);
		int maxRank = Math.max (rankLeft, rankRight);
		return rankCenter > minRank && rankCenter < maxRank;
	}

	//=======================================================================
	// Betting methods.
	//=======================================================================

	/**
	 * Set the table state.
	 */
	void setState (int state)
	{
		this.state = state;
	}

	/**
	 * Reset seat state for new hand or quitting player.
	 */
	void resetSeat (int seatIndex, boolean hasPlayer)
	{
		seats[seatIndex].state = hasPlayer ? SEAT_READY : SEAT_OUT;
	}

	/**
	 * Player antes up.
	 */
	void showAnte (int seatIndex, Bet anteBet)
	{
		// FOR NOW, table automatically clears itself the first time a
		// player antes up for the next hand.
		//
		if (state == TABLE_END_OF_HAND)
		{
			clearAllBets();
			sweepCards();
		}

		seats[seatIndex].state = SEAT_BLOCKED;
		seats[seatIndex].anteBet = anteBet;
		seats[seatIndex].raise = null;
		seats[seatIndex].take = null;
	}

	/**
	 * Clear bets preparation for the next deal.
	 */
	private void clearAllBets ()
	{
		payFactor = 0;

		for (int seatIndex = 0; seatIndex < seats.length; ++seatIndex)
		{
			seats[seatIndex].anteBet = null;
			seats[seatIndex].raise = null;
			seats[seatIndex].take = null;
		}
	}

	/**
	 * The player at the indexed seat continues.  Raise is optional.
	 */
	void showContinue (int seatIndex, Money raise)
	{
		// ante stays as is.
		seats[seatIndex].state = SEAT_BLOCKED;
		seats[seatIndex].raise = raise;
	}

	/**
	 * Show some winnings.
	 */
	void showTake (int seatIndex, Money take)
	{
		seats[seatIndex].take = take;
	}

	/**
	 * Private implementation of a seat at the table.
	 */
	private static class SeatImpl extends RedDogSeat
		implements java.io.Serializable
	{
		int state = SEAT_OUT;
		Bet anteBet;
		Money raise;
		Money take;

		SeatImpl ()
		{
		}

		SeatImpl (SeatImpl that)
		{
			this.state = that.state;
			this.anteBet = that.anteBet;
			this.raise = that.raise;
			this.take = that.take;
		}

		//================================================================
		// RedDogSeat interface
		//================================================================

		public int getState ()
		{
			return state;
		}

		public Bet getAnteBet ()
		{
			return anteBet;
		}

		public Bet getRaiseBet ()
		{
			return raise == null ? null : new Bet (raise, anteBet.getPurse());
		}

		public Bet getTotalBet ()
		{
			if (anteBet == null)
				return null;
			if (raise == null)
				return anteBet;
			return new Bet (anteBet.getAmount().add(raise), anteBet.getPurse());
		}

		public Money getTake ()
		{
			return take;
		}
	}
}
