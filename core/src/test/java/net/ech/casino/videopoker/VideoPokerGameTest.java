package net.ech.casino.videopoker;

import net.ech.casino.*;
import net.ech.casino.videopoker.machines.*;
import org.junit.*;
import static org.junit.Assert.*;

public class VideoPokerGameTest
{
	GameContext gameContext;
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
    public void testDealAndDraw() throws Exception
    {
		game.wagerAndDeal(5);
		assertEquals(0, game.getVideoPokerState().getWinCredits());
		game.draw("HH   ");
		assertNotNull(game.getVideoPokerState().getMachine());
		assertEquals(5, game.getVideoPokerState().getWagerCredits());
	}

	@Test
    public void testWin() throws Exception
    {
		game.wagerAndDeal(1);
		assertEquals(0, game.getVideoPokerState().getWinCredits());
		assertNotNull(game.getVideoPokerState().getGradeIndex());
		game.draw("HHHHH");
		assertNotNull(game.getVideoPokerState().getMachine());
		assertEquals(50, game.getVideoPokerState().getWinCredits());
	}

	@Test
    public void testWinMax() throws Exception
    {
		game.wagerAndDeal(5);
		assertEquals(0, game.getVideoPokerState().getWinCredits());
		game.draw("HHHHH");
		assertNotNull(game.getVideoPokerState().getMachine());
		assertEquals(250, game.getVideoPokerState().getWinCredits());
		assertEquals("STRAIGHT FLUSH", game.getVideoPokerState().getGradeLabel());
		assertEquals(new Integer(4), game.getVideoPokerState().getGradeIndex());
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
