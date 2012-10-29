//
// CardTest.java
//

package net.ech.casino;

import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test program for Card and CardConstants.
 *
 * @see net.ech.casino.Card
 * @see net.ech.casino.CardConstants
 *
 * @author James Echmalian, ech@ech.net
 * @version 1.0
 */
public class CardTest implements CardConstants
{
    @Test
    public void testNil ()
    {
        assertTrue (Card.value((byte)0,(byte)0) == NilCard);
        assertTrue (Card.value((byte)0,(byte)1) != NilCard);
        assertTrue (Card.value((byte)1,(byte)0) != NilCard);
        assertTrue (Card.valueOf ("XX") == NilCard);
    }

    @Test
    public void testJoker ()
    {
        assertTrue (Card.value((byte)0,JokerSuit) == Joker);
        assertTrue (Card.value((byte)1,JokerSuit) != Joker);
        assertTrue (Card.value((byte)0,(byte)(JokerSuit + 1)) != Joker);
    }

    @Test
    public void testRanks ()
    {
        assertTrue (NilRank == 0);
        assertTrue (Card.rankOf (NilCard) == 0);
        assertTrue (Card.rankOf (Joker) == 0);
        testRank (Deuce, 2);
        testRank (Three, 3);
        testRank (Four, 4);
        testRank (Five, 5);
        testRank (Six, 6);
        testRank (Seven, 7);
        testRank (Eight, 8);
        testRank (Nine, 9);
        testRank (Ten, 10);
        testRank (Jack, 11);
        testRank (Queen, 12);
        testRank (King, 13);
        testRank (Ace, 14);
    }

    private void testRank (byte rank1, int rank2)
    {
        assertTrue (rank1 == rank2);
        assertTrue (Card.rankOf (Card.value(rank1, Diamonds)) == rank1);
        assertTrue (Card.rankOf (Card.value(rank1, Clubs)) == rank1);
        assertTrue (Card.rankOf (Card.value(rank1, Hearts)) == rank1);
        assertTrue (Card.rankOf (Card.value(rank1, Spades)) == rank1);
    }

	@Test
    public void testSuits ()
    {
        assertTrue (NilSuit == 0);
        assertTrue (Card.suitOf (NilCard) == 0);
        assertTrue (Card.suitOf (Joker) == JokerSuit);
        assertTrue (NilSuit != Diamonds);
        assertTrue (NilSuit != Clubs);
        assertTrue (NilSuit != Hearts);
        assertTrue (NilSuit != Spades);
        assertTrue (Diamonds != Clubs);
        assertTrue (Diamonds != Hearts);
        assertTrue (Diamonds != Spades);
        assertTrue (Clubs != Hearts);
        assertTrue (Clubs != Spades);
        assertTrue (Hearts != Spades);
        assertTrue (Card.suitOf (Card.value((byte)1, Diamonds)) == Diamonds);
        assertTrue (Card.suitOf (Card.value((byte)2, Diamonds)) == Diamonds);
        assertTrue (Card.suitOf (Card.value((byte)1, Clubs)) == Clubs);
        assertTrue (Card.suitOf (Card.value((byte)2, Clubs)) == Clubs);
        assertTrue (Card.suitOf (Card.value((byte)1, Hearts)) == Hearts);
        assertTrue (Card.suitOf (Card.value((byte)2, Hearts)) == Hearts);
        assertTrue (Card.suitOf (Card.value((byte)1, Spades)) == Spades);
        assertTrue (Card.suitOf (Card.value((byte)2, Spades)) == Spades);
    }

