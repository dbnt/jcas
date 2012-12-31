//
// WarModel.java
//

package net.ech.casino.war;

import net.ech.casino.*;

/**
 * WarModel encapsulates the state of a game of Casino War.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class WarModel
	extends WarTable
{
	// Warning: copy method is not based on clone()!  Must copy 
	// individual instance variables.

	// Hand id number
	private int roundCount;

	// The shoe:
	private Shoe shoe;
	
	// The dealer's cards.
	private byte[] dealerCards;

	// The player seats.
	private SeatImpl[] seats;
	
	/**
	 * Constructor.
	 */
	public WarModel (WarMachine machine)
	{
		// Initialize shoe.
		shoe = machine.createNewShoe();

		dealerCards = new byte [MAX_DEALER_CARDS];

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
	// Properties 
	//=======================================================================

	/**
	 * Get a number that identifies the hand in play.
	 * This number begins at zero and increments with the start of
	 * each new hand.  A round of zero indicates that no plays have yet
	 * been made.  
	 * @return the round counter
	 */
	public int getRoundCount()
	{
		return roundCount;
	}

	/**
	 * Get a dealer card.
	 * @param index one of the DEALER_CARD_ constants.
	 * @return the card value, or NilCard if index is invalid.
	 * @see net.ech.casino.war.Constants
	 */
	public byte getDealerCard (int index)
	{
		try
		{
			return dealerCards[index];
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			// We promised.
			return NilCard;
		}
	}

	/**
	 * Get the number of seats.
	 */
	public int getSeatCount ()
	{
		return seats.length;
	}

	/**
	 * Get the model for the indexed seat.
	 */
	public WarSeat getSeat (int index)
	{
		return seats[index];
	}

	/**
	 * Shortcut method.	 Returns true if the the indexed player's initial
	 * card and the dealer's are tied
	 */
	public boolean isTieAt (int seatIndex)
	{
		byte pCard = seats[seatIndex].getPlayerCard (PLAYER_CARD_INITIAL);
		byte dCard = dealerCards[DEALER_CARD_INITIAL];
		return pCard != NilCard && Card.rankOf(pCard) == Card.rankOf(dCard);
	}

	//=======================================================================
	// Game state properties
	//=======================================================================

	/**
	 * Return true if there are no cards on the table.
	 */
	public boolean isClear ()
	{
		return dealerCards[DEALER_CARD_INITIAL] == NilCard;
	}

	/**
	 * Return true if this table is showing a finished hand.
	 */
	public boolean isEndOfHand ()
	{
		if (isClear())
			return false;

		// War always ends it.
		if (dealerCards[DEALER_CARD_WAR] != NilCard)
			return true;

		// Otherwise it depends on whether any non-surrendered ties remain
		// on the table.
		//
		for (int i = 0; i < seats.length; ++i)
		{
			if (isTieAt (i) && !seats[i].isSurrendered())
			{
				return false;
			}
		}

		return true;
	}

	/**
	 * Return true if the player at the indexed seat may ante up.
	 */
	public boolean playerMayAnte (int seatIndex)
	{
		if (isEndOfHand())
			return true;

		if (isClear())
		{
			// Ante is ok only if player has not already ante'd.
			// TODO: allow undo and redo of ante.
			//
			Bet ante = seats[seatIndex].ante;
			return ante == null || ante.getAmount().signum() == 0;
		}

		return false;
	}

	/**
	 * Return true if the player at the indexed seat may surrender or
	 * go to war.
	 */
	public boolean playerMayGoToWar (int seatIndex)
	{
		if (isEndOfHand())
			return false;

		if (!isTieAt (seatIndex))
			return false;

		// Check for pre-existing war/surrender bet.
		return seats[seatIndex].raise == null;
	}

	//=======================================================================
	// Copying
	//=======================================================================

	/**
	 * The copy method.
	 */
	public WarModel copy ()
	{
		return new WarModel (this);
	}
	
	/**
	 * Constructor.
	 */
	private WarModel (WarModel that)
	{
		this.roundCount = that.roundCount;
		this.shoe = that.shoe.copy();
		this.dealerCards = (byte[]) that.dealerCards.clone();
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
		for (int i = 0; i < dealerCards.length; ++i)
		{
			dealerCards[i] = NilCard;
		}

		for (int i = 0; i < seats.length; ++i)
		{
			seats[i].card0 = NilCard;
			seats[i].card1 = NilCard;
		}
	}

	/**
	 * Deal one card to each player, and one to the dealer.
	 */
	void dealInitial (Randomizer randomizer)
	{
		++roundCount;

		// Shuffle the shoe if necessary.
		shoe.shuffleIfPending (randomizer);

		// Deal one card to each betting player.
		for (int i = 0; i < seats.length; ++i)
		{
			if (seats[i].ante != null &&
				seats[i].ante.getAmount().signum() > 0)
			{
				seats[i].card0 = shoe.draw();
			}
		}

		this.dealerCards[DEALER_CARD_INITIAL] = shoe.draw();
	}

	/**
	 * Deal one card to each player holding a tie, burn three cards,
	 * and deal one card to the dealer.
	 */
	void dealWar ()
	{
		// Burn three.
		dealerCards[DEALER_CARD_BURN1] = shoe.draw();
		dealerCards[DEALER_CARD_BURN2] = shoe.draw();
		dealerCards[DEALER_CARD_BURN3] = shoe.draw();

		// Deal one card to each tying player.
		for (int i = 0; i < seats.length; ++i)
		{
			if (isTieAt (i))
			{
				seats[i].card1 = shoe.draw();
			}
		}

		// This is the one that counts.
		dealerCards[DEALER_CARD_WAR] = shoe.draw();
	}

	//=======================================================================
	// Player betting methods.
	//=======================================================================

	/**
	 * Player antes up.
	 */
	void showAnte (int seatIndex, Bet ante, Bet tieBet)
	{
		// FOR NOW, table automatically clears itself the first time a
		// player antes up for the next hand.
		//
		if (isEndOfHand())
		{
			clearAllBets();
			sweepCards();
		}
		seats[seatIndex].ante = ante;
		seats[seatIndex].tieBet = tieBet;
	}

	/**
	 * Clear bets preparation for the next deal.
	 */
	private void clearAllBets ()
	{
		for (int i = 0; i < seats.length; ++i)
		{
			clearPlayerBets (i);
		}
	}

	/**
	 * Clear bets from one player seat.
	 */
	void clearPlayerBets (int seatIndex)
	{
		seats[seatIndex].ante = null;
		seats[seatIndex].tieBet = null;
		seats[seatIndex].raise = null;
		seats[seatIndex].take = null;
	}

	/**
	 * The player at the indexed seat surrenders.
	 */
	void showSurrender (int seatIndex, Money refund)
	{
		seats[seatIndex].raise = new Bet (0, seats[seatIndex].ante.getPurse());
		seats[seatIndex].take = refund;
	}

	/**
	 * The player at the indexed seat raises.
	 */
	void showRaise (int seatIndex)
	{
		seats[seatIndex].raise = seats[seatIndex].ante;
	}

	/**
	 * Show some winnings, or not.
	 */
	void showTake (int seatIndex, Money take)
	{
		seats[seatIndex].take = take;
	}

	/**
	 * Private implementation of a seat at the table.
	 */
	private static class SeatImpl
		extends WarSeat
		implements java.io.Serializable
	{
		// Cards.  Each player can be dealt up to two per hand.
		byte card0;
		byte card1;

		// Bets and winnings.
		Bet ante;
		Bet tieBet;
		Bet raise;
		Money take;

		SeatImpl ()
		{
		}

		SeatImpl (SeatImpl that)
		{
			this.card0 = that.card0;
			this.card1 = that.card1;
			this.ante = that.ante;
			this.tieBet = that.tieBet;
			this.raise = that.raise;
			this.take = that.take;
		}

		// WarSeat interface...

		public byte getPlayerCard (int index)
		{
			switch (index)
			{
			case 0:
				return card0;
			case 1:
				return card1;
			}
			return NilCard;
		}

		public Bet getAnte()
		{
			return ante;
		}

		public Bet getTieBet()
		{
			return tieBet;
		}

		public Bet getRaise()
		{
			return raise;
		}

		public Money getTake()
		{
			return take;
		}
	}
}
