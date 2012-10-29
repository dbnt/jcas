//
// Constants.java
//

package net.ech.casino.war;

import net.ech.casino.*;

/**
 * Constants related to Casino War.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public interface Constants extends CardConstants
{
	// Play codes.
	public static final int PLAY_NULL = 0;
	public static final int PLAY_ANTE = 1;
	public static final int PLAY_SURRENDER = 2;
	public static final int PLAY_WAR = 3;

	// Initial card index is same for player and dealer.
	public static final int CARD_INITIAL = 0;

	// Dealer card array.
	public static final int DEALER_CARD_INITIAL = CARD_INITIAL;
	public static final int DEALER_CARD_BURN1 = 1;
	public static final int DEALER_CARD_BURN2 = 2;
	public static final int DEALER_CARD_BURN3 = 3;
	public static final int DEALER_CARD_WAR = 4;
	public static final int MAX_DEALER_CARDS = 5;

	// Player card array.
	public static final int PLAYER_CARD_INITIAL = CARD_INITIAL;
	public static final int PLAYER_CARD_WAR = 1;
	public static final int MAX_PLAYER_CARDS = 2;

	// The payout multiple for a winning war.
	public static final int WAR_PAYOUT_MULTIPLE = 3;

	// The payout multiple for a winning tie bet.
	public static final int TIE_PAYOUT_MULTIPLE = 10;

	// The refund divisor for a surrender.
	public static final int SURRENDER_DIVISOR = 2;
}
