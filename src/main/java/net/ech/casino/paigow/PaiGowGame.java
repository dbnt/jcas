//
// PaiGowGame.java
//

package net.ech.casino.paigow;

import java.util.*;
import net.ech.casino.*;

/**
 * A PaiGowGame is a servelet for single-player pai gow poker.
 *
 * @version 1.1
 * @author I. Mannino imannino@pacificnet.net
 */
public class PaiGowGame extends TableGame implements Constants
{
	/**
	 * Note: serial version is fixed!
	 */
	static final long serialVersionUID = 3088029775366991903L;

	// Machine settings.
	private float commissionRate;

	// Player bet.
	private int bet;

	// Player/dealer hands
	private byte[] playerHand = null;
	private byte[] dealerHand = null;

	private boolean dealOk = true;
	private boolean playerIsBanker = false;

	private int gameResult = NoResult;
	private int fiveHandResult = 0;
	private int twoHandResult = 0;
	private String[] handDesc;

	/**
	 * Constructor.
	 */
	public PaiGowGame(Casino casino, PaiGowMachine machine)
	{
		super (casino, machine);
		this.commissionRate = machine.getCommissionRate ();
	}

	//=======================================================================
	// PROPERTIES	 
	//=======================================================================

	/**
	 * Get my machine.
	 */
	public PaiGowMachine getPaiGowMachine()
	{
		return (PaiGowMachine) getMachine();
	}

	/**
	 * Get the most recent bet in dollars.
	 * @return the bet, as an integer.
	 */
	public int getBet()
	{
		return bet;
	}

	/**
	 * Get the amount returned to the player.
	 * @return the win, as a floating point number
	 */
	public double getWin()
	{
		double winnings = 0;

		if (gameResult == PlayerWin) {
			// Player wins twice the bet minus the 5% commission.
			winnings = bet * (2 - commissionRate);
		}
		else if (gameResult == Push)
			// No one wins so the player gets his or her original bet back.
			winnings = bet;

		return winnings;
	}

	/**
	 * Get the amount returned to the player.
	 * @return the win, as a Money value
	 */
	public Money getWinMoney ()
	{
		return new Money (getWin ());
	}

	/**
	 * Return true if the game is ready to deal a new hand, false if
	 * a hand is in progress.
	 */
	public boolean isDealOk()
	{
		return dealOk;
	}

	/**
	 * Tell whether the player or dealer is currently the banker.
	 */
	public int getBankerCode ()
	{
		return playerIsBanker ? PlayerIsBanker : DealerIsBanker;
	}

	/**
	 * Get a string that represents the most recent player hand in 
	 * Card encoding. 
	 */
	public String getPlayerString()
	{
		return Card.toString (playerHand, " "); 
	}

