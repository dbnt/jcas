//
// VideoPokerGame.java
//

package net.ech.casino.videopoker;

import java.util.*;
import net.ech.casino.CasinoException;
import net.ech.casino.IllegalPlayException;
import net.ech.casino.Money;
import net.ech.casino.Randomizer;
import net.ech.casino.Transaction;

/**
 * Video poker game servlet.
 */
public class VideoPokerGame
	implements Constants
{
	private GameContext gameContext;
	private VideoPokerState state;

	public VideoPokerGame(GameContext gameContext)
	{
		this.gameContext = gameContext;
	}

	public void setVideoPokerState(VideoPokerState state)
	{
		this.state = state;
	}

	public VideoPokerState getVideoPokerState()
	{
		return state;
	}

	/**
	 * User move: select a machine.
	 */
	public void selectMachine(final VideoPokerMachine machine)
		throws CasinoException
	{
		new VideoPokerMove() {
			@Override
			protected void execute()
				throws CasinoException
			{
				validatePendingAction(VideoPokerState.Action.DEAL);
				state.setMachine(machine);
			}
		}.run();
	}

	/**
	 * User move: select a credit value.
	 */
	public void selectCreditValue(final Money creditValue)
		throws CasinoException
	{
		new VideoPokerMove() {
			@Override
			protected void execute()
				throws CasinoException
			{
				validatePendingAction(VideoPokerState.Action.DEAL);
				validateCreditValue(creditValue);
				state.setCreditValue(creditValue);
			}
		}.run();
	}

	/**
	 * User move: set the wager amount and deal.
	 */
	public void wagerAndDeal(final int wagerCredits)
		throws CasinoException
	{
		new VideoPokerMove() {
			@Override
			protected void execute()
				throws CasinoException
			{
				validatePendingAction(VideoPokerState.Action.DEAL);
				validateWagerCredits(wagerCredits);
				String cards = dealCards();
				String hand = cards.substring(0, CARDS_IN_HAND * 2);
				state.setWagerCredits(wagerCredits);
				state.setWinCredits(0);
				state.setCards(cards);
				state.setHand(hand);
				state.setHolds(null);
				grade(false);
				state.setPendingAction(VideoPokerState.Action.DRAW);
				transaction.setWagerAmount(state.getCreditValue().multiply(wagerCredits));
			}
		}.run();
	}

	/**
	 * User move: set holds and draw new cards.
	 */
	public void draw (final String holds)
		throws CasinoException
	{
		new VideoPokerMove() {
			@Override
			protected void execute()
				throws CasinoException
			{
				validatePendingAction(VideoPokerState.Action.DRAW);
				String newHolds = validateHolds(holds);
				String newHand = drawCards(newHolds);
				state.setHand(newHand);
				state.setHolds(newHolds);
				grade(true);
				state.setPendingAction(VideoPokerState.Action.DEAL);
				int winCredits = state.getWinCredits();
				int returnCredits = Math.max(winCredits - state.getWagerCredits(), 0);
				winCredits -= returnCredits;
				transaction.setReturnAmount(state.getCreditValue().multiply(returnCredits));
				transaction.setWinAmount(state.getCreditValue().multiply(winCredits));
			}
		}.run();
	}

	abstract private class VideoPokerMove
	{
		VideoPokerState state;
		Transaction transaction;

		final public void run()
			throws CasinoException
		{
			this.state = copyOrCreateInitialState();
			this.transaction = new Transaction();
			execute();
			commit();
		}

		abstract protected void execute()
			throws CasinoException;

		protected void validatePendingAction(VideoPokerState.Action expected)
			throws IllegalPlayException
		{
			if (state.getPendingAction() != expected) {
				throw new IllegalPlayException("bad state");
			}
		}

		protected void commit()
			throws CasinoException
		{
			transaction.setGameState(state);
			gameContext.get(Accounting.class).executeTransaction(transaction);
			setVideoPokerState(state);
		}
	}

	private VideoPokerState copyOrCreateInitialState()
		throws CasinoException
	{
		VideoPokerState state = getVideoPokerState();
		if (state == null) {
			state = new VideoPokerState();
			state.setMachine(getConfiguration().getDefaultMachine());
			state.setCreditValue(getConfiguration().getDefaultCreditValue());
			setVideoPokerState(state);
		}
		else {
			state = state.copy();
		}
		return state;
	}

	private void validateCreditValue(Money creditValue)
		throws CasinoException
	{
		if (!getConfiguration().getValidCreditValues().contains(creditValue)) {
			throw new IllegalPlayException("invalid credit value: " + creditValue);
		}
	}

	private void validateWagerCredits(int wagerCredits)
		throws CasinoException
	{
		if (wagerCredits <= 0 || wagerCredits > getMaximumWager()) {
			throw new IllegalPlayException("invalid wager: " + wagerCredits);
		}
	}

	private boolean isMaximumWager()
		throws CasinoException
	{
		return state.getWagerCredits() == getMaximumWager();
	}

	private int getMaximumWager()
		throws CasinoException
	{
		return getConfiguration().getMaximumWager();
	}

	private String validateHolds(String holds)
		throws CasinoException
	{
		if (holds == null) {
			holds = "";
		}
		while (holds.length() < CARDS_IN_HAND) {
			holds = holds + " ";
		}

		if (!holds.matches("[H ]{5}")) {
			throw new IllegalPlayException("invalid holds: " + holds);
		}

		return holds;
	}

	private VideoPokerConfig getConfiguration()
		throws CasinoException
	{
		return gameContext.get(VideoPokerConfig.class);
	}

	private void grade(boolean applyWin)
		throws CasinoException
	{
		Grader.Grade grade = new Grader(state.getMachine()).grade(state.getHand(), isMaximumWager());
		state.setGradeIndex(grade == null ? null : new Integer(grade.index));
		state.setGradeLabel(grade == null ? null : grade.label);
		if (applyWin && grade != null && grade.reward != null) {
			grade.reward.grant(state);
		}
	}

	private String dealCards()
		throws CasinoException
	{
		int nJokers = state.getMachine().getNumberOfJokers();
		return new Deck(nJokers).shuffleAndDeal(gameContext.get(Randomizer.class), CARDS_IN_HAND * 2);
	}

	private String drawCards(String holds)
	{
		String hand = state.getHand();
		String cards = state.getCards();
		int cardsIndex = CARDS_IN_HAND;

		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < CARDS_IN_HAND; ++i) {
			String card;
			switch (holds.charAt(i)) {
			case 'H':
				card = hand.substring(i * 2, (i + 1) * 2);
				break;
			default:
				card = cards.substring((cardsIndex + i) * 2, (cardsIndex + i + 1) * 2);
			}
			buf.append(card);
		}

		return buf.toString();
	}
}
