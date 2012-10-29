//
// Hand.java  
// 

package net.ech.casino.blackjack;

import net.ech.casino.*;

/**
 * A hand of cards, blackjack-specific.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class Hand implements Constants, Cloneable, java.io.Serializable
{
	/**
	 * Note: serial version is fixed!
	 */
	static final long serialVersionUID = -5329555999313652342L;

	public final static int MaxCards = 25;

	private byte[] cards = new byte [MaxCards];
	private int size;
	private boolean firstCardDown;

	/**
	 * Constructor.
	 */
	public Hand ()
	{
	}

	/**
	 * Return the size of this hand.
	 */
	public int getSize ()
	{
		return size;
	}

	/**
	 * Quick check for empty hand.
	 */
	public boolean isEmpty ()
	{
		return size == 0;
	}

	/** 
	 * @return the byte code of the indexed card.
	 */
	public byte getCard (int index)
	{
		if (index >= size)
			throw new ArrayIndexOutOfBoundsException (index	 + " >= " + size);
		return cards[index];
	}

	/** 
	 * @return the rank of the indexed card.
	 */
	public int getCardRank (int index)
	{
		return Card.rankOf (getCard (index));
	}

	/** 
	 * @return the suit of the indexed card.
	 */
	public int getCardSuit (int index)
	{
		return Card.suitOf (getCard (index));
	}

	/**
	 * Get the score of this hand.
	 * @return a score between 0 and 22.  Zero indicates bust or no cards.
	 * BLACKJACK indicates blackjack.
	 */
	public int getScore ()
	{
		int score = getHighTotal ();
		if (score > 21)
			score = 0;
		else if (score == 21 && size == 2)
			score = BLACKJACK;
		return score;
	}

	/**
	 * Get the best possible rank total of this hand, treating aces
	 * as elevens or ones, face cards as tens.
	 */
	public int getHighTotal ()
	{
		int total = 0;
		int aceCount = 0;

		for (int i = 0; i < size; ++i)
		{
			int faceValue = Card.faceValueOf (cards[i]);
			total += faceValue;
			if (faceValue == 1)
			{
				++aceCount;
			}
		}

		if (aceCount > 0 && total <= 11)
			total += 10;

		return total;
	}

	/**
	 * Get the rank total of this hand, treating all aces as ones,
	 * face cards as tens.
	 */
	public int getLowTotal ()
	{
		int total = 0;

		for (int i = 0; i < size; ++i)
		{
			total += Card.faceValueOf (cards[i]);
		}

		return total;
	}

	/** 
	 * Tell whether this hand is "soft", that is, contains an ace and
	 * can be hit with no chance of busting. 
	 */
	public boolean isSoft ()
	{
		return getHighTotal () != getLowTotal ();
	}

	/**
	 * Hit this hand.
	 */
	public void hit (byte card)
	{
		if (size == MaxCards)
			throw new RuntimeException ("BLACKJACK HAND OVERFLOW");

		cards[size++] = card;
	}

	/**
	 * Show/hide the first card (shown by default).
	 */
	public void setFirstCardDown (boolean firstCardDown)
	{
		this.firstCardDown = firstCardDown;
	}

	/**
	 * Format a string that represents the cards in this 
	 * hand.  Each card is represented by two characters: a rank
	 * character in A23456789TJQK followed by a suit character in
	 * DCHS.
	 * @return a string that represents the cards in this hand.
	 */
	public String getCards ()
	{
		return toString ("");
	}

	/**
	 * Format a string that represents the cards in this 
	 * hand.  Each card is represented by two characters: a rank
	 * character in A23456789TJQK followed by a suit character in
	 * DCHS.
	 * @return a string that represents the cards in this hand.
	 */
	public String toString ()
	{
		return toString ("");
	}

	/**
	 * Format a string that represents the cards in this
	 * hand.
	 * @param separator			String to insert between cards in output
	 * @return a string that represents the cards in this
	 * hand.
	 */
	public String toString (String separator)
	{
		StringBuffer buf = new StringBuffer ();

		for (int i = 0; i < size; ++i)
		{
			if (i == 0 && separator != null)
			{
				buf.append (separator);
			}
			if (i == 0 && firstCardDown)
			{
				buf.append ("--");
			}
			else
			{
				buf.append (Card.toString (cards[i]));
			}
		}

		return buf.toString();
	}

	/**
	 * Get a label that describes the score of this hand (from player's
	 * point of view).
	 */
	public String getScoreLabel (boolean isFinal)
	{
		if (size == 0)
			return "";

		int score = getScore ();
		switch (score)
		{
		case BLACKJACK:
			return "BLACKJACK";
		case 0:
			return "BUST";
		case 21:
			return "21";
		default:
			if (isFinal || score == getLowTotal ())
				return Integer.toString (score);
			else
				return score + " (" + (score - 10) + ")";
		}
	}

	/**
	 * Make a backup copy (shallow) of this Hand.
	 */
	public Hand copy ()
	{
		// Let the default clone() do a shallow copy, which is what
		// we want.
		try
		{
			return (Hand) clone ();
		}
		catch (CloneNotSupportedException e)
		{
			// But it is.
			return null;
		}
	}
}
