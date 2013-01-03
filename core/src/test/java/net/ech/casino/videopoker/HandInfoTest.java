package net.ech.casino.videopoker;

import net.ech.casino.*;
import org.junit.*;
import static org.junit.Assert.*;

public class HandInfoTest
{
	@Test
    public void testWildCount()
    {
		assertEquals(0, new HandInfo("ACAH2CADAS", new NullCardPattern()).getWildCount());
		assertEquals(1, new HandInfo("ACjo2CADAS", new NullCardPattern()).getWildCount());
		assertEquals(2, new HandInfo("ACAH2Cjojo", new NullCardPattern()).getWildCount());
		assertEquals(3, new HandInfo("ACAH2Cjojo", new RankCardPattern("2")).getWildCount());
		assertEquals(0, new HandInfo("ACAH2Cjojo", new RankCardPattern("2")).getRankCount(0));
	}

	@Test
    public void testGetHand()
    {
		assertEquals("ASQDJCKH2C", new HandInfo("ASQDJCKH2C", new NullCardPattern()).getHand()); 
	}

	@Test
	public void testGetRankCount()
	{
		assertEquals(0, new HandInfo("ASQDJCKH3C", new NullCardPattern()).getRankCount(0)); 
		assertEquals(1, new HandInfo("ASQDJCKH2C", new NullCardPattern()).getRankCount(0)); 
		assertEquals(3, new HandInfo("ASQDJCKH3C", new NullCardPattern()).getRankCount(10, 13)); 
	}
}
