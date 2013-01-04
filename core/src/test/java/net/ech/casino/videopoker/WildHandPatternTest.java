package net.ech.casino.videopoker;

import net.ech.casino.*;
import org.junit.*;
import static org.junit.Assert.*;

public class WildHandPatternTest
{
	@Test
    public void test2 ()
    {
		assertFalse(new WildHandPattern(2).matches(new HandInfo("JSQSAS9STS", new NullCardPattern())));
		assertFalse(new WildHandPattern(2).matches(new HandInfo("ACjoQCJCKC", new NullCardPattern())));
		assertTrue(new WildHandPattern(2).matches(new HandInfo("KCTCjojoAC", new NullCardPattern())));
		assertFalse(new WildHandPattern(2).matches(new HandInfo("jojojoJCKC", new NullCardPattern())));
	}

	@Test
    public void testDeuce ()
    {
		assertFalse(new WildHandPattern(2).matches(new HandInfo("JSQSAS9STS", new RankCardPattern("2"))));
		assertFalse(new WildHandPattern(2).matches(new HandInfo("AC2HQCJCKC", new RankCardPattern("2"))));
		assertTrue(new WildHandPattern(2).matches(new HandInfo("KCTC2H2CAC", new RankCardPattern("2"))));
		assertFalse(new WildHandPattern(2).matches(new HandInfo("2C2S2HJCKC", new RankCardPattern("2"))));
	}

	@Test
    public void test3 ()
    {
		assertFalse(new WildHandPattern(3).matches(new HandInfo("joQSAS9STS", new RankCardPattern("2"))));
		assertFalse(new WildHandPattern(3).matches(new HandInfo("jo2HQCJCKC", new RankCardPattern("2"))));
		assertTrue(new WildHandPattern(3).matches(new HandInfo("joTC2H2CAC", new RankCardPattern("2"))));
		assertFalse(new WildHandPattern(3).matches(new HandInfo("2C2S2HJCjo", new RankCardPattern("2"))));
	}
}
