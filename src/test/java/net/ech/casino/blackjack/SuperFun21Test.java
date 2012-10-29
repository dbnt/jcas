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
    public void testSuperFun21 ()
        throws Exception
    {
        // Test that player may double after a split.
        // Test that player may double split aces.
        BlackjackPlayer player = stackTheDeck ("ASAC3D3H7S7D", "2C6HTC");
        player.deal (1);
        player.split ();
        player.doubledown ();
        player.doubledown ();

        // Test late surrender
        player = stackTheDeck ("2SAC3D3H7S7D", "2C6HTC");
        player.deal (1);
        player.hit ();
        player.hit ();
        player.surrender ();

        // Test late double-down.
        // Test double-down rescue.
        player = stackTheDeck ("2SAC3D3H7S7D", "2C6HTC");
        player.deal (1);
        player.hit ();
        player.doubledown ();
        player.surrender ();

        // Player blackjack in diamonds pays 2-1.
        player = stackTheDeck ("TDAD", "6CTC5H");
        player.deal (1);
        testReturns (player, 3);

        // Other blackjack pays only 1-1.
        player = stackTheDeck ("TSAD", "6CTC5H");
        player.deal (1);
        testReturns (player, 2);

        // Player hand consisting of 6 cards or more automatically wins.
        player = stackTheDeck ("2C2D2H2S3H8H", "6CTC5H");
        player.deal (1);
        player.hit ();
        player.hit ();
        player.hit ();
        player.hit ();
        testReturns (player, 2);
        player = stackTheDeck ("2C2D2H2S3H9H", "6CTC5H");
        player.deal (1);
        player.hit ();
        player.hit ();
        player.hit ();
        player.hit ();
        testReturns (player, 2);

        // ...except after doubling.
        player = stackTheDeck ("2C2D2H2S3H9H", "6CTC5H");
        player.deal (1);
        player.hit ();
        player.hit ();
        player.hit ();
        player.doubledown ();
        player.stand(); // don't rescue.
        testReturns (player, 0);
        player = stackTheDeck ("2C2D2H2S3H9H", "6CTC8H");
        player.deal (1);
        player.hit ();
        player.hit ();
        player.hit ();
        player.doubledown ();
        player.stand(); // don't rescue.
        testReturns (player, 4);
        player = stackTheDeck ("2C2D2H2S3H9H", "TC3C6H");
        player.deal (1);
        player.hit ();
        player.hit ();
        player.hit ();
        player.doubledown ();
        player.stand(); // don't rescue.
        testReturns (player, 4);

        // Player hand of 21, consisting of 5 cards or more, wins 2-1.
        player = stackTheDeck ("2C2D2H5SJS", "6CTC5H");
        player.deal (1);
        player.hit ();
        player.hit ();
        player.hit ();
        testReturns (player, 3);

        // ...except after doubling.
        player = stackTheDeck ("2C2D2H5SJS", "6CTC5H");
        player.deal (1);
        player.hit ();
        player.hit ();
        player.doubledown ();
        testReturns (player, 2);
        player = stackTheDeck ("2C2D2H5SJS", "6CTC3H");
        player.deal (1);
        player.hit ();
        player.hit ();
        player.doubledown ();
        testReturns (player, 4);
    }

    BlackjackPlayer stackTheDeck (
        String playerCards,
        String dealerCards)
        throws Exception
    {
        Casino casino = new TestCasino (machine);
        BlackjackGame game = new BlackjackGame (casino, machine);
        game.setShoe (Utils.stackedShoe (machine, playerCards, dealerCards));
        BlackjackPlayer player = new BlackjackPlayer ("test", game);
        player.sitDown ();
        return player;
    }

    /**
     * Assert player gets expected amount.
     */
    public static void testReturns (BlackjackPlayer player, int expected)
		throws Exception
    {
        assertTrue(player.getBlackjackGame().isDealOk());
        assertEquals(expected, (int) (player.getBlackjackGame().getTotalReturn() + 0.5));
    }
}
