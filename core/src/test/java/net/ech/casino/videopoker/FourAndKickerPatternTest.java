//
// FlushPatternTest.java
//

package net.ech.casino.videopoker;

import net.ech.casino.*;
import org.junit.*;
import static org.junit.Assert.*;

public class FourAndKickerPatternTest
{
	@Test
    public void testPositives ()
    {
		assertTrue(matches("A", "234", "ACAH2CADAS"));
		assertTrue(matches("A", "234", "ACAHAD3DAS"));
		assertTrue(matches("A", "234", "4CAHACADAS"));
		assertTrue(matches("234", "A234", "2C2H3C2D2S"));
		assertTrue(matches("234", "A234", "4C4H3C4D4S"));
		assertTrue(matches("234", "A234", "3C3H3C3DAS"));
	}

	@Test
    public void testNegatives ()
    {
		assertFalse(matches("A", "2", "AD2CASAH2D"));
		assertFalse(matches("A", "2", "ADACASAHQD"));
		assertFalse(matches("TKQ", "A", "TDTCTSTHQD"));
		assertFalse(matches("TKQ", "A", "KDKCjoKHKS"));
	}

	private boolean matches(String quadRanks, String kickRanks, String hand)
	{
		return new FourAndKickerPattern(quadRanks, kickRanks).matches(new HandInfo(hand, new NullCardPattern()));
	}
}
