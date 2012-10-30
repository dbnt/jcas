//
// PokerScore.java	
// 

package net.ech.casino;

/**
 * A PokerScore represents the grade of a poker hand.  Only the
 * information related to scoring is included.	A PokerScore
 * can be compared to other PokerScores, and can be formatted for
 * presentation to the player.
 * 
 * @version 1.0
 * @author James Echmalian, ech@ech.net
 */
public class PokerScore
	implements CardConstants, java.io.Serializable, java.lang.Cloneable
{
	/**
	 * Primary hand type codes.
	 * High card hand is represented by the rank of the high card.
	 */
	public final static byte FiveOfAKind		= 100;
	public final static byte StraightFlush		= 90;	// subsumes Royal
	public final static byte FourOfAKind		= 80;
	public final static byte FullHouse			= 70;
	public final static byte Flush				= 60;
	public final static byte Straight			= 50;
	public final static byte ThreeOfAKind		= 40;
	public final static byte TwoPair			= 30;
	public final static byte Pair				= 20;

	// A hand score is effectively just an array of bytes in decreasing
	// order of significance!
	//
	private byte primary;
	private byte rank;
	private byte kicker1 = (byte) -1;
	private byte kicker2 = (byte) -1;
	private byte kicker3 = (byte) -1;

	//===================================================================
	// Factory methods.
	//===================================================================

	/**
	 * Factory method for a 5 of a kind.
	 */
	public static PokerScore makeFiveOfAKind (byte rank)
	{
		return new PokerScore (FiveOfAKind, rank);
	}

	/**
	 * Factory method for a straight (or royal) flush.
	 */
	public static PokerScore makeStraightFlush (byte highRank)
	{
		PokerScore ps = new PokerScore (StraightFlush, highRank);
		ps.setKickers (highRank - 1, highRank - 2, highRank - 3);
		return ps;
	}

	/**
	 * Factory method for an ace-low straight flush.  This is
	 * required for Pai Gow, in which the ace-low straight flush
	 * is ranked below an ace-high but above all other straight
	 * flushes.
	 */
	public static PokerScore makeAceLowStraightFlush ()
	{
		PokerScore ps = new PokerScore (StraightFlush, Ace);
		ps.setKickers (Five, Four, Three);
		return ps;
	}

	/**
	 * Factory method for a 4 of a kind.
	 */
	public static PokerScore makeFourOfAKind (byte rank)
	{
		return new PokerScore (FourOfAKind, rank);
	}

	/**
	 * Factory method for a full house.
	 * @param tripleRank		The three of a kind rank
	 * @param pairRank			The rank of the pair
	 */
	public static PokerScore makeFullHouse (byte tripleRank, byte pairRank)
	{
		PokerScore ps = new PokerScore (FullHouse, tripleRank);
		ps.kicker1 = pairRank;
		return ps;
	}

	/**
	 * Factory method for a flush.
	 */
	public static PokerScore makeFlush (byte r1, byte r2, byte r3, byte r4)
	{
		PokerScore ps = new PokerScore (Flush, r1);
		ps.setKickers (r2, r3, r4);
		return ps;
	}

	/**
	 * Factory method for a straight.
	 */
	public static PokerScore makeStraight (byte highRank)
	{
		PokerScore ps = new PokerScore (Straight, highRank);
		ps.setKickers (highRank - 1, highRank - 2, highRank - 3);
		return ps;
	}

	/**
	 * Factory method for an ace-low straight.	This is
	 * required for Pai Gow, in which the ace-low straight 
	 * is ranked below an ace-high but above all other straights.
	 */
	public static PokerScore makeAceLowStraight ()
	{
		PokerScore ps = new PokerScore (Straight, Ace);
		ps.setKickers (Five, Four, Three);
		return ps;
	}

	/**
	 * Factory method for a 3 of a kind.
	 */
	public static PokerScore makeThreeOfAKind (byte rank)
	{
		return new PokerScore (ThreeOfAKind, rank);
	}

	/**
	 * Factory method for a two pair.
	 * Ranks must be in descending order.
	 */
	public static PokerScore makeTwoPair (byte rank1, byte rank2, byte kicker)
	{
		PokerScore ps = new PokerScore (TwoPair, rank1);
		ps.kicker1 = rank2;
		ps.kicker2 = kicker;
		return ps;
	}

	/**
	 * Factory method for a pair (no kickers or kickers irrelevant).
	 */
	public static PokerScore makePair (byte rank)
	{
		return new PokerScore (Pair, rank);
	}

	/**
	 * Factory method for a pair.
	 * Kickers must be in descending order.
	 */
	public static PokerScore makePair (byte rank, byte kicker1, byte kicker2,
									   byte kicker3)
	{
		PokerScore ps = new PokerScore (Pair, rank);
		ps.setKickers (kicker1, kicker2, kicker3);
		return ps;
	}

	/**
	 * Factory method for a high card hand. (pai gow low hand)
	 */
	public static PokerScore makeHighCard (byte rank, byte kicker)
	{
		return new PokerScore (rank, kicker);
	}

	/**
	 * Factory method for a high card hand.
	 * Kickers must be in descending order.
	 */
	public static PokerScore makeHighCard (byte rank, byte kicker1,
										   byte kicker2, byte kicker3,
										   byte kicker4)
	{
		PokerScore ps = new PokerScore (rank, kicker1);
		ps.setKickers (kicker2, kicker3, kicker4);
		return ps;
	}

	//===================================================================

	/**
	 * Constructor is private.	Use a factory method.
	 */
	private PokerScore (byte primary, byte rank)
	{
		this.primary = primary;
		this.rank = rank;
	}

	private void setKickers (int k1, int k2, int k3)
	{
		kicker1 = (byte) k1;
		kicker2 = (byte) k2;
		kicker3 = (byte) k3;
	}

	/**
	 * Get the primary type of hand.
	 */
	public byte getPrimary ()
	{
		return primary;
	}

	/** 
	 * Get a rank.
	 */
	public byte getRank (int index)
	{
		if (primary < Pair) --index;
		switch (index)
		{
		case -1:
			return primary;
		case 0:
			return rank;
		case 1:
			return kicker1;
		default:
			throw new ArrayIndexOutOfBoundsException (index);
		}
	}

	/**
	 * Clone me.
	 */
	public Object clone ()
	{
		try 
		{
			return (PokerScore) super.clone ();
		}
		catch (CloneNotSupportedException e)
		{ 
			// Should not happen, since this class is Cloneable
			throw new InternalError ();
		}
	}

	//===================================================================
	// Comparison methods
	//===================================================================

	/**
	 * Compare this score to another.
	 */
	public boolean equals (Object that)
	{
		if (that instanceof PokerScore)
		{
			PokerScore thatScore = (PokerScore) that;
			return thatScore.primary == primary &&
				   thatScore.rank == rank &&
				   thatScore.kicker1 == kicker1 &&
				   thatScore.kicker2 == kicker2 &&
				   thatScore.kicker3 == kicker3;
		}

		return false;
	}

	/**
	 * Compare this score to another.
	 * @param that		the object to compare to this object 
	 * @exception ClassCastException if that is not a PokerScore
	 */
	public int compareTo (Object that)
	{
		PokerScore thatScore = (PokerScore) that;
		int diff = primary - thatScore.primary;
		if (diff != 0) return diff;
		diff = rank - thatScore.rank;
		if (diff != 0) return diff;
		diff = kicker1 - thatScore.kicker1;
		if (diff != 0) return diff;
		diff = kicker2 - thatScore.kicker2;
		if (diff != 0) return diff;
		return kicker3 - thatScore.kicker3;
	}

	/**
	 * Tell where the difference lies between two scores.
	 * @return a detail number suitable for passing to the format method
	 */
	public int howDiffers (PokerScore that)
	{
		if (primary != that.primary)
			return 1;
		if (rank != that.rank)
			return 2;
		if (kicker1 != that.kicker1)
			return 3;
		if (kicker2 != that.kicker2)
			return 4;
		if (kicker3 != that.kicker3)
			return 5;
		return 0;
	}

	//===================================================================
	// Formatting methods
	//===================================================================

	/**
	 * Produce a dump of this score for debugging.
	 */
	public String dump ()
	{
		return "[" + primary + " " + rank + " " + kicker1 + " " +
				kicker2 + " " + kicker3 + "]";
	}

	/**
	 * Produce a human-readable String representation of this score.
	 */
	public String toString ()
	{
		return format (0);
	}

	/**
	 * Produce a human-readable String representation of this score.
	 * @param detail  One of the following values:<dl>
	 *				  <dt>0</dt><dd>default detail level</dd>
	 *				  <dt>1</dt><dd>show only primary score type</dd>
	 *				  <dt>2</dt><dd>show type and rank</dd>
	 *				  <dt>3</dt><dd>show type, rank and kicker</dd>
	 *				  <dt>4</dt><dd>show type, rank and two kickers</dd>
	 *				  <dt>5</dt><dd>show type, rank and all kickers</dd>
	 *</dl>
	 */
	public String format (int detail)
	{
		switch (primary)
		{
		case FiveOfAKind:
			return formatTuple ("five");
		case StraightFlush:
			if (rank == Ace)
				return "royal flush";
			else
				return formatStraight ("straight flush", detail);
		case FourOfAKind:
			return formatTuple ("four");
		case FullHouse:
			return formatFullHouse (detail);
		case Flush:
			return formatFlush (detail);
		case Straight:
			return formatStraight ("straight", detail);
		case ThreeOfAKind:
			return formatTuple ("three");
		case TwoPair:
			return formatTwoPair (detail);
		case Pair:
			return formatPair (detail);
		default:
			return formatHighCard (detail);
		}
	}

	private String formatStraight (String title, int detail)
	{
		if (rank == Ace && kicker1 == Five)
			return "ace-low " + title;

		if (detail <= 1)
			return title;

		return title + ", " + rankString (rank) + " high";
	}

	private String formatFlush (int detail)
	{
		StringBuilder buf = new StringBuilder ();
		buf.append ("flush");
		if (detail > 1)
		{
			buf.append (", ");
			buf.append (rankString (rank));
			if (detail > 2)
			{
				buf.append (", ");
				buf.append (rankString (kicker1));
			}
			if (detail > 3)
			{
				buf.append (", ");
				buf.append (rankString (kicker2));
			}
			if (detail > 4)
			{
				buf.append (", ");
				buf.append (rankString (kicker3));
			}
			buf.append (" high");
		}

		return buf.toString();
	}

	private String formatFullHouse (int detail)
	{
		if (detail == 1)
			return "full house";

		StringBuilder buf = new StringBuilder ();
		appendRankString (rank, true, buf);
		buf.append (" full of ");
		appendRankString (kicker1, true, buf);
		return buf.toString ();
	}

	private String formatTwoPair (int detail)
	{
		StringBuilder buf = new StringBuilder ();

		appendRankString (rank, true, buf);
		buf.append (" over ");
		appendRankString (kicker1, true, buf);

		if (detail >= 4)
		{
			buf.append (", ");
			appendRankString (kicker2, false, buf);
			buf.append (" kicker");
		}

		return buf.toString ();
	}

	private String formatTuple (String prefix)
	{
		StringBuilder buf = new StringBuilder ();
		buf.append (prefix);
		buf.append (' ');
		appendRankString (rank, true, buf);
		return buf.toString ();
	}

	private String formatPair (int detail)
	{
		StringBuilder buf = new StringBuilder ();

		buf.append ("pair of ");
		appendRankString (rank, true, buf);

		if (detail == 0)
			detail = defaultDetail ();

		if (detail >= 3)
		{
			buf.append (", ");
			appendRankString (kicker1, false, buf);

			if (detail >= 4)
			{
				buf.append (", ");
				appendRankString (kicker2, false, buf);
			}

			if (detail >= 5)
			{
				buf.append (", ");
				appendRankString (kicker3, false, buf);
			}

			buf.append (" kicker");
		}

		if (detail >= 4)
			buf.append ('s');

		return buf.toString ();
	}

	private String formatHighCard (int detail)
	{
		StringBuilder buf = new StringBuilder ();

		appendRankString (primary, false, buf);
		buf.append (" high");

		if (detail == 0)
			detail = defaultDetail ();

		if (detail >= 2)
		{
			buf.append (", ");
			appendRankString (rank, false, buf);

			if (detail >= 3 && kicker1 >= 0)
			{
				buf.append (", ");
				appendRankString (kicker1, false, buf);

				if (detail >= 4 && kicker2 >= 0)
				{
					buf.append (", ");
					appendRankString (kicker2, false, buf);

					if (detail >= 5 && kicker3 >= 0)
					{
						buf.append (", ");
						appendRankString (kicker3, false, buf);
					}
				}
			}

			buf.append (" kicker");
			if (detail >= 3 && kicker1 >= 0)
				buf.append ('s');
		}

		return buf.toString ();
	}

	private int defaultDetail ()
	{
		if (kicker3 > Ten)
			return 5;
		if (kicker2 > Ten)
			return 4;
		if (kicker1 > Ten)
			return 3;
		//if (rank > Ten)
			return 2;
		//return 1;
	}

	private void appendRankString (byte rank, boolean plural, StringBuilder buf)
	{
		buf.append (rankString (rank));
		if (plural)
		{
			if (rank == Six)
				buf.append ('e');
			buf.append ('s');
		}
	}

	private String rankString (byte rank)
	{
		switch (rank)
		{
		case Deuce:
			return "deuce";
		case Three:
			return "trey";
		case Four:
			return "four";
		case Five:
			return "five";
		case Six:
			return "six";
		case Seven:
			return "seven";
		case Eight:
			return "eight";
		case Nine:
			return "nine";
		case Ten:
			return "ten";
		case Jack:
			return "jack";
		case Queen:
			return "queen";
		case King:
			return "king";
		case Ace:
			return "ace";
		}

		return null;
	}

	//===================================================================
	// Test code.
	//===================================================================

	public static void main (String[] args)
	{
		PokerScore fiveAces = makeFiveOfAKind (Ace);
		PokerScore royalFlush = makeStraightFlush (Ace);
		PokerScore aceLoStrFlush = makeAceLowStraightFlush ();
		PokerScore fourAces = makeFourOfAKind (Ace);
		PokerScore fullHouse = makeFullHouse (Ace, Ten);
		PokerScore flush = makeFlush (Ace, King, Eight, Deuce);
		PokerScore straight = makeStraight (Ace);
		PokerScore aceLowStraight = makeAceLowStraight ();
		PokerScore threeOfAKind = makeThreeOfAKind (Ace);
		PokerScore twoPair = makeTwoPair (Ace, Ten, Eight);
		PokerScore barePair = makePair (Ace);
		PokerScore acePair = makePair (Ace, Queen, Ten, Eight);
		PokerScore twoCards = makeHighCard (Ace, Eight);
		PokerScore aceHigh = makeHighCard (Ace, King, Queen, Ten, Eight);

		// Test primary comparison
		report (fiveAces, fiveAces);
		report (aceLoStrFlush, aceLoStrFlush);
		report (fourAces, fourAces);
		report (fullHouse, fullHouse);
		report (flush, flush);
		report (aceLowStraight, aceLowStraight);
		report (threeOfAKind, threeOfAKind);
		report (twoPair, twoPair);
		report (barePair, barePair);
		report (acePair, acePair);
		report (twoCards, twoCards);
		report (aceHigh, aceHigh);

		report (fiveAces, royalFlush);
		report (aceLoStrFlush, fourAces);
		report (fourAces, fullHouse);
		report (fullHouse, flush);
		report (flush, straight);
		report (aceLowStraight, threeOfAKind);
		report (threeOfAKind, twoPair);
		report (twoPair, acePair);
		report (acePair, aceHigh);
		report (barePair, twoCards);

		// Test secondary comparison
		report (royalFlush, aceLoStrFlush);
		report (straight, aceLowStraight);
		report (fiveAces, makeFiveOfAKind (Queen));
		report (fourAces, makeFourOfAKind (Queen));
		report (fullHouse, makeFullHouse (Eight, Deuce));
		report (makeFlush (Ace, King, Ten, Six),
				makeFlush (Ace, Ten, Six, Five));
		report (makeFlush (Ace, King, Six, Deuce),
				makeFlush (Ace, King, Five, Deuce));
		report (makeFlush (Ace, King, Ten, Six),
				makeFlush (Ace, King, Ten, Five));
		report (aceLowStraight, makeStraight (King));
		report (threeOfAKind, makeThreeOfAKind (Four));
		report (twoPair, makeTwoPair (Nine, Eight, King));
		report (acePair, makePair (King, Jack, Eight, Seven));
		report (twoCards, makeHighCard (Ace, Nine));
		report (aceHigh, makeHighCard (Ace, King, Queen, Ten, Seven));
	}

	private static void report (PokerScore score1, PokerScore score2)
	{
		if (score1.compareTo (score2) < 0)
		{
			PokerScore temp = score1;
			score1 = score2;
			score2 = temp;
		}
		String verb = score1.equals (score2) ? "TIES" : "BEATS";
		int detail = score1.howDiffers (score2);
		System.out.println (score1.format (detail) + " " + verb + " " +
							score2.format (detail));
	}
}
