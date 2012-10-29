//
// HandInfo.java  
// 

package net.ech.casino.paigow;

import net.ech.casino.Card;
import net.ech.casino.PokerScore;

/**
 * A HandInfo object is a digested pai gow poker hand.
 * Class includes static methods for scoring pai gow hands.
 * Class assumes valid pai gow hands and cards.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class HandInfo implements Constants
{
	/**
	 * Score a two-card hand.
	 */
	public static PokerScore score2 (byte[] hand, int start)
	{
		byte rank0 = Card.rankOf (hand[start + 0]);
		byte rank1 = Card.rankOf (hand[start + 1]);

		if (rank0 == NilRank /* joker */) rank0 = Ace;
		if (rank1 == NilRank /* joker */) rank1 = Ace;

		if (rank0 == rank1)
			return PokerScore.makePair (rank0);
		else if (rank0 > rank1)
			return PokerScore.makeHighCard (rank0, rank1);
		else 
			return PokerScore.makeHighCard (rank1, rank0);
	}

	/**
	 * Score a five-card hand.
	 */
	public static PokerScore score5 (byte[] hand, int start)
	{
		HandInfo info = new HandInfo (hand, start);

		if (info.hasTuple (5))
			return PokerScore.makeFiveOfAKind (Ace);

		if (isFlush (hand, start))
		{
			// Look for ace-low straight first, in case of jo-2-3-4-5.
			if (info.isAceLowStraight ())
				return PokerScore.makeAceLowStraightFlush ();

			if (info.isStraight ())
				return PokerScore.makeStraightFlush (info.getStraightRank ());

			return PokerScore.makeFlush (info.getRank (0), info.getRank(1),
										 info.getRank (2), info.getRank(3));
		}

		if (info.hasTuple (4))
			return PokerScore.makeFourOfAKind (info.getRank (4, 0));

		if (info.hasTuple (3) && info.hasTuple (2))
			return PokerScore.makeFullHouse (info.getRank (3, 0),
											 info.getRank (2, 0));

		// Look for ace-low straight first, in case of jo-2-3-4-5.
		if (info.isAceLowStraight ())
			return PokerScore.makeAceLowStraight ();

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
	public static boolean isFlush (byte[] hand, int start)
	{
		byte firstSuit = NilSuit;

		for (int i = start; i < start + 5; ++i)
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
	private boolean aceLowStraightFlag;

	//
	// Constructor.	 This pre-computes the results of all the queries.
	//
	public HandInfo (byte[] hand, int start)
	{
		// Rank the cards, skipping joker for now.
		// Gather statistics used for detection of straights.
		//
		boolean hasJoker = false;
		boolean anyPair = false;
		boolean anySixThruKing = false;
		byte highRank = NilRank;
		byte lowRank = NilRank;
		for (int i = start; i < start + 5; ++i)
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
			switch (highRank - lowRank)
			{
			case 3:		// Joker outside the straight.
				if (highRank != Ace)
					highRank += 1;
			case 4:		// If there's a joker, it's inside the straight.
				this.straightRank = highRank;
			}

			this.aceLowStraightFlag = !anySixThruKing;
		}

		// Now count the joker as an ace for the purpose of tuples
		// and high card.
		//
		if (hasJoker)
			rankCounts[Ace - MinRank] += 1;
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

	/**
	 * Tell whether this hand may be considered an ace-low straight.
	 */
	public boolean isAceLowStraight ()
	{
		return aceLowStraightFlag;
	}

	public static void main (String[] args)
	{
		try
		{
			byte[] cards = new byte [5];
			int cardCount = 0;
			for (int i = 0; i < args.length; ++i)
			{
				String arg = args[i];
				for (int cx = 0; cx < arg.length (); cx += 2)
				{
					byte card = Card.parse (arg, cx);
					if (card == NilCard)
						throw new Exception ("Bad card: " +
												arg.substring (cx, cx + 2));
					cards[cardCount++] = card;
				}
			}

			switch (cardCount)
			{
			case 5:
				System.out.println (score5 (cards, 0).format (5));
				break;
			case 2:
				System.out.println (score2 (cards, 0).format (5));
				break;
			default:
				throw new Exception ("Wrong number of cards.");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace (System.err);
			System.exit (1);
		}
	}
}
