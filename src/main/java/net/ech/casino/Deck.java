//
// Deck.java
//

package net.ech.casino;

/**
 * Class Deck defines the composition of a standard deck of cards,
 * represented as bytes encoded according to class Card.  
 * Deck is the base class for special types of decks.
 * 
 * A Deck is immutable.
 * 
 * @see net.ech.casino.Card
 * @see net.ech.casino.CardConstants
 *
 * @author James Echmalian, ech@ech.net
 * @version 1.0
 */
public class Deck implements CardConstants, java.io.Serializable
{
	private int nJokers = 0;

	/**
	 * Construct standard deck.
	 */
	public Deck ()
	{
	}

	/**
	 * Construct a deck with a number of jokers.
	 */
	public Deck (int nJokers)
	{
		if (nJokers < 0 || nJokers > CardsInStandardDeck)
			throw new IllegalArgumentException ("nJokers=" + nJokers);
		this.nJokers = nJokers;
	}

	/**
	 * Return the number of cards in this deck.
	 */
	public int getSize ()
	{
		return CardsInStandardDeck + nJokers;
	}

	/**
	 * Allocate an array large enough for all the cards in this deck and 
	 * copy the cards into the array.  
	 * @return the array
	 */
	public final byte[] copyCards ()
	{
		return copyCards (new byte [getSize()], 0);
	}

	/**
	 * Copy all of the cards in this deck to external storage.
	 * Subclasses may reimplement.
	 */
	public byte[] copyCards (byte[] outCards, int startIndex)
	{
		int i = startIndex;

		for (byte suit = MinSuit; suit < (MinSuit + NumberOfSuits); ++suit)
		{
			for (byte rank = MinRank; rank < (MinRank + NumberOfRanks); ++rank)
			{
				outCards[i++] = Card.value (rank, suit);
			}
		}
	
		while (i < getSize())
		{
			outCards[i++] = Joker;
		}

		return outCards;
	}

	/**
	 * Shuffle the deck and deal out the first n cards. 
	 */
	public final byte[] deal (int n, Randomizer random)
	{
		return deal (new byte [n], random);
	}

	/**
	 * Shuffle the deck and deal out the first n cards. 
	 */
	public final byte[] deal (byte[] someCards, Randomizer random)
	{
		byte[] allCards = copyCards ();
		random.shuffle (allCards);
		System.arraycopy (allCards, 0, someCards, 0, someCards.length);
		return someCards;
	}
}
