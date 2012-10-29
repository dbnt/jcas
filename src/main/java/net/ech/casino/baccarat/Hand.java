//
// Hand.java
//

package net.ech.casino.baccarat;

import net.ech.casino.*;

/**
 * Class Hand represents a baccarat hand.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class Hand implements Constants, java.io.Serializable
{
	private byte[] cards = new byte [MaxCardsInHand];
	private int length;

	/**
	 * Constructor.
	 */
	public Hand ()
	{
	}

	/**
	 * Deal a new card onto this hand.
	 */
	public void hit (byte card)
	{
		cards[length] = card;
		++length;
	}

	/**
	 * Get the final score of this hand.
	 */
	public int getScore ()
	{
		int score = 0;

		for (int i = 0; i < length; ++i)
			score += Card.faceValueOf (cards[i]);

		return score % 10;
	}

	/**
	 * Return true if this hand represents a "natural", or a 2-card
	 * hand scoring 8 or more.
	 */
	public boolean isNatural ()
	{
		return getScore () >= 8 && cards[2] == 0;
	}

	/**
	 * Return a string that details the score history of this hand.
	 * In the case of a 2-card hand, the score history is one number.
	 * In the case of a 3-card hand, the score history is two numbers,
	 * separated by a space.
	 */
	public String getScoresString ()
	{
		StringBuffer buf = new StringBuffer ();

		int score = 0;
		for (int i = 0; i < length; ++i)
		{
			score = (score + Card.faceValueOf (cards[i])) % 10;

			switch (i)
			{
			case 2:
				buf.append (' ');
			case 1:
				buf.append (score);
			}
		}

		return buf.toString ();
	}

	/**
	 * Return the contents of this hand in standard Card encoding.
	 */
	public String toString ()
	{
		return toString (false);
	}

	/**
	 * Return the contents of this hand in standard Card encoding.
	 */
	public String toString (boolean separateCards)
	{
		StringBuffer buf = new StringBuffer ();

		for (int i = 0; i < length; ++i)
		{
			if (separateCards && i > 0)
				buf.append (' ');
			buf.append (Card.toString (cards[i]));
		}

		return buf.toString ();
	}
}