	@Test
    public void testStringRep ()
    {
        testStringRep (NilCard, NilString);
        testStringRep (Joker, JokerString);
        testStringRep (Card.value(Ace, Diamonds), "AD");
        testStringRep (Card.value(Deuce, Diamonds), "2D");
        testStringRep (Card.value(Three, Diamonds), "3D");
        testStringRep (Card.value(Four, Diamonds), "4D");
        testStringRep (Card.value(Five, Diamonds), "5D");
        testStringRep (Card.value(Six, Diamonds), "6D");
        testStringRep (Card.value(Seven, Diamonds), "7D");
        testStringRep (Card.value(Eight, Diamonds), "8D");
        testStringRep (Card.value(Nine, Diamonds), "9D");
        testStringRep (Card.value(Ten, Diamonds), "TD");
        testStringRep (Card.value(Jack, Diamonds), "JD");
        testStringRep (Card.value(Queen, Diamonds), "QD");
        testStringRep (Card.value(King, Diamonds), "KD");
        testStringRep (Card.value(Ace, Clubs), "AC");
        testStringRep (Card.value(Deuce, Clubs), "2C");
        testStringRep (Card.value(Three, Clubs), "3C");
        testStringRep (Card.value(Four, Clubs), "4C");
        testStringRep (Card.value(Five, Clubs), "5C");
        testStringRep (Card.value(Six, Clubs), "6C");
        testStringRep (Card.value(Seven, Clubs), "7C");
        testStringRep (Card.value(Eight, Clubs), "8C");
        testStringRep (Card.value(Nine, Clubs), "9C");
        testStringRep (Card.value(Ten, Clubs), "TC");
        testStringRep (Card.value(Jack, Clubs), "JC");
        testStringRep (Card.value(Queen, Clubs), "QC");
        testStringRep (Card.value(King, Clubs), "KC");
        testStringRep (Card.value(Ace, Hearts), "AH");
        testStringRep (Card.value(Deuce, Hearts), "2H");
        testStringRep (Card.value(Three, Hearts), "3H");
        testStringRep (Card.value(Four, Hearts), "4H");
        testStringRep (Card.value(Five, Hearts), "5H");
        testStringRep (Card.value(Six, Hearts), "6H");
        testStringRep (Card.value(Seven, Hearts), "7H");
        testStringRep (Card.value(Eight, Hearts), "8H");
        testStringRep (Card.value(Nine, Hearts), "9H");
        testStringRep (Card.value(Ten, Hearts), "TH");
        testStringRep (Card.value(Jack, Hearts), "JH");
        testStringRep (Card.value(Queen, Hearts), "QH");
        testStringRep (Card.value(King, Hearts), "KH");
        testStringRep (Card.value(Ace, Spades), "AS");
        testStringRep (Card.value(Deuce, Spades), "2S");
        testStringRep (Card.value(Three, Spades), "3S");
        testStringRep (Card.value(Four, Spades), "4S");
        testStringRep (Card.value(Five, Spades), "5S");
        testStringRep (Card.value(Six, Spades), "6S");
        testStringRep (Card.value(Seven, Spades), "7S");
        testStringRep (Card.value(Eight, Spades), "8S");
        testStringRep (Card.value(Nine, Spades), "9S");
        testStringRep (Card.value(Ten, Spades), "TS");
        testStringRep (Card.value(Jack, Spades), "JS");
        testStringRep (Card.value(Queen, Spades), "QS");
        testStringRep (Card.value(King, Spades), "KS");
    }

    private void testStringRep (byte cardValue, String cardString)
    {
        assertTrue (cardValue == Card.valueOf (cardString));
        assertTrue (Card.toString (cardValue).equals (cardString));
    }

	@Test
    public void testParse ()
    {
        assertTrue (Card.parse ("XXADXX", 2) == Card.value (Ace, Diamonds));
    }

