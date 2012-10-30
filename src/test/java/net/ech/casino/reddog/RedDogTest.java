//
// RedDogTest.java  
// 

package net.ech.casino.reddog;

import net.ech.casino.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test driver for Red Dog.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public final class RedDogTest implements Constants
{
    private static int ANTE = 2;

    @Test
	public void testTie ()
        throws Exception
    {
        assertTieWinEither ("2S2C2D");
        assertTieWinEither ("6S6C6D");
        assertTieWinEither ("8S8C8D");
        assertTieWinEither ("TSTCTD");
        assertTiePushEither ("3D3H4D");
        assertTiePushEither ("4C4HAC");
        assertTiePushEither ("KCKHAC");
    }

    @Test
	public void testPush ()
        throws Exception
    {
        assertPushEither ("2D3H");
        assertPushEither ("7D8H");
        assertPushEither ("ACKH");
    }

    @Test
	public void testSpreads ()
        throws Exception
    {
        assertMissEither ("2D4HAS");
        assertMissEither ("2D4HKS");
        assertMissEither ("2D4H2S");
        assertMissEither ("2D4H4S");
        assertMissEither ("2D4H5S");
        assertHitEither ("2D4H3S", 5);

        assertMissEither ("3H6S2C");
        assertMissEither ("3H6S3C");
        assertMissEither ("3H6S6C");
        assertMissEither ("3H6S7C");
        assertMissEither ("3H6S9C");
        assertMissEither ("3H6SKC");
        assertHitEither ("3H6S4C", 4);
        assertHitEither ("3H6S5C", 4);

        assertMissEither ("4H8S2C");
        assertMissEither ("4H8S3C");
        assertMissEither ("4H8S4C");
        assertMissEither ("4H8S8C");
        assertMissEither ("4H8STC");
        assertMissEither ("4H8SAC");
        assertHitEither ("4H8S5C", 2);
        assertHitEither ("4H8S6C", 2);
        assertHitEither ("4H8S7C", 2);

        assertMissEither ("4H9S2C");
        assertMissEither ("4H9S3C");
        assertMissEither ("4H9S4C");
        assertMissEither ("4H9S9C");
        assertMissEither ("4H9STC");
        assertMissEither ("4H9SAC");
        assertHitEither ("4H9S5C", 1);
        assertHitEither ("4H9S6C", 1);
        assertHitEither ("4H9S7C", 1);
        assertHitEither ("4H9S8C", 1);

        assertMissEither ("QS3CAD");
        assertMissEither ("QS3CKD");
        assertMissEither ("QS3CQD");
        assertMissEither ("QS3C2D");
        assertMissEither ("QS3C3D");
        assertHitEither ("QS3C4D", 1);
        assertHitEither ("QS3C8D", 1);
        assertHitEither ("QS3CJD", 1);

        assertMissEither ("AS2CAD");
        assertMissEither ("AS2C2D");
        assertHitEither ("AS2C3D", 1);
        assertHitEither ("AS2C8D", 1);
        assertHitEither ("AS2CKD", 1);
    }
    
    @Test
	public void testRaise ()
        throws Exception
    {
        assertRaiseable ("ACAS", 1, false);
        assertRaiseable ("ACKS", 1, false);
        for (int raise = 0; raise <= ANTE; ++raise)
        {
            assertRaiseable ("ACQC", raise, true);
        }
        assertRaiseable ("ACQC", ANTE + 1, false);
    }

    private void assertTieWinEither (String cards)
        throws Exception
    {
        assertTieWin (cards);
        assertTieWin (switchup (cards));
    }

    private void assertTiePushEither (String cards)
        throws Exception
    {
        assertTiePush (cards);
        assertTiePush (switchup (cards));
    }

    private void assertPushEither (String cards)
        throws Exception
    {
        assertPush (cards);
        assertPush (switchup (cards));
    }

    private void assertMissEither (String cards)
        throws Exception
    {
        assertMiss (cards);
        assertMiss (switchup (cards));
    }

    private void assertHitEither (String cards, int expectedPayFactor)
        throws Exception
    {
        assertHit (cards, expectedPayFactor);
        assertHit (switchup (cards), expectedPayFactor);
    }

    private String switchup (String cards)
    {
        return cards.substring(2, 4) +
               cards.substring(0, 2) +
               cards.substring (4);
    }

    private void assertTieWin (String cards)
        throws Exception
    {
        RedDogGame game = setupGame (cards);
        assertSpread (game, -1);
        assertCards (game, cards);
        assertWin (game, 11);
    }

    private void assertTiePush (String cards)
        throws Exception
    {
        RedDogGame game = setupGame (cards);
        assertSpread (game, -1);
        assertCards (game, cards);
        assertPush (game);
    }

    private void assertPush (String cards)
        throws Exception
    {
        RedDogGame game = setupGame (cards);
        assertPush (game);

        game = setupGame (cheapMachine(), cards);
        assertPush (game);
    }

    private void assertMiss (String cards)
        throws Exception
    {
        RedDogGame game = setupGame (cards);
        continueNoRaise (game);
        assertLoss (game);
    }

    private void assertHit (String cards, int expectedPayFactor)
        throws Exception
    {
        RedDogGame game = setupGame (cards);
        continueNoRaise (game);
        assertWin (game, expectedPayFactor);
    }

    private void assertRaiseable (String cards, int raise,
                                  boolean expectToRaise)
        throws Exception
    {
        RedDogGame game = setupGame (cards);
        assertSeatState (game, SEAT_READY);
        try
        {
            ContinuePlay play = game.getContinuePlay (0);
            play.setRaise (new Money (raise));
            play.activate ();
            if (!expectToRaise)
            {
                fail("raise should not be allowed");
            }
        }
        catch (CasinoException e)
        {
            if (expectToRaise)
            {
                throw e;
            }
        }
    }

    private RedDogGame setupGame (String cards)
        throws Exception
    {
        return setupGame (generousMachine (), cards);
    }

    private RedDogMachine cheapMachine ()
    {
        RedDogMachine machine = new RedDogMachine ();
        machine.setMaximumBet (ANTE);
        return machine;
    }

    private RedDogMachine generousMachine ()
    {
        RedDogMachine machine = new RedDogMachine ();
        machine.setMaximumBet (ANTE);
        machine.setMultipleAppliedToAnte (true);
        return machine;
    }

    private RedDogGame setupGame (RedDogMachine machine, String cards)
        throws Exception
    {
        RedDogGame game = stackTheDeck (machine, cards);
        assertClearGame (game);
        AntePlay antePlay = game.getAntePlay (0);
        antePlay.setAnteBet (new Bet (ANTE, ""));
        antePlay.activate ();
        assertCards (game, cards.substring (0, 4) + "**");
        assertBet (game.getTable().getSeat(0).getAnteBet(), ANTE, "ante bet");
        assertBet (game.getTable().getSeat(0).getTotalBet(), ANTE, "total bet");
        return game;
    }

    private RedDogGame stackTheDeck (RedDogMachine machine, String cards)
        throws Exception
    {
		Casino casino = new TestCasino (machine);
        RedDogGame game = new RedDogGame (casino, machine);
        Shoe shoe = machine.createNewShoe ();
        shoe.setStackedCards (cards);
        game.setShoe (shoe);

        // Until damned Player class goes away...
        Player player = new Player ("test", game);
        player.sitDown ();
        return game;
    }

    private void continueNoRaise (RedDogGame game)
        throws Exception
    {
        assertTableState (game, TABLE_WORKING);
        assertSeatState (game, SEAT_READY);
        ContinuePlay play = game.getContinuePlay (0);
        play.activate ();
    }

    private void assertClearGame (RedDogGame game)
    {
        assertTableState (game, TABLE_CLEAR);
        assertCards (game, "");
        assertNull (game.getTable().getSeat(0).getAnteBet());
        assertNull (game.getTable().getSeat(0).getTotalBet());
        assertNull (game.getTable().getSeat(0).getTake());
        assertSpread (game, -1);
    }

    private void assertWin (RedDogGame game, int expectedPayFactor)
    {
        assertTableState (game, TABLE_END_OF_HAND);
        assertBet (game.getTable().getSeat(0).getAnteBet(), ANTE, "ante bet");
        Money totalBetAmount =
            game.getTable().getSeat(0).getTotalBet().getAmount();
        assertTake(game, totalBetAmount.multiply (expectedPayFactor + 1));
    }

    private void assertPush (RedDogGame game)
    {
        assertTableState (game, TABLE_END_OF_HAND);
        assertBet (game.getTable().getSeat(0).getAnteBet(), ANTE, "ante bet");
        assertBet (game.getTable().getSeat(0).getTotalBet(), ANTE, "total bet");
        assertTake(game, ANTE);
    }

    private void assertLoss (RedDogGame game)
    {
        assertTableState (game, TABLE_END_OF_HAND);
        assertBet (game.getTable().getSeat(0).getAnteBet(), ANTE, "ante bet");
        assertNull (game.getTable().getSeat(0).getTake());
    }

    private void assertCards (RedDogGame game, String cards)
    {
        assertCard (game, 0, cards);
        assertCard (game, 1, cards.length() > 2 ? cards.substring (2) : null);
        assertCard (game, 2, cards.length() > 4 ? cards.substring (4) : null);
    }

    private void assertCard (RedDogGame game, int cardIndex, String cardString)
    {
        if (cardString != null && cardString.equals("**"))
            return;
        byte card2 = (cardString == null || cardString.length() == 0)
                        ? NilCard : Card.parse (cardString);
        assertEquals (
			"card #" + cardIndex,
			new Byte (game.getTable().getCard(cardIndex)),
			new Byte (card2));
    }

    private void assertBet (Bet bet, int expectedAmount, String what)
    {
        assertEquals (what, bet.getAmount(), new Money (expectedAmount));
    }

    private void assertTableState (RedDogGame game, int expected)
    {
        assertEquals ("table state", new Integer (game.getTable().getState()), 
                     new Integer (expected));
    }

    private void assertSeatState (RedDogGame game, int expected)
    {
        assertEquals ("seat state", new Integer (game.getTable().getSeat(0).getState()), 
                     new Integer (expected));
    }

    public void assertSpread (RedDogGame game, int expected)
    {
        assertEquals ("spread", new Integer (game.getTable().getSpread()), 
                     new Integer (expected));
    }

    public void assertTake (RedDogGame game, int expected)
    {
        assertTake (game, new Money (expected));
    }

    public void assertTake (RedDogGame game, Money expected)
    {
        assertEquals ("take", game.getTable().getSeat(0).getTake(), expected);
    }
}
