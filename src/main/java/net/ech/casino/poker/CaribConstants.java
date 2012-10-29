//
// CaribConstants.java
//

package net.ech.casino.poker;

import net.ech.casino.*;

/**
 * Constants for Caribbean Stud Poker.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public interface CaribConstants extends CardConstants
{
	/**
	 * The base (starting) jackpot amount.
	 */
	public final static double BaseJackpotAmount = 10000;

	/**
	 * Factors used in determining the percent of the jackpot won.
	 */
	public final static double StraightFlushFactor = 0.1;
	public final static double RoyalFlushFactor = 1;

	/**
	 * Game result codes.
	 */
	public final static int NoResult = 0;
	public final static int PlayerFolded = 1;
	public final static int DealerNotQualify = 2;
	public final static int DealerWon = 3;
	public final static int PlayerWon = 4;
	public final static int Push = 5;

	/**
	 * This is five-card stud poker.
	 */
	public final static int CardsInHand = 5;
}
