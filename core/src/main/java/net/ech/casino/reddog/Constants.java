//
// Constants.java
//

package net.ech.casino.reddog;

import net.ech.casino.*;

/**
 * Constants related to Casino War.
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public interface Constants extends CardConstants
{
	/**
	 * Table state constant.  Indicates that there are no cards on the 
	 * table.
	 */
	public static final int TABLE_CLEAR = 0;

	/**
	 * Table state constant.  Indicates that the first two cards are 
	 * showing, the hand is still in progress, and the game is waiting
	 * for all players to respond.
	 */
	public static final int TABLE_WORKING = 1;

	/**
	 * Table state constant.  Indicates that the final result of a 
	 * hand is showing.
	 */
	public static final int TABLE_END_OF_HAND = 2;

	/**
	 * Seat state constant.	 Indicates that there is no player in the
	 * seat or the player is sitting out.
	 */
	public static final int SEAT_OUT = 0;

	/**
	 * Seat state constant.	 Indicates that the game is waiting for the
	 * player to respond.
	 */
	public static final int SEAT_READY = 1;

	/**
	 * Seat state constant.	 Indicates that the player is waiting for 
	 * game activity.
	 */
	public static final int SEAT_BLOCKED = 2;

	/**
	 * The default number of decks in the shoe.
	 */
	public static final int DEFAULT_NUMBER_OF_DECKS = 6;

	/**
	 * Index of the left card.
	 */
	public static final int CARD_LEFT = 0;

	/**
	 * Index of the right card.
	 */
	public static final int CARD_RIGHT = 1;

	/**
	 * Index of the center card.
	 */
	public static final int CARD_CENTER = 2;

	/**
	 * The maximum number of cards dealt per hand.
	 */
	public static final int MAXIMUM_CARDS_PER_HAND = 3;

	/**
	 * Pay factor for a triple tie.
	 */
	public static final int PAY_FOR_TIE = 11;

	/**
	 * Pay factor for a spread of one.
	 */
	public static final int PAY_FOR_SPREAD_1 = 5;

	/**
	 * Pay factor for a spread of two.
	 */
	public static final int PAY_FOR_SPREAD_2 = 4;

	/**
	 * Pay factor for a spread of three.
	 */
	public static final int PAY_FOR_SPREAD_3 = 2;

	/**
	 * Pay factor for a spread of four or more.
	 */
	public static final int PAY_FOR_SPREAD_4_PLUS = 1;
}
