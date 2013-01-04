package net.ech.casino.videopoker;

import net.ech.casino.*;
import org.junit.*;
import static org.junit.Assert.*;

public class TwoPairsPatternTest
{
	@Test
    public void testPositives ()
    {
		assertTrue(matches("2CAH2CADAS"));
		assertTrue(matches("ACAHAD3D3S"));
		assertTrue(matches("4C4HAC4DAS"));
		assertTrue(matches("2C2H3C3D2S"));
		assertTrue(matches("5D2C5S5H2D"));
		assertTrue(matches("TDTCQS8HQD"));
	}

	@Test
    public void testNegatives ()
    {
		assertFalse(matches("ADACASAHQD"));
		assertFalse(matches("9DTCQS8HQD"));
	}

	@Test
    public void testWildCases()
    {
		String[] WILD_CASES = {
			"jo2CASAH2D",
			"ADjoASAH2D",
			"AD2CjoAH2D",
			"AD2CASjo2D",
			"AD2CASAHjo",
			"jojoTSTHQD",
			"joTCjoTHQD",
			"joTCTSjoQD",
			"joTCTSTHjo",
			"QDjojoTHQD",
			"QDjoTSjoQD",
			"QDjoTSTHjo",
			"QDTCjojoQD",
			"QDTCTSjojo",
			"jojojoADAS",
			"2CjojojoAS",
			"2Cjo2Cjojo",
			"jojo2Cjojo",
			"jojojojojo",
		};
		for (String wild : WILD_CASES) {
			assertTrue(wild, matches(wild));
		}
	}

	private boolean matches(String hand)
	{
		return new TwoPairsPattern().matches(new HandInfo(hand, new NullCardPattern()));
	}
}
