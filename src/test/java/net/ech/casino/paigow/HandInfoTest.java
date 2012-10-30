//
// HandInfoTest.java  
// 

package net.ech.casino.paigow;

import net.ech.casino.Card;
import net.ech.casino.PokerScore;
import org.junit.*;
import static org.junit.Assert.fail;

/**
 * Test program for the Pai Gow Poker HandInfo class.
 *
 * @see net.ech.casino.paigow.HandInfo
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class HandInfoTest implements Constants
{
    @Test
	public void testTwoCardPaiGowScoring()
		throws Exception
    {
		assertEqual ("3H4H", PokerScore.makeHighCard (Four, Three));
		assertEqual ("6C4H", PokerScore.makeHighCard (Six, Four));
		assertEqual ("6C6H", PokerScore.makePair (Six));
		assertEqual ("KCQH", PokerScore.makeHighCard (King, Queen));
		assertEqual ("QHjo", PokerScore.makeHighCard (Ace, Queen));
		assertEqual ("jo2H", PokerScore.makeHighCard (Ace, Deuce));
		assertEqual ("joAH", PokerScore.makePair (Ace));
	}

    @Test
	public void testFiveCardPaiGowScoring()
		throws Exception
    {
		assertEqual ("KH6H3H4HAH",
			PokerScore.makeFlush (Ace, King, Six, Four));
		assertEqual ("jo6H3H4H8H",
			PokerScore.makeFlush (Ace, Eight, Six, Four));
		assertEqual ("jo6H3H4HAH",
			PokerScore.makeFlush (Ace, Six, Four, Three));
		assertEqual ("4H4SASAD4C",
			PokerScore.makeFullHouse (Four, Ace));
		assertEqual ("jo4SASAD4C",
			PokerScore.makeFullHouse (Ace, Four));
		assertEqual ("4S6SAD4C8D",
			PokerScore.makePair (Four, Ace, Eight, Six));
		assertEqual ("QCQSTD6S3S",
			PokerScore.makePair (Queen, Ten, Six, Three));
		assertEqual ("9C9SASKSQS",
			PokerScore.makePair (Nine, Ace, King, Queen));
		
		// This straight counts as ace-low straight in pai gow, not
		// as a 6-high straight, because in pai gow, the ace-low
		// straight is the *second* highest straight.
		assertEqual ("jo5H4D3S2C", PokerScore.makeAceLowStraight ());
	 }

    static void assertEqual (String cardString, PokerScore trueScore)
        throws Exception
    {
        byte[] cards = new byte [5];
        int cardCount = 0;
        for (int cx = 0; cx < cardString.length (); cx += 2)
        {
            byte card = Card.parse (cardString, cx);
            if (card == NilCard)
            {
                fail("Bad card: " + cardString.substring (cx, cx + 2));
            }
            cards[cardCount++] = card;
        }

        PokerScore testScore = null;
        switch (cardCount)
        {
        case 5:
            testScore = HandInfo.score5 (cards, 0);
            break;
        case 2:
            testScore = HandInfo.score2 (cards, 0);
            break;
        default:
            fail("Wrong number of cards.");
        }

        if (!testScore.equals (trueScore))
        {
            String testString = testScore.format (cardCount);
            String trueString = trueScore.format (cardCount);
            fail ("Pai Gow scoring: expected " + trueString + "; got " + testString);
        }
    }
}
