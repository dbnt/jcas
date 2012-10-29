//
// SpanishDeck.java	 
// 

package net.ech.casino.blackjack;

import net.ech.casino.Deck;
import net.ech.casino.Card;

/**
 * A SpanishDeck is the deck type for Spanish 21.  A Spanish deck lacks tens.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class SpanishDeck extends Deck
{
	private final static int CardsPerSpanishDeck = 48;

	/**
	 * Constructor.
	 */
	public SpanishDeck ()
	{
	}

	/**
	 * @return the number of cards in this deck.
	 */
	public int getSize ()
	{
		return CardsPerSpanishDeck;
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
				if (rank != Ten)
				{
					outCards[i++] = Card.value (rank, suit);
				}
			}
		}
	
		return outCards;
	}
}
