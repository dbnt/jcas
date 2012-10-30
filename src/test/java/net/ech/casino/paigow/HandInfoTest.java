//
// HandInfoTest.java  
// 

package net.ech.casino.paigow;

import net.ech.casino.Card;
import net.ech.casino.PokerScore;
import org.junit.*;
import static org.junit.Assert.assertEquals;
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
		scoreAndAssert("3H4H", PokerScore.makeHighCard (Four, Three));
		scoreAndAssert("6C4H", PokerScore.makeHighCard (Six, Four));
		scoreAndAssert("6C6H", PokerScore.makePair (Six));
		scoreAndAssert("KCQH", PokerScore.makeHighCard (King, Queen));
		scoreAndAssert("QHjo", PokerScore.makeHighCard (Ace, Queen));
		scoreAndAssert("jo2H", PokerScore.makeHighCard (Ace, Deuce));
		scoreAndAssert("joAH", PokerScore.makePair (Ace));
	}

    @Test
	public void testFiveCardPaiGowScoring()
		throws Exception
    {
		scoreAndAssert("KH6H3H4HAH", PokerScore.makeFlush (Ace, King, Six, Four));
		scoreAndAssert("jo6H3H4H8H", PokerScore.makeFlush (Ace, Eight, Six, Four));
		scoreAndAssert("jo6H3H4HAH", PokerScore.makeFlush (Ace, Six, Four, Three));
		scoreAndAssert("4H4SASAD4C", PokerScore.makeFullHouse (Four, Ace));
		scoreAndAssert("jo4SASAD4C", PokerScore.makeFullHouse (Ace, Four));
		scoreAndAssert("4S6SAD4C8D", PokerScore.makePair (Four, Ace, Eight, Six));
		scoreAndAssert("QCQSTD6S3S", PokerScore.makePair (Queen, Ten, Six, Three));
		scoreAndAssert("9C9SASKSQS", PokerScore.makePair (Nine, Ace, King, Queen));
		
		// This straight counts as ace-low straight in pai gow, not
		// as a 6-high straight, because in pai gow, the ace-low
		// straight is the *second* highest straight.
		scoreAndAssert("jo5H4D3S2C", PokerScore.makeAceLowStraight ());
	 }

    private static void scoreAndAssert (String cardString, PokerScore expected)
        throws Exception
    {
        byte[] cards = Card.parseHand(cardString);

        PokerScore testScore = null;
        switch (cards.length)
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

        assertEquals(expected, testScore);
    }
}
