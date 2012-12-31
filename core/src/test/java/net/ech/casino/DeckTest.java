//
// DeckTest.java
//

package net.ech.casino;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test program for Card and CardConstants.  Deck, too.
 *
 * @see net.ech.casino.Card
 * @see net.ech.casino.CardConstants
 * @see net.ech.casino.Deck
 *
 * @author James Echmalian, ech@ech.net
 * @version 1.0
 */
public class DeckTest implements CardConstants
{
	@Test
    public void testDefaultSize ()
    {
        assertTrue (new Deck ().getSize () == 52);
        assertTrue (new Deck ().copyCards ().length == 52);
	}

	@Test
	public void testSizeWithJokers()
	{
        Deck deck = new Deck (2);
        assertTrue (deck.getSize() == 54);

        byte[] cards = deck.copyCards ();
        assertTrue (cards.length == 54);
	}

	@Test
	public void testFullDeck()
	{
        Deck deck = new Deck (2);
        byte[] cards = deck.copyCards ();

        for (byte suit = MinSuit; suit < (MinSuit + NumberOfSuits); ++suit)
        {
            int count = 0;
            for (int i = 0; i < cards.length; ++i)
            {
                if (Card.suitOf (cards[i]) == suit)
                    ++count;
            }
            assertTrue (count == NumberOfRanks);
        }
        for (byte rank = MinRank; rank < (MinRank + NumberOfRanks); ++rank)
        {
            int count = 0;
            for (int i = 0; i < cards.length; ++i)
            {
                if (Card.rankOf (cards[i]) == rank)
                    ++count;
            }
            assertTrue (count == NumberOfSuits);
        }
        assertTrue (cards[52] == Joker);
        assertTrue (cards[53] == Joker);
    }
}
