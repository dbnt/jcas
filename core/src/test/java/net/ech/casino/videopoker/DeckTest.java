//
// DeckTest.java
//

package net.ech.casino.videopoker;

import net.ech.casino.*;
import net.ech.math.RandomNumberGenerator;
import org.junit.*;
import static org.junit.Assert.*;

public class DeckTest
{
	Randomizer randomizer = new NotRandomizer();

	@Test
    public void testShuffleLength ()
    {
		Deck deck = new Deck(0);
        assertEquals(2, deck.shuffleAndDeal(randomizer, 1).length());
        assertEquals(4, deck.shuffleAndDeal(randomizer, 2).length());
	}

	@Test
	public void testFullDeck()
	{
        Deck deck = new Deck (0);
		String cards = deck.shuffleAndDeal(randomizer, 52);
		assertEquals(
			"2D3D4D5D6D7D8D9DTDJDQDKDAD" +
			"2C3C4C5C6C7C8C9CTCJCQCKCAC" +
			"2H3H4H5H6H7H8H9HTHJHQHKHAH" +
			"2S3S4S5S6S7S8S9STSJSQSKSAS",
			cards);
	}

	@Test
	public void testDeckLimit()
	{
        Deck deck = new Deck (0);
		try {
			deck.shuffleAndDeal(randomizer, 54);
			fail("should not be reached");
		}
		catch (ArrayIndexOutOfBoundsException e) {
		}
    }

	@Test
	public void testJokers()
	{
        Deck deck = new Deck (2);
		String cards = deck.shuffleAndDeal(randomizer, 54);
		assertEquals(
			"2D3D4D5D6D7D8D9DTDJDQDKDAD" +
			"2C3C4C5C6C7C8C9CTCJCQCKCAC" +
			"2H3H4H5H6H7H8H9HTHJHQHKHAH" +
			"2S3S4S5S6S7S8S9STSJSQSKSASjojo",
			cards);
	}
}
