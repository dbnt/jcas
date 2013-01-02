//
// VideoPokerGame.java
//

package net.ech.casino.videopoker;

import java.util.*;
import net.ech.casino.*;

/**
 * Video poker game servlet.
 */
public class VideoPokerGame
	implements Constants
{
	private GameContext gameContext;
	private VideoPokerState state;

	public VideoPokerGame(GameContext gameContext, VideoPokerState state)
	{
		this.gameContext = gameContext;
		this.state = state;
	}

	public void setVideoPokerState(VideoPokerState state)
	{
		this.state = state;
	}

	public void getVideoPokerState()
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
		}.execute();
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
		}.execute();
	}

	/**
	 * User move: set the wager amount and deal.
	 */
	public void wagerAndDeal(final int wagerCredits)
	{
		new VideoPokerMove() {
			@Override
			protected void execute()
				throws CasinoException
			{
				validatePendingAction(VideoPokerState.Action.DEAL);
				validateWagerCredits(wagerCredits);
				String cards = dealNewCards();
				String hand = cards.substring(0, CARDS_IN_HAND * 2);
				state.setWagerCredits(wagerCredits);
				state.setWinCredits(0);
				state.setCards(cards);
				state.setHand(hand);
				state.setHolds(null);
				state.setGrade(grade(hand));
				state.setPendingAction(VideoPokerState.Action.DRAW);
				trans.setWagerAmount(state.getCreditValue().multiply(wagerCredits));
			}
		}.execute();
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
				String newHand = draw(newHolds);
				int grade = grade(newHand);
				Reward reward = getReward(grade);
				state.setHand(newHand);
				state.setHolds(newHolds);
				state.setGrade(grade);
				state.setPendingAction(VideoPokerState.Action.DEAL);
				if (reward != null) {
					reward.grant(state);
				}
				int winCredits = state.getWinCredits();
				int returnCredits = Math.max(winCredits - state.getWagerCredits(), 0);
				winCredits -= returnCredits;
				trans.setReturnAmount(state.getCreditValue().multiply(returnCredits));
				trans.setWinAmount(state.getCreditValue().multiply(winCredits));
			}
		}.execute();
	}

	abstract private class VideoPokerMove
	{
		VideoPokerState state;
		Transaction transaction;

		public VideoPokerMove()
		{
			this.state = copyOrCreateInitialState();
			this.transaction == newTransaction();
		}

		abstract public void execute()
			throws CasinoException;

		protected void validatePendingAction(VideoPokerState.Action expected)
		{
			if (state.getPendingAction() != expected) {
				throw new IllegalMoveException("bad state");
			}
		}

		protected void commit()
			throws CasinoException
		{
			trans.setUpdatedState(state);
			executeTransaction(transaction);
			setVideoPokerState(state);
		}
	}

	private VideoPokerState copyOrCreateInitialState()
	{
		VideoPokerState state = getVideoPokerState();
		if (state == null) {
			setVideoPokerState(state = new VideoPokerState());
			state.setMachine(config.getDefaultMachine());
			state.setCreditValue(config.getDefaultCreditValue());
		}
		else {
			state = (VideoPokerState) state.clone();
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
		if (wagerCredits <= 0 || wagerCredits > getConfiguration().getMaximumWager()) {
			throw new IllegalPlayException("invalid wager: " + wagerCredits);
		}
	}

	private String validateHolds(String holds)
		throws CasinoException
	{
		if (holds == null) {
			holds = "";
		}
		while (holds.length() < CARDS_PER_HAND) {
			holds = holds + " ";
		}

		if (!holds.matches("[H ]{5}")) {
			throw new IllegalPlayException("invalid holds: " + holds);
		}

		return holds;
	}

	private VideoPokerConfig getConfiguration()
	{
		VideoPokerConfig config =  gameContext.get(VideoPokerConfig.class);
		return config == null ? new VideoPokerConfig() : config;
	}

	private String dealCards()
	{
		int nJokers = state.getMachine().getNumberOfJokers();
		Deck deck = new Deck(nJokers);
		Randomizer randomizer = gameContext.get(Randomizer.class);
		return deck.shuffleAndDeal(CARDS_PER_HAND * 2);
	}

	private String drawCards(String holds)
	{
		String hand = state.getHand();
		String cards = state.getCards();
		int cardsIndex = CARDS_PER_HAND;

		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < CardsInHand; ++i) {
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

	/**
	 * Grade the current hand.
	 * @return the index of the corresponding payout row, or -1 if no win.
	 */
	public int grade (String hand)
	{
		HandInfo handInfo = new HandInfo (hand, state.getMachine().getWildCardPattern());
		for () {
		}
	}
}
