//
// HandInfoTest.java  
// 

package net.ech.casino.poker;

import net.ech.casino.Card;
import net.ech.casino.CardConstants;
import net.ech.casino.PokerScore;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test program for the Poker HandInfo class.
 *
 * @see net.ech.casino.poker.HandInfo
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class HandInfoTest implements CardConstants
{
    @Test
	public void testFlush() throws Exception
    {
		scoreAndAssert ("KH6H3H4HAH", PokerScore.makeFlush (Ace, King, Six, Four));
	}

	@Test
	public void testFullHouse() throws Exception
	{
	    scoreAndAssert ("4H4SASAD4C", PokerScore.makeFullHouse (Four, Ace));
	}

	@Test
	public void testPair4() throws Exception
	{
	    scoreAndAssert ("4S6SAD4C8D", PokerScore.makePair (Four, Ace, Eight, Six));
	}

	@Test
	public void testPairQueen() throws Exception
	{
	    scoreAndAssert ("QCQSTD6S3S", PokerScore.makePair (Queen, Ten, Six, Three));
	}

	@Test
	public void testPairNine() throws Exception
	{
	    scoreAndAssert ("9C9SASKSQS", PokerScore.makePair (Nine, Ace, King, Queen));
    }

	private void scoreAndAssert(String handString, PokerScore expected) throws Exception
	{
		assertEquals (expected, HandInfo.score(Card.parseHand(handString)));
	}
}
