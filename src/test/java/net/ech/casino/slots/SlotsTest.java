//
// SlotsTest.java  
// 

package net.ech.casino.slots;

import net.ech.casino.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test driver for slots.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public final class SlotsTest
{
	TestCasino casino;
	SlotMachine machine;
	SlotsGame game;

	@Before
	public void setUp() throws Exception
	{
        machine = new StumpworldGold ();
        machine.setTestMode(true);
        casino = new TestCasino (machine);
        game = new SlotsGame (casino, machine);
        game.seatPlayer("test");
	}

	@Test
    public void testJackpotSlots ()
        throws Exception
    {
        // Spin the reels until the payout is zero, to reset test mode.
        do
        {
            game.pull (1, false, false);
        }
        while (game.getWin() > 0);

        int bet = machine.getMaximumBet();

        // Assert 5 pay lines with no jackpot claim but jackpot contrib.
        for (int i = 0; i < 5; ++i)
        {
            game.pull (bet, false, true);
            assertContribution (casino, bet * StumpworldGold.CONTRIB);
            assertJackpot (casino, false);
        }

        // Assert jackpot claim.
        game.pull (bet, false, true);
        assertContribution (casino, bet * StumpworldGold.CONTRIB);
        assertJackpot (casino, true);
    }

    private void assertContribution (TestCasino casino,
                                     double expectedAmount)
        throws Exception
    {
        JackpotTransaction jtrans =
            casino.getLastTransaction().getJackpotTransaction(0);
        assertTrue (jtrans.isContribution());
        assertEquals (expectedAmount, jtrans.getContributionAmount(), 0.00001);
    }

    private void assertJackpot (TestCasino casino, boolean jackpot)
        throws Exception
    {
        JackpotTransaction jtrans =
            casino.getLastTransaction().getJackpotTransaction(0);
        assertEquals (jackpot, jtrans.isClaim());
        assertEquals (jackpot ? 1 : 0, jtrans.getClaimFactor(), 0.00001);
    }
}
