//
// RideGame.java
//

package net.ech.casino.poker;

import java.util.*;
import net.ech.casino.*;
import net.ech.casino.PokerScore;

/**
 * A RideGame handles a game of single-player Let it Ride Stud poker.
 *
 * @version 1.0
 * @author Dave Giese, dgiese@ech.net
 */
public class RideGame extends TableGame implements CardConstants
{

	/**
	 * This is five-card stud poker.  There is really only one hand, so just keep track of it that way.
	 */
	public final static int CardsInHand = 5;

	// The game has 3 stages: deal, action 1, action 2.
	// An action is either withdraw or let it ride.
	//
	public final static int DEAL_OK = 0;		 
	public final static int CHOICE_1 = 1;
	public final static int CHOICE_2 = 2;
	public final static int STATE_MAX = 3;

	// current state of the game.
	private int state = 0;
	
	// The player's original bet quantity:
	private int ante;
	// Becomes true if player withdraws first ante:
	private boolean withdrawn1;
	// Becomes true if player withdraws second ante:
	private boolean withdrawn2;

	// Player/dealer cards and scores:
	private byte[] hand;

	// Pay lines and table
	private final static int NoResult = -1;
	private final static int Royal = 0;
	private final static int StraightFlush = 1;
	private final static int FourOfAKind = 2;
	private final static int FullHouse = 3;
	private final static int Flush = 4;
	private final static int Straight = 5;
	private final static int ThreeOfAKind = 6;
	private final static int TwoPair = 7;
	private final static int Pair = 8;

	private int handResult = NoResult;

	private final static int[] payTable = {1000,200,50,11,8,5,3,2,1};
	
	/**
	 * Constructor.
	 */
	public RideGame (Casino casino, RideMachine machine)
	{
		super (casino, machine);
	}

	//=======================================================================
	// Properties	 
	//=======================================================================

	/**
	 * Get my machine.
	 */
	public RideMachine getRideMachine()
	{
		return (RideMachine) getMachine();
	}

	/**
	 * Get the state of the game.
	 */
	public int getState ()
	{
		return state;
	}
	
	/**
	 * Return true if the game is ready to deal a new hand, false if
	 * a hand is in progress.
	 */
	public boolean isDealOk ()
	{
		return state == DEAL_OK;
	}

	/**
	 * Get the player's most recent bet in dollars.	 This is the amount per
	 * bet disc on the table.
	 * @return the bet, as an integer.
	 */
	public int getAnte ()
	{
		return ante;
	}

	/**
	 * Get the player's total bet on the most recent hand.	This is the
	 * total for all 3 bet discs.
	 * @return the total bet, as an integer.
	 */
	public int getTotalBet ()
	{
		return ante * getNumActiveBets ();
	}

	/**
	 * Get the number of active bet disks currently showing.
	 */
	public int getNumActiveBets ()
	{
		if (ante == 0)
			return 0;
		return 3 - (withdrawn1 ? 1 : 0) - (withdrawn2 ? 1 : 0);
	}

	/**
	 * Return false if the player withdrew the first bet on the most
	 * recent hand.
	 */
	public boolean isFirstChipDown ()
	{
		return ante > 0 && !withdrawn1;
	}

	/**
	 * Return false if the player withdrew the second bet on the most
	 * recent hand.
	 */
	public boolean isSecondChipDown ()
	{
		return ante > 0 && !withdrawn2;
	}

	/**
	 * Get the player's total bet on the last hand.
	 * @return the bet, as a money value
	 */
	public Money getTotalBetMoney ()
	{
		return new Money (getTotalBet ());
	}

	/**
	 * Get a string that represents the most recent player hand in 
	 * Card encoding. 
	 */
	public String getCardString ()
	{
		return Card.toString (getCardBytes ()); 
	}

	/**
	 * Get the player hand as an array of bytes.  We potentially snip the last 2 cards depending on where we are in the game.
	 */
	public byte[] getCardBytes()
	{
		if (hand == null)
			return null;
		
		byte returnCards[] = new byte [getNumCardsShowing ()];
		System.arraycopy (hand, 0, returnCards, 0, returnCards.length );
		return returnCards; 
	}

	/**
	 * Get the number of cards shown to the player.
	 */
	public int getNumCardsShowing ()
	{
		int num = CardsInHand;

		switch (state)
		{
		case CHOICE_1:
			num -= 2;
			break;
		case CHOICE_2:
			num -= 1;
			break;
		}

		return num;
	}

	/**
	 * Get the result of the most recently finished hand.
	 * @return offset into the pay table.  -1 means player lost.
	 */
	public int getHandResult ()
	{
		return handResult;
	}

	/**
	 * Get the amount returned to the player.
	 * @return the win, as an integer
	 */
	public int getWin ()
	{
		if (state == DEAL_OK && handResult >= 0)
		{
			return ante * getNumActiveBets () * (payTable[handResult] + 1);
		}
		return 0;
	}

	/**
	 * Get the amount returned to the player.
	 * @return the win, as a Money value
	 */
	public Money getWinMoney ()
	{
		return new Money (getWin ());
	}

	//=======================================================================
	// METHODS	  
	//=======================================================================

	/**
	 * Request the next deal.
	 * @param player		the player
	 * @param ante		  the amount bet on a sungle bet disk.
	 */
	public synchronized void deal (Player player, int ante)
		throws CasinoException
	{
		new Deal (ante).play (player);
	}

	/**
	 * The player has withdrawn a bet.
	 * @param player		the player
	 */
	public synchronized void withdraw (Player player)
		throws CasinoException
	{
		new WithdrawOrLetItRide (true).play (player);
	}

