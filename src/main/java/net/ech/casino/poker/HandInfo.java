//
// HandInfo.java  
// 

package net.ech.casino.poker;

import net.ech.casino.*;

/**
 * A HandInfo object is a digested poker hand.
 * Class includes static method for scoring.
 * Class assumes valid poker hands and cards.
 * There is no assumption about the number of decks
 * dealt from.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class HandInfo implements CardConstants
{
	/**
	 * Score a hand.
	 */
	public static PokerScore score (byte[] hand)
	{
		HandInfo info = new HandInfo (hand);

		if (info.hasTuple (5))
			return PokerScore.makeFiveOfAKind (Ace);

		boolean flush = isFlush (hand);
		if (flush && info.isStraight ())
			return PokerScore.makeStraightFlush (info.getStraightRank ());

		if (info.hasTuple (4))
			return PokerScore.makeFourOfAKind (info.getRank (4, 0));

		if (info.hasTuple (3) && info.hasTuple (2))
			return PokerScore.makeFullHouse (info.getRank (3, 0),
											 info.getRank (2, 0));

		if (flush)
			return PokerScore.makeFlush (info.getRank (0), info.getRank (1),
										 info.getRank (2), info.getRank (3));

		if (info.isStraight ())
			return PokerScore.makeStraight (info.getStraightRank ());

		if (info.hasTuple (3))
			return PokerScore.makeThreeOfAKind (info.getRank (3, 0));

		if (info.getPairCount () == 2)
			return PokerScore.makeTwoPair (info.getRank (2, 0),
										   info.getRank (2, 1),
										   info.getRank (1, 0));

		if (info.getPairCount () > 0)
			return PokerScore.makePair (info.getRank (2, 0),
										info.getRank (1, 0),
										info.getRank (1, 1),
										info.getRank (1, 2));

		return PokerScore.makeHighCard (info.getRank (0),
										info.getRank (1),
										info.getRank (2),
										info.getRank (3),
										info.getRank (4));
	}

	/**
	 * Detect flush.
	 */
	public static boolean isFlush (byte[] hand)
	{
		byte firstSuit = NilSuit;

		for (int i = 0; i < hand.length; ++i)
		{
			if (hand[i] == Joker)
				continue;

			byte suit = Card.suitOf (hand[i]);
			if (firstSuit == NilSuit)
			{
				firstSuit = suit;
			}
			else if (suit != firstSuit)
			{
				return false;
			}
		}

		return true;
	}

	private byte[] rankCounts = new byte [NumberOfRanks];
	private byte straightRank = NilRank;
	private byte highRank = NilRank;
	private byte lowRank = NilRank;
	private boolean flush;

	/**
	 * Constructor.
	 */
	public HandInfo (byte[] hand)
	{
		// Rank the cards, skipping joker for now.
		// Gather statistics used for detection of straights.
		//
		boolean hasJoker = false;
		boolean anyPair = false;
		boolean anySixThruKing = false;
		for (int i = 0; i < hand.length; ++i)
		{
			byte rank = Card.rankOf (hand[i]);
			if (rank == NilRank /* joker */)
			{
				hasJoker = true;
			}
			else
			{
				if (rank >= Six && rank <= King)
					anySixThruKing = true;

				if (highRank == NilRank || rank > highRank)
					highRank = rank;

				if (lowRank == NilRank || rank < lowRank)
					lowRank = rank;

				if ((rankCounts[rank - MinRank] += 1) > 1)
					anyPair = true;
			}
		}

		// Check for straights.
		// Any natural pair eliminates the possibility of a straight.
		//
		if (!anyPair)
		{
			if (!anySixThruKing)		// ace-low straight.
				this.straightRank = Five;
			else
			{
				switch (highRank - lowRank)
				{
				case 3: // Joker outside the straight.
					if (highRank != Ace)
						highRank += 1;
				case 4: // If there's a joker, it's inside the straight.
					this.straightRank = highRank;
				}
			}
		}

		// Now count the joker as an ace for the purpose of tuples
		// and high card.
		//
		if (hasJoker)
			rankCounts[Ace - MinRank] += 1;

		flush = isFlush (hand);
	}

	/**
	 * Is this a flush?
	 */
	public boolean isFlush ()
	{
		return flush;
	}


	/**
	 * Get the highest rank in the hand, treating Joker as ace.
	 */
	public byte getHighRank ()
	{
		return highRank;
	}

	/**
	 * Get the lowest rank in the hand, treating Joker as ace.
	 */
	public byte getLowRank ()
	{
		return lowRank;
	}

	/**
	 * Return the nth highest rank in the hand, treating joker
	 * as ace.
	 */
	public byte getRank (int n)
	{
		for (int i = rankCounts.length; --i >= 0; )
		{
			if (rankCounts[i] > 0 && n-- == 0)
				return (byte) (MinRank + i);
		}

		throw new RuntimeException ("should not be reached");
	}

	/**
	 * Return the nth highest rank of an m-tuple in the hand.
	 */
	public byte getRank (int m, int n)
	{
		for (int i = rankCounts.length; --i >= 0; )
		{
			if (rankCounts[i] == m && n-- == 0)
				return (byte) (MinRank + i);
		}

		throw new RuntimeException ("should not be reached");
	}

	/**
	 * Return the number of pairs (excluding triples, etc.) in the hand.
	 */
	public int getPairCount ()
	{
		int pairCount = 0;

		for (int i = rankCounts.length; --i >= 0; )
		{
			if (rankCounts[i] == 2)
				++pairCount;
		}

		return pairCount;
	}

	/**
	 * Tell whether there is any m-tuple in this hand.
	 */
	public boolean hasTuple (int m)
	{
		for (int i = rankCounts.length; --i >= 0; )
		{
			if (rankCounts[i] == m)
				return true;
		}

		return false;
	}

	/**
	 * Tell whether this hand may be considered a straight, ace-low straight
	 * excluded.
	 */
	public boolean isStraight ()
	{
		return straightRank != NilRank;
	}

	/**
	 * If this hand may be considered a straight (ace-low straight excluded),
	 * return the rank of the straight, else return NilRank.
	 */
	public byte getStraightRank ()
	{
		return straightRank;
	}
}
