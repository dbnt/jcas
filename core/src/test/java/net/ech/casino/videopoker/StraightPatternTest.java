package net.ech.casino.videopoker;

import net.ech.casino.*;
import org.junit.*;
import static org.junit.Assert.*;

public class StraightPatternTest
{
	@Test
    public void testPositives ()
    {
		String[] POSITIVES = {
			"AC2H3C4D5S",
			"5C4H3D2DAS",
			"2C4H6C5D3S",
			"8C6H7C9D5S",
			"JCTHACQDKS",
			"jo2H3C4D5S",
			"jo4H3D2DAS",
			"2C4H6C5Djo",
			"8C6Hjo9D5S",
			"JCTHjoQDKS",
			"JCjoACQDKS"
		};
		for (String pos : POSITIVES) {
			assertTrue(pos, matches(pos));
		}
	}

	@Test
    public void testNegatives ()
    {
		assertFalse(matches("AD2CAS4H5D"));
		assertFalse(matches("ADKCKSTHQD"));
		assertFalse(matches("ADjoAS4H5D"));
		assertFalse(matches("joKCKSTHQD"));
	}

	private boolean matches(String hand)
	{
		return new StraightPattern().matches(new HandInfo(hand, new NullCardPattern()));
	}
}
