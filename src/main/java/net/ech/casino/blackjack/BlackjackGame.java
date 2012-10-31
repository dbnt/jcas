//
// BlackjackGame.java  
// 

package net.ech.casino.blackjack;

import java.util.*;
import net.ech.casino.*;

/**
 * A BlackjackGame executes single-player blackjack.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class BlackjackGame extends TableGame implements Constants
{
	// A bit array showing which moves are currently allowed.
	private int moveFlags;

	// The shoe.
	private Shoe shoe;

	// Player/dealer hands.
	private PlayerHand[] playerHands;
	private int currentPlayerHand;
	private int nPlayerHands;
	private Hand dealerHand = new Hand ();

	// Other state.
	private int originalBet;
	private boolean insured;
	private boolean surrendered;

	// Optional card counter.
	private CardListener cardListener;

	//=================================================================
	// INITIALIZATION
	//=================================================================

	/**
	 * Constructor.
	 */
	public BlackjackGame (Casino casino, BlackjackMachine machine)
	{
		super (casino, machine);

		playerHands = new PlayerHand [machine.getMaximumHands ()];
		moveFlags = DealOk;
	}

	//=================================================================
	// PROPERTIES
	//=================================================================

	/**
	 * Get my machine.
	 */
	public BlackjackMachine getBlackjackMachine ()
	{
		return (BlackjackMachine) getMachine ();
	}

	/**
	 * Replace my old brown shoe.  (For testing.)
	 */
	public void setShoe (Shoe shoe)
	{
		this.shoe = shoe;
	}

	/**
	 * @return whether the "cut" point in the shoe has been 
	 * reached, indicating that the shoe must be reshuffled
	 * before the next deal.
	 */
	public boolean isShufflePending ()
	{
		return shoe.isShufflePending ();
	}

	/**
	 * Get the number of cards dealt from the current shoe previous
	 * to the current hand.
	 */
	public int getNumberOfCardsDealt ()
	{
		return shoe.getNumberOfCardsDealt ();
	}

	/**
	 * @return whether it is legal to request a new deal at this stage
	 * of the game.
	 */
	public boolean isDealOk ()
	{
		return (moveFlags & DealOk) != 0;
	}

	/**
	 * @return whether it is legal to take a hit at this stage
	 * of the game.
	 */
	public boolean isHitOk ()
	{
		return (moveFlags & HitOk) != 0;
	}

	/**
	 * @return whether it is legal to stand at this stage
	 * of the game.
	 */
	public boolean isStandOk ()
	{
		return (moveFlags & StandOk) != 0;
	}

	/**
	 * @return whether it is legal to double down at this stage
	 * of the game.
	 */
	public boolean isDoubleDownOk ()
	{
		return (moveFlags & DoubleDownOk) != 0;
	}

	/**
	 * @return whether it is legal to surrender or rescue double down
	 * at this stage of the game.
	 */
	public boolean isSurrenderOk ()
	{
		return (moveFlags & SurrenderOk) != 0;
	}

	/**
	 * @return whether it is legal to split at this stage
	 * of the game.
	 */
	public boolean isSplitOk ()
	{
		return (moveFlags & SplitOk) != 0;
	}

	/**
	 * @return whether it is legal to buy insurance at this stage
	 * of the game.
	 */
	public boolean isInsuranceOk ()
	{
		return (moveFlags & InsuranceOk) != 0;
	}

	/**
	 * Get the amount of the original bet the player placed before
	 * any doubling down, splitting, or insurance.
	 */
	public int getOriginalBet ()
	{
		return originalBet;
	}

	/**
	 * Get the total amount the player has wagered on this hand.
	 * @return the total bet as a floating point value
	 */
	public double getTotalBet ()
	{
		double totalBet = 0;

		//
		// Sum bets per player hand.
		//
		for (int i = 0; i < nPlayerHands; ++i)
		{
			totalBet += playerHands[i].getBet ();
		}

		return totalBet;
	}

	/**
	 * Get the total amount the player has wagered.
	 * @return the total bet as a decimal value
	 */
	public Money getTotalBetMoney ()
	{
		return new Money (getTotalBet ());
	}

	/**
	 * Get the total amount the player has wagered (as getTotalBetMoney).
	 * @return the total bet as a decimal value
	 */
	public Money getBet ()
	{
		return getTotalBetMoney ();
	}

	/**
	 * Get the total amount the player received or would receive from the
	 * current hand.
	 * @return the total return as a floating point value
	 */
	public double getTotalReturn ()
	{
		double totalWin = 0;
		int dealerScore = dealerHand.getScore ();
		BlackjackMachine machine = getBlackjackMachine (); 

		//
		// Sum up returns from each hand.
		//
		for (int i = 0; i < nPlayerHands; ++i)
		{
			totalWin += playerHands[i].getReturn ();
		}

		return totalWin;
	}

	/**
	 * Get the total amount the player received or would receive from the
	 * current hand.
	 * @return the total return as a decimal value
	 */
	public Money getTotalReturnMoney ()
	{
		return new Money (getTotalReturn ());
	}

	/**
	 * Get the total amount the player received or would receive from the
	 * current hand (as getTotalReturnMoney).
	 * @return the total return as a decimal value
	 */
	public Money getWin ()
	{
		return getTotalReturnMoney ();
	}

	/**
	 * Tell whether the player bought insurance.
	 */
	public boolean isInsured ()
	{
		return insured;
	}

	/**
	 * Return the amount of the insurance wager for the current hand.
	 * If no insurance wager, return null.
	 */
	public Money getInsuranceBet ()
	{
		return insured ? new Money (originalBet / 2.0) : null;
	}

	/**
	 * Return the amount won (returned to the player) on the insurance
	 * wager for the current hand.	If no insurance wager, return null.
	 * An amount of zero indicates insurance lost.
	 */
	public Money getInsuranceWin ()
	{
		return insured ? new Money (getDealerScore() == BLACKJACK
									   ? (originalBet * 1.5) : 0) : null;
	}

	/**
	 * Tell whether the player surrendered entirely (not just rescued
	 * a double down)
	 */
	public boolean isSurrendered ()
	{
		return surrendered;
	}

	/**
	 * Get the current player hand.
	 */
	public PlayerHand getCurrentPlayerHand ()
	{
		return playerHands[currentPlayerHand];
	}

	/**
	 * Get the index of the current player hand.
	 */
	public int getCurrentPlayerHandIndex ()
	{
		return currentPlayerHand;
	}

	/**
	 * @return the number of hands the player is playing (exceeds one
	 * only if split)
	 */
	public int getNumberOfPlayerHands ()
	{
		return nPlayerHands;
	}

	/**
	 * Get the array of player hand objects.
	 */
	public PlayerHand[] getPlayerHands ()
	{
		PlayerHand[] result = new PlayerHand [nPlayerHands];

		for (int i = 0; i < result.length; ++i)
			result[i] = playerHands[i];

		return result;
	}

	/**
	 * Get a single player hand object by index.
	 */
	public PlayerHand getPlayerHand (int index)
	{
		if (index >= nPlayerHands)
			throw new ArrayIndexOutOfBoundsException (index);
		return playerHands[index];
	}

	/**
	 * Tell whether the indicated player hand won, pushed or lost.
	 * Return value is positive for player win, zero for push, negative
	 * for dealer win.	Return value is BLACKJACK for winning player
	 * blackjack.
	 */
	public int getPlayerVsDealer (int index)
	{
		PlayerHand playerHand = playerHands[index];

		int cmp =
			getBlackjackMachine ().playerVersusDealer (
				playerHand, dealerHand.getScore ());

		// Silly backward compatibility feature:
		if (cmp > 0 && playerHand.getScore () == BLACKJACK)
			return BLACKJACK;

		return cmp;
	}

	/**
	 * Get the dealer hand.
	 */
	public Hand getDealerHand ()
	{
		return dealerHand;
	}

	/**
	 * Get the score of the dealer's hand.
	 * @return a score between 0 and 22.  Zero indicates a bust;
	 * 22 indicates blackjack.
	 */
	public int getDealerScore ()
	{
		return dealerHand.getScore ();
	}

	/**
	 * Format a string that represents the cards in the dealer's
	 * hand.  Each card is represented by two characters: a rank
	 * character in A23456789TJQK followed by a suit character in
	 * DCHS.
	 * @return a string that represents the cards in the dealer's
	 * hand.
	 */
	public String getDealerString ()
	{
		return dealerHand.toString ();
	}

	/**
	 * @return the rank of the dealer's exposed card.
	 */
	public int getRankOfExposedDealerCard ()
	{
		return dealerHand.getCardRank (1);
	}

	//=================================================================
	// EVENTS
	//=================================================================

	/**
	 * Register a card listener (counter).
	 */
	public void addCardListener (CardListener listener)
		throws java.util.TooManyListenersException
	{
		if (cardListener != null)
			throw new java.util.TooManyListenersException ();
		this.cardListener = listener;
	}

	/**
	 * Unregister a card listener (counter).
	 */
	public void removeCardListener (CardListener listener)
	{
		if (cardListener == listener)
			this.cardListener = null;
	}

	private void fireCardShown (byte cardValue)
	{
		CardListener cardListener = this.cardListener;
		if (cardListener != null)
			cardListener.cardShown (new CardEvent (this, cardValue));
	}

	private void fireShuffle ()
	{
		CardListener cardListener = this.cardListener;
		if (cardListener != null)
			cardListener.shuffle (new CardEvent (this));
	}

	//=================================================================
	// METHODS
	//=================================================================

	/**
	 * Clear the cards from the table.	This is an administrative function
	 * (not player function) that be done only between hands.
	 */
	public void clearTable ()
		throws CasinoException
	{
		if (!isDealOk ())
			throw new IllegalPlayException (this);

		doClear();
	}

	/**
	 * Request a deal.
	 */
	public void deal (Player player, final int bet)
		throws CasinoException
	{
		testBet (bet);

		new BlackjackPlay (Deal)
		{
			protected void computePlay ()
			{
				// Clear hands.
				doClear ();

				// Take bet.
				originalBet = bet;
				setBetIncrease (bet);

				if (shoe == null)
				{
					shoe = getBlackjackMachine().createNewShoe();
				}

				if (isShufflePending ())
				{
					shuffle ();
					shuffled = true;
				}

				// Deal.
				newCards[0] = hit (playerHands[0]);
				hit (dealerHand);
				newCards[1] = hit (playerHands[0]);
				newCards[2] = hit (dealerHand);
				dealerHand.setFirstCardDown (true);

				updateState ();
			}
		}.play (player);
	}

	/**
	 * Take a hit on the current hand.
	 */
	public void hit (Player player)
		throws CasinoException
	{
		new BlackjackPlay (Hit)
		{
			protected void computePlay ()
			{
				// If dealer has blackjack, then player has turned down
				// insurance.  Abort the hit.
				if (dealerHand.getScore () == BLACKJACK)
					++currentPlayerHand;
				else
					newCards[0] = hit (playerHands[currentPlayerHand]);

				updateState ();
			}
		}.play (player);
	}

	/**
	 * Stand on the current hand.
	 */
	public void stand (Player player)
		throws CasinoException
	{
		new BlackjackPlay (Stand)
		{
			protected void computePlay ()
			{
				++currentPlayerHand;
				updateState ();
			}
		}.play (player);
	}

	/**
	 * Double down on the current hand.
	 */
	public void doubledown (Player player)
		throws CasinoException
	{
		new BlackjackPlay (DoubleDown)
		{
			protected void computePlay ()
			{
				// If dealer has blackjack, then player has turned
				// down insurance.	Abort the double down.
				//
				if (dealerHand.getScore () == BLACKJACK)
					++currentPlayerHand;
				else
				{
					PlayerHand hand = playerHands[currentPlayerHand];
					hand.doubleDown ();
					newCards[0] = hit (hand);
					setBetIncrease (originalBet);
				}

				updateState ();
			}
		}.play (player);
	}

	/**
	 * Surrender.
	 */
	public void surrender (Player player)
		throws CasinoException
	{
		new BlackjackPlay (Surrender)
		{
			protected void computePlay ()
			{
				playerHands[currentPlayerHand].surrender ();

				// The 'surrendered' flag distinguishes between surrender and
				// double down rescue.
				surrendered = playerHands[currentPlayerHand].getSize () == 2;

				// Treat otherwise as a bust.
				++currentPlayerHand;
				updateState ();
			}
		}.play (player);
	}

	/**
	 * Split the current hand.
	 */
	public void split (Player player)
		throws CasinoException
	{
		new BlackjackPlay (Split)
		{
			protected void computePlay ()
			{
				// If player has just turned down insurance, and dealer has
				// 21, don't double the bet, and don't hit the player!
				//
				if (dealerHand.getScore () == BLACKJACK)
					++currentPlayerHand;
				else
				{
					// Split the current hand.
					PlayerHand[] splitHands
						= playerHands[currentPlayerHand].split ();

					// Slide hands up to make room for new one.
					//
					for (int i = nPlayerHands - 1; i > currentPlayerHand; --i)
						playerHands[i + 1] = playerHands[i];

					playerHands[currentPlayerHand] = splitHands[0];
					playerHands[currentPlayerHand + 1] = splitHands[1];
					++nPlayerHands;

					// Hit both hands.	THIS IS INCORRECT!
					// Should finish the first before hitting the second.
					newCards[0] = hit (splitHands[0]);
					newCards[1] = hit (splitHands[1]);

					setBetIncrease (originalBet);
				}

				updateState ();
			}
		}.play (player);
	}

	/**
	 * Buy insurance.
	 */
	public void insurance (Player player)
		throws CasinoException
	{
		new BlackjackPlay (Insurance)
		{
			protected void computePlay ()
			{
				insured = true;
				setBetIncrease (originalBet / 2.0);
				updateState ();
			}
		}.play (player);
	}

	/**
	 * Return true if the indicated Player can legally quit the game.
	 */
	public boolean isQuitLegal (Player player)
	{
		return isDealOk ();
	}

	/**
	 * Return the amount that the indicated Player should be credited
	 * if it leaves the game now.
	 */
	public Money getRedemptionAmount (Player player)
	{
		return new Money (isDealOk () ? 0 : originalBet);
	}

	//=================================================================
	// IMPLEMENTATION
	//=================================================================

	//
	// Implement methods common to all plays...
	//
	private class BlackjackPlay extends GamePlay
	{
		// For transactions:
		private int move;
		private double betIncrease;

		// Saved state:
		private int oldMoveFlags;
		private Shoe oldShoe;
		private PlayerHand[] oldPlayerHands;
		private int oldNPlayerHands;
		private int oldCurrentPlayerHand;
		private Hand oldDealerHand;
		private int oldOriginalBet;
		private boolean oldInsured;
		private boolean oldSurrendered;
		private int oldPlayerBustCount;

		// For card counters:
		boolean shuffled;
		byte[] newCards = new byte [3];

		BlackjackPlay (int move)
		{
			this.move = move;
		}

		protected void setBetIncrease (double betIncrease)
		{
			this.betIncrease = betIncrease;
		}

		protected void validate ()
			throws GameException
		{
			if ((moveFlags & (1<<move)) == 0)
				throw new IllegalPlayException (BlackjackGame.this);
		}

		protected void transact ()
			throws CasinoException
		{
			boolean closeRound = isDealOk ();

			// Execute transaction.
			Transaction trans = new Transaction (BlackjackGame.this);
			if (betIncrease > 0)
				trans.setWagerAmount (betIncrease);
			if (closeRound)
			{
				double totalBet = getTotalBet ();
				double totalReturn = getTotalReturn ();
				trans.setReturnAmount (Math.min (totalBet, totalReturn));
				trans.setWinAmount (Math.max (totalReturn - totalBet, 0));
			}
			getCasino().executeTransaction (trans);

			// Now that transaction has succeeded, let the card
			// counter do its thing.
			//
			if (shuffled)
				fireShuffle ();

			// Show the card counter all the new cards.
			for (int i = 0; i < newCards.length && newCards[i] != 0; ++i)
				fireCardShown (newCards[i]);

			if (closeRound)
			{
				// All the dealer's cards but the second were just shown.
				for (int i = 0; i < dealerHand.getSize (); ++i)
					if (i != 1)
						fireCardShown (dealerHand.getCard (i));
			}
		}

		protected void saveState ()
		{
			oldMoveFlags = moveFlags;
			oldShoe = shoe == null ? null : shoe.copy ();
			oldPlayerHands = new PlayerHand [playerHands.length];
			for (int i = 0; i < playerHands.length; ++i)
				if (playerHands[i] != null)
					oldPlayerHands[i] = (PlayerHand) playerHands[i].copy ();
			oldNPlayerHands = nPlayerHands;
			oldCurrentPlayerHand = currentPlayerHand;
			oldDealerHand = dealerHand.copy ();
			oldOriginalBet = originalBet;
			oldInsured = insured;
			oldSurrendered = surrendered;
		}

		protected void restoreState ()
		{
			moveFlags = oldMoveFlags;
			shoe = oldShoe;
			playerHands = oldPlayerHands;
			nPlayerHands = oldNPlayerHands;
			currentPlayerHand = oldCurrentPlayerHand;
			dealerHand = oldDealerHand;
			originalBet = oldOriginalBet;
			insured = oldInsured;
			surrendered = oldSurrendered;
		}

		private double getTotalReturn ()
		{
			double totalReturn = BlackjackGame.this.getTotalReturn();
			Money insuranceWin = getInsuranceWin();
			if (insuranceWin != null)
			{
				totalReturn += insuranceWin.doubleValue();
			}
			return totalReturn;
		}
	}

	private void doClear ()
	{
		playerHands[0] = new PlayerHand (BlackjackGame.this);
		for (int i = 1; i < playerHands.length; ++i)
			playerHands[i] = null;
		nPlayerHands = 1;
		dealerHand = new Hand ();

		// Reset other state.
		originalBet = 0;
		insured = false;
		surrendered = false;
		currentPlayerHand = 0;
	}

	private void shuffle ()
	{
		shoe.shuffle (getRandomizer ());
	}

	private byte hit (Hand hand)
	{
		byte card = shoe.draw ();
		hand.hit (card);
		return card;
	}

	//
	// Update state following any play.
	//
	private void updateState ()
	{
		moveFlags = 0;

		BlackjackMachine machine = getBlackjackMachine ();

		// Can the player buy insurance?
		boolean insuranceOk = !insured &&
			getRankOfExposedDealerCard () == Ace &&
			machine.playerMayBuyInsurance () &&
			nPlayerHands == 1 &&
			playerHands[0].getSize() == 2;

		for (; currentPlayerHand < nPlayerHands; ++currentPlayerHand)
		{
			// If dealer has blackjack, then unless the player can buy 
			// insurance, end the hand.
			//
			if (!insuranceOk && dealerHand.getScore () == BLACKJACK)
			{
				break;
			}

			PlayerHand playerHand = playerHands[currentPlayerHand];

			// If hand is busted or surrendered, no more plays.
			// If hand automatically beats or pushes a dealer 21,
			// no more plays.
			//
			boolean isPlayable = playerHand.getScore () != BUST && 
				machine.playerVersusDealer (playerHand, 21) < 0;

			// Can the player hit this hand?
			boolean hitOk = isPlayable && machine.playerMayHit (playerHand);

			// Can the player surrender or rescue?
			boolean surrenderOk = isPlayable &&
				machine.playerMaySurrender (playerHand);

			if (hitOk || insuranceOk || surrenderOk)
			{
				// Offer stand as the alternative to all other options.
				moveFlags |= StandOk;
				if (hitOk)
					moveFlags |= HitOk;
				if (insuranceOk)
					moveFlags |= InsuranceOk;
				if (surrenderOk)
					moveFlags |= SurrenderOk;

				if (hitOk)
				{
					// Can the player double down?
					if (machine.playerMayDoubleDown (playerHand))
					{
						moveFlags |= DoubleDownOk;
					}

					// Can the player split?
					if (nPlayerHands < playerHands.length &&
						machine.playerMaySplit (playerHand))
					{
						moveFlags |= SplitOk;
					}
				}

				// Wait for the player's next move.
				return;
			}

			// No options to offer for this hand?  Onward to next hand.
		}

		// Dealer's turn.
		dealerHand.setFirstCardDown (false);
		if (dealerMayPlay (machine))
		{
			while (machine.dealerMustHit (dealerHand))
				hit (dealerHand);
		}

		// Record results per hand.
		closePlayerHands (machine);

		moveFlags = DealOk;
	}

	//
	// Determine whether the dealer should bother playing at all.
	//
	private boolean dealerMayPlay (BlackjackMachine machine)
	{
		// Examine all player hands...
		for (int i = 0; i < nPlayerHands; ++i)
		{
			// Search for at least one player hand that "matters"-- 
			// that is, depends on the dealer's play.
			//
			// If player hand has surrendered or busted, keep searching.
			//
			if (playerHands[i].getScore() == 0)
				continue;

			// Can the dealer push or beat this hand by drawing to 21?
			//
			if (machine.playerVersusDealer (playerHands[i], 21) <= 0)
				return true;
		}

		return false;
	}

	//
	// Store results in each player hand.
	//
	private void closePlayerHands (BlackjackMachine machine)
	{
		int dealerScore = dealerHand.getScore ();

		// Visit each player hand.
		for (int i = 0; i < nPlayerHands; ++i)
		{
			PlayerHand playerHand = playerHands[i];

			// Compare to dealer hand.
			int cmp = machine.playerVersusDealer (playerHand, dealerScore);

			// Compute win multiple.
			double mult = 0.0;
			if (playerHand.isSurrendered ())
			{
				mult = 0.5;
			}
			else if (cmp == 0)
			{
				mult = 1 + machine.getBonus (playerHand, false);
			}
			else if (cmp >= 0)
			{
				mult = 2 + machine.getBonus (playerHand, true);
			}

			playerHand.close (cmp, playerHand.getBet () * mult);
		}
	}
}
