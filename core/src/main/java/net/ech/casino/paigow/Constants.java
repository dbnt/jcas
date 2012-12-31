package net.ech.casino.paigow;

import net.ech.casino.*;

public interface Constants extends CardConstants
{
	public static final int CardsInHand = 7;
	public static final int CardsInFiveHand = 5;

	/**
	 * Banker codes.
	 */
	public final static int DealerIsBanker = 0;
	public final static int PlayerIsBanker = 1;

	/**
	 * Game result codes.
	 */
	public final static int NoResult = -1;
	public final static int PlayerFoul = 0;
	public final static int DealerWin = 1;
	public final static int PlayerWin = 2;
	public final static int Push = 3;
}
