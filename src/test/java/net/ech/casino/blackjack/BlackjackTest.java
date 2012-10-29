//
// BlackjackTest.java  
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
 * Test driver for blackjack games.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public final class BlackjackTest implements Constants
{
	@Test
    public void testPush ()
        throws Exception
    {
        CasinoBlackjack machine = new CasinoBlackjack ();
        BlackjackPlayer player = stackTheDeck (machine, "THAD", "AHTD");
        player.deal (1);
        testReturns (player, 1);

        player = stackTheDeck (machine, "TH6H5H", "TD6D5D");
        player.deal (1);
        player.hit ();
        testReturns (player, 1);
    }

	@Test
    public void testDealerHitsSoft17 ()
        throws Exception
    {
        CasinoBlackjack machine = new CasinoBlackjack ();
        machine.setDealerHitsSoft17 (true);
        BlackjackPlayer player = stackTheDeck (machine, "THTD", "5HASAC4D");
        player.deal (1);
        player.stand ();
        testReturns (player, 0);

        machine = new CasinoBlackjack ();
        machine.setDealerHitsSoft17 (false);
        player = stackTheDeck (machine, "THTD", "5HASAC4D");
        player.deal (1);
        player.stand ();
        testReturns (player, 2);
    }

	@Test
    public void testScoring ()
        throws Exception
    {
        CasinoBlackjack machine = new CasinoBlackjack ();
        BlackjackPlayer player = stackTheDeck (machine, "7H7S7D", "THJH");
        player.deal (1);
        player.hit ();
        testReturns (player, 2);

        player = stackTheDeck (machine, "7S7H7C", "TH2H9S");
        player.deal (1);
        player.hit ();
        testReturns (player, 1);
    }

	@Test
    public void testInsurance ()
        throws Exception
    {
        CasinoBlackjack machine = new CasinoBlackjack ();
        machine.setMaximumBet (10);

        // Player loses insurance but wins hand.
        BlackjackPlayer player = stackTheDeck (machine, "7H7S6D", "8HAH");
        player.deal (2);
        player.insurance();
        player.hit();
        player.stand();
        testReturns (player, 4);
        testInsuranceBet (player, 1);
        testInsuranceWin (player, 0);

        // Player loses insurance and loses hand.
        player = stackTheDeck (machine, "7H7S3D", "9HAH");
        player.deal (2);
        player.insurance();
        player.hit();
        player.stand();
        testReturns (player, 0);
        testInsuranceBet (player, 1);
        testInsuranceWin (player, 0);

        // Regression test for bug case.
        player = stackTheDeck (machine, "7H7S3D", "ACAHTC9C");
        player.deal (2);
        player.insurance();
        player.hit();         // hit was not allowed here!
        player.stand();
        testReturns (player, 0);
        testInsuranceBet (player, 1);
        testInsuranceWin (player, 0);

        // Player wins insurance bet and loses hand.
        player = stackTheDeck (machine, "7H7S7D", "THAH");
        player.deal (4);
        player.insurance();
        testReturns (player, 0);
        testInsuranceBet (player, 2);
        testInsuranceWin (player, 6);

        // Player wins insurance bet and hand pushes.
        player = stackTheDeck (machine, "JHAD", "THAH");
        player.deal (4);
        player.insurance();
        testReturns (player, 4);
        testInsuranceBet (player, 2);
        testInsuranceWin (player, 6);
    }

	@Test
    public void testLucky777 ()
        throws Exception
    {
        CasinoBlackjack machine = new Lucky777 (6);
        BlackjackPlayer player = stackTheDeck (machine, "7H7S7D", "THJH");
        player.deal (1);
        player.hit ();
        testReturns (player, 7);

        player = stackTheDeck (machine, "7S7S7S", "THJH");
        player.deal (1);
        player.hit ();
        testReturns (player, 8);

        player = stackTheDeck (machine, "7D7H7S", "TH2H9S");
        player.deal (1);
        player.hit ();
        testReturns (player, 6);

        player = stackTheDeck (machine, "7S7S7S", "TH2H9S");
        player.deal (1);
        player.hit ();
        testReturns (player, 7);
    }

    static BlackjackPlayer stackTheDeck (
        BlackjackMachine machine,
        String playerCards,
        String dealerCards)
        throws Exception
    {
        Casino casino = new TestCasino (machine);
        BlackjackGame game = new BlackjackGame (casino, machine);
        game.setShoe(Utils.stackedShoe (machine, playerCards, dealerCards));
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

    /**
     * Assert insurance bet is as expected.
     */
    public static void testInsuranceBet (BlackjackPlayer player, int expected)
		throws Exception
    {
        Money bet = player.getBlackjackGame().getInsuranceBet();
		assertEquals(expected, bet == null ? 0 : bet.intValue());
    }

    /**
     * Assert insurance win is as expected.
     */
    public static void testInsuranceWin (BlackjackPlayer player, int expected)
		throws Exception
    {
        Money win = player.getBlackjackGame().getInsuranceWin();
		assertEquals(expected, win == null ? 0 : win.intValue());
    }
}
