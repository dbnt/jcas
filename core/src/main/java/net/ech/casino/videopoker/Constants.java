//
// Constants.java  
// 

package net.ech.casino.videopoker;

import net.ech.casino.CardConstants;

/**
 * Constants for Video Poker.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public interface Constants extends CardConstants
{
	public final static int CardsInHand = 5;

	/**
	 * Game state constants.
	 * DealState: waiting for player to bet/deal
	 * DrawState: waiting for player to hold/draw
	 * PickState: waiting for player to pick a card (hi/lo)
	 */
	public static final int DealState = 1;
	public static final int DrawState = 2;
	public static final int PickState = 3;

	/**
	 * Special grade values.
	 * NoGrade: a losing hand
	 */
	public static final int NoGrade = -1;
}
