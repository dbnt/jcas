//
// CrapsGame.java  
// 

package net.ech.casino.craps;

import java.util.*;
import net.ech.casino.*;

/**
 * Class CrapsGame runs a single-player craps table.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class CrapsGame extends TableGame implements Constants
{
	private final static int NoPoint = -1;

	// Result constants...
	private final static byte UNRESOLVED = 0;
	private final static byte WINNER = 1;
	private final static byte LOSER = 2;
	private final static byte PUSH = 3;

	private int die0, die1;
	private int rollValue;
	private int passLinePointIndex = NoPoint;
	private int previousPoint = 0;
	private int[] bets = new int [0];
	private int[] remainingBets = new int [0];
	private int[] returns = new int [0];
	private byte[] results = new byte [0];

	/**
	 * Constructor.
	 * @param casino the casino environment
	 * @param machine machine object describing the table parameters
	 */
	public CrapsGame (Casino casino, CrapsMachine machine)
	{
		super (casino, machine);
	}

	//================================================================
	// PROPERTIES
	//================================================================

	/**
	 * Get my machine.
	 */
	public CrapsMachine getCrapsMachine ()
	{
		return (CrapsMachine) getMachine ();
	}

	/**
	 * Get the value of the first die.
	 * @return a number in [1..6] or zero if no rolls yet
	 */
	public int getDie0 ()
	{
		return die0;
	}

	/**
	 * Get the value of the second die.
	 * @return a number in [1..6] or zero if no rolls yet
	 */
	public int getDie1 ()
	{
		return die1;
	}

	/**
	 * Return true iff point is established.
	 */
	public boolean isOn()
	{
		return passLinePointIndex != NoPoint;
	}

	/**
	 * Get the current point, or zero if none established.
	 */
	public int getPoint ()
	{
		return !isOn () ? 0 : points[passLinePointIndex];
	}

	/**
	 * Get the point prior to the most recent roll.
	 * @return the point value, or zero if point was not established.
	 */
	public int getPreviousPoint()
	{
		return previousPoint;
	}

	/**
	 * Get the total amount bet on the last roll.
	 */
	public int getTotalBet ()
	{
		int total = 0;

		if (bets != null)
		{
			for (int i = 0; i < bets.length; ++i)
				total += bets[i];
		}

		return total;
	}

	/**
	 * Get the total amount bet on the last roll.
	 * @return the total amount as a Money value
	 */
	public Money getTotalBetMoney ()
	{
		return new Money (getTotalBet ());
	}

	/**
	 * Get the total return on the last roll. 
	 * This includes the sum of all unresolved bets.
	 */
	public int getTotalReturn ()
	{
		int total = 0;

		for (int i = 0; i < returns.length; ++i)
			total += returns[i];

		return total;
	}

	/**
	 * Get the final return on the indexed bet.	 If there is no bet,
	 * or the bet is unresolved, or the bet has moved, return -1.
	 */
	public int getFinalReturn (int index)
	{
		if (index >= results.length)
			return -1;

		int result = results[index];
		if (result == UNRESOLVED)
			return -1;

		return returns[index];
	}

	/**
	 * Get an array of Bet objects representing the bets the 
	 * player made on the last roll.
	 */
	public CrapsBet[] getBets ()
	{
		Vector v = new Vector ();

		for (int i = 0; i < bets.length; ++i)
		{
			if (bets[i] != 0)
			{
				String name = getCrapsMachine ().getBetName (i);
				v.addElement (new CrapsBet (i, bets[i], returns[i]));
			}
		}

		CrapsBet[] result = new CrapsBet [v.size ()];
		v.copyInto (result);
		return result;
	}

	/**
	 * Get the amount still on the table at the indexed position.
	 */
	public int getRemainingBet (int index)
	{
		return index < remainingBets.length ? remainingBets[index] : 0;
	}

	//================================================================
	// METHODS
	//================================================================

	/**
	 * Bet on the next roll of the dice.
	 * @param newBets		 an array of bet values
	 */
	public void play (int[] newBets)
		throws CasinoException
	{
		play (newBets, null);
	}

	/**
	 * Apply given dice values to the next roll, for testing.  This 
	 * method fails
	 * @param newBets  an array of bet values
	 * @param dice	   the die values to roll!
	 */
	public void play (int[] newBets, int[] dice)
		throws CasinoException
	{
		if (dice != null && !getMachine ().isTestMode ())
			throw new GameException ("This craps table is not for testing.", this);

		// Validate bets.
		checkBets (newBets);

		// Save state.
		int oldDie0 = die0;
		int oldDie1 = die1;
		int oldRollValue = rollValue;
		int oldPassLinePointIndex = passLinePointIndex;
		int oldPreviousPoint = previousPoint;
		int[] oldBets = bets;
		int[] oldRemainingBets = remainingBets;
		int[] oldReturns = returns;
		byte[] oldResults = results;

		try
		{
			// Shift bets.
			bets = (int[]) newBets.clone ();
			remainingBets = new int [NUM_BETS];

			// Remember point.
			this.previousPoint = getPoint();

			// Roll.
			die0 = dice != null ? dice[0] : rollDie ();
			die1 = dice != null ? dice[1] : rollDie ();
			this.rollValue = die0 + die1;

			// Calculate bet results.
			payBets ();
			moveBets();

			// Update the point.
			int pointIndex = indexOf (rollValue, points);
			if (isOn ())
			{
				if (passLinePointIndex == pointIndex || rollValue == 7)
					passLinePointIndex = NoPoint;		 // point comes off.
			}
			else if (pointIndex >= 0)
			{
				passLinePointIndex = pointIndex;		// point established.
			}

			// All bets are subtracted with each roll.	All unresolved bets are
			// restored to the player's account with each roll.
			//
			Transaction trans = new Transaction ();
			trans.setWagerAmount (getTotalBet ());
			trans.setWinAmount (getTotalReturn ());
			getCasino().executeTransaction (trans);
		}
		catch (Exception e)
		{
			// Restore state.
			die0 = oldDie0;
			die1 = oldDie1;
			rollValue = oldRollValue;
			passLinePointIndex = oldPassLinePointIndex;
			previousPoint = oldPreviousPoint;
			bets = oldBets;
			remainingBets = oldRemainingBets;
			results = oldResults;
			returns = oldReturns;

			throw (e instanceof CasinoException) ? (CasinoException) e
												 : new CasinoException (e);
		}
	}

	private int rollDie ()
	{
		return getRandomizer ().roll (1, 6);
	}

	/**
	 * Verify that the given set of bet amounts does not conflict
	 * with the rules for craps betting, that is, bets which are
	 * frozen remain and no bet exceeds the limit.
	 */
	public void checkBets (int[] newBets)
		throws GameException
	{
		int length = Math.max (newBets.length, remainingBets.length);

		for (int i = 0; i < length; ++i)
		{
			int oldBet = i < remainingBets.length ? remainingBets[i] : 0;
			int newBet = i < newBets.length ? newBets[i] : 0;
			if (newBet < oldBet)
			{
				if (!canReduceBet (i))
					throw new CrapsException ("Cannot reduce bet #" + i, this);
				if (newBet < 0)
					throw new CrapsException ("Bad bet #" + i, this);
			}
			else if (newBet > oldBet)
			{
				// Apply table limits to pass/don't pass bets.
				switch (i)
				{
				case PASS_LINE: case DONT_PASS:
				case COME: case DONT_COME:
					if (newBet < getMachine().getMinimumBet() ||
						newBet > getMachine().getMaximumBet())
					{
						throw new GameException (
							"pass/don't pass bet out of range: " + newBet,
							this);
					}
				}

				int max = maxPlacedBet (i);
				if (newBet > max)
				{
					if (max == 0)
					{
						throw new CrapsException ("Illegal " + BetNames[i] + " bet.", this);
					}
					else
					{
						throw new CrapsException ("Cannot increase " + BetNames[i] + " bet past " + max + ".", this);
					}
				}
			}
		}
	}

	/**
	 * Return true if the indicated player can legally quit the game.
	 */
	public boolean isQuitLegal (int seatIndex)
	{
		for (int i = 0; i < remainingBets.length; ++i) {
			if (remainingBets[i] > 0 && !canReduceBet (i)) {
				return false;
			}
		}

		return true;
	}

	//================================================================
	//	IMPLEMENTATION
	//================================================================

	private boolean canReduceBet (int betIndex)
	{
		if (betIndex == PASS_LINE || betIndex == DONT_PASS) 
			return !isOn ();

		int pointBetBase = getPointBetInfo(betIndex);
		switch (pointBetBase)
		{
		case COME_4:
		case DONT_COME_4:
			return false;
		}

		return true;
	}

	private int maxPlacedBet (int betIndex)
	{
		switch (betIndex)
		{
		case PASS_LINE:
			//
			// Applet does not allow player to increase this bet once
			// point has been established, though it is to the casino's 
			// advantage to do so...
			// (same goes for put bets: COME_4, etc.)
			//
			break;
		case DONT_PASS:
			if (isOn ())
				return 0;
			break;
		case PASS_ODDS:
			if (!isOn ())
				return 0;
			return maxPassOddsBet(PASS_LINE);
		case DONT_PASS_ODDS:
			if (!isOn ())
				return 0;
			return maxDontPassOddsBet(DONT_PASS, passLinePointIndex);
		case COME:
		case DONT_COME:
			if (!isOn ())
				return 0;
			break;
		default:
			int pointBetBase = getPointBetInfo(betIndex);
			int pointBetIndex = this.pointBetIndex;
			switch (pointBetBase)
			{
			case COME_ODDS_4:
				return maxPassOddsBet(COME_4 + pointBetIndex);
			case DONT_COME_ODDS_4:
				return maxDontPassOddsBet(DONT_COME_4 + pointBetIndex,
										  pointBetIndex);
			case DONT_COME_4:
				return 0;		 // can't place these directly.
			}
		}

		//
		// No limit set by the rules of the game.  Table limit applies.
		//
		return getMaximumBet ();
	}

	private int maxPassOddsBet(int passBetIndex)
	{
		// Get the base bet amount.
		int passBet = getRemainingBet (passBetIndex);
		return getCrapsMachine().getMaxPassOddsBet (passBet);
	}

	private int maxDontPassOddsBet(int dontPassBetIndex, int pointIndex)
	{
		int point = points[pointIndex];
		int dontPassBet = getRemainingBet (dontPassBetIndex);
		return getCrapsMachine().getMaxDontPassOddsBet (point, dontPassBet);
	}

	private void payBets ()
	{
		returns = new int [bets.length];
		results = new byte [bets.length];

		boolean doubles = die0 == die1;
		boolean isCraps = isA(rollValue, craps);
		boolean isComeOutWinner = isA(rollValue, winners);
		boolean isSeven = rollValue == 7;
		boolean isTwelve = rollValue == 12;
		boolean isOn = isOn();

		int pointIndex = indexOf(rollValue, points);

		for (int betIndex = 0; betIndex < bets.length; ++betIndex)
		{
			results[betIndex] = UNRESOLVED;
			returns[betIndex] = 0;

			if (bets[betIndex] == 0) 
				continue;

			byte state = UNRESOLVED;

			int pointBetBase = getPointBetInfo(betIndex);
			int pointBetIndex = this.pointBetIndex;

			if (betIndex == PASS_LINE || betIndex == PASS_ODDS) {
				if (isOn) {
					if (pointIndex == passLinePointIndex)
						state = WINNER;
					else if (isSeven)
						state = LOSER;
				}
				else if (isComeOutWinner)
					state = WINNER;
				else if (isCraps)
					state = LOSER;
			}
			else if (betIndex == DONT_PASS || betIndex == DONT_PASS_ODDS) {
				if (isOn) {
					if (pointIndex == passLinePointIndex)
						state = LOSER;
					else if (isSeven)
						state = WINNER;
				}
				else if (isComeOutWinner)
					state = LOSER;
				else if (isTwelve)
					state = PUSH;
				else if (isCraps)
					state = WINNER;
			}
			else if (betIndex == COME) {
				if (isComeOutWinner)
					state = WINNER;
				else if (isCraps)
					state = LOSER;
			}
			else if (betIndex == DONT_COME) {
				if (isComeOutWinner)
					state = LOSER;
				else if (isTwelve)
					state = PUSH;
				else if (isCraps)
					state = WINNER;
			}
			else if (pointBetBase == COME_4) {
				if (pointBetIndex == pointIndex)
					state = WINNER;
				else if (isSeven)
					state = LOSER;
			}
			else if (pointBetBase == COME_ODDS_4) {
				if (pointBetIndex == pointIndex)
					state = WINNER;
				else if (isSeven)
					state = LOSER;
			}
			else if (pointBetBase == DONT_COME_4 ||
					 pointBetBase == DONT_COME_ODDS_4) {
				if (pointBetIndex == pointIndex)
					state = LOSER;
				else if (isSeven)
					state = WINNER;
			}
			else if (pointBetBase == PLACE_4) {
				if (pointBetIndex == pointIndex)
					state = WINNER;
				else if (isSeven)
					state = LOSER;
			}
			else if (betIndex == BIG_6 || betIndex == BIG_8) {
				if ((rollValue == 6 && betIndex == BIG_6) ||
					(rollValue == 8 && betIndex == BIG_8))
					state = WINNER;
				else if (isSeven)
					state = LOSER;
			}
			else if (betIndex == FIELD)
				state = isA(rollValue, field) ? WINNER : LOSER;
			else if (betIndex == ANY_SEVEN)
				state = (isSeven) ? WINNER : LOSER;
			else if (betIndex >= HARD_4 && betIndex <= HARD_10) {
				if (rollValue == hardways[betIndex - HARD_4])
					state = doubles ? WINNER : LOSER;
				else if (isSeven)
					state = LOSER;
			}
			else if (betIndex >= ROLL_2 && betIndex <= ROLL_12)
				state = (rollValue == rolls[betIndex - ROLL_2]) ? WINNER : LOSER;
			else if (betIndex == HORN)
				state = isA(rollValue, rolls) ? WINNER : LOSER;
			else if (betIndex == ANY_CRAPS)
				state = isCraps ? WINNER : LOSER;

			returns[betIndex] = payBet (betIndex, state);
			results[betIndex] = state;
			//remainingBets[betIndex] = state != UNRESOLVED ? 0 : bets[betIndex];
			// If bet has lost, won or pushed, remove it.
			// Exception: leave unresolved place bets on the table.
			//
			if (state == LOSER || (state != UNRESOLVED && 
				(betIndex < PLACE_4 || betIndex >= BIG_6))) {
				remainingBets[betIndex] = 0;
			}
			else {
				remainingBets[betIndex] = bets[betIndex];
			}
		}
	}

	private int payBet(int betIndex, int state)
	{
		int betAmt = bets[betIndex];

		int returnAmt = 0;
		switch (state)
		{
		case WINNER:
			returnAmt = betAmt + getWinnerPayoff (betIndex, betAmt);
			break;
		case LOSER:
			break;
		default:				// PUSH or UNRESOLVED
			returnAmt = betAmt;
		}

		return returnAmt;
	}

	private int getWinnerPayoff(int betIndex, int betAmt)
	{
		int pointBetBase = getPointBetInfo(betIndex);
		int pointBetIndex = this.pointBetIndex;
		if (betIndex == FIELD && (rollValue == 2 || rollValue == 12))
			return betAmt * 2;
		if (betIndex == ANY_SEVEN)
			return betAmt * 4;
		if (betIndex == HARD_4 || betIndex == HARD_10 ||
			betIndex == ANY_CRAPS)
			return betAmt * 7;
		if (betIndex == HARD_6 || betIndex == HARD_8)
			return betAmt * 9;
		if (betIndex == ROLL_3 || betIndex == ROLL_11)
			return betAmt * 15;
		if (betIndex == ROLL_2 || betIndex == ROLL_12)
			return betAmt * 30;
		if (betIndex == HORN)
		{
			switch (rollValue)
			{
			case 2: case 12:
				return betAmt * 30 / 4;
			case 3: case 11:
				return betAmt * 15 / 4;
			}
		}
		if (betIndex == PASS_ODDS)
			return getPassOddsPayoff(betAmt, passLinePointIndex);
		if (betIndex == DONT_PASS_ODDS)
			return getDontPassOddsPayoff(betAmt, passLinePointIndex);
		if (pointBetBase == COME_ODDS_4)
			return getPassOddsPayoff(betAmt, pointBetIndex);
		if (pointBetBase == DONT_COME_ODDS_4)
			return getDontPassOddsPayoff(betAmt, pointBetIndex);
		if (pointBetBase == PLACE_4)
			return getPlaceBetPayoff(betAmt, pointBetIndex);
		return betAmt;
	}

	private int getPassOddsPayoff(int betAmt, int pointIndex)
	{
		int point = points[pointIndex];
		return getCrapsMachine().getPassOddsWin (point, betAmt);
	}

	private int getDontPassOddsPayoff(int betAmt, int pointIndex)
	{
		int point = points[pointIndex];
		return getCrapsMachine().getDontPassOddsWin (point, betAmt);
	}

	private int getPlaceBetPayoff(int betAmt, int pointIndex)
	{
		int point = points[pointIndex];
		return getCrapsMachine().getPlaceWin (point, betAmt);
	}

	private void moveBets ()
	{
		if (isA(rollValue, points)) {
			moveBet (COME);
			moveBet (DONT_COME);
		}
	}

	private void moveBet (int betIndex)
	{
		if (betIndex >= bets.length || bets[betIndex] == 0)
			return;

		int newIndex = (betIndex == COME) ? COME_4 : DONT_COME_4;
		newIndex += indexOf(rollValue, points);

		if (remainingBets[newIndex] != 0)		  // sanity
			throw new RuntimeException ("bet " + newIndex + " != 0");
		remainingBets[newIndex] = bets[betIndex];
		remainingBets[betIndex] = 0;
	}

	private int pointBetIndex;
	private int getPointBetInfo(int betIndex)
	{
		for (int i = 0; i < pointBets.length; ++i) {
			int pointBetBase = pointBets[i];
			pointBetIndex = betIndex - pointBetBase;
			if (pointBetIndex >= 0 && pointBetIndex < points.length)
				return pointBetBase;
		}
		return -1;
	}

	private static int indexOf(int value, int values[])
	{
		for (int i = 0; i < values.length; ++i)
			if (values[i] == value)
				return i;
		return -1;
	}

	private static boolean isA(int value, int values[])
	{
		return indexOf(value, values) != -1;
	}
}