	/**
	 * The player has let it ride.
	 * @param player				the player
	 */
	public synchronized void letItRide (Player player)
		throws CasinoException
	{
		new WithdrawOrLetItRide(false).play (player);
	}

	/**
	 * Return true if the indicated Player can legally quit the game.
	 */
	public synchronized boolean isQuitLegal (Player player)
	{
		return state == DEAL_OK;
	}

	/**
	 * Return the amount that the indicated Player should be credited
	 * if it leaves the game now.
	 */
	public synchronized Money getRedemptionAmount (Player player)
	{
		// Return an amount equivalent to the player withdrawing for the remainder of the game.
		return state == DEAL_OK ? null : new Money (getTotalBet() - ante);
	}

	//======================================================================= 
	// Implementation
	//=======================================================================


	//
	// Base class for 'play' objects.  A play object is instantiated
	// each time the player makes a play and lives for the duration
	// of the transaction.	If the transaction fails, the play object
	// is responsible for restoring the state of the game.
	//
	private class RidePlay extends GamePlay
	{
		private int oldState;
		private int oldAnte;
		private boolean oldWithdrawn1;	  
		private boolean oldWithdrawn2;	  
		private byte[] oldHand;
		private int oldHandResult;

		protected void saveState ()
		{
			oldState = state;
			oldAnte = ante;
			oldWithdrawn1 = withdrawn1;	   
			oldWithdrawn2 = withdrawn2;	   
			oldHand = hand;
			oldHandResult = handResult;
		}

		protected void restoreState ()
		{
			state = oldState;
			ante = oldAnte;
			withdrawn1 = oldWithdrawn1;	   
			withdrawn2 = oldWithdrawn2;	   
			hand = oldHand;
			handResult = oldHandResult;
		}
	}

	//
	// Implementation of the deal play.
	//
	private class Deal extends RidePlay
	{
		private int newAnte;

		//
		// Constructor takes initial bet amount.
		//
		Deal (int newAnte)
		{
			this.newAnte = newAnte;
		}

		//
		// Validate whether move is legal, validate bet amounts.
		//
		protected void validate ()
		   throws GameException
		{
			if (state != DEAL_OK)
				throw new IllegalPlayException (RideGame.this);

			testBet (newAnte);
		}

		//
		// Carry out the deal.
		//
		protected void computePlay ()
		{
			withdrawn1 = false;
			withdrawn2 = false;
			handResult = NoResult;
			
			// Accept bets.
			ante = newAnte;

			// Deal cards.
			Deck deck = new Deck ();
			hand = deck.deal (CardsInHand, RideGame.this.getRandomizer ());

			state = CHOICE_1;
		}

		//
		// Handle the accounting for this deal.
		//
		protected void transact (Session session)
			throws CasinoException
		{
			Transaction trans = new Transaction ("deal");
			trans.setWagerAmount (newAnte * 3);
			session.executeTransaction (trans);
		}
	}

	//
	// Implementation of a game play, either withdraw or 'let it ride'.
	//
	private class WithdrawOrLetItRide extends RidePlay
	{
		private boolean isWithdraw;

		//
		// Constructor takes whether this a withdraw or 'let it ride' commad.
		//
		WithdrawOrLetItRide (boolean isWithdraw)
		{
			this.isWithdraw = isWithdraw;
		}
		//
		// Validate whether move is legal.
		//
		protected void validate ()
		   throws GameException
		{
			if (state == DEAL_OK)
				throw new IllegalPlayException (RideGame.this);
		}

		//
		// Resolve the hand.
		//
		protected void computePlay ()
		{
			// If Withdraw, Give the guy some dough back
			if (isWithdraw)
			{
				switch (state)
				{
				case CHOICE_1:
					withdrawn1 = true;
					break;
				case CHOICE_2:
					withdrawn2 = true;
					break;
				}
			}
			
			state = (state + 1) % STATE_MAX;
			
			// Game Over?  Score the hand 
			if (state == DEAL_OK)
			{
				handResult = calcHandResult (); 
			} 
		}

		//
		// Handle the accounting.
		//
		protected void transact (Session session)
			throws CasinoException
		{
			Transaction trans = new Transaction (isWithdraw ? "withdraw"
															: "let_it_ride");
			int returnAmount = isWithdraw ? ante : 0;
			if (isDealOk ())
			{
				int totalBet = getTotalBet ();
				int totalReturn = getWin ();
				returnAmount += Math.min (totalBet, totalReturn);
				trans.setWinAmount (Math.max (totalReturn - totalBet, 0));
			}
			trans.setReturnAmount (returnAmount);

			session.executeTransaction (trans);
		}
	}

	/**
	 * Grade the current hand.	Return the index into the pay table, or
	 * NoResult for a loss.
	 */
	private int calcHandResult ()
	{
		PokerScore score = HandInfo.score (hand);

		switch (score.getPrimary ())
		{
			case PokerScore.StraightFlush:
				return score.getRank (0) == Ace ? Royal : StraightFlush;
			case PokerScore.FourOfAKind:
				return FourOfAKind;
			case PokerScore.FullHouse:
				return FullHouse;
			case PokerScore.Flush:
				return Flush;
			case PokerScore.Straight:
				return Straight;
			case PokerScore.ThreeOfAKind:
				return ThreeOfAKind;
			case PokerScore.TwoPair:
				return TwoPair;
			case PokerScore.Pair:
				return score.getRank (0) >= Ten ? Pair : NoResult;
			default:
				return NoResult;
		}
	}
	
	//
	// Replicated from superclass here so that an inner class may call it.
	//
	protected void testBet (int bet) throws GameException
	{
		super.testBet (bet);
	}
	
	//
	// Replicated from superclass here so that an inner class may call it.
	//
	protected Randomizer getRandomizer ()
	{
		return super.getRandomizer ();
	}
}
