package net.ech.casino.videopoker;

import net.ech.casino.Randomizer;

public class Deck
	implements Constants
{
	private int[] cards;

	public Deck (int nJokers)
	{
		this.cards = new int[NUMBER_OF_RANKS * NUMBER_OF_SUITS + nJokers];

		int ci = 0;
		for (int suit = 0; suit < NUMBER_OF_SUITS; ++suit) {
			for (int rank = 0; rank < NUMBER_OF_RANKS; ++rank) {
				cards[ci++] = pack(RANK_CHARS[rank], SUIT_CHARS[suit]);
			}
		}
	
		while (ci < cards.length) {
			cards[ci++] = pack('j', 'o');
		}
	}

	/**
	 * Shuffle the deck and deal out the first n cards. 
	 */
	public final String shuffleAndDeal (Randomizer random, int ncards)
	{
		StringBuilder buf = new StringBuilder(ncards);
		random.shuffle (cards, ncards);
		for (int i = 0; i < ncards; ++i) {
			unpack(cards[i], buf);
		}
		return new String(buf);
	}

	private static int pack(char r, char s)
	{
		return ((r & 0xffff) << 16) | (s & 0xffff);
	}

	private static void unpack(int v, StringBuilder out)
	{
		out.append((char) ((v >> 16) & 0xffff));
		out.append((char) (v & 0xffff));
	}
}
