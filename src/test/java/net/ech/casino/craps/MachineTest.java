//
// MachineTest.java  
// 

package net.ech.casino.craps;

import net.ech.casino.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test driver for Craps - static machine tests only.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public final class MachineTest implements Constants
{
	CrapsMachine machine;

	@Before
	public void setUp()
		throws Exception
	{
        machine = new CrapsMachine ();
	}

	@Test
    public void testSingleOddsPass ()
        throws Exception
    {
        machine.setOdds (1);
        assertMaxPassOddsBet (1, 1);
        assertMaxPassOddsBet (3, 3);
        assertMaxPassOddsBet (12, 12);
        assertPassOddsWin ();
    }

	@Test
    public void testDoubleOddsPass ()
        throws Exception
    {
        machine.setOdds (2);
        assertMaxPassOddsBet (1, 2);
        assertMaxPassOddsBet (3, 6);
        assertMaxPassOddsBet (12, 24);
        assertPassOddsWin ();
    }

	@Test
    public void testTripleOddsPass ()
        throws Exception
    {
        machine.setOdds (3);
        assertMaxPassOddsBet (1, 3);
        assertMaxPassOddsBet (3, 9);
        assertMaxPassOddsBet (12, 36);
        assertPassOddsWin ();
    }

    private void assertPassOddsWin ()
        throws Exception
    {
        assertPassOddsWin (4, 1, 2);
        assertPassOddsWin (4, 2, 4);
        assertPassOddsWin (4, 3, 6);
        assertPassOddsWin (4, 4, 8);
        assertPassOddsWin (4, 5, 10);
        assertPassOddsWin (4, 6, 12);
        assertPassOddsWin (4, 10, 20);
        assertPassOddsWin (4, 11, 22);
        assertPassOddsWin (4, 12, 24);
        assertPassOddsWin (4, 19, 38);
        assertPassOddsWin (4, 24, 48);
        assertPassOddsWin (4, 39, 78);

        assertPassOddsWin (5, 1, 1);
        assertPassOddsWin (5, 2, 3);
        assertPassOddsWin (5, 3, 4);
        assertPassOddsWin (5, 5, 7);
        assertPassOddsWin (5, 6, 9);
        assertPassOddsWin (5, 11, 16);
        assertPassOddsWin (5, 12, 18);
        assertPassOddsWin (5, 19, 28);
        assertPassOddsWin (5, 24, 36);

        assertPassOddsWin (6, 1, 1);
        assertPassOddsWin (6, 4, 4);
        assertPassOddsWin (6, 5, 6);
        assertPassOddsWin (6, 11, 13);
        assertPassOddsWin (6, 12, 14);
        assertPassOddsWin (6, 20, 24);
        assertPassOddsWin (6, 24, 28);

        assertPassOddsWin (8, 1, 1);
        assertPassOddsWin (8, 4, 4);
        assertPassOddsWin (8, 5, 6);
        assertPassOddsWin (8, 11, 13);
        assertPassOddsWin (8, 12, 14);

        assertPassOddsWin (9, 1, 1);
        assertPassOddsWin (9, 2, 3);
        assertPassOddsWin (9, 3, 4);
        assertPassOddsWin (9, 5, 7);
        assertPassOddsWin (9, 11, 16);
        assertPassOddsWin (9, 12, 18);

        assertPassOddsWin (10, 1, 2);
        assertPassOddsWin (10, 2, 4);
        assertPassOddsWin (10, 3, 6);
        assertPassOddsWin (10, 5, 10);
        assertPassOddsWin (10, 10, 20);
        assertPassOddsWin (10, 12, 24);
        assertPassOddsWin (10, 17, 34);
        assertPassOddsWin (10, 35, 70);
    }

	@Test
    public void testSingleOddsDontPass ()
        throws Exception
    {
        machine.setOdds (1);
        assertMaxDontPassOddsBet (4, 1, 2);
        assertMaxDontPassOddsBet (4, 3, 6);
        assertMaxDontPassOddsBet (4, 12, 24);

        assertMaxDontPassOddsBet (5, 1, 2);
        assertMaxDontPassOddsBet (5, 4, 6);
        assertMaxDontPassOddsBet (5, 13, 20);
  
        assertMaxDontPassOddsBet (6, 1, 2);
        assertMaxDontPassOddsBet (6, 5, 6);
        assertMaxDontPassOddsBet (6, 13, 16);
  
        assertMaxDontPassOddsBet (8, 1, 2);
        assertMaxDontPassOddsBet (8, 5, 6);
        assertMaxDontPassOddsBet (8, 13, 16);

        assertMaxDontPassOddsBet (9, 1, 2);
        assertMaxDontPassOddsBet (9, 4, 6);
        assertMaxDontPassOddsBet (9, 13, 20);

        assertMaxDontPassOddsBet (10, 1, 2);
        assertMaxDontPassOddsBet (10, 3, 6);
        assertMaxDontPassOddsBet (10, 12, 24);

        assertDontPassOddsWin (4, 2, 1);
        assertDontPassOddsWin (4, 4, 2);
        assertDontPassOddsWin (4, 6, 3);
        assertDontPassOddsWin (4, 11, 5);
        assertDontPassOddsWin (4, 21, 10);
        assertDontPassOddsWin (4, 24, 12);

        assertDontPassOddsWin (5, 2, 1);
        assertDontPassOddsWin (5, 5, 3);
        assertDontPassOddsWin (5, 6, 4);
        assertDontPassOddsWin (5, 19, 12);
        assertDontPassOddsWin (5, 20, 13);

        assertDontPassOddsWin (6, 2, 1);
        assertDontPassOddsWin (6, 3, 2);
        assertDontPassOddsWin (6, 6, 5);
        assertDontPassOddsWin (6, 14, 11);
        assertDontPassOddsWin (6, 16, 13);

        assertDontPassOddsWin (8, 2, 1);
        assertDontPassOddsWin (8, 3, 2);
        assertDontPassOddsWin (8, 6, 5);
        assertDontPassOddsWin (8, 14, 11);
        assertDontPassOddsWin (8, 16, 13);

        assertDontPassOddsWin (9, 2, 1);
        assertDontPassOddsWin (9, 5, 3);
        assertDontPassOddsWin (9, 6, 4);
        assertDontPassOddsWin (9, 19, 12);
        assertDontPassOddsWin (9, 20, 13);

        assertDontPassOddsWin (10, 2, 1);
        assertDontPassOddsWin (10, 4, 2);
        assertDontPassOddsWin (10, 6, 3);
        assertDontPassOddsWin (10, 11, 5);
        assertDontPassOddsWin (10, 21, 10);
        assertDontPassOddsWin (10, 24, 12);
    }

	@Test
    public void testDoubleOddsDontPass ()
        throws Exception
    {
        machine.setOdds (2);

        assertMaxDontPassOddsBet (4, 1, 4);
        assertMaxDontPassOddsBet (4, 3, 12);
        assertMaxDontPassOddsBet (4, 12, 48);
        
        assertMaxDontPassOddsBet (5, 1, 3);
        assertMaxDontPassOddsBet (5, 4, 12);
        assertMaxDontPassOddsBet (5, 13, 39);
        
        assertMaxDontPassOddsBet (6, 1, 3);
        assertMaxDontPassOddsBet (6, 5, 12);
        assertMaxDontPassOddsBet (6, 13, 32);
        
        assertMaxDontPassOddsBet (8, 1, 3);
        assertMaxDontPassOddsBet (8, 5, 12);
        assertMaxDontPassOddsBet (8, 13, 32);
        
        assertMaxDontPassOddsBet (9, 1, 3);
        assertMaxDontPassOddsBet (9, 4, 12);
        assertMaxDontPassOddsBet (9, 13, 39);

        assertMaxDontPassOddsBet (10, 1, 4);
        assertMaxDontPassOddsBet (10, 3, 12);
        assertMaxDontPassOddsBet (10, 12, 48);

        assertDontPassOddsWin (4, 4, 2);
        assertDontPassOddsWin (4, 12, 6);
        assertDontPassOddsWin (4, 25, 12);
        assertDontPassOddsWin (4, 39, 19);
        assertDontPassOddsWin (4, 48, 24);

        assertDontPassOddsWin (5, 2, 1);
        assertDontPassOddsWin (5, 3, 2);
        assertDontPassOddsWin (5, 10, 6);
        assertDontPassOddsWin (5, 12, 8);
        assertDontPassOddsWin (5, 27, 18);
        assertDontPassOddsWin (5, 39, 26);

        assertDontPassOddsWin (6, 2, 1);
        assertDontPassOddsWin (6, 3, 2);
        assertDontPassOddsWin (6, 5, 4);
        assertDontPassOddsWin (6, 10, 8);
        assertDontPassOddsWin (6, 12, 10);
        assertDontPassOddsWin (6, 30, 25);
        assertDontPassOddsWin (6, 32, 26);

        assertDontPassOddsWin (8, 2, 1);
        assertDontPassOddsWin (8, 3, 2);
        assertDontPassOddsWin (8, 5, 4);
        assertDontPassOddsWin (8, 10, 8);
        assertDontPassOddsWin (8, 12, 10);
        assertDontPassOddsWin (8, 30, 25);
        assertDontPassOddsWin (8, 32, 26);

        assertDontPassOddsWin (9, 2, 1);
        assertDontPassOddsWin (9, 3, 2);
        assertDontPassOddsWin (9, 10, 6);
        assertDontPassOddsWin (9, 12, 8);
        assertDontPassOddsWin (9, 27, 18);
        assertDontPassOddsWin (9, 39, 26);

        assertDontPassOddsWin (10, 4, 2);
        assertDontPassOddsWin (10, 12, 6);
        assertDontPassOddsWin (10, 25, 12);
        assertDontPassOddsWin (10, 39, 19);
        assertDontPassOddsWin (10, 48, 24);
    }

	@Test
    public void testTripleOddsDontPass ()
        throws Exception
    {
        machine.setOdds (3);

        assertMaxDontPassOddsBet (4, 1, 6);
        assertMaxDontPassOddsBet (4, 3, 18);
        assertMaxDontPassOddsBet (4, 15, 90);

        assertMaxDontPassOddsBet (5, 1, 5);
        assertMaxDontPassOddsBet (5, 4, 18);
        assertMaxDontPassOddsBet (5, 13, 59);

        assertMaxDontPassOddsBet (6, 1, 4);
        assertMaxDontPassOddsBet (6, 5, 12);
        assertMaxDontPassOddsBet (6, 13, 32);

        assertMaxDontPassOddsBet (8, 1, 4);
        assertMaxDontPassOddsBet (8, 5, 12);
        assertMaxDontPassOddsBet (8, 13, 32);

        assertMaxDontPassOddsBet (9, 1, 5);
        assertMaxDontPassOddsBet (9, 4, 18);
        assertMaxDontPassOddsBet (9, 13, 59);

        assertMaxDontPassOddsBet (10, 1, 6);
        assertMaxDontPassOddsBet (10, 3, 18);
        assertMaxDontPassOddsBet (10, 15, 90);

        assertDontPassOddsWin (4, 6, 3);
        assertDontPassOddsWin (4, 15, 7);
        assertDontPassOddsWin (4, 18, 9);
        assertDontPassOddsWin (4, 38, 19);
        assertDontPassOddsWin (4, 65, 32);
        assertDontPassOddsWin (4, 90, 45);

        assertDontPassOddsWin (5, 2, 1);
        assertDontPassOddsWin (5, 5, 3);
        assertDontPassOddsWin (5, 18, 12);
        assertDontPassOddsWin (5, 59, 39);

        assertDontPassOddsWin (6, 3, 2);
        assertDontPassOddsWin (6, 4, 3);
        assertDontPassOddsWin (6, 6, 5);
        assertDontPassOddsWin (6, 12, 10);
    }

	@Test
    public void testMaxOddsSanity ()
        throws Exception
    {
        for (int odds = 1; odds < 5; ++odds)
        {
            machine.setOdds (odds);

            for (int pointIndex = 0; pointIndex < points.length; ++pointIndex)
            {
                int point = points[pointIndex];

                for (int bet = 1; bet < 100; ++bet)
                {
                    int maxOdds = machine.getMaxDontPassOddsBet (point, bet);
                    int winForMax = machine.getDontPassOddsWin (point, maxOdds);
                    int expectedWin = machine.getMaxPassOddsBet (bet);
                    if (winForMax != expectedWin)
                    {
                        throw new RuntimeException (
                            "odds sanity failure, bet=" + bet + " point=" + point);
                    }
                }
            }
        }
    }

    private void assertMaxPassOddsBet (int passLineBet,
                                       int expectedMax)
    {
        int max = machine.getMaxPassOddsBet (passLineBet);
        assertTrue (
			machine.getOdds() + "X machine should allow $" +
			expectedMax + " odds on a $" + passLineBet + 
			" pass line bet but allows only $" + max,
			max >= expectedMax);
    }

    private void assertPassOddsWin (int point,
                                    int oddsBet,
                                    int expectedWin)
    {
        int win = machine.getPassOddsWin (point, oddsBet);
		assertEquals (
			machine.getOdds() + "X machine should pay $" +
			expectedWin + " on a $" + oddsBet + " pass odds bet on " +
			point + " but paid " + win,
			expectedWin, win);
    }

    public void assertMaxDontPassOddsBet (int point,
                                           int dontPassBet,
                                           int expectedMax)
    {
        int max = machine.getMaxDontPassOddsBet (point, dontPassBet);
		assertTrue (
			machine.getOdds() + "X machine should allow $" +
			expectedMax + " odds on a $" + dontPassBet +
			" don't pass bet on " +
			point + " but allows only $" + max,
			max >= expectedMax);
    }

    public void assertDontPassOddsWin (int point,
                                        int oddsBet,
                                        int expectedWin)
    {
        int win = machine.getDontPassOddsWin (point, oddsBet);
		assertEquals (
			machine.getOdds() + "X machine should pay $" +
			expectedWin + " on a $" + oddsBet + " don't pass odds bet " +
			" but paid " + win,
			expectedWin, win);
    }
}
