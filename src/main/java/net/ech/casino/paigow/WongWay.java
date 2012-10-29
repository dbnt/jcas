//
// WongWay.java	 
// 

package net.ech.casino.paigow;

import net.ech.casino.*;
import net.ech.casino.PokerScore;

/**
 * An implementation of Stanford Wong's "Optimal Strategy for Pai Gow Poker."
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class WongWay extends HouseWay 
{
	private final static int H_FiveAces			= 1<<0;
	private final static int H_StraightFlush	= 1<<1;
	private final static int H_FourOfAKind		= 1<<2;
	private final static int H_FullHouse		= 1<<3;
	private final static int H_Flush			= 1<<4;
	private final static int H_Straight			= 1<<5;
	private final static int H_ThreeOfAKind		= 1<<6;
	private final static int H_TwoPair			= 1<<7;
	private final static int H_Pair				= 1<<8;

	private final static int I_Banker = 0;
	private final static int I_Player = 1;

	/**
	 * Table 4.	 Fivecard two pair rather than split only if twocard
	 * hand ties or beats the hand listed here.
	 */
	private final static String[][] minSingletonToFiveCardTwoPair =
	{
		// Banker
		{
			/* 2 */ "",		   // impossible
			/* 3 */ "JT",
			/* 4 */ "Q5 Q6",
			/* 5 */ "QT QT QJ",
			/* 6 */ "K4 K4 K3 KT KJ",
			/* 7 */ "K4 K4 K3 KT KJ",
			/* 8 */ "K4 K5 KJ KJ KQ KQ",
			/* 9 */ "KT KJ KQ KQ KQ A3 A3",
			/* T */ "KQ KQ KQ A3 A3 A6 AJ AJ",
			/* J */ "A4 A4 A3 A8 AT AQ AQ AQ AK",
			/* Q */ "A8 A9 AT AJ AK AK AK AK",
			/* K */ "AJ AJ AQ AQ",
			/* A */ ""
		},

		// Player
		{
			/* 2 */ "",		   // impossible
			/* 3 */ "JT",
			/* 4 */ "Q5 Q5",
			/* 5 */ "Q9 QT K3",
			/* 6 */ "K4 K4 K3 KT KJ",
			/* 7 */ "K4 K4 K3 KT KJ",
			/* 8 */ "K4 K5 KJ KQ A3 A3",
			/* 9 */ "KT KJ KQ A3 A3 A3 A3",
			/* T */ "KQ A4 A3 A3 A3 A5 AJ AQ",
			/* J */ "A4 A4 A3 A9 AT AQ AK AK",
			/* Q */ "A8 AT AJ AK AK",
			/* K */ "AQ",
			/* A */ ""
		}
	};

	/**
	 * Table 5.	 Break a flush in favor of a pair only if the twocard
	 * hand accompanying the flush is equal to or worse than the hand
	 * listed here.
	 */
	private final static String[][] maxSingletonToBreakFlush =
	{
		// Banker
		{
			/* AJ */ "		97 T9",
			/* AQ */ "	 98 T9 T9 JT",
			/* AK */ "T7 T8 J9 JT JT QQ",
		},

		// Player
		{
			/* AJ */ "		   T8",
			/* AQ */ "		98 T9 JT",
			/* AK */ "	 98 T9 JT JT QQ",
		}
	};
	
	/**
	 * Constructor.
	 */
	public WongWay ()
	{
	}

	/**
	 * Constructor.
	 */
	public WongWay (boolean banker)
	{
		super (banker);
	}

	private int getBankerPlayerIndex ()
	{
		return isBanker () ? I_Banker : I_Player;
	}

	/**
	 * Of all usable settings, pick the one to use.
	 * Subclass may extend this method to apply more sophisticated
	 * strategy.
	 */
	protected ScoredSetting applyStrategy (ScoredSetting[] ss, int n)
	{
		// Create a bitset of available 5-card hands.
		int handSet = makeHandSet (ss, n);

		// Jump to an applicable strategy...
		ScoredSetting result = null;
		switch (handSet)
		{
		// 1. High card.
		// Must implement table 3.
		case 0:
			break;

		// 2. Pair
		case H_Pair:
			result = bestOf (ss, n, PokerScore.Pair);
			break;

		// 3. Two pair, or
		// 4. Three pair
		case H_TwoPair:
		case H_TwoPair | H_Pair:		
			result = strategy3 (ss, n);
			break;

		// 5. Three of a kind
		case H_ThreeOfAKind | H_Pair:
			result = strategy5 (ss, n);
			break;

		// 6. Straight but no flush and no pair
		case H_Straight:
			result = bestOf (ss, n, PokerScore.Straight);
			break;

		// 7. Flush but no straight and no pair
		case H_Flush:
			result = bestOf (ss, n, PokerScore.Flush);
			break;

		// 8. Straight and pair but no joker
		case H_Straight | H_Pair:
			if (!handContainsJoker())
			{
				result = strategy8 (ss, n);
			}
			break;

		// 9. Flush and a pair, but no joker
		// 11. Joker Flush and a pair
		case H_Flush | H_Pair:
			if (handContainsJoker())
			{
				result = strategy11 (ss, n);
			}
			else
			{
				result = strategy9 (ss, n);
			}
			break;

		// 13.	Straight and flush (but no pairs)
		// 14.	Straight and flush and pair
		case H_Flush | H_Straight:
		case H_Flush | H_Straight | H_Pair:
			result = strategy13 (ss, n);
			break;

		// 15.	Straight and two pair
		case H_Straight | H_TwoPair:
		case H_Straight | H_TwoPair | H_Pair:
			result = strategy15 (ss, n);
			break;

		case H_Flush | H_TwoPair | H_Pair:
			result = strategy16 (ss, n);
			break;

		// Straight flush stopgap.
		case H_StraightFlush:
			result = bestOf (ss, n, PokerScore.StraightFlush);
			break;
		case H_StraightFlush | H_Pair:
			result = misusedStrategy8 (ss, n);
			break;

		// 25. Four of a kind and a pair
		case H_FourOfAKind | H_TwoPair:
		case H_FourOfAKind | H_FullHouse | H_TwoPair:
		case H_FourOfAKind | H_ThreeOfAKind | H_TwoPair:
		case H_FourOfAKind | H_FullHouse | H_ThreeOfAKind | H_TwoPair:
			result = strategy25 (ss, n);
			break;

		// 27. Five aces.
		case H_FiveAces | H_FourOfAKind | H_FullHouse:
			result = strategy27 (ss, n);
			break;
		}

		// If no strategy applies, the default ain't bad...
		return result != null ? result : super.applyStrategy (ss, n);
	}

	private int makeHandSet (ScoredSetting[] ss, int n)
	{
		int handSet = 0;

		for (int i = 0; i < n; ++i)
		{
			switch (ss[i].fiveScore.getPrimary ())
			{
			case PokerScore.FiveOfAKind:
				handSet |= H_FiveAces;
				break;
			case PokerScore.StraightFlush:
				handSet |= H_StraightFlush;
				break;
			case PokerScore.FourOfAKind:
				handSet |= H_FourOfAKind;
				break;
			case PokerScore.FullHouse:
				handSet |= H_FullHouse;
				break;
			case PokerScore.Flush:
				handSet |= H_Flush;
				break;
			case PokerScore.Straight:
				handSet |= H_Straight;
				break;
			case PokerScore.ThreeOfAKind:
				handSet |= H_ThreeOfAKind;
				break;
			case PokerScore.TwoPair:
				handSet |= H_TwoPair;
				break;
			case PokerScore.Pair:
				handSet |= H_Pair;
			}
		}

		return handSet;
	}

	/**
	 * Strategy 3.	Two pair.
	 * The choices are to fivecard the two pair or to split it (which is
	 * what the default strategy does).	 In the former case, the kicker
	 * counts.
	 */
	private ScoredSetting strategy3 (ScoredSetting[] ss, int n)
	{
		// Strategy 4: in the case of three pairs, always two-card the
		// high pair. 
		//
		ScoredSetting twoPairSetting = bestOf(ss, n, PokerScore.TwoPair);
		if (twoPairSetting.twoScore.getPrimary() == PokerScore.Pair)
		{
			return twoPairSetting;
		}

		// 
		// Two pair handling logic.
		//
		// Default logic splits the pairs.
		//
		// If there is at least one setting that keeps the two pairs 
		// together AND has a high enough singleton hand in the two-card
		// hand to qualify, go with the best one.
		//
		PokerScore twoPair = twoPairSetting.fiveScore;
		
		// Index into table.
		PokerScore min = indexTwoCard (minSingletonToFiveCardTwoPair,
									   twoPair.getRank (0) - MinRank,
									   twoPair.getRank (1) - MinRank);

		if (min != null && twoPairSetting.twoScore.compareTo (min) >= 0)
		{
			return twoPairSetting;
		}

		// Let default strategy run.
		return null;
	}

	/**
	 * Strategy 5.	Three of a kind.
	 * Fivecard either the three of a kind or just a pair.
	 */
	private ScoredSetting strategy5 (ScoredSetting[] ss, int n)
	{
		// Find the three.
		ScoredSetting k3 = find (ss, n, PokerScore.ThreeOfAKind);

		// Default strategy always twocards two highest singletons.
		switch (k3.fiveScore.getRank (0))
		{
		case Ace:
			// Always break 3 aces.
			return null;
		case King:
			// If twohand is JT or better, leave the kings together.
			PokerScore jackTen = PokerScore.makeHighCard (Jack, Ten);
			if (k3.twoScore.compareTo (jackTen) >= 0)
				break;
			// Otherwise, fivecard a pair and twocard the best leftover.
			return bestOf (ss, n, PokerScore.Pair);
		}
		
		// Fivecard the three of a kind.
		return k3;
	}

	/**
	 * Strategy 8.	Straight and pair but no joker.
	 * Choose between straight and pair.  Default strategy often breaks
	 * the straight to put the pair in the fivehand.
	 */
	private ScoredSetting strategy8 (ScoredSetting[] ss, int n)
	{
		// In general, the straight is the better hand.
		ScoredSetting straight = bestOf (ss, n, PokerScore.Straight);
		if (straight.twoScore.getPrimary() == PokerScore.Pair)
		{
			return straight;
		}

		ScoredSetting pair = bestOf (ss, n, PokerScore.Pair);
		if (pair.twoScore.getPrimary() == PokerScore.Pair)
		{
			// Wong covers this case, in which two pair is available but
			// dominated by straight, under strategy 15.
			//
			return strategy15 (ss, n);
		}

		// The exceptions occur when the straight is an ace-high.

		switch (pair.fiveScore.getRank(0))
		{
		case Queen:
		case Jack:
		case Ten:
			// If the queen, jack or ten is paired (leaving A-K in the twohand),
			// fivecard the pair.
			//
			if (pair.twoScore.getRank(1) == King)
			{
				return pair;
			}
			break;
		case King:
			// If the king is paired (leaving A-Q in the twohand),
			// the banker should fivecard the pair.
			if (pair.twoScore.getRank(1) == Queen && isBanker ())
			{
				return pair;
			}
			break;
		}

		return straight;
	}

	/**
	 * Strategy 9.	Flush and pair but no joker.
	 * Choose between flush and pair.  Default strategy often breaks
	 * the flush to put the pair in the fivehand.
	 */
	private ScoredSetting strategy9 (ScoredSetting[] ss, int n)
	{
		// This strategy applies when there is one possible flush and
		// one of the other two cards pairs a card of the flush, creating
		// a choice between fivecarding the flush and fivecarding the pair.
		//
		// In the case of a fivecard flush with a twocard pair, we never
		// make it here (due to domination).
		//
		ScoredSetting flush = bestOf (ss, n, PokerScore.Flush);
		ScoredSetting pair = bestOf (ss, n, PokerScore.Pair);

		// WAIT! This strategy is not meant to handle cases in which
		// there are two pairs, e.g.: ACTH4H2HAH9H9C.  Wong covers this
		// case under strategy 16.	 But some flush vs. two pair cases
		// end up here because of domination.
		// 
		if (pair.twoScore.getPrimary () == PokerScore.Pair)
		{
			// Apply some of strategy 16 here.
			if (flush.twoScore.getRank(0) == Ace &&
				flush.twoScore.getRank(1) == Deuce)
			{
				return flush;
			}

			if (flush.twoScore.getRank(0) == Ace &&
				flush.twoScore.getRank(1) == King &&
				isBanker())
			{
				return flush;
			}

			return pair;
		}

		// Back to strategy 9...
		//
		// In general, the flush is the better play.  But if the pair
		// hand has a significantly better twocard hand, the pair is the 
		// better play.
		//

		// The test below excludes two possible flushes, in which case
		// the hand with the better twohand is preferred.
		//
		if (count (ss, n, PokerScore.Flush) > 1)
		{
			return flush;
		}

		// Rank of pair must be at least 7, and the twocard hand
		// accompanying the pair must be at least A-J, to even 
		// consider breaking the flush.
		//
		byte pairRank = pair.fiveScore.getRank (0);
		if (pairRank >= Seven &&
			pair.twoScore.compareTo (PokerScore.makeHighCard (Ace, Jack)) >= 0)
		{
			// Then it comes down to the rank of the pair, the rank of
			// the lower of the twocard singletons, and whether this 
			// player is the banker.
			//
			byte rank1 = pair.twoScore.getRank (1);
			PokerScore max = indexTwoCard (maxSingletonToBreakFlush,
										   rank1 - Jack,
										   pairRank - Seven);
			if (max != null && flush.twoScore.compareTo (max) <= 0)
				return pair;
		}

		return flush;
	}

	/**
	 * Strategy 11.	 Joker Flush and pair.
	 * A flush, a joker and a pair.
	 */
	private ScoredSetting strategy11 (ScoredSetting[] ss, int n)
	{
		// Obviously, if the pair can be twocarded without destroying the
		// flush, your best play is to twocard the pair.
		//
		// If the joker can be twocarded without destroying the flush,
		// then your best play is to fivecard the flush.
		//
		ScoredSetting bestFlush = find (ss, n, PokerScore.Flush);
		if (bestFlush.twoScore.getPrimary() == PokerScore.Pair ||
			bestFlush.twoScore.getPrimary() == Ace)
		{
			return bestFlush;
		}

		// An interpretation of what the book says about the case
		// in which you have ace of the flush and joker, and both are 
		// needed for the flush.  There is a narrow case in which it is
		// advantageous to fivecard the pair.
		//
		ScoredSetting bestPair = find (ss, n, PokerScore.Pair);
		if (bestPair.fiveScore.getRank(0) == Ace)
		{
			if (bestFlush.twoScore.getRank(0) == King)
			{
				return bestFlush;
			}

			if (bestPair.twoScore.getRank(0) == King)
			{
				return bestPair;
			}

			if (bestPair.twoScore.getRank(0) == Queen &&
				bestPair.twoScore.getRank(1) >= Ten &&
				bestFlush.twoScore.getRank(0) < Ten)
			{
				return bestPair;
			}

			return bestFlush;
		}

		// The best plays for the rest of the hands containing a flush, a
		// joker and a pair are the same as shown in table 5 (strategy 9)
		// where A must be understood to mean joker.
		//
		return strategy9 (ss, n);
	}

	/**
	 * Strategy 13.	 Straight and Flush (no pairs)
	 * Strategy 14.	 Straight and Flush and Pair
	 */
	private ScoredSetting strategy13 (ScoredSetting[] ss, int n)
	{
		// Consider the best of both.
		ScoredSetting straight = bestOf (ss, n, PokerScore.Straight);
		ScoredSetting flush = bestOf (ss, n, PokerScore.Flush);

		// If either gives us a pair in the two-card hand, take it.
		// (From Strategy 14)
		if (straight.twoScore.getPrimary() == PokerScore.Pair)
		{
			return straight;
		}
		if (flush.twoScore.getPrimary() == PokerScore.Pair)
		{
			return flush;
		}

		// If fivecarding a flush rather than a straight means twocarding a
		// stronger hand, then your best play is to fivecard the flush.
		//
		int cmp2 = flush.twoScore.compareTo (straight.twoScore);
		if (cmp2 > 0)
		{
			return flush;
		}

		// If fivecarding a straight instead of a flush means you can 
		// twocard a higher high card, and that card is jack or better,
		// then fivecard the straight.	Otherwise fivecard the flush.
		//
		if (straight.twoScore.getRank(0) > flush.twoScore.getRank(0))
		{
			return (straight.twoScore.getRank(0) >= Jack) ? straight : flush;
		}
 
		// If we're this far, then both settings show the same rank high card
		// but the straight's lower card is higher.
		
		// If the high card is an Ace (or joker), then if the straight's
		// lower is queen or better, go with the straight.	If the lower i
		// jack, then only the banker should go with straight.	Go with 
		// flush in all other cases.
		//
		if (straight.twoScore.getRank(0) == Ace)
		{
			switch (straight.twoScore.getRank(1))
			{
			case Queen: case King: case Ace:
				return straight;
			case Jack:
				if (isBanker())
				{
					return straight;
				}
			}
			return flush;
		}

		// If the high card is less than Ace, and the low twocard is 9
		// or better, go with straight, else flush.

		return (straight.twoScore.getRank(1) >= Nine) ? straight : flush;
	}

	/**
	 * Strategy 15.	 Straight and two pair.
	 */
	private ScoredSetting strategy15 (ScoredSetting[] ss, int n)
	{
		ScoredSetting straight = bestOf (ss, n, PokerScore.Straight);
		if (straight.twoScore.getPrimary() == PokerScore.Pair)
		{
			return straight;
		}

		ScoredSetting twoPair = bestOf (ss, n, PokerScore.TwoPair);
		if (twoPair.fiveScore.getRank(0) <= Four && 
			twoPair.fiveScore.getRank(1) == Deuce &&
			twoPair.twoScore.getPrimary() != Ace)
		{
			return straight;
		}

		return strategy3 (ss, n);
	}

	/**
	 * Strategy 16.	 Flush and two pair.
	 */
	private ScoredSetting strategy16 (ScoredSetting[] ss, int n)
	{
		// If you can twocard a pair and still have a flush, go for it.
		ScoredSetting flush = bestOf (ss, n, PokerScore.Flush);
		if (flush.twoScore.getPrimary() == PokerScore.Pair)
		{
			return flush;
		}

		// Otherwise, the choice is between two pair and one pair.	
		ScoredSetting twoPair = bestOf (ss, n, PokerScore.TwoPair);

		// Exception: if your two pair totals seven or less, and your
		// highest singletons are Q-10 or worse, fivecard the flush.

		if (twoPair.fiveScore.getRank(0) + twoPair.fiveScore.getRank(1) <= 7)
		{
			PokerScore minTwo = PokerScore.makeHighCard (Queen, Jack);
			if (twoPair.twoScore.compareTo(minTwo) < 0)
			{
				return flush;
			}
		}

		// Another exception: if your two pair is A2, fivecard the flush.
		if (twoPair.fiveScore.getRank(0) == Ace &&
			twoPair.fiveScore.getRank(1) == Deuce)
		{
			return flush;
		}

		// Another exception: if your two pair is AK, banker fivecards the
		// flush, while player twocards the KK.
		if (twoPair.fiveScore.getRank(0) == Ace &&
			twoPair.fiveScore.getRank(1) == King)
		{
			return isBanker() ? flush : bestOf (ss, n, PokerScore.Pair);
		}

		return strategy3 (ss, n);
	}

	/**
	 * Apply strategy 8 to straight flush vs. pair.	 Temporary.
	 */
	private ScoredSetting misusedStrategy8 (ScoredSetting[] ss, int n)
	{
		// In general, the straight is the better hand.
		ScoredSetting straight = find (ss, n, PokerScore.StraightFlush);

		// The exceptions occur when the straight is an ace-high.
		if (straight.fiveScore.equals (PokerScore.makeStraightFlush (Ace)))
		{
			switch (straight.twoScore.getRank (0))
			{
			case Queen:
			case Jack:
			case Ten:
				// If the queen, jack or ten is paired, fivecard the pair.
				return null;
			case King:
				// If the king is paired, the banker should fivecard the pair.
				if (isBanker ())
					return null;
			}
		}

		return straight;
	}

	/**
	 * Strategy 25.	 Four of a kind plus a pair.
	 */
	private ScoredSetting strategy25 (ScoredSetting[] ss, int n)
	{
		// See Table 10.
		// The difference between the rank of the quad and the rank of
		// the pair is the factor.

		ScoredSetting quad = find (ss, n, PokerScore.FourOfAKind);
		byte rank0 = quad.fiveScore.getRank (0);
		int diff = rank0 - quad.twoScore.getPrimary ();
		if (diff >= 9 || (diff == 8 &&
			(rank0 == Queen || rank0 == Jack ||
				(rank0 == King && !isBanker ()))))
			return bestOf (ss, n, PokerScore.TwoPair);

		return quad;
	}

	/**
	 * Strategy 27.	 Five aces.
	 */
	private ScoredSetting strategy27 (ScoredSetting[] ss, int n)
	{
		// Find the full house.
		ScoredSetting fullHouse = find (ss, n, PokerScore.FullHouse);

		// If the other two cards are a pair of kings, fivecard the 5 aces.
		if (fullHouse.fiveScore.getRank (1) == King)
			return find (ss, n, PokerScore.FiveOfAKind);

		// Otherwise, let the default logic twocard two of the aces.
		return null;
	}

	/**
	 * Pick the best of the given primary type of hand.
	 */
	private ScoredSetting bestOf (ScoredSetting[] ss, int n, int primary)
	{
		int nn = 0;
		for (int i = 0; i < n; ++i)
		{
			if (ss[i].fiveScore.getPrimary () == primary)
			{
				ScoredSetting temp = ss[i];
				ss[i] = ss[nn];
				ss[nn++] = temp;
			}
		}

		return super.applyStrategy (ss, nn);
	}

	private static ScoredSetting find (ScoredSetting[] ss, int n, int primary)
	{
		for (int i = 0; i < n; ++i)
		{
			if (ss[i].fiveScore.getPrimary () == primary)
				return ss[i];
		}

		return null;
	}

	private static int count (ScoredSetting[] ss, int n, int primary)
	{
		int count = 0;

		for (int i = 0; i < n; ++i)
		{
			if (ss[i].fiveScore.getPrimary () == primary)
				++count;
		}

		return count;
	}

	/**
	 * Common table lookup code.
	 */
	private PokerScore indexTwoCard (String[][] table, int index1, int index2)
	{
		String str = table[getBankerPlayerIndex ()][index1];
		int strIndex = index2 * 3;
		if (strIndex + 1 >= str.length ())
			return null;

		// Get the minimum two-card hand required to set hand this way.
		int srank0 = Card.RankChars.indexOf (str.charAt (strIndex));
		int srank1 = Card.RankChars.indexOf (str.charAt (strIndex + 1));
		if (srank0 < 0 || srank1 < 0)
			return null;

		return PokerScore.makeHighCard (
			(byte) (MinRank + srank0), (byte) (MinRank + srank1));
	}
}
