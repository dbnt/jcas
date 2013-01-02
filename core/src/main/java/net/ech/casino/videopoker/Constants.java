//
// Constants.java  
// 

package net.ech.casino.videopoker;

/**
 * Constants for Video Poker.
 */
public interface Constants
{
	public final static int NUMBER_OF_RANKS = 13;
	public final static int NUMBER_OF_SUITS = 4;
	public final static int CARDS_IN_HAND = 5;

	public final static String RANKS_STRING = "23456789TJQKA";
	public final static char[] RANK_CHARS = RANKS_STRING.toCharArray();
	public final static String SUITS_STRING = "DCHS";
	public final static char[] SUIT_CHARS = SUITS_STRING.toCharArray();
	public final static String JOKER_STRING = "jo";
}
