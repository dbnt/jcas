//
// SuperFun21Test.java  
// 

package net.ech.casino.blackjack;

import net.ech.casino.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test driver for Super Fun 21.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public final class SuperFun21Test implements Constants
{
    private BlackjackMachine machine = new SuperFun21 ();

	@Test
    public void testSuperFun21AlwaysReshuffles ()
        throws Exception
    {
        Shoe shoe = machine.createNewShoe();
        Randomizer random = new Randomizer ();
        for (int i = 0; i < 50; ++i)
        {
            assertTrue(shoe.isShufflePending());
            shoe.shuffle (random);
            shoe.draw();
        }
    }

	@Test
    public void testCanSplitAndDoubleSplitAces ()
        throws Exception
    {
        BlackjackGame game = stackTheDeck ("ASAC3D3H7S7D", "2C6HTC");
        game.deal (1);
        game.split ();
        game.doubledown ();
        game.doubledown ();
	}

	@Test
    public void testLateSurrender ()
        throws Exception
    {
        BlackjackGame game = stackTheDeck ("2SAC3D3H7S7D", "2C6HTC");
        game.deal (1);
        game.hit ();
        game.hit ();
        game.surrender ();
	}

	@Test
    public void testLateDoubleDownAndRescue ()
        throws Exception
    {
        BlackjackGame game = stackTheDeck ("2SAC3D3H7S7D", "2C6HTC");
        game.deal (1);
        game.hit ();
        game.doubledown ();
        game.surrender ();
	}

	@Test
    public void testPlayerBlackjackInDiamondsPays2to1 ()
        throws Exception
    {
        BlackjackGame game = stackTheDeck ("TDAD", "6CTC5H");
        game.deal (1);
        testReturns (game, 3);
	}

	@Test
    public void testNonDiamondBlackjackPays1to1 ()
        throws Exception
	{
        BlackjackGame game = stackTheDeck ("TSAD", "6CTC5H");
        game.deal (1);
        testReturns (game, 2);
	}

	@Test
    public void testPlayerHandOf6CardsAutoWins1 ()
        throws Exception
	{
        BlackjackGame game = stackTheDeck ("2C2D2H2S3H8H", "6CTC5H");
        game.deal (1);
        game.hit ();
        game.hit ();
        game.hit ();
        game.hit ();
        testReturns (game, 2);
	}

	@Test
    public void testPlayerHandOf6CardsAutoWins2 ()
        throws Exception
	{
        BlackjackGame game = stackTheDeck ("2C2D2H2S3H9H", "6CTC5H");
        game.deal (1);
        game.hit ();
        game.hit ();
        game.hit ();
        game.hit ();
        testReturns (game, 2);
	}

	@Test
    public void testPlayerHandOf6CardsDoesntAutoWinAfterDouble ()
        throws Exception
	{
        BlackjackGame game = stackTheDeck ("2C2D2H2S3H9H", "6CTC5H");
        game.deal (1);
        game.hit ();
        game.hit ();
        game.hit ();
        game.doubledown ();
        game.stand(); // don't rescue.
        testReturns (game, 0);
	}

	@Test
    public void testLateDoubleDownPayout1 ()
        throws Exception
	{
        BlackjackGame game = stackTheDeck ("2C2D2H2S3H9H", "6CTC8H");
        game.deal (1);
        game.hit ();
        game.hit ();
        game.hit ();
        game.doubledown ();
        game.stand(); // don't rescue.
        testReturns (game, 4);
	}

	@Test
    public void testLateDoubleDownPayout2 ()
        throws Exception
	{
        BlackjackGame game = stackTheDeck ("2C2D2H2S3H9H", "TC3C6H");
        game.deal (1);
        game.hit ();
        game.hit ();
        game.hit ();
        game.doubledown ();
        game.stand(); // don't rescue.
        testReturns (game, 4);
	}

	@Test
    public void test5Card21Pays2to1 ()
        throws Exception
	{
        BlackjackGame game = stackTheDeck ("2C2D2H5SJS", "6CTC5H");
        game.deal (1);
        game.hit ();
        game.hit ();
        game.hit ();
        testReturns (game, 3);
	}

	@Test
    public void test5Card21PaysRegularAfterDoubling ()
        throws Exception
	{
        BlackjackGame game = stackTheDeck ("2C2D2H5SJS", "6CTC5H");
        game.deal (1);
        game.hit ();
        game.hit ();
        game.doubledown ();
        testReturns (game, 2);
	}

	@Test
    public void testInstantLateDoubleDownPayout ()
        throws Exception
	{
        BlackjackGame game = stackTheDeck ("2C2D2H5SJS", "6CTC3H");
        game.deal (1);
        game.hit ();
        game.hit ();
        game.doubledown ();
        testReturns (game, 4);
    }

    BlackjackGame stackTheDeck (
        String playerCards,
        String dealerCards)
        throws Exception
    {
        Casino casino = new TestCasino (machine);
        BlackjackGame game = new BlackjackGame (casino, machine);
        game.setShoe (Utils.stackedShoe (machine, playerCards, dealerCards));
        assertTrue(game.seatPlayer("test"));
        return game;
    }

    /**
     * Assert player gets expected amount.
     */
    public static void testReturns (BlackjackGame game, int expected)
		throws Exception
    {
        assertTrue(game.isDealOk());
        assertEquals(expected, (int) (game.getTotalReturn() + 0.5));
    }
}
