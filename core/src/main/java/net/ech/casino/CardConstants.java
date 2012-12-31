//
// CardConstants.java  
// 

package net.ech.casino;

/**
 * Card game constants.	 Includes values for all standard ranks and suits, 
 * plus a nil card value and a joker value.
 * 
 * @see net.ech.casino.Card
 *
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public interface CardConstants
{
	/**
	 * Suits 
	 */
	public final static byte NilSuit = 0;
	public final static byte MinSuit = 1;
	public final static byte Diamonds = 1;
	public final static byte Clubs = 2;
	public final static byte Hearts = 3;
	public final static byte Spades = 4;
	public final static byte NumberOfSuits = 4;
	public final static byte JokerSuit = 5;		// not truly a suit

	/**
	 * Ranks
	 */
	public final static byte NilRank = 0;
	public final static byte MinRank = 2;
	public final static byte Deuce = 2;
	public final static byte Three = 3;
	public final static byte Four = 4;
	public final static byte Five = 5;
	public final static byte Six = 6;
	public final static byte Seven = 7;
	public final static byte Eight = 8;
	public final static byte Nine = 9;
	public final static byte Ten = 10;
	public final static byte Jack = 11;
	public final static byte Queen= 12;
	public final static byte King = 13;
	public final static byte Ace = 14;	// ace is high rank
	public final static byte MaxRank = 14;
	public final static byte NumberOfRanks = 13;

	/**
	 * Special card values
	 */
	public final static byte NilCard = 0;
	public final static byte Joker = (byte) (JokerSuit << 4);

	/**
	 * String representations for the two special card values.
	 */
	public final static String NilString = "--";
	public final static String JokerString = "jo";

	/**
	 * Deck constants.
	 */
	public final static int CardsInStandardDeck =
		NumberOfRanks * NumberOfSuits;

}
