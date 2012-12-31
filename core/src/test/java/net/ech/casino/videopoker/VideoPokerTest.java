//
// VideoPokerTest.java  
// 

package net.ech.casino.videopoker;

import net.ech.casino.*;
import java.io.*;
import org.junit.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Test driver for video poker.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public final class VideoPokerTest
{
    @Test
	public void testPayout ()
        throws Exception
    {
        Payout payout = new Payout("foo", 500);
        assertEquals("foo", payout.getWinnerId());
        assertEquals("foo", payout.getLabel());
    }
}
