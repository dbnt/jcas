//
// FlushPatternTest.java
//

package net.ech.casino.videopoker;

import net.ech.casino.*;
import org.junit.*;
import static org.junit.Assert.*;

public class FlushPatternTest
{
	public String[] FLUSHES = {
		"AC2C3C4C5C",
		"KD2DTD4D5D",
		"7H2HTH4H5H",
		"9S2STS4SAS",
		"jo2C3C4C5C",
		"KDjoTD4D5D",
		"7H2Hjojo5H",
	};

	public String[] NON_FLUSHES = {
		"AC2C3H4C5C",
		"KD2CTD4D5D",
		"7H2HTH4S5S",
		"9S2DTS4SAD",
		"jo2C3C4S5C",
		"KSjoTD4D5D",
		"7H2Hjojo5C",
	};

	@Test
    public void testFlushes ()
    {
		for (String flush : FLUSHES) {
			assertEquals(flush, matches(flush) ? flush : "-fail-");
        }
	}

	@Test
    public void testNonFlushes ()
    {
		for (String nonflush : NON_FLUSHES) {
			assertEquals(nonflush, matches(nonflush) ? "-fail-" : nonflush);
        }
	}

	@Test
	public void testSuited()
	{
		assertTrue(matches("S", "9S2STS4SAS"));
		assertTrue(matches("CS", "9S2STS4SAS"));
		assertFalse(matches("C", "9S2STS4SAS"));
		assertFalse(matches("D", "9S2STS4SAS"));
		assertFalse(matches("H", "9S2STS4SAS"));
		assertTrue(matches("C", "jo2C3C4C5C"));
		assertFalse(matches("S", "jo2C3C4C5C"));
	}

	private boolean matches(String hand)
	{
		return new FlushPattern().matches(new HandInfo(hand, new NullCardPattern()));
	}

	private boolean matches(String suits, String hand)
	{
		return new FlushPattern(suits).matches(new HandInfo(hand, new NullCardPattern()));
	}
}
