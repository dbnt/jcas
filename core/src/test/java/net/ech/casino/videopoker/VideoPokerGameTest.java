package net.ech.casino.videopoker;

import net.ech.casino.*;
import net.ech.casino.videopoker.machines.*;
import org.junit.*;
import static org.junit.Assert.*;

public class VideoPokerGameTest
{
	TestGameContext gameContext;
	VideoPokerGame game;

	@Before
	public void setUp() throws Exception
	{
		gameContext = new TestGameContext();
		game = new VideoPokerGame(gameContext);
	}

	@Test
    public void testSelectMachine() throws Exception
    {
		game.selectMachine(new DeucesAndJokerWild());
	}

	@Test
    public void testSelectCreditValue() throws Exception
    {
		game.selectCreditValue(new Money(1));
		assertEquals(new Money(1), game.getVideoPokerState().getCreditValue());
	}

	@Test
    public void testSelectCreditValueValidationError() throws Exception
    {
		try {
			game.selectCreditValue(new Money(2));
			fail("should not be reached");
		}
		catch (IllegalPlayException e) {
		}
	}

	@Test
	public void testHand() throws Exception
	{
		assertNull(game.getVideoPokerState());
		game.wagerAndDeal(1);
		assertEquals(10, game.getVideoPokerState().getHand().length());
		game.draw("");
		assertEquals(10, game.getVideoPokerState().getHand().length());
	}

	@Test
    public void testDealAndDraw() throws Exception
    {
		game.wagerAndDeal(5);
		assertEquals(0, game.getVideoPokerState().getWinCredits());
		game.draw("HH   ");
		assertNotNull(game.getVideoPokerState().getMachine());
		assertEquals(5, game.getVideoPokerState().getWagerCredits());
	}

	@Test
	public void testLoss() throws Exception
	{
		gameContext.notRandomizer.enqueue(0);
		gameContext.notRandomizer.enqueue(14);
		gameContext.notRandomizer.enqueue(27);
		gameContext.notRandomizer.enqueue(39);
		gameContext.notRandomizer.enqueue(7);
		game.wagerAndDeal(1);
		assertEquals("", game.getVideoPokerState().getHand());
		game.draw("HHHHH");
		assertEquals(0, game.getVideoPokerState().getWinCredits());
		assertNull(game.getVideoPokerState().getGradeIndex());
	}

	@Test
    public void testWin() throws Exception
    {
		game.wagerAndDeal(1);
		assertEquals("", game.getVideoPokerState().getHand());
		assertEquals(0, game.getVideoPokerState().getWinCredits());
		assertNotNull(game.getVideoPokerState().getGradeIndex());
		game.draw("HHHHH");
		assertEquals("STRAIGHT FLUSH", game.getVideoPokerState().getGradeLabel());
		assertEquals(50, game.getVideoPokerState().getWinCredits());
		assertEquals(new Integer(4), game.getVideoPokerState().getGradeIndex());
	}

	@Test
    public void testWinMax1() throws Exception
    {
		game.wagerAndDeal(5);
		assertEquals("", game.getVideoPokerState().getHand());
		assertEquals(0, game.getVideoPokerState().getWinCredits());
		assertNotNull(game.getVideoPokerState().getGradeIndex());
		game.draw("HHHHH");
		assertEquals("STRAIGHT FLUSH", game.getVideoPokerState().getGradeLabel());
		assertEquals(250, game.getVideoPokerState().getWinCredits());
		assertEquals(new Integer(4), game.getVideoPokerState().getGradeIndex());
	}

	@Test
    public void testWinMax2() throws Exception
    {
		gameContext.notRandomizer.enqueue(8);
		gameContext.notRandomizer.enqueue(8);
		gameContext.notRandomizer.enqueue(8);
		gameContext.notRandomizer.enqueue(8);
		gameContext.notRandomizer.enqueue(8);
		game.wagerAndDeal(5);
		assertEquals("", game.getVideoPokerState().getHand());
		assertEquals(0, game.getVideoPokerState().getWinCredits());
		game.draw("HHHHH");
		assertEquals("ROYAL FLUSH", game.getVideoPokerState().getGradeLabel());
		assertEquals(2000, game.getVideoPokerState().getWinCredits());
		assertEquals(new Integer(0), game.getVideoPokerState().getGradeIndex());
	}

	@Test
    public void testDealAndDrawShortHold() throws Exception
    {
		game.wagerAndDeal(5);
		game.draw("HH ");
		assertEquals("HH   ", game.getVideoPokerState().getHolds());
	}

	@Test
    public void testDealAndDrawNullHold() throws Exception
    {
		game.wagerAndDeal(5);
		game.draw(null);
		assertEquals("     ", game.getVideoPokerState().getHolds());
	}

	@Test
    public void testDefaultState() throws Exception
    {
		game.wagerAndDeal(5);
		assertNotNull(game.getVideoPokerState());
	}

	@Test
    public void testDealValidationError1() throws Exception
    {
		try {
			game.wagerAndDeal(0);
			fail("should not be reached");
		}
		catch (IllegalPlayException e) {
		}
	}

	@Test
    public void testDealValidationError2() throws Exception
    {
		try {
			game.wagerAndDeal(100);
			fail("should not be reached");
		}
		catch (IllegalPlayException e) {
		}
	}

	@Test
    public void testDealValidationError3() throws Exception
    {
		game.wagerAndDeal(5);
		try {
			game.wagerAndDeal(5);
			fail("should not be reached");
		}
		catch (IllegalPlayException e) {
		}
	}

	@Test
    public void testDrawValidationError1() throws Exception
    {
		game.wagerAndDeal(5);
		try {
			game.draw("H H H H H");
			fail("should not be reached");
		}
		catch (IllegalPlayException e) {
		}
	}

	@Test
    public void testDrawValidationError2() throws Exception
    {
		game.wagerAndDeal(5);
		try {
			game.draw("HoHoH");
			fail("should not be reached");
		}
		catch (IllegalPlayException e) {
		}
	}

	@Test
    public void testDrawValidationError3() throws Exception
    {
		try {
			game.draw("HHHHH");
			fail("should not be reached");
		}
		catch (IllegalPlayException e) {
		}
	}
}
