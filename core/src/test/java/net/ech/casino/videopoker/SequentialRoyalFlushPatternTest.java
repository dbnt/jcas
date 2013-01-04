package net.ech.casino.videopoker;

import net.ech.casino.*;
import org.junit.*;
import static org.junit.Assert.*;

public class SequentialRoyalFlushPatternTest
{
	String[] POSITIVES = {
		"TSJSQSKSAS",
		"TCJCQCKCAC",
		"TDJDQDKDAD",
		"THJHQHKHAH"
	};

	String[] NEGATIVES = {
		"TSJSQSKDAS",
		"TCQCJCKCAC",
		"TDJDjoKDAD",
		"AHJHQHKHTH"
	};

	@Test
    public void testPositives ()
    {
		for (String pos : POSITIVES) {
			assertTrue(pos, matches(pos));
		}
	}

	@Test
    public void testNegatives ()
    {
		for (String neg : NEGATIVES) {
			assertFalse(neg, matches(neg));
		}
	}

	@Test
    public void testSuitedPositives ()
    {
		assertTrue(POSITIVES[0], matches("S", POSITIVES[0]));
		assertFalse(POSITIVES[0], matches("C", POSITIVES[0]));
	}

	private boolean matches(String hand)
	{
		return new SequentialRoyalFlushPattern().matches(new HandInfo(hand, new NullCardPattern()));
	}

	private boolean matches(String suits, String hand)
	{
		return new SequentialRoyalFlushPattern(suits).matches(new HandInfo(hand, new NullCardPattern()));
	}
}
