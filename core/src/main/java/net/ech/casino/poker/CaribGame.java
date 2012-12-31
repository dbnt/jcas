//
// CaribGame.java
//

package net.ech.casino.poker;

import java.util.*;
import net.ech.casino.*;

/**
 * A CaribGame handles a game of single-player Caribbean Stud poker.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class CaribGame extends TableGame implements CaribConstants
{
	/**
	 * Dealer's hand must be Ace-King or better to qualify.
	 */
	public final static PokerScore MinQualifyingScore =
		PokerScore.makeHighCard (Ace, King);

	// Machine parameters:
	private int maximumBonusPayout;
	private JackpotParameters jackpotParams;

	// The game has two stages: deal and call/fold:
	private boolean dealOk = true;

	// The player's original bet:
	private int ante;
	// The optional jackpot bet:
	private int drop;

	// Player/dealer cards and scores:
	private byte[] playerHand;
	private byte[] dealerHand;
	private PokerScore playerScore;
	private PokerScore dealerScore;

	// Hand results:
	private boolean jackpotWin;
	private Money dropPayout;
	private boolean playerCalled;
	private int antePayout;
	private int bonusPayout;
	private int result = NoResult;

	// Jackpot cache:
	private Money jackpotAmount;

	/**
	 * Constructor.
	 */
	public CaribGame (Casino casino, CaribMachine machine)
	{
		super (casino, machine);
		this.maximumBonusPayout = machine.getMaximumBonusPayout ();
		this.jackpotParams = machine.getJackpotParameters ();
	}

	//=======================================================================
	// Properties	 
	//=======================================================================

	/**
	 * Get my machine.
	 */
	public CaribMachine getCaribMachine()
	{
		return (CaribMachine) getMachine();
	}

	/**
	 * Return true if the game is ready to deal a new hand, false if
	 * a hand is in progress.
	 */
	public boolean isDealOk ()
	{
		return dealOk;
	}

	/**
	 * Get the player's most recent ante in dollars
	 * @return the ante, as an integer.
	 */
	public int getAnte ()
	{
		return ante;
	}

	/**
	 * Get the player's most recent drop slot bet.
	 * This amount is either zero or one (dollars).
	 * @return the drop slot bet, as an integer.
	 */
	public int getDrop ()
	{
		return drop;
	}

	/**
	 * Get the result of the last completed game.
	 * @return the game result as a numeric code
	 * @see net.ech.casino.poker.CaribConstants
	 */
	public int getResult ()
	{
		return result;
	}

	/**
	 * Get the result of the last completed game.
	 * @return the game result as a character
	 */
	public char getResultCode ()
	{
		char resultCode = ' ';
		switch (getResult ())
		{
		case PlayerFolded:
			resultCode = 'F';
			break;
		case DealerNotQualify:
			resultCode = 'p';
			break;
		case PlayerWon:
			resultCode = 'P';
			break;
		case DealerWon:
			resultCode = 'D';
			break;
		case Push:
			resultCode = '=';
			break;
		}

		return resultCode;
	}

	/**
	 * Get a string that represents the most recent player hand in 
	 * Card encoding. 
	 */
	public String getPlayerString ()
	{
		return Card.toString (playerHand); 
	}

	/**
	 * Get a string that represents the most recent dealer hand in 
	 * Card encoding.  If only the dealer's top card is showing, 
	 * return only that card.
	 */
	public String getDealerString ()
	{
		if (dealerHand == null)
			return null;
		return dealOk ? Card.toString (dealerHand) 
					  : Card.toString (dealerHand[CardsInHand - 1]);
	}

	/**
	 * Get the player hand as an array of bytes.
	 */
	public byte[] getPlayerBytes()
	{
		return (byte[]) (playerHand == null ? null : playerHand.clone ()); 
	}

	/**
	 * Get the dealer hand as an array of bytes.
	 */
	public byte[] getDealerBytes()
	{
		return (byte[]) (dealerHand == null ? null : dealerHand.clone ()); 
	}

	/**
	 * Get a string describing the player's hand.
	 * @param preliminary true if dealer cards are hidden
	 * @return a string, or null if no hand has been dealt.
	 */
	public String getPlayerHandDescription ()
	{
		if (playerScore == null)
			return null;
		if (!isPlayerCalled ())
			return playerScore.format (1);
		else
			return playerScore.format (dealerScore.howDiffers (playerScore));
	}

	/**
	 * Get a string describing the dealer's hand.
	 * @return a string, or null if no hand has been dealt.
	 */
	public String getDealerHandDescription ()
	{
		if (dealerScore == null)
			return null;

		return dealerScore.format (dealerScore.howDiffers (playerScore));
	}

	/**
	 * Get the amount won on the drop slot bet.	 This amount
	 * is awarded immediately after the player's hand is dealt.
	 * @return the amount won (zero or greater).
	 */
	public double getDropPayout ()
	{
		return dropPayout == null ? 0 : dropPayout.doubleValue ();
	}

	/**
	 * Get the amount won on the drop slot bet, if any.	 This amount
	 * is awarded immediately after the player's hand is dealt.
	 * @return the amount won, or null.
	 */
	public Money getDropPayoutMoney ()
	{
		return dropPayout;
	}

	/**
	 * Return true if the player called the last hand.
	 */
	public boolean isPlayerCalled ()
	{
		return playerCalled;
	}

	/**
	 * Return true if the dealer's hand qualifies.
	 */
	public boolean isDealerQualified ()
	{
		return dealerScore != null &&
			   dealerScore.compareTo (MinQualifyingScore) >= 0;
	}

	/**
	 * Return the amount returned to the player for the ante bet.
	 */
	public int getAntePayout ()
	{
		return antePayout;
	}

	/**
	 * Return the amount returned to the player as a bonus payout
	 * or a return of the calling bet.
	 */
	public int getBonusPayout ()
	{
		return bonusPayout;
	}

	/**
	 * Get the amount returned to the player, excluding any jackpot
	 * payout.
	 * @return the win, as an integer
	 */
	public int getWin ()
	{
		return bonusPayout + antePayout;
	}

	/**
	 * Get the amount returned to the player, excluding any jackpot
	 * payout.
	 * @return the win, as a Money value
	 */
	public Money getWinMoney ()
	{
		return new Money (getWin ());
	}

	/**
	 * Get cached jackpot amount.  Do not go to the source.
	 */
	public Money getJackpotAmount ()
	{
		return jackpotAmount;
	}

	//=======================================================================
	// METHODS	  
	//=======================================================================

	/**
	 * Request the next deal.
	 * @param player		the player
	 * @param ante		  the original wager
	 * @param drop		  the drop slot bet
	 */
	public void deal (int ante, int drop)
		throws CasinoException
	{
		new Deal (ante, drop).play ();
	}

	/**
	 * The player has called.  Resolve the hand.
	 * @param player		the player
	 * @param hand		  the hand as a byte array
	 */
	public void call ()
		throws CasinoException
	{
		new Call ().play ();
	}

	/**
	 * The player has folded.  Resolve the hand.
	 * @param player				the player
	 */
	public void fold ()
		throws CasinoException
	{
		new Fold ().play ();
	}

	/**
	 * Return true if the indicated player can legally quit the game.
	 */
	@Override
	public boolean isQuitLegal (int seatIndex)
	{
		return dealOk;
	}

	/**
	 * Return the amount that the indicated Player should be credited
	 * if it leaves the game now.
	 */
	public Money getRedemptionAmount ()
	{
		// Returning their ante is very generous.
		return dealOk ? null : new Money (ante);
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
	private class CaribPlay extends GamePlay
	{
		private boolean oldDealOk;
		private int oldAnte;
		private int oldDrop;
		private byte[] oldPlayerHand;
		private byte[] oldDealerHand;
		private PokerScore oldPlayerScore;
		private PokerScore oldDealerScore;
		private boolean oldPlayerCalled;
		private int oldAntePayout;
		private int oldBonusPayout;
		private boolean oldJackpotWin;
		private Money oldDropPayout;
		private int oldResult;

		protected void saveState ()
		{
			oldDealOk = dealOk;
			oldAnte = ante;
			oldDrop = drop;
			oldPlayerHand = playerHand;
			oldDealerHand = dealerHand;
			oldPlayerScore = playerScore;
			oldDealerScore = dealerScore;
			oldPlayerCalled = playerCalled;
			oldAntePayout = antePayout;
			oldBonusPayout = bonusPayout;
			oldJackpotWin = jackpotWin;
			oldDropPayout = dropPayout;
			oldResult = result;
		}

		protected void restoreState ()
		{
			dealOk = oldDealOk;
			ante = oldAnte;
			drop = oldDrop;
			playerHand = oldPlayerHand;
			dealerHand = oldDealerHand;
			playerScore = oldPlayerScore;
			dealerScore = oldDealerScore;
			playerCalled = oldPlayerCalled;
			antePayout = oldAntePayout;
			bonusPayout = oldBonusPayout;
			jackpotWin = oldJackpotWin;
			dropPayout = oldDropPayout;
			result = oldResult;
		}
	}

	//
	// Implementation of the deal play.
	//
	private class Deal extends CaribPlay
	{
		private int newAnte;
		private int newDrop;

		//
		// Constructor takes initial bet amounts.
		//
		Deal (int newAnte, int newDrop)
		{
			this.newAnte = newAnte;
			this.newDrop = newDrop;
		}

		//
		// Validate whether move is legal, validate bet amounts.
		//
		protected void validate ()
		   throws GameException
		{
			if (!dealOk) 
				throw new IllegalPlayException(CaribGame.this);

			testBet (newAnte);

			if (newDrop != 0 && newDrop != 1)
				throw new GameException ("Illegal drop bet: " + newDrop, CaribGame.this);
		}

		//
		// Carry out the deal.
		//
		protected void computePlay ()
		{
			// Clear hand results.
			dealOk = false;
			playerCalled = false;
			antePayout = 0;
			bonusPayout = 0;
			result = NoResult;

			// Accept bets.
			ante = newAnte;
			drop = newDrop;

			// Deal cards.
			deal ();

			// Pay out on the drop bet immediately.
			dropPayout = null;
			if (drop > 0)
			{	 
				switch (playerScore.getPrimary ())
				{
				case PokerScore.StraightFlush:
					// This is just the base payout.  The progressive jackpot
					// payout transaction is handled separately. 
					double factor = playerScore.getRank (0) == Ace
						? RoyalFlushFactor : StraightFlushFactor;
					dropPayout = new Money (BaseJackpotAmount * factor);
					jackpotWin = true;
					break;
				case PokerScore.FourOfAKind:
					dropPayout = new Money (500);
					break;
				case PokerScore.FullHouse:
					dropPayout = new Money (100);
					break;
				case PokerScore.Flush:
					dropPayout = new Money (75);
					break;
				default:
					dropPayout = null;
					break;
				}
			}	 
		}

		//
		// Handle the accounting for this deal.
		//
		protected void transact ()
			throws CasinoException
		{
			// Two transactions -- one for the drop, one for the ante --
			// packaged as one.
			Transaction trans = new Transaction (CaribGame.this);
			trans.setWagerAmount (ante + drop);
			trans.setWinAmount (dropPayout);

			// If the player bet on the jackpot and there is a progressive
			// jackpot, handle the extra transaction.
			//
			if (drop > 0 && jackpotParams != null &&
				jackpotParams.getJackpotName () != null)
			{
				JackpotTransaction jtrans = new JackpotTransaction ();
				jtrans.setJackpotName (jackpotParams.getJackpotName ());
				jtrans.setContributionAmount (jackpotParams.getJackpotContrib ());
				if (jackpotWin)
				{
					double factor = playerScore.getRank (0) == Ace
						? RoyalFlushFactor : StraightFlushFactor;
					jtrans.setClaimFactor (factor);
				}
				trans.addJackpotTransaction (jtrans);
			}

			getCasino().executeTransaction (trans);
		}
	}

	//
	// Implementation of call play.
	//
	private class Call extends CaribPlay
	{
		//
		// Validate whether move is legal.
		//
		protected void validate ()
		   throws GameException
		{
			if (dealOk) 
				throw new IllegalPlayException(CaribGame.this);
		}

		//
		// Resolve the hand.
		//
		protected void computePlay ()
		{
			playerCalled = true;

			if (!isDealerQualified ())
			{
				// Awwwwwww.... dealer didn't qualify.
				antePayout = ante * 2;		  // pay even money
				bonusPayout = ante * 2;		   // push
				result = DealerNotQualify;
			}
			else
			{
				int cmp = playerScore.compareTo (dealerScore);
				if (cmp == 0)
				{
					// Push.
					antePayout = ante;
					bonusPayout = ante * 2;
					result = Push;
				}
				else if (cmp > 0)
				{
					// Player beats dealer's qualifying hand.
					antePayout = ante * 2;
					bonusPayout = (ante * 2) + calcBonusPayout ();
					result = PlayerWon;
				}
				else
				{
					// Dealer won.
					result = DealerWon;
				}	 
			}

			// Next!
			dealOk = true;
		}

		//
		// Handle the accounting.
		//
		protected void transact ()
			throws CasinoException
		{
			Transaction trans = new Transaction (CaribGame.this);
			trans.setWagerAmount (ante * 2);
			double totalBet = ante * 3;
			double totalReturn = getWin ();
			trans.setReturnAmount (Math.min (totalBet, totalReturn));
			trans.setWinAmount (Math.max (totalReturn - totalBet, 0));
			getCasino().executeTransaction (trans);
		}
	}

	//
	// Implementation of fold play.
	//
	private class Fold extends CaribPlay
	{
		//
		// Validate whether move is legal.
		//
		protected void validate ()
		   throws GameException
		{
			if (dealOk) 
				throw new IllegalPlayException(CaribGame.this);
		}

		//
		// Mark state as folded.
		//
		protected void computePlay ()
		{
			// Next!
			dealOk = true;
			result = PlayerFolded;
		}

		//
		// Handle the accounting.
		//
		protected void transact ()
			throws CasinoException
		{
			getCasino().executeTransaction (new Transaction (CaribGame.this));
		}
	}
	
	private static String [] testData = {	"TDTH2C3C5H",		// pair
											"TDTH2C2H5H",		// 2 pair
											"TDTHTS2H5H",		// 3 of a kind
											"ADAHASACTD",		// 4 of a kind
											"TD2D4D5D8D",		// flush
											"2D3H4D5H6D",		// straight
											"2D3D4D5D6D",		// str flush
											"TDJDQDKDAD"  };	 // royal
	
	private int testIterator = 0;
	// set this true for testing
	private boolean useTestData = false;
	
	/**
	 * Deal out new cards.
	 */
	private void deal ()
	{
		// Shuffle out 10 cards of a single deck.
		Deck deck = new Deck ();
		byte[] cards = deck.deal (CardsInHand * 2, getRandomizer ());
		
		// Allocate new hand arrays.
		playerHand = new byte [CardsInHand];
		dealerHand = new byte [CardsInHand];

		// Deal the hands.
		for (int i = 0; i < CardsInHand; ++i)
		{
			if (useTestData)
			{	 
				if (testIterator < testData.length)
					playerHand[i] = Card.parse (testData[testIterator],i*2);
			}					 
			else
			{	 
				playerHand[i] = cards[i * 2];
			}	 
			
			dealerHand[i] = cards[i * 2 + 1];
		}
		
		if (useTestData)		
			testIterator = (testIterator+1) % testData.length;
		
		// Score the hands now (why not?)
		playerScore = HandInfo.score (playerHand);
		dealerScore = HandInfo.score (dealerHand);
	}

	//
	// Calculate the bonus payout for the player's winning hand,
	// excluding the player's calling bet.
	//
	private int calcBonusPayout ()
	{
		int payout = (ante * 2) * calcBonusMultiple ();
		if (maximumBonusPayout > 0)
			payout = Math.min (payout, maximumBonusPayout);
		return payout;
	}

	// XXX: should be table-driven.
	//
	private int calcBonusMultiple ()
	{
		switch (playerScore.getPrimary ())
		{
		case PokerScore.StraightFlush:
			return playerScore.getRank (0) == Ace ? 100 : 50;
		case PokerScore.FourOfAKind:
			return 20;
		case PokerScore.FullHouse:
			return 7;
		case PokerScore.Flush:
			return 5;
		case PokerScore.Straight:
			return 4;
		case PokerScore.ThreeOfAKind:
			return 3;
		case PokerScore.TwoPair:
			return 2;
		default:
			return 1;
		}
	}

	//
	// Replicated from superclass here so that an inner class may call it.
	//
	protected void testBet (int bet) throws GameException
	{
		super.testBet (bet);
	}

	/**
	 * Apply the latest jackpot amount to this Game's properties.
	 * The Session must call this method whenever the player
	 * contributes to a jackpot or wins a jackpot.
	 */
	protected void applyJackpotAmount (String jackpotName, Money jackpotAmount)
	{
		if (jackpotParams == null)
			return;		   // should not happen (?)

		Money baseAmount = jackpotParams.getJackpotBaseAmount ();
		
		if (jackpotWin)
		{
			// jackpotAmount is the total won including base
			dropPayout = new Money (jackpotAmount.doubleValue ());
			jackpotWin = false;
			
			// show the new value for the jackpot, taking into account the recent win.
			// This may be be a little off, but will be corrected on the next game transaction
				this.jackpotAmount = this.jackpotAmount.subtract (dropPayout);
			
			// jackpot never falls below the base amount
			if (this.jackpotAmount.compareTo (baseAmount) < 0)
				this.jackpotAmount = new Money (baseAmount.doubleValue ());
		}
		else
		{	 
			// jackpotAmount is the variable portion of the total jackpot
			this.jackpotAmount = jackpotAmount.add (jackpotParams.getJackpotBaseAmount ());
		}		 
	}
	
}
