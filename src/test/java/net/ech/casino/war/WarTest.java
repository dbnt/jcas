//
// WarTest.java  
// 

package net.ech.casino.war;

import net.ech.casino.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test driver for Casino War.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public final class WarTest implements Constants
{
    @Test
	public void testPlayerWinsDeal ()
        throws Exception
    {
        assertPlayerWinsDeal ("3D", "2H");
        assertPlayerWinsDeal ("4C", "2S");
        assertPlayerWinsDeal ("5H", "2D");
        assertPlayerWinsDeal ("6S", "2C");
        assertPlayerWinsDeal ("7D", "6H");
        assertPlayerWinsDeal ("8C", "7S");
        assertPlayerWinsDeal ("9H", "8D");
        assertPlayerWinsDeal ("TS", "9C");
        assertPlayerWinsDeal ("JD", "TH");
        assertPlayerWinsDeal ("QC", "JS");
        assertPlayerWinsDeal ("KH", "QD");
        assertPlayerWinsDeal ("AS", "KC");
    }

    private void assertPlayerWinsDeal (String pCards, String dCards)
        throws Exception
    {
        WarMachine machine = new WarMachine ();
        WarGame game = stackTheDeck (machine, pCards, dCards);
        ante (game, 1, 0);
        assertEquals (Card.parse(pCards), game.getTable().getSeat(0).getPlayerCard(0));
        assertEquals (NilCard, game.getTable().getSeat(0).getPlayerCard(PLAYER_CARD_WAR));
        assertEquals (Card.parse(dCards), game.getTable().getDealerCard(0));
        assertEquals (NilCard, game.getTable().getDealerCard(DEALER_CARD_WAR));
        assertFinalTake (game, 2);
    }

    @Test
	public void testDealerWinsDeal ()
        throws Exception
    {
        assertDealerWinsDeal ("2H", "3D");
        assertDealerWinsDeal ("2S", "4C");
        assertDealerWinsDeal ("2D", "5H");
        assertDealerWinsDeal ("2C", "6S");
        assertDealerWinsDeal ("6H", "7D");
        assertDealerWinsDeal ("7S", "8C");
        assertDealerWinsDeal ("8D", "9H");
        assertDealerWinsDeal ("9C", "TS");
        assertDealerWinsDeal ("TH", "JD");
        assertDealerWinsDeal ("JS", "QC");
        assertDealerWinsDeal ("QD", "KH");
        assertDealerWinsDeal ("KC", "AS");
    }

    private void assertDealerWinsDeal (String pCards, String dCards)
        throws Exception
    {
        WarMachine machine = new WarMachine ();
        WarGame game = stackTheDeck (machine, pCards, dCards);
        ante (game, 1, 0);
        assertFinalTake (game, 0);
    }

    @Test
	public void testSurrender ()
        throws Exception
    {
        assertSurrender ("2H", "2C");
        assertSurrender ("3C", "3D");
        assertSurrender ("4D", "4S");
        assertSurrender ("5S", "5H");
        assertSurrender ("6S", "6S");
    }

    private void assertSurrender (String pCards, String dCards)
        throws Exception
    {
        WarMachine machine = new WarMachine ();
        machine.setMaximumBet (10);
        WarGame game = stackTheDeck (machine, pCards, dCards);
        ante (game, 2, 0);
        assertQuitLegal(game, false);
        game.getSurrenderPlay(0).activate();
        assertFinalTake (game, 1);
    }

    @Test
	public void testPlayerWinsWar ()
        throws Exception
    {
        WarMachine machine = new WarMachine ();
        assertWarOutcome (machine, "5D3D", "5H2H", 3);
        assertWarOutcome (machine, "AH4C", "AH2S", 3);
        assertWarOutcome (machine, "3S5H", "3S2D", 3);
        assertWarOutcome (machine, "KC6S", "KC2C", 3);
        assertWarOutcome (machine, "2D7D", "2D6H", 3);
        assertWarOutcome (machine, "9H8C", "9H7S", 3);
        assertWarOutcome (machine, "5C9H", "5C8D", 3);
        assertWarOutcome (machine, "8HTS", "8H9C", 3);
        assertWarOutcome (machine, "QHJD", "QHTH", 3);
        assertWarOutcome (machine, "8SQC", "8DJS", 3);
        assertWarOutcome (machine, "4DKH", "4CQD", 3);
        assertWarOutcome (machine, "4SAS", "4HKC", 3);
    }

    @Test
	public void testDealerWinsWar ()
        throws Exception
    {
        WarMachine machine = new WarMachine ();
        assertWarOutcome (machine, "5H2H", "5D3D", 0);
        assertWarOutcome (machine, "AH2S", "AH4C", 0);
        assertWarOutcome (machine, "3S2D", "3S5H", 0);
        assertWarOutcome (machine, "KC2C", "KC6S", 0);
        assertWarOutcome (machine, "2D6H", "2D7D", 0);
        assertWarOutcome (machine, "9H7S", "9H8C", 0);
        assertWarOutcome (machine, "5C8D", "5C9H", 0);
        assertWarOutcome (machine, "8H9C", "8HTS", 0);
        assertWarOutcome (machine, "QHTH", "QHJD", 0);
        assertWarOutcome (machine, "8DJS", "8SQC", 0);
        assertWarOutcome (machine, "4CQD", "4DKH", 0);
        assertWarOutcome (machine, "4HKC", "4SAS", 0);
    }

    @Test
	public void testTieBonus ()
        throws Exception
    {
        WarMachine machine = new WarMachine ();
        machine.setWarTieBonusEnabled (false);
        assertWarOutcome (machine, "3S2D", "3H2H", 3);
        assertWarOutcome (machine, "TS2D", "TS2H", 3);
        assertWarOutcome (machine, "2DQH", "2HQH", 3);
        machine.setWarTieBonusEnabled (true);
        assertWarOutcome (machine, "3S2D", "3H2H", 4);
        assertWarOutcome (machine, "TS2D", "TS2H", 4);
        assertWarOutcome (machine, "2DQH", "2HQH", 4);
    }

    @Test
	public void testVariousTakes ()
        throws Exception
    {
        WarMachine machine = new WarMachine ();
        machine.setMaximumBet (10);
        WarGame game;

        // Win, no side bet on tie.
        game = stackTheDeck (machine, "3H", "2H");
        ante (game, 3, 0);
        assertFinalTake (game, 6);

        // Win, side bet on tie.
        game = stackTheDeck (machine, "3H", "2H");
        ante (game, 3, 1);
        assertFinalTake (game, 6);

        // Loss, no side bet on tie.
        game = stackTheDeck (machine, "2H", "3H");
        ante (game, 3, 0);
        assertFinalTake (game, 0);

        // Loss, side bet on tie.
        game = stackTheDeck (machine, "2H", "3H");
        ante (game, 3, 1);
        assertFinalTake (game, 0);

        // Surrender, no side bet on tie.
        game = stackTheDeck (machine, "2H3D", "2H2D");
        ante (game, 4, 0);
        assertTake (game, 0);
        game.getSurrenderPlay(0).activate();
        assertFinalTake (game, 2);

        // Surrender, side bet on tie.
        game = stackTheDeck (machine, "2H3D", "2H2D");
        ante (game, 4, 1);
        assertTake (game, 11);
        game.getSurrenderPlay(0).activate();
        assertFinalTake (game, 2);

        // War win, no side bet on tie.
        game = stackTheDeck (machine, "2H3D", "2H2D");
        ante (game, 4, 0);
        assertTake (game, 0);
        goToWar(game);
        assertFinalTake (game, 12);

        // War win, side bet on tie.
        game = stackTheDeck (machine, "2H3D", "2H2D");
        ante (game, 4, 1);
        assertTake (game, 11);
        goToWar(game);
        assertFinalTake (game, 12);

        // War loss, no side bet on tie.
        game = stackTheDeck (machine, "2H2D", "2H3D");
        ante (game, 4, 0);
        assertTake (game, 0);
        goToWar(game);
        assertFinalTake (game, 0);

        // War loss, side bet on tie.
        game = stackTheDeck (machine, "2H2D", "2H3D");
        ante (game, 4, 2);
        assertTake (game, 22);
        goToWar(game);
        assertFinalTake (game, 0);
    }

    private void assertWarOutcome (
        WarMachine machine, String pCards, String dCards, int take)
        throws Exception
    {
        WarGame game = stackTheDeck (machine, pCards, dCards);
        ante (game, 1, 0);

        assertEquals (Card.parse(dCards), game.getTable().getDealerCard(0));
        assertEquals (NilCard, game.getTable().getDealerCard(DEALER_CARD_WAR));
        assertEquals (Card.parse(pCards), game.getTable().getSeat(0).getPlayerCard(0));
        assertEquals (NilCard, game.getTable().getSeat(0).getPlayerCard(PLAYER_CARD_WAR));
        assertTake (game, 0);
   
        goToWar(game);

        assertEquals (Card.parse(pCards), game.getTable().getSeat(0).getPlayerCard(0));
        assertEquals (Card.parse(pCards, 2), game.getTable().getSeat(0).getPlayerCard(PLAYER_CARD_WAR));
        assertEquals (Card.parse(dCards), game.getTable().getDealerCard(0));
        assertEquals (Card.parse(dCards, 2), game.getTable().getDealerCard(DEALER_CARD_WAR));

        assertFinalTake (game, take);
    }

    static WarGame stackTheDeck (
        WarMachine machine,
        String pCards,
        String dCards)
        throws Exception
    {
        Casino casino = new TestCasino (machine);
        WarGame game = new WarGame (casino, machine);
        game.setShoe (stackedShoe (machine, pCards, dCards));
		game.seatPlayer("test");
        assertRoundCount(game, 0);
        return game;
    }

    /**
     * Return a Shoe created by the provided machine but with a stacked
     * deck.
     */
    public static Shoe stackedShoe (
        WarMachine machine,
        String playerCardString,
        String dealerCardString)
    {
        StringBuffer stackedCards = new StringBuffer (48);

        stackedCards.append (playerCardString.substring (0, 2));
        stackedCards.append (dealerCardString.substring (0, 2));

        if (playerCardString.length() > 2)
        {
            // Burn cards:
            stackedCards.append ("------");
            stackedCards.append (playerCardString.substring (2, 4));
            stackedCards.append (dealerCardString.substring (2, 4));
        }

        Shoe shoe = machine.createNewShoe ();
        shoe.setStackedCards (stackedCards.toString());
        return shoe;
    }

    private static void ante (WarGame game, int ante, int tieBet)
        throws CasinoException
    {
        int prevRoundCount = game.getTable().getRoundCount();

        assertQuitLegal(game, true);

        AntePlay controller = game.getAntePlay (0);
        controller.setAnte (new Bet (ante, ""));
        controller.setTieBet (new Bet (tieBet, ""));
        controller.activate ();
        assertBetEqual (game.getTable().getSeat(0).getAnte(),
            new Money (ante));
        assertBetEqual (game.getTable().getSeat(0).getTieBet(),
            new Money (tieBet));
        assertRoundCount(game, prevRoundCount + 1);
    }

    private static void goToWar(WarGame game)
        throws CasinoException
    {
        assertQuitLegal(game, false);

        int prevRoundCount = game.getTable().getRoundCount();
        game.getGoToWarPlay(0).activate();
        assertRoundCount(game, prevRoundCount);
    }

    /**
     * Assert end of hand and player gets expected amount.
     */
    public void assertFinalTake (WarGame game, int expected)
    {
        assertTrue (game.getTable().isEndOfHand());
        assertQuitLegal(game, true);
        assertTake (game, expected);
    }

    /**
     * Assert player gets expected amount.
     */
    public void assertTake (WarGame game, int expected)
    {
        assertMoneyEqual (new Money(expected), game.getTable().getSeat(0).getTake());
    }

    public static void assertBetEqual (Bet b, Money expected)
    {
        assertMoneyEqual (expected, b.getAmount());
    }

    public static void assertMoneyEqual (Money m1, Money m2)
    {
        assertEquals(Money.materialize(m1), Money.materialize(m2));
    }

    public static void assertRoundCount (WarGame game, int expected)
    {
        assertEquals(expected,  game.getTable().getRoundCount());
    }

    public static void assertQuitLegal (WarGame game, boolean expected)
    {
        assertEquals(expected, game.isQuitLegal(0));
    }
}
