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
    public void testPush1 ()
        throws Exception
    {
        BlackjackGame game = stackTheDeck (new CasinoBlackjack(), "THAD", "AHTD");
        game.deal (1);
        testReturns (game, 1);
	}

	@Test
    public void testPush2 ()
        throws Exception
    {
        BlackjackGame game = stackTheDeck (new CasinoBlackjack(), "TH6H5H", "TD6D5D");
        game.deal (1);
        game.hit ();
        testReturns (game, 1);
    }

	@Test
    public void testDealerHitsSoft17AndWins ()
        throws Exception
    {
        CasinoBlackjack machine = new CasinoBlackjack ();
        machine.setDealerHitsSoft17 (true);
        BlackjackGame game = stackTheDeck (machine, "THTD", "5HASAC4D");
        game.deal (1);
        game.stand ();
        testReturns (game, 0);
	}

	@Test
    public void testDealerHitsSoft17AndLoses ()
        throws Exception
    {
        CasinoBlackjack machine = new CasinoBlackjack ();
        machine.setDealerHitsSoft17 (false);
        BlackjackGame game = stackTheDeck (machine, "THTD", "5HASAC4D");
        game.deal (1);
        game.stand ();
        testReturns (game, 2);
    }

	@Test
    public void testScoring ()
        throws Exception
    {
        BlackjackGame game = stackTheDeck (new CasinoBlackjack(), "7H7S7D", "THJH");
        game.deal (1);
        game.hit ();
        testReturns (game, 2);
	}

	@Test
    public void testScoring2 ()
        throws Exception
	{
        BlackjackGame game = stackTheDeck (new CasinoBlackjack(), "7S7H7C", "TH2H9S");
        game.deal (1);
        game.hit ();
        testReturns (game, 1);
    }

	@Test
    public void testInsurance ()
        throws Exception
    {
        CasinoBlackjack machine = new CasinoBlackjack ();
        machine.setMaximumBet (10);

        // Player loses insurance but wins hand.
        BlackjackGame game = stackTheDeck (machine, "7H7S6D", "8HAH");
        game.deal (2);
        game.insurance();
        game.hit();
        game.stand();
        testReturns (game, 4);
        testInsuranceBet (game, 1);
        testInsuranceWin (game, 0);

        // Player loses insurance and loses hand.
        game = stackTheDeck (machine, "7H7S3D", "9HAH");
        game.deal (2);
        game.insurance();
        game.hit();
        game.stand();
        testReturns (game, 0);
        testInsuranceBet (game, 1);
        testInsuranceWin (game, 0);

        // Regression test for bug case.
        game = stackTheDeck (machine, "7H7S3D", "ACAHTC9C");
        game.deal (2);
        game.insurance();
        game.hit();         // hit was not allowed here!
        game.stand();
        testReturns (game, 0);
        testInsuranceBet (game, 1);
        testInsuranceWin (game, 0);

        // Player wins insurance bet and loses hand.
        game = stackTheDeck (machine, "7H7S7D", "THAH");
        game.deal (4);
        game.insurance();
        testReturns (game, 0);
        testInsuranceBet (game, 2);
        testInsuranceWin (game, 6);

        // Player wins insurance bet and hand pushes.
        game = stackTheDeck (machine, "JHAD", "THAH");
        game.deal (4);
        game.insurance();
        testReturns (game, 4);
        testInsuranceBet (game, 2);
        testInsuranceWin (game, 6);
    }

	@Test
    public void testLucky777 ()
        throws Exception
    {
        CasinoBlackjack machine = new Lucky777 (6);
        BlackjackGame game = stackTheDeck (machine, "7H7S7D", "THJH");
        game.deal (1);
        game.hit ();
        testReturns (game, 7);

        game = stackTheDeck (machine, "7S7S7S", "THJH");
        game.deal (1);
        game.hit ();
        testReturns (game, 8);

        game = stackTheDeck (machine, "7D7H7S", "TH2H9S");
        game.deal (1);
        game.hit ();
        testReturns (game, 6);

        game = stackTheDeck (machine, "7S7S7S", "TH2H9S");
        game.deal (1);
        game.hit ();
        testReturns (game, 7);
    }

    static BlackjackGame stackTheDeck (
        BlackjackMachine machine,
        String playerCards,
        String dealerCards)
        throws Exception
    {
        Casino casino = new TestCasino (machine);
        BlackjackGame game = new BlackjackGame (casino, machine);
        game.setShoe(Utils.stackedShoe (machine, playerCards, dealerCards));
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

    /**
     * Assert insurance bet is as expected.
     */
    public static void testInsuranceBet (BlackjackGame game, int expected)
		throws Exception
    {
        Money bet = game.getInsuranceBet();
		assertEquals(expected, bet == null ? 0 : bet.intValue());
    }

    /**
     * Assert insurance win is as expected.
     */
    public static void testInsuranceWin (BlackjackGame game, int expected)
		throws Exception
    {
        Money win = game.getInsuranceWin();
		assertEquals(expected, win == null ? 0 : win.intValue());
    }
}
