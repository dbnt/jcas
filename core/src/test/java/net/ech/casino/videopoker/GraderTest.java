package net.ech.casino.videopoker;

import net.ech.casino.*;
import net.ech.casino.videopoker.machines.DoubleDoubleBonus;
import org.junit.*;
import static org.junit.Assert.*;

public class GraderTest
{
	Grader grader = new Grader(new DoubleDoubleBonus());

	@Test
	public void testLosingHand()
	{
		Grader.Grade grade = grader.grade("2CJDQD8S6C", false);
		assertNull(grade);
	}

	@Test
	public void testWinningHand()
	{
		Grader.Grade grade = grader.grade("2CJDJC8S2S", false);
		assertEquals(11, grade.index);
		assertEquals("TWO PAIR", grade.label);
		assertNotNull(grade.reward);
	}

	@Test
	public void testBestReward()
	{
		Grader.Grade grade = grader.grade("ACADACAS2S", false);
		assertEquals(1, grade.index);
		assertEquals("FOUR ACES & 2,3,4", grade.label);
		assertNotNull(grade.reward);
	}

	@Test
	public void testRoyalFlushNotMax()
	{
		Grader.Grade grade = grader.grade("ACKCJCQCTC", false);
		assertEquals(0, grade.index);
		assertEquals("ROYAL FLUSH", grade.label);
		assertNotNull(grade.reward);
		VideoPokerState state = new VideoPokerState();
		state.setCreditValue(new Money(1));
		state.setWagerCredits(1);
		grade.reward.grant(state);
		assertEquals(250, state.getWinCredits());
	}

	@Test
	public void testRoyalFlushMax()
	{
		Grader.Grade grade = grader.grade("ACKCJCQCTC", true);
		assertEquals(0, grade.index);
		assertEquals("ROYAL FLUSH", grade.label);
		assertNotNull(grade.reward);
		VideoPokerState state = new VideoPokerState();
		state.setCreditValue(new Money(1));
		state.setWagerCredits(5);
		grade.reward.grant(state);
		assertEquals(4000, state.getWinCredits());
	}
}
