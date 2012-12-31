//
// Constants.java  
// 

package net.ech.casino.blackjack;

import net.ech.casino.CardConstants;

/**
 * Blackjack constants.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public interface Constants extends CardConstants
{
	/**
	 * Hand scores.
	 */ 
	public final static int BUST = 0;
	public final static int BLACKJACK = 22;

	/**
	 * @deprecated 
	 * @see CasinoBlackjack#setTotalsEnablingDouble
	 */
	public final static int DoubleTwo9_11 = 1;

	/**
	 * Split policy.
	 */
	public final static int SPLIT_NEVER = 0;
	public final static int SPLIT_EQUAL_DENOMINATION = 1;
	public final static int SPLIT_ANY_TEN = 2;

	/**
	 * Enumeration of the various blackjack moves.
	 */
	public final static int Deal		= 0;
	public final static int Stand		= 1;
	public final static int Hit = 2;
	public final static int Split		= 3;
	public final static int DoubleDown	= 4;
	public final static int Surrender	= 5;
	public final static int Insurance	= 6;

	/**
	 * Definition of moveFlags bits.
	 */
	public final static int DealOk				= 1<<Deal;
	public final static int StandOk				= 1<<Stand;
	public final static int HitOk				= 1<<Hit;
	public final static int SplitOk				= 1<<Split;
	public final static int DoubleDownOk		= 1<<DoubleDown;
	public final static int SurrenderOk = 1<<Surrender;
	public final static int InsuranceOk = 1<<Insurance;

	// Transaction codes for the various moves:
	public final static String[] MoveCodes =
	{
		"deal",
		"stand",
		"hit",
		"split",
		"dd",
		"surrender",
		"insurance",
	};
}
