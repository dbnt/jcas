//
// CrapsTest.java  
// 

package net.ech.casino.craps;

import net.ech.casino.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

/**
 * Test driver for Casino Craps.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public final class CrapsTest implements Constants
{
	CrapsGame game;
	CrapsPlayer player;

	@Before
	public void setUp()
        throws Exception
	{
        CrapsMachine machine = new CrapsMachine ();
        machine.setTestMode (true);
        Casino casino = new TestCasino (machine);
        game = new CrapsGame (casino, machine);
        player = new CrapsPlayer ("test", game);
        player.sitDown ();
	}

    @Test
	public void testPassLineInstantWin34 ()
        throws Exception
    {
        assertPassLineWin (3, 4);
	}

    @Test
	public void testPassLineInstantWin43 ()
        throws Exception
	{
        assertPassLineWin (4, 3);
	}

    @Test
	public void testPassLineInstantWin16 ()
        throws Exception
	{
        assertPassLineWin (1, 6);
	}

    @Test
	public void testPassLineInstantWin56 ()
        throws Exception
	{
        assertPassLineWin (5, 6);
    }

    private void assertPassLineWin (int die1, int die2)
        throws Exception
    {
        // Bet one on the pass line and roll whatever was specified.
        int[] bets = new int[PASS_LINE + 1];
        bets[PASS_LINE] = 1;
        player.bet (bets, new int[] { die1, die2 });

        assertFalse(game.isOn());
        assertEquals(game.getDie0(), die1);
		assertEquals(game.getDie1(), die2);
		assertEquals(game.getTotalBetMoney(), new Money (1));
        assertEquals(game.getTotalReturn(), 2);
        assertEquals(game.getFinalReturn(PASS_LINE), 2);
        assertEquals(game.getRemainingBet(PASS_LINE), 0);
    }

    @Test
	public void testPoint ()
        throws Exception
    {
        assertPoint (3, 4, 0, 0);
        assertPoint (5, 6, 0, 0);
        assertPoint (1, 1, 0, 0);
        assertPoint (2, 2, 0, 4);
        assertPoint (2, 2, 4, 0);
        assertPoint (3, 3, 0, 6);
        assertPoint (3, 3, 6, 0);
        assertPoint (5, 3, 0, 8);
        assertPoint (1, 3, 8, 8);
        assertPoint (2, 3, 8, 8);
        assertPoint (5, 1, 8, 8);
        assertPoint (6, 6, 8, 8);
        assertPoint (2, 5, 8, 0);
        assertPoint (3, 4, 0, 0);
        assertPoint (5, 6, 0, 0);
        assertPoint (1, 1, 0, 0);
    }

    private void assertPoint (int die1, int die2, 
                              int expectedPreviousPoint,
                              int expectedPoint)
        throws Exception
    {
        // Bet one on the pass line and roll whatever was specified.
        int[] bets = new int[PASS_LINE + 1];
        bets[PASS_LINE] = 1;
        game.play (game.getPlayer(0), bets, new int[] { die1, die2 });

        assertEquals(game.getDie0(), die1);
		assertEquals(game.getDie1(), die2);
		assertEquals(expectedPoint, game.getPoint());
		assertEquals(expectedPreviousPoint, game.getPreviousPoint());
    }
}
