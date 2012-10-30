//
// SlotsGame.java  
// 

package net.ech.casino.slots;

import java.util.*;
import net.ech.casino.*;

/**
 * A slot machine servelet.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class SlotsGame extends CreditsGame
{
	//
	// For encoding of spin results:
	//
	private final static String SymbolCharString = 
		"ABCDEFGHIJKLMNOPQRSTUVWXYZ" + 
		"abcdefghijklmnopqrstuvwxyz" + 
		"0123456789";
	private final static char[] SymbolChars = SymbolCharString.toCharArray ();

	//
	// Charge-up constants.
	// All games charge for 2 minutes or 20 pulls.	These values are 
	// currently constants, but could be configuration variables.
	//
	private final static long MinuteMillis = 60 * 1000L;
	private final static int PullsPerMinute = 10;
	private final static int ChargeTimeMinutes = 2;
	private final static long ChargeTimeMillis =
		ChargeTimeMinutes * MinuteMillis;
	private final static int ChargePullMax = 
		ChargeTimeMinutes * PullsPerMinute;

	// Game state...
	private int[] stops;
	private int winningPayLines;
	private int winLevel;
	private transient Money jackpotAmount;
	private boolean jackpotWin;
	private Money jackpotWinAmount;
	private boolean charged;
	private long chargeTimeout;
	private int chargePullCount;
	private boolean chargeup;

	/**
	 * Constructor.
	 */
	public SlotsGame (Casino casino, SlotMachine machine)
	{
		super (casino, machine);

		this.stops = new int [machine.getReelCount ()];

		// Randomize the initial positions of the reel.
		spin ();
	}

	//==================================================================
	// PROPERTIES
	//==================================================================

	/**
	 * Get my machine description.
	 */
	public SlotMachine getSlotMachine ()
	{
		return (SlotMachine) getMachine ();
	}

	/**
	 * Get the stop positions of the reels.
	 * @return an array of reel indexes specifying the stops under the
	 * <strong>center</strong> line.
	 */
	public int[] getStops ()
	{
		return (int[]) stops.clone ();
	}

	/**
	 * Get the stop position of a reel.
	 */
	public int getStop (int index)
	{
		return stops[index];
	}

	/**
	 * Get the array of symbols appearing under the center line.
	 * @return an array of integers, with zero representing the first
	 * symbol, etc.
	 */
	public int[] getSymbols ()
	{
		return getSymbols (0);
	}

	/**
	 * Get the array of symbols appearing under the specified pay line.
	 * @return an array of integers, with zero representing the first
	 * symbol, etc.
	 * @exception ArrayIndexOutOfBoundsException if payLineIndex invalid
	 */
	public int[] getSymbols (int payLineIndex)
	{
		return getSlotMachine ().stopsToSymbols (stops, payLineIndex,
												 new int [stops.length]);
	}

	/**
	 * Get the array of symbols appearing under the center line.
	 * @return a String, with 'A' representing the first symbol, etc.
	 */
	public String getSymbolString ()
	{
		return getSymbolString (0);
	}

	/**
	 * Get the array of symbols appearing under the indexed pay line.
	 * @return a String, with 'A' representing the first symbol, etc.
	 */
	public String getSymbolString (int payLineIndex)
	{
		int[] symbols = getSymbols (payLineIndex);

		StringBuilder buf = new StringBuilder (symbols.length);

		for (int i = 0; i < symbols.length; ++i)
		{
			char c = '?';
			try
			{
				c = SymbolChars[symbols[i]];
			}
			catch (Exception e)
			{
				// Not expected.
			}
			buf.append (c);
		}

		return buf.toString ();
	}

	/**
	 * Get the current win level.  Win level is the graduated amount of
	 * feedback to give the user for a win.	 Win level is non-negative;
	 * zero indicates no win.  The maximum depends on the pay table.
	 */
	public int getWinLevel ()
	{
		return winLevel;
	}

	/**
	 * Return the last known jackpot amount.
	 * @return a Money value
	 */
	public Money getJackpotAmount ()
	{
		return jackpotAmount;
	}

	/**
	 * Return true iff the last pull resulted in a jackpot win.
	 */
	public boolean isJackpotWin ()
	{
		return jackpotWin;
	}

	/**
	 * If the last pull resulted in a jackpot win, return the total amount
	 * won.	 Otherwise, return null.  Note: this value is not set properly
	 * until the accounting system calls applyJackpotAmount().
	 */
	public Money getJackpotWinAmount ()
	{
		return jackpotWinAmount;
	}

	/**
	 * Tell whether there was a winner on the specified pay line.
	 */
	public boolean isWinningLine (int payLineIndex)
	{
		return (winningPayLines & (1 << payLineIndex)) != 0;
	}

	/**
	 * Get "charged" state.	 When game is charged, ChargedPayouts pay out.
	 */
	public boolean isCharged ()
	{
		return charged;
	}

	/**
	 * If the game is currently charged up, get the number of full seconds
	 * remaining to the charge.	 Otherwise, return zero.
	 */
	public int getChargeSecondsLeft ()
	{
		if (chargeTimeout == 0 || chargePullCount >= ChargePullMax)
			return 0;
	   
		return (int) ((chargeTimeout - System.currentTimeMillis()) / 1000);
	}

	/**
	 * Return true if this game was charged up by the last pull.
	 */
	public boolean getChargeUp ()
	{
		return chargeup;
	}

	//==================================================================
	// METHODS
	//==================================================================

	/**
	 * Pull the lever.
	 * @param bet			 number of credits to bet
	 */
	public void pull (int bet)
		throws CasinoException
	{
		pull (bet, false);
	}

	/**
	 * Pull the lever.
	 * @param bet			 number of credits to bet
	 * @param assertCharged	 true if client thinks game is charged up
	 */
	public void pull (int bet, boolean assertCharged)
		throws CasinoException
	{
		pull (bet, assertCharged, false);
	}

	/**
	 * Pull the lever.
	 * @param bet			 number of credits to bet
	 * @param assertCharged	 true if client thinks game is charged up
	 * @param testPayouts	 true to enable pay table testing
	 */
	public synchronized void pull (
		int bet,
		boolean assertCharged,
		boolean testPayouts
	)
		throws CasinoException
	{
		// Validate bet.
		if (bet <= 0 || bet > getMaximumBet ())
		{
			throw new GameException ("bad bet amount: " + bet, this);
		}

		// Validate charge assertion.
		if (assertCharged)
		{
			assertCharged();
		}

		// Save state.
		int oldBet = getBet ();
		int oldWin = getWin ();
		boolean oldJackpotWin = jackpotWin;
		Money oldJackpotWinAmount = jackpotWinAmount;
		int[] oldStops = (int[]) stops.clone ();
		int oldWinningPayLines = winningPayLines;
		int oldWinLevel = winLevel;
		boolean oldCharged = charged;
		long oldChargeTimeout = chargeTimeout;
		int oldChargePullCount = chargePullCount;
		boolean oldChargeup = chargeup;

		try
		{
			setBet (bet);
			setWin (0);
			winningPayLines = 0;
			winLevel = 0;
			jackpotWin = false;
			jackpotWinAmount = null;
			charged = assertCharged;
			chargeup = false;

			// Set the stops.
			if (testPayouts)
			{
				if (!getSlotMachine().isTestMode())
				{
					throw new GameException ("not for testing", this);
				}
				// To test, iterate through the pay table.
				fabricate();
			}
			else
			{
				spin();
			}

			rewardMatches();
			executeTransaction();
		}
		catch (Exception e)
		{
			setBet (oldBet);
			setWin (oldWin);
			stops = oldStops;
			winningPayLines = oldWinningPayLines;
			winLevel = oldWinLevel;
			jackpotWin = oldJackpotWin;
			jackpotWinAmount = oldJackpotWinAmount;
			charged = oldCharged;
			chargeTimeout = oldChargeTimeout;
			chargePullCount = oldChargePullCount;
			chargeup = oldChargeup;

			throw (e instanceof CasinoException) ? (CasinoException) e
												 : new CasinoException (e);
		}
	}

	/**
	 * Throw an exception if the game is not charged up.
	 */
	private void assertCharged () throws GameException
	{
		// Invalid assertion means illegal client.
		// Allow a full minute grace period, but no more than the 
		// maximum number of pulls.
		//
		if (chargePullCount	 >= ChargePullMax || 
			System.currentTimeMillis() > chargeTimeout + MinuteMillis)
		{
			throw new GameException ("invalid charge assertion", this);
		}
	}

	/**
	 * Randomize the positions of the reels.
	 */
	private void spin ()
	{
		Randomizer random = getRandomizer ();
		for (int i = 0; i < stops.length; ++i)
		{
			int reelSize = getSlotMachine ().getReelPattern (i).length;
			stops[i] = random.roll (0, reelSize - 1);
		}
	}

	/**
	 * Position the reels to the next winner in the pay table.
	 */
	private void fabricate ()
		throws GameException
	{
		SlotMachine machine = getSlotMachine ();

		// Create the callback object.
		CoreAdapter core = new CoreAdapter ();

		// What win is showing currently?
		int targetPayTableIndex = 0;
		int targetPayLine = 0;

		int numPayLines = machine.getNumberOfPayLines ();
		for (int i = 0; i < numPayLines; ++i)
		{
			int thisPayTableIndex =
				machine.getPayTable().findMatch (getSymbols (i), core);
			if (thisPayTableIndex >= 0)
			{
				targetPayTableIndex = thisPayTableIndex;
				targetPayLine = i;
				break;
			}
		}

		for (int tries = 0; tries < 100; ++tries)
		{
			// Advance cyclicly through the pay table and pay lines.
			targetPayTableIndex -= 1;
			if (targetPayTableIndex < 0)
			{
				targetPayTableIndex = machine.getPayTable().getLength () - 1;
				targetPayLine -= 1;
				if (targetPayLine < 0)
					targetPayLine = machine.getNumberOfPayLines () - 1;
			}

			// Ask the machine to position a winning pattern under the
			// specified pay line.
			//
			if (machine.fabricate (targetPayTableIndex, targetPayLine, core, stops))
				return;
		}

		throw new GameException ("cannot fabricate winner for testing", this);
	}

	/**
	 * Reward the player according to the current spin result.
	 */
	private void rewardMatches ()
	{
		SlotMachine machine = getSlotMachine();

		// Create the callback object.
		CoreAdapter core = new CoreAdapter ();

		// Get the number of active pay lines.
		int numActivePayLines = machine.getNumberOfActivePayLines (core);

		// Process each active pay line.
		for (; core.payLineIndex < numActivePayLines; ++core.payLineIndex)
		{
			// Get the symbols that appear on the pay line.
			int[] stops = getStops ();
			int[] symbols =
				machine.stopsToSymbols (stops, core.payLineIndex,
					new int [stops.length]);

			// Let the machine exercise its pay table.
			machine.rewardMatches (symbols, core);
		}

		if (jackpotWin)
		{
			chargeTimeout = 0;
		}

		++chargePullCount;

		if (chargeup)
		{
			chargeTimeout = System.currentTimeMillis() + ChargeTimeMillis;
			chargePullCount = 0;
		}
	}

	private class CoreAdapter implements SlotCore
	{
		int payLineIndex = 0;

		public SlotMachine getMachine ()
		{
			return SlotsGame.this.getSlotMachine();
		}

		public int getBet ()
		{
			return SlotsGame.this.getBet();
		}

		public boolean isCharged ()
		{
			return SlotsGame.this.isCharged();
		}

		public void addWin (int winLevel, int amountCredits)
		{
			SlotsGame.this.winLevel =
				Math.max (SlotsGame.this.winLevel, winLevel);
			SlotsGame.this.setWin (SlotsGame.this.getWin() + amountCredits);
			SlotsGame.this.winningPayLines |= 1 << payLineIndex;
		}

		public void addCharge ()
		{
			SlotsGame.this.chargeup = true;
		}

		public void addJackpot (int winLevel)
		{
			SlotsGame.this.jackpotWin = true;
			SlotsGame.this.winLevel =
				Math.max (SlotsGame.this.winLevel, winLevel);
		}
	}

	private void executeTransaction ()
		throws CasinoException
	{
		Transaction trans = new Transaction (this);
		trans.setWagerAmount (getBetMoney ());
		if (getWin () > 0)
		{
			trans.setReturnAmount (getBetMoney ());
			trans.setWinAmount (getWinMoney ().subtract (getBetMoney ()));
		}

		// Jackpot.
		JackpotParameters jackpotParams =
			getSlotMachine().getJackpotParameters();
		if (jackpotParams != null && jackpotParams.getJackpotName() != null)
		{
			JackpotTransaction jtrans = new JackpotTransaction ();
			jtrans.setJackpotName (jackpotParams.getJackpotName());

			double betDollars = getBet() * getDollarsPerCredit();

			double factor =
				getBet() == getMaximumBet()
					? jackpotParams.getJackpotContribMaxBet ()
					: jackpotParams.getJackpotContrib ();

			// Always contribute to pot.
			jtrans.setContributionAmount (betDollars * factor);

			if (jackpotWin)
			{
				// Stake a claim.
				jtrans.setClaimFactor (1.0);
			}

			trans.addJackpotTransaction (jtrans);
		}

		// Commit to database.
		getCasino().executeTransaction (trans);
	}

	/**
	 * Apply the latest jackpot amount to this Game's properties.
	 * The Session must call this method whenever the player
	 * contributes to a jackpot or wins a jackpot.
	 */
	protected void applyJackpotAmount (String jackpotName, Money jackpotAmount)
	{
		if (jackpotWin)
		{
			// Player won the jackpot.

			// Add the jackpot to the most recent win.	The win is
			// expressed in credits.   Also hang on to the jackpot win 
			// amount in Money terms.
			//
			jackpotWinAmount = jackpotAmount.add (
				new Money (getWin() * getDollarsPerCredit()));
			setWin ((int) (jackpotWinAmount.doubleValue () / getDollarsPerCredit()));
			jackpotAmount = 
				getSlotMachine().getJackpotParameters().getJackpotBaseAmount();
		}
		else
		{
			// Otherwise, cache the jackpot amount.
			this.jackpotAmount =
				jackpotAmount.add (
					getSlotMachine().getJackpotParameters().getJackpotBaseAmount());
		}
	}
}
