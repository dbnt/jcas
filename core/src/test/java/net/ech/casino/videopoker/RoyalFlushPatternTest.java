package net.ech.casino.videopoker;

import net.ech.casino.*;
import org.junit.*;
import static org.junit.Assert.*;

public class RoyalFlushPatternTest
{
	@Test
    public void testPlain ()
    {
		assertTrue(new RoyalFlushPattern().matches(new HandInfo("ACTCQCJCKC", new NullCardPattern())));
		assertTrue(new RoyalFlushPattern().matches(new HandInfo("KCTCQCJCAC", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern().matches(new HandInfo("ACTCQSJCKC", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern().matches(new HandInfo("JSQSAS9STS", new NullCardPattern())));
		assertTrue(new RoyalFlushPattern().matches(new HandInfo("JSQSASKSTS", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern().matches(new HandInfo("JHjoAHKHTH", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern().matches(new HandInfo("JDQDjo9DTD", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern().matches(new HandInfo("joTCQCJCAC", new NullCardPattern())));
	}

	@Test
    public void testWild ()
    {
		assertTrue(new RoyalFlushPattern(true).matches(new HandInfo("ACTCQCJCKC", new NullCardPattern())));
		assertTrue(new RoyalFlushPattern(true).matches(new HandInfo("KCTCQCJCAC", new NullCardPattern())));
		assertTrue(new RoyalFlushPattern(true).matches(new HandInfo("JSQSASKSTS", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern(true).matches(new HandInfo("ACTCQSJCKC", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern(true).matches(new HandInfo("JSQSAS9STS", new NullCardPattern())));
		assertTrue(new RoyalFlushPattern(true).matches(new HandInfo("JHjoAHKHTH", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern(true).matches(new HandInfo("JDQDjo9DTD", new NullCardPattern())));
		assertTrue(new RoyalFlushPattern(true).matches(new HandInfo("joTCQCJCAC", new NullCardPattern())));
	}

	@Test
    public void testSuited ()
    {
		assertTrue(new RoyalFlushPattern("CS").matches(new HandInfo("ACTCQCJCKC", new NullCardPattern())));
		assertTrue(new RoyalFlushPattern("DC").matches(new HandInfo("KCTCQCJCAC", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern("D").matches(new HandInfo("ACTCQSJCKC", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern("C").matches(new HandInfo("JSQSAS9STS", new NullCardPattern())));
		assertTrue(new RoyalFlushPattern("S").matches(new HandInfo("JSQSASKSTS", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern("HD").matches(new HandInfo("JHjoAHKHTH", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern("D").matches(new HandInfo("JDQDjo9DTD", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern("SC").matches(new HandInfo("joTCQCJCAC", new NullCardPattern())));
	}

	@Test
    public void testSuitedAndWild ()
    {
		assertTrue(new RoyalFlushPattern("CS", true).matches(new HandInfo("ACTCQCJCKC", new NullCardPattern())));
		assertTrue(new RoyalFlushPattern("DC", true).matches(new HandInfo("KCTCQCJCAC", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern("D", true).matches(new HandInfo("JSQSASKSTS", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern("C", true).matches(new HandInfo("ACTCQSJCKC", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern("S", true).matches(new HandInfo("JSQSAS9STS", new NullCardPattern())));
		assertTrue(new RoyalFlushPattern("HD", true).matches(new HandInfo("JHjoAHKHTH", new NullCardPattern())));
		assertFalse(new RoyalFlushPattern("D", true).matches(new HandInfo("JDQDjo9DTD", new NullCardPattern())));
		assertTrue(new RoyalFlushPattern("SC", true).matches(new HandInfo("joTCQCJCAC", new NullCardPattern())));
	}
}
