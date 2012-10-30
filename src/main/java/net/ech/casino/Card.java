//
// Card.java
//

package net.ech.casino;

/**
 * Implementation of a card encoding scheme related to CardConstants.
 * A card value is a byte.
 * 
 * @see net.ech.casino.CardConstants
 *
 * @author James Echmalian, ech@ech.net
 * @version 1.0
 */
public class Card implements CardConstants
{
	/**
	 * Standard character representation
	 */
	public final static String RankChars = "23456789TJQKA";
	public final static String SuitChars = "DCHS";

	/**
	 * Do not instantiate.	A card value is a byte.
	 */
	private Card ()
	{
	}

	/**
	 * Create a card value from a rank and suit.
	 */
	public static byte value (byte rank, byte suit)
	{
		return (byte) ((suit << 4) + (rank & 0xf));
	}

	/**
	 * Create a card value from a two-character String.
	 * @return the card value, or NilCard if string is invalid.
	 * @exception StringIndexOutOfBoundsException
	 */
	public static byte valueOf (String cardString)
	{
		return parse (cardString);
	}

	/**
	 * Create an array of card values from a String.
	 * @return an array of card values - NilCard for any invalid string entry
	 * @exception StringIndexOutOfBoundsException if the length of the string is odd
	 */
	public static byte[] parseHand (String handString)
	{
		byte[] result = new byte[handString.length() / 2];
		for (int i = 0; i < handString.length(); i += 2) {
			result[i / 2] = parse (handString, i);
		}
		return result;
	}

	/**
	 * Create a card value from a two-character String.
	 * @return the card value, or NilCard if string is invalid.
	 * @exception StringIndexOutOfBoundsException
	 */
	public static byte parse (String cardString)
	{
		return parse (cardString, 0);
	}

	/**
	 * Create a card value from the two-character substring at the
	 * given starting index of the given String.
	 * @return the card value, or NilCard if string is invalid.
	 * @exception StringIndexOutOfBoundsException
	 */
	public static byte parse (String cardString, int start)
	{
		if (JokerString.regionMatches (0, cardString, start, 2))
			return Joker;

		char c0 = cardString.charAt (start);
		int rank = RankChars.indexOf (c0);
		if (rank < 0)
			return NilCard;
		rank += MinRank;

		char c1 = cardString.charAt (start + 1);
		int suit = SuitChars.indexOf (c1);
		if (suit < 0)
			return NilCard;
		suit += MinSuit;

		return value ((byte) rank, (byte) suit);
	}

	/**
	 * Return true iff the value represents a valid, non-null card.
	 */
	public static boolean isValid (byte cardValue)
	{
		// Test for valid rank.
		switch (rankOf (cardValue))
		{
		case NilRank:
			return cardValue == Joker;
		case Ace: case Deuce: case Three: case Four: case Five: 
		case Six: case Seven: case Eight: case Nine: case Ten: 
		case Jack: case Queen: case King:
			break;
		default:
			return false;
		}

		// Test for valid suit.
		switch (suitOf (cardValue))
		{
		case Diamonds: case Clubs: case Hearts: case Spades:
			break;
		default:
			return false;
		}

		// OK.
		return true;
	}

	/**
	 * Get the rank of a card.	Ace is high.  Rank of Joker is NilRank.
	 * Poker games are concerned with this value.
	 */
	public static byte rankOf (byte cardValue)
	{
		return (byte) (cardValue & 0xf);
	}

	/**
	 * Get the face value of a card.  Face value of Ace is one. 
	 * Face value of a court card is ten.  Face value of Joker is zero.
	 * Blackjack/21 and baccarat are concerned with this value.
	 */
	public static byte faceValueOf (byte cardValue)
	{
		byte rank = rankOf (cardValue);
		switch (rank)
		{
		case Ace:
			return 1;
		case Jack:
		case Queen:
		case King:
			return 10;
		default:
			return rank;
		}
	}

	/**
	 * Get the suit of a card.
	 */
	public static byte suitOf (byte cardValue)
	{
		return (byte) (cardValue >> 4);
	}

	/**
	 * Return a string encoding of a card value.
	 */
	public static String toString (byte cardValue)
	{
		return appendCard (cardValue, new StringBuffer ()).toString ();
	}

	/**
	 * Return a string encoding of a hand of cards.
	 */
	public static String toString (byte[] cards)
	{
		return toString (cards, null);
	}

	/**
	 * Return a string encoding of a hand of cards.
	 */
	public static String toString (byte[] cards, String separator)
	{
		if (cards == null)
			return null;

		StringBuffer buf = new StringBuffer (20);

		for (int i = 0; i < cards.length; ++i)
		{
			if (i > 0 && separator != null)
				buf.append (separator);
			appendCard (cards[i], buf);
		}

		return buf.toString ();
	}

	private static StringBuffer appendCard (byte cardValue, StringBuffer buf)
	{
		if (!isValid (cardValue))
		{
			buf.append (NilString);
		}
		else if (cardValue == Joker)
		{
			buf.append (JokerString);
		}
		else
		{
			buf.append (RankChars.charAt (rankOf (cardValue) - MinRank));
			buf.append (SuitChars.charAt (suitOf (cardValue) - MinSuit));
		}
		return buf;
	}
}
