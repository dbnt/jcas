//
// BaccaratGame.java  
// 

package net.ech.casino.baccarat;

import java.util.*;
import net.ech.casino.*;

/**
 * A BaccaratGame plays single-player mini-Baccarat.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class BaccaratGame extends TableGame implements Constants
{
	// Machine parameters:
	private float tieOdds;
	private boolean canRequestReshuffle;

	// The shoe.
	private Shoe shoe;

	// Player bet.
	private int playerBet;
	private int bankBet;
	private int tieBet;

	// Player/bank hands
	private Hand playerHand = new Hand ();
	private Hand bankHand = new Hand ();

	//
	// Table that governs when bank takes a third card.	 Index is
	// value of player's third card.  (Value of 10 or above is zero.)
	//
	private final static int[] BankStaysOn =
	{
		4, 4, 5, 5, 6, 6, 7, 7, 3, 4
	};

	/**
	 * Constructor.
	 */
	public BaccaratGame (Casino casino, BaccaratMachine machine)
	{
		super (casino, machine);
		this.tieOdds = machine.getTieOdds ();
		this.canRequestReshuffle = machine.getCanRequestReshuffle ();

		this.shoe = new Shoe (machine.getNumberOfDecks());
		this.shoe.setCutRange (0.6, 0.8);
	}

	//=====================================================================
	// PROPERTIES
	//=====================================================================

	/**
	 * Get my machine.
	 */
	public BaccaratMachine getBaccaratMachine ()
	{
		return (BaccaratMachine) getMachine ();
	}

	/**
	 * Get the amount last bet on the bank hand.
	 */
	public int getBankBet ()
	{
		return bankBet;
	}

	/**
	 * Get the amount last bet on the player hand.
	 */
	public int getPlayerBet ()
	{
		return playerBet;
	}

	/**
	 * Get the amount last bet on a tie result.
	 */
	public int getTieBet ()
	{
		return tieBet;
	}

	/**
	 * Get the total amount last bet.
	 */
	public int getTotalBet ()
	{
		return playerBet + bankBet + tieBet;
	}

	/**
	 * Get the total amount last bet.
	 * @return the amount as a Money value
	 */
	public Money getTotalBetMoney ()
	{
		return new Money (getTotalBet ());
	}

	/**
	 * Get the bank hand.
	 * @return the bank hand as a Hand object
	 */
	public Hand getBankHand ()
	{
		return bankHand;
	}

	/**
	 * Get the player hand.
	 * @return the player hand as a Hand object
	 */
	public Hand getPlayerHand ()
	{
		return playerHand;
	}

	/**
	 * Get the bank hand.
	 * @return the bank hand as a string of rank/suit codes
	 */
	public String getBankCards ()
	{
		return bankHand.toString (false);
	}

	/**
	 * Get the player hand.
	 * @return the bank hand as a string of rank/suit codes
	 */
	public String getPlayerCards ()
	{
		return playerHand.toString (false);
	}

	/**
	 * Get the bank score.
	 */
	public int getBankScore ()
	{
		return bankHand.getScore ();
	}

	/**
	 * Get the player score.
	 */
	public int getPlayerScore ()
	{
		return playerHand.getScore ();
	}

	/**
	 * Return true if a reshuffle is required before the next hand
	 * is played.
	 */
	public boolean mustShuffle ()
	{
		return shoe.isShufflePending ();
	}

	/**
	 * Get the total return on the last hand.
	 * @return the total return as a floating-point value
	 */
	public float getTotalReturn ()
	{
		int playerScore = playerHand.getScore ();
		int bankScore = bankHand.getScore ();

		if (playerScore > bankScore)
		{
			return playerBet * 2.0f;
		}
		else if (bankScore > playerScore)
		{
			return bankBet * 1.95f;
		}
		else
		{
			return playerBet + bankBet + tieBet + (tieBet * tieOdds);
		}
	}

	/**
	 * Get the total return on the last hand.
	 * @return the total return as a decimal value
	 */
	public Money getTotalReturnMoney ()
	{
		return new Money (getTotalReturn ());
	}

	/**
	 * Get the total return (an alias for getTotalReturnMoney).
	 */
	public Money getWin ()
	{
		return getTotalReturnMoney ();
	}

	/**
	 * Return a localized String that describes the outcome of the
	 * latest hand.
	 */
	public int getResult ()
	{
		int playerScore = playerHand.getScore ();
		int bankScore = bankHand.getScore ();

		if (playerScore > bankScore)
			return playerHand.isNatural () ? PlayerNatural : PlayerWin;

		if (bankScore > playerScore)
			return bankHand.isNatural () ? BankNatural : BankWin;
		
		return Tie;
	}

	//=====================================================================
	// METHODS
	//=====================================================================

	/**
	 * Play the game.  (Baccarat is a one-step game for the purpose
	 * of a client-server implementation).
	 */
	public void play (Player player, int playerBet,
					int bankBet, int tieBet,
					boolean reshuffleRequested)
		throws CasinoException
	{
		// Validate bet amount.
		//
		int minBet = getMinimumBet ();
		int maxBet = getMaximumBet ();
		int bankPlayerBet = playerBet + bankBet;
		int totalBet = bankPlayerBet + tieBet;

		if (totalBet <= 0 || (bankPlayerBet > 0 && bankPlayerBet < minBet))
			throw new GameException ("Minimum bet is required.", this);

		if (playerBet > maxBet || bankBet > maxBet || tieBet > maxBet)
			throw new GameException ("Bet exceeds maximum.", this);

		// Save state.
		//
		Shoe oldShoe = shoe.copy ();
		int oldPlayerBet = playerBet;
		int oldBankBet = bankBet;
		int oldTieBet = tieBet;
		Hand oldPlayerHand = playerHand;
		Hand oldBankHand = bankHand;

		// Here we go...
		try
		{
			computeHand (playerBet, bankBet, tieBet, reshuffleRequested);
			Transaction trans = new Transaction (this);
			double totalReturn = getTotalReturn ();
			trans.setWagerAmount (totalBet);
			trans.setReturnAmount (Math.min (totalBet, totalReturn));
			trans.setWinAmount (Math.max (totalReturn - totalBet, 0));
			getCasino().executeTransaction (trans);
		}
		catch (Exception e)
		{
			// Restore state.
			//
			shoe = oldShoe;
			playerBet = oldPlayerBet;
			bankBet = oldBankBet;
			tieBet = oldTieBet;
			playerHand = oldPlayerHand;
			bankHand = oldBankHand;
			throw (e instanceof CasinoException) ? (CasinoException) e
												 : new CasinoException (e);
		}
	}

	private void computeHand (int playerBet, int bankBet, int tieBet,
							  boolean reshuffleRequested)
	{
		this.playerBet = playerBet;
		this.bankBet = bankBet;
		this.tieBet = tieBet;
		playerHand = new Hand ();
		bankHand = new Hand ();

		// Reshuffle if necessary.
		if ((reshuffleRequested && canRequestReshuffle) || mustShuffle ())
		{
			shoe.shuffle (getRandomizer ());
		}

		// Deal.
		playerHand.hit (shoe.draw());
		bankHand.hit (shoe.draw());
		playerHand.hit (shoe.draw());
		bankHand.hit (shoe.draw());

		//
		// If either hand has 8 or 9, round is over.
		//
		if (playerHand.getScore () >= 8 || bankHand.getScore () >= 8)
			return;

		//
		// If player score is 5 or less, player draws another card.
		//
		byte playerCard3 = NilCard;
		if (playerHand.getScore () <= 5)
		{
			playerCard3 = shoe.draw();
			playerHand.hit (playerCard3);
		}

		//
		// Whether bank hits depends on current bank score and value of 
		// player's third card (value of no card is zero).
		//
		if (bankHand.getScore () < bankStaysOn (playerCard3))
			bankHand.hit (shoe.draw());
	}

	private int bankStaysOn (byte playerCard3)
	{
		if (playerCard3 == NilCard)
			return 6;
		return BankStaysOn[Card.faceValueOf (playerCard3) % 10];
	}
}