	/**
	 * Get a string that represents the most recent player hand in 
	 * Card encoding.
	 */
	public String getDealerString()
	{
		return Card.toString (dealerHand, " "); 
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
	 * Get the overall result of the last hand.
	 */
	public int getGameResult()
	{
		return gameResult;
	}

	/**
	 * Get the result of comparing the player's most recent 5-card hand
	 * against the dealer's most recent 5-card hand.
	 * @return a positive number if the player's hand beat the dealer's
	 * hand, a negative number if the dealer's hand beat the player's
	 * hand, zero otherwise.
	 */
	public int getFiveHandResult ()
	{
		return fiveHandResult;
	}

	/**
	 * Get the result of comparing the player's most recent 2-card hand
	 * against the dealer's most recent 2-card hand.
	 * @return a positive number if the player's hand beat the dealer's
	 * hand, a negative number if the dealer's hand beat the player's
	 * hand, zero otherwise.
	 */
	public int getTwoHandResult ()
	{
		return twoHandResult;
	}

	/**
	 * Get a string describing the dealer's 5-card hand.
	 * @return a string, or null if the hand is still in progress.
	 */
	public String getDealerFiveHandDescription ()
	{
		return handDesc == null ? null : handDesc[0];
	}

	/**
	 * Get a string describing the dealer's 2-card hand.
	 * @return a string, or null if the hand is still in progress.
	 */
	public String getDealerTwoHandDescription ()
	{
		return handDesc == null ? null : handDesc[1];
	}

	/**
	 * Get a string describing the player's 5-card hand.
	 * @return a string, or null if the hand is still in progress.
	 */
	public String getPlayerFiveHandDescription ()
	{
		return handDesc == null ? null : handDesc[2];
	}

	/**
	 * Get a string describing the player's 2-card hand.
	 * @return a string, or null if the hand is still in progress.
	 */
	public String getPlayerTwoHandDescription ()
	{
		return handDesc == null ? null : handDesc[3];
	}

	//=======================================================================
	// METHODS	  
	//=======================================================================

	/**
	 * Request the next deal.
	 * @param player		the player
	 * @param bet		 the amount wagered
	 */
	public synchronized void deal(Player player, int bet)
		throws CasinoException
	{
		new Deal (bet).play (player);
	}

	/**
	 * Set the player hand according to the house strategy.
	 * Don't play it yet.
	 */
	public synchronized void houseway (Player player)
		throws CasinoException
	{
		new HouseWayPlay ().play (player);
	}

	/**
	 * The player has finished setting cards.  Resolve the hand.
	 * @param player				the player
	 * @param handString		 the hand as a string of card encodings,
	 *								  separated by spaces
	 */
	public void play(Player player, String handString)
		throws CasinoException
	{
		byte[] bHand = new byte[CardsInHand]; 
		int count = 0;

		// Convert hand string to byte array.
		try
		{
			for (StringTokenizer st = new StringTokenizer (handString);
				 st.hasMoreTokens (); )
			{
				// If card string is invalid, sameCards will catch it.
				bHand[count++] = Card.parse (st.nextToken ());
			}
		}
		catch (Exception e)
		{
			throw new GameException ("bad hand string: " + handString, this);
		}

		if (count != CardsInHand)
			throw new GameException ("not enough cards: " + handString, this);

		play (player, bHand);
	}

	/**
	 * The player has finished setting cards.  Resolve the hand.
	 * @param player		the player
	 * @param hand		  the hand as a byte array
	 */
	public synchronized void play(Player player, byte[] hand)
		throws CasinoException
	{
		new Resolve (hand).play (player);
	}

	/**
	 * Return true if the indicated Player can legally quit the game.
	 */
	public synchronized boolean isQuitLegal (Player player)
	{
		return dealOk;
	}

	/**
	 * Return the amount that the indicated Player should be credited
	 * if it leaves the game now.
	 */
	public synchronized Money getRedemptionAmount (Player player)
	{
		return dealOk ? null : new Money (bet);
	}

	//======================================================================= 
	// Implementation
	//=======================================================================

	private class PaiGowPlay extends GamePlay
	{
		private int oldBet;
		private byte[] oldPlayerHand;
		private byte[] oldDealerHand;
		private int oldGameResult;
		private int oldFiveHandResult;
		private int oldTwoHandResult;
		private String[] oldHandDesc;
		private boolean oldDealOk;
		private boolean oldPlayerIsBanker;

		protected void saveState ()
		{
			oldBet = bet;
			oldPlayerHand = playerHand;
			oldDealerHand = dealerHand;
			oldGameResult = gameResult;
			oldFiveHandResult = gameResult;
			oldTwoHandResult = twoHandResult;
			oldHandDesc = handDesc;
			oldPlayerIsBanker = playerIsBanker;
			oldDealOk = dealOk;
		}

		protected void restoreState ()
		{
			bet = oldBet;
			playerHand = oldPlayerHand;
			dealerHand = oldDealerHand;
			gameResult = oldGameResult;
			fiveHandResult = oldFiveHandResult;
			twoHandResult = oldTwoHandResult;
			handDesc = oldHandDesc;
			playerIsBanker = oldPlayerIsBanker;
			dealOk = oldDealOk;
		}
	}

	private class Deal extends PaiGowPlay
	{
		private int newBet;

		Deal (int newBet)
		{
			this.newBet = newBet;
		}

		protected void validate ()
		   throws GameException
		{
			if (!dealOk) 
				throw new IllegalPlayException(PaiGowGame.this);
		}

		protected void computePlay ()
		{
			bet = newBet;
			deal ();
			gameResult = NoResult;
			twoHandResult = 0;
			fiveHandResult = 0;
			handDesc = null;
			dealOk = false;
		}

		protected void transact ()
			throws CasinoException
		{
			Transaction trans = new Transaction (PaiGowGame.this);
			trans.setWagerAmount (bet);
			getCasino().executeTransaction (trans);
		}
	}

	private class HouseWayPlay extends PaiGowPlay
	{
		protected void validate ()
		   throws GameException
		{
			if (dealOk) 
				throw new IllegalPlayException (PaiGowGame.this);
		}

		protected void computePlay ()
		{
			new WongWay (playerIsBanker).set (playerHand);
		}

		protected void transact ()
			throws CasinoException
		{
			getCasino().executeTransaction (new Transaction (PaiGowGame.this));
		}
	}

	private class Resolve extends PaiGowPlay
	{
		private byte[] hand;

		Resolve (byte[] hand)
		{
			this.hand = hand;
		}

		protected void validate ()
		   throws GameException
		{
			if (dealOk) 
				throw new IllegalPlayException (PaiGowGame.this);

			// Check given hand against the hand dealt originally 
			// to ensure that the client is resending the same set
			// of cards.
			if (!sameCards (hand))
				throw new GameException ("Client must return original cards.", PaiGowGame.this);
		}

		protected void computePlay ()
		{
			// Take the rearranged hand.
			playerHand = (byte[]) hand.clone ();

			// Set the dealer's hand.
			new WongWay (!playerIsBanker).set (dealerHand);

			// Evaluate hands.
			PokerScore playerFPS = HandInfo.score5 (playerHand, 0);
			PokerScore playerTPS = HandInfo.score2 (playerHand, 5);
			PokerScore dealerFPS = HandInfo.score5 (dealerHand, 0);
			PokerScore dealerTPS = HandInfo.score2 (dealerHand, 5);

			if (playerFPS.compareTo(playerTPS) < 0)
			{
				// Player's hand has fouled.
				gameResult = PlayerFoul; 
			}
			else
			{
				// Record individual hand results.
				fiveHandResult = playerFPS.compareTo(dealerFPS);
				if (fiveHandResult == 0)
					fiveHandResult = playerIsBanker ? 1 : -1;
				twoHandResult = playerTPS.compareTo(dealerTPS);
				if (twoHandResult == 0)
					twoHandResult = playerIsBanker ? 1 : -1;

				// Record overall result.
				if (fiveHandResult < 0 && twoHandResult < 0) 
					gameResult = DealerWin; 
				else if (fiveHandResult > 0 && twoHandResult > 0)
					gameResult = PlayerWin; 
				else 
					gameResult = Push; 
			}

			int diff5 = dealerFPS.howDiffers (playerFPS);
			int diff2 = dealerTPS.howDiffers (playerTPS);
			int diffP = playerFPS.howDiffers (playerTPS);
			handDesc = new String []
			{
				dealerFPS.format (diff5),
				dealerTPS.format (diff2),
				playerFPS.format (gameResult == PlayerFoul ? diffP : diff5),
				playerTPS.format (gameResult == PlayerFoul ? diffP : diff2)
			};

			// Next!
			dealOk = true;
			playerIsBanker = !playerIsBanker;
		}

		protected void transact ()
			throws CasinoException
		{
			Transaction trans = new Transaction (PaiGowGame.this);
			double totalReturn = getWin ();
			if (totalReturn > 0)
			{
				trans.setReturnAmount (bet);
				trans.setWinAmount (totalReturn - bet);
			}
			getCasino().executeTransaction (trans);
		}
	}

	/**
	 * Deal out new cards.
	 */
	private void deal ()
	{
		// Shuffle and deal out enough cards for both players.
		Deck deck = new Deck (1);
		byte[] cards = deck.deal (CardsInHand * 2, getRandomizer ());

		playerHand = new byte [CardsInHand];
		dealerHand = new byte [CardsInHand];

		// Deal the hands.
		for (int i = 0; i < CardsInHand; ++i)
		{
			playerHand[i] = cards[i * 2];
			dealerHand[i] = cards[i * 2 + 1];
		}
	}

	/**
	 * Validate that the given hand contains the same set of cards
	 * as the current player hand.
	 */
	private boolean sameCards (byte[] hand)
	{
		if (hand.length != playerHand.length)
			return false;

		// This test assumes that the playerHand contains no duplicate
		// cards.  That assumption is valid for single deck poker.
		//
		int trueCount = 0;
		for (int i = 0; i < playerHand.length; i++) {
			for (int j = 0; j < hand.length; j++) {
				if (playerHand[i] == hand[j]) {
					trueCount++;
					break;
				}
			}
		}

		return trueCount == playerHand.length;
	}
}
