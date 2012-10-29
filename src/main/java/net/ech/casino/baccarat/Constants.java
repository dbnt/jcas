//
// Constants.java
//

package net.ech.casino.baccarat;

/**
 * Baccarat constants.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public interface Constants extends net.ech.casino.CardConstants
{
	public static final int MaxCardsInHand = 3;
	public static final int MaxScore = 9;

	/**
	 * Bet position enumeration.
	 */
	public final static int BetOnPlayer = 0;
	public final static int BetOnBank = 1;
	public final static int BetOnTie = 2;

	/**
	 * Result enumeration.
	 */
	public final static int Tie = 0;
	public final static int PlayerWin = 1;
	public final static int PlayerNatural = 2;
	public final static int BankWin = 3;
	public final static int BankNatural = 4;
}