	@Test
    public void testValid ()
    {
        assertTrue (!Card.isValid (NilCard));
        assertTrue (Card.isValid (Joker));
        assertTrue (Card.isValid(Card.value(Ace, Diamonds)));
        assertTrue (Card.isValid(Card.value(Deuce, Diamonds)));
        assertTrue (Card.isValid(Card.value(Three, Diamonds)));
        assertTrue (Card.isValid(Card.value(Four, Diamonds)));
        assertTrue (Card.isValid(Card.value(Five, Diamonds)));
        assertTrue (Card.isValid(Card.value(Six, Diamonds)));
        assertTrue (Card.isValid(Card.value(Seven, Diamonds)));
        assertTrue (Card.isValid(Card.value(Eight, Diamonds)));
        assertTrue (Card.isValid(Card.value(Nine, Diamonds)));
        assertTrue (Card.isValid(Card.value(Ten, Diamonds)));
        assertTrue (Card.isValid(Card.value(Jack, Diamonds)));
        assertTrue (Card.isValid(Card.value(Queen, Diamonds)));
        assertTrue (Card.isValid(Card.value(King, Diamonds)));
        assertTrue (Card.isValid(Card.value(Ace, Clubs)));
        assertTrue (Card.isValid(Card.value(Deuce, Clubs)));
        assertTrue (Card.isValid(Card.value(Three, Clubs)));
        assertTrue (Card.isValid(Card.value(Four, Clubs)));
        assertTrue (Card.isValid(Card.value(Five, Clubs)));
        assertTrue (Card.isValid(Card.value(Six, Clubs)));
        assertTrue (Card.isValid(Card.value(Seven, Clubs)));
        assertTrue (Card.isValid(Card.value(Eight, Clubs)));
        assertTrue (Card.isValid(Card.value(Nine, Clubs)));
        assertTrue (Card.isValid(Card.value(Ten, Clubs)));
        assertTrue (Card.isValid(Card.value(Jack, Clubs)));
        assertTrue (Card.isValid(Card.value(Queen, Clubs)));
        assertTrue (Card.isValid(Card.value(King, Clubs)));
        assertTrue (Card.isValid(Card.value(Ace, Hearts)));
        assertTrue (Card.isValid(Card.value(Deuce, Hearts)));
        assertTrue (Card.isValid(Card.value(Three, Hearts)));
        assertTrue (Card.isValid(Card.value(Four, Hearts)));
        assertTrue (Card.isValid(Card.value(Five, Hearts)));
        assertTrue (Card.isValid(Card.value(Six, Hearts)));
        assertTrue (Card.isValid(Card.value(Seven, Hearts)));
        assertTrue (Card.isValid(Card.value(Eight, Hearts)));
        assertTrue (Card.isValid(Card.value(Nine, Hearts)));
        assertTrue (Card.isValid(Card.value(Ten, Hearts)));
        assertTrue (Card.isValid(Card.value(Jack, Hearts)));
        assertTrue (Card.isValid(Card.value(Queen, Hearts)));
        assertTrue (Card.isValid(Card.value(King, Hearts)));
        assertTrue (Card.isValid(Card.value(Ace, Spades)));
        assertTrue (Card.isValid(Card.value(Deuce, Spades)));
        assertTrue (Card.isValid(Card.value(Three, Spades)));
        assertTrue (Card.isValid(Card.value(Four, Spades)));
        assertTrue (Card.isValid(Card.value(Five, Spades)));
        assertTrue (Card.isValid(Card.value(Six, Spades)));
        assertTrue (Card.isValid(Card.value(Seven, Spades)));
        assertTrue (Card.isValid(Card.value(Eight, Spades)));
        assertTrue (Card.isValid(Card.value(Nine, Spades)));
        assertTrue (Card.isValid(Card.value(Ten, Spades)));
        assertTrue (Card.isValid(Card.value(Jack, Spades)));
        assertTrue (Card.isValid(Card.value(Queen, Spades)));
        assertTrue (Card.isValid(Card.value(King, Spades)));
        assertTrue (!Card.isValid(Card.value((byte)15, Spades)));
        assertTrue (!Card.isValid(Card.value(King, (byte)10)));
        assertTrue (!Card.isValid ((byte)-1));
    }

	@Test
    public void testFaceValue ()
    {
        assertTrue (Card.faceValueOf (NilCard) == 0);
        assertTrue (Card.faceValueOf (Joker) == 0);
        assertTrue (Card.faceValueOf (Card.value (Ace, Clubs)) == 1);
        assertTrue (Card.faceValueOf (Card.value (Deuce, Clubs)) == 2);
        assertTrue (Card.faceValueOf (Card.value (Three, Clubs)) == 3);
        assertTrue (Card.faceValueOf (Card.value (Four, Clubs)) == 4);
        assertTrue (Card.faceValueOf (Card.value (Five, Clubs)) == 5);
        assertTrue (Card.faceValueOf (Card.value (Six, Clubs)) == 6);
        assertTrue (Card.faceValueOf (Card.value (Seven, Clubs)) == 7);
        assertTrue (Card.faceValueOf (Card.value (Eight, Clubs)) == 8);
        assertTrue (Card.faceValueOf (Card.value (Nine, Clubs)) == 9);
        assertTrue (Card.faceValueOf (Card.value (Ten, Clubs)) == 10);
        assertTrue (Card.faceValueOf (Card.value (Jack, Clubs)) == 10);
        assertTrue (Card.faceValueOf (Card.value (Queen, Clubs)) == 10);
        assertTrue (Card.faceValueOf (Card.value (King, Clubs)) == 10);
    }
}
