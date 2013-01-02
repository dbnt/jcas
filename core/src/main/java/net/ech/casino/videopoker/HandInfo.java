//
// HandInfo.java
// 

package net.ech.casino.videopoker;

/**
 * A HandInfo object is a digested video poker hand.
 */
public class HandInfo
	implements Constants
{
	private String hand;
	private int wildCount;
	private String rankCounts = "";
	private String uniqueSuits = "";

	//
	// Constructor.	 This pre-computes the results of all the queries.
	//
	public HandInfo (String hand, CardPattern wildCardPattern)
	{
		this.hand = hand;

		int[] rankCounts = new int[NUMBER_OF_RANKS];
		int[] suitCounts = new int[NUMBER_OF_SUITS];

		// Decompose the hand into its rank and suit counts.
		for (int i = 0; i < CARDS_IN_HAND; ++i) {
			char rank = hand.charAt(i * 2);
			char suit = hand.charAt(i * 2 + 1);
			if (rank == JOKER_STRING.charAt(0) || wildCardPattern.matches(rank, suit)) {
				++wildCount;
			}
			else {
				rankCounts[RANKS_STRING.indexOf(rank)] += 1;
				suitCounts[SUITS_STRING.indexOf(suit)] += 1;
			}
		}

		for (int i = 0; i < rankCounts.length; ++i) {
			this.rankCounts += (char)('0' + rankCounts[i]);
		}

		for (int i = 0; i < suitCounts.length; ++i) {
			if (suitCounts[i] > 0) {
				uniqueSuits += SUIT_CHARS[i];
			}
		}
	}

	public String getHand ()
	{
		return hand;
	}

	public int getWildCount ()
	{
		return wildCount;
	}

	public int getRankCount(int r)
	{
		return rankCounts.charAt(r) - '0';
	}

	public int getRankCount(int lo, int hi)
	{
		int sum = 0;
		for (int i = lo; i < hi; ++i) {
			sum += getRankCount(i);
		}
		return sum;
	}

	public String getSuits ()
	{
		return uniqueSuits;
	}

	public boolean isStraight ()
	{
		return
			rankCounts.matches("0*[01][01][01][01][01]0*") ||
			// Ace-low straight:
			rankCounts.matches("[01][01][01][01]000000000[01]");
	}
}
