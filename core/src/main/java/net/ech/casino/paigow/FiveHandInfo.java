//
// FiveHandInfo.java  
// 

package net.ech.casino.paigow;

import net.ech.casino.PokerScore;

/**
 * A FiveHandInfo object is the digested pai gow poker high hand.
 * 
 * @author Istina Mannino, imannino@pacificnet.net
 */
public class FiveHandInfo extends PGHandInfo 
{
	//
	// Constructor.
	//
	public FiveHandInfo(byte[] byteHand)
	{
		super(byteHand, 5);
	}

	public PokerScore evaluateFiveHand()
	{
		byte highRank = (byte) getHighRank();
		byte[] ranks = getSortedBytes();
		int highCardIndex = ranks.length - 1;

		if (isFiveAces()) {
			// System.out.println("Your five card hand is five aces.");
			return PokerScore.makeFiveOfAKind(Ace);
		}
		else if (isRoyalFlush()) {
			// System.out.println("Your five card hand is a royal flush.");
			return PokerScore.makeStraightFlush(Ace);
		}
		else if (isAceLowSF()) {
			// System.out.println("Your five card hand is an Ace-low straight flush.");
			return PokerScore.makeAceLowStraightFlush();
		}
		else if (isStraightFlush()) {
			// System.out.println("Your five card hand is a straight flush.");
			return PokerScore.makeStraightFlush(highRank);
		}
		else if (isFourOfAKind()) {
			// System.out.println("Your five card hand is four of a kind.");
			return PokerScore.makeFourOfAKind((byte) getQuadRank());
		}
		else if (isFullHouse()) {
			// System.out.println("Your five card hand is a full house.");
			return PokerScore.makeFullHouse((byte) getTripleRank(), 
											(byte) getHighPairRank());
		}
		else if (isFlush()) {
			// System.out.println("Your five card hand is a flush.");
			return PokerScore.makeFlush(ranks[0], ranks[1], ranks[2], ranks[3]);
		}
		else if (isAceLowStraight()) {
			// System.out.println("Your five card hand is an Ace-low straight.");
			return PokerScore.makeAceLowStraight();
		}
		else if (isStraight()) {
			// System.out.println("Your five card hand is a straight.");
			return PokerScore.makeStraight(highRank);
		}
		else if (isThreeOfAKind()) {
			// System.out.println("Your five card hand is three of a kind.");
			return PokerScore.makeThreeOfAKind((byte) getTripleRank());
		}
		else if (isTwoPair()) {
			// System.out.println("Your five card hand is two pairs.");
			// Find highest and second highest card that aren't
			// part of the pairs.
			byte highPair = (byte) getHighPairRank();
			byte lowPair = (byte) getLowPairRank();

			// Find the position of the pair cards.
			int hpos2 = 0;
			for (int i = 0; i < ranks.length - 1; i++) {
				if (ranks[i] == ranks[i + 1]) {
					if (ranks[i] == highPair)
						hpos2 = i + 1;
				}
			}
			byte rank1 = -1;
			if (highPair == ranks[highCardIndex]) {
				if (lowPair != ranks[hpos2 - 3])
					rank1 = ranks[hpos2 - 2];
				else
					rank1 = ranks[hpos2 - 4];
			}
			else if (highPair == ranks[highCardIndex - 1])
				rank1 = ranks[highCardIndex];

			return PokerScore.makeTwoPair(highPair, lowPair, rank1);
		}
		else if (isPair()) {
			// System.out.println("Your five card hand is one pair.");
			// Find the position of the pair cards.
			int pos2 = 0;
			byte pairRank = -1;
			for (int i = 0; i < ranks.length - 1; i++) {
				if (ranks[i] == ranks[i + 1]) {
					pairRank = ranks[i];
					pos2 = i + 1;
				}
			}

			// Find highest and second highest card that aren't
			// part of the pair.
			byte rank1;
			byte rank2;
			byte rank3; 
			if (ranks[pos2] == ranks[highCardIndex]) {
				rank1 = ranks[pos2 - 2];
				rank2 = ranks[pos2 - 3];
				rank3 = ranks[pos2 - 4];
			}
			else if (ranks[pos2] == ranks[highCardIndex - 1]) {
				rank1 = ranks[highCardIndex];
				rank2 = ranks[pos2 - 2];
				rank3 = ranks[pos2 - 3];
			}
			else {
				rank1 = ranks[highCardIndex];
				rank2 = ranks[highCardIndex - 1];
				if (pos2 == highCardIndex - 2)
					rank3 = ranks[pos2 - 2];
				else
					rank3 = ranks[highCardIndex - 2];
			}
			return PokerScore.makePair(pairRank, rank1, rank2, rank3);
		}
		else {
			// System.out.println("Your five card hand is " + highRank + " high.");
			byte[] ascending = getSortedBytes();
			return PokerScore.makeHighCard(ascending[4], ascending[3],
										   ascending[2], ascending[1],
										   ascending[0]);
		}
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
					char rankChar = arg.charAt (cx);
					int rank = "23456789TJQKA".indexOf (rankChar);
					if (rank < 0)
						throw new Exception ("Bad rank " + rankChar);
					char suitChar = arg.charAt (cx + 1);
					int suit = "DCHS".indexOf (suitChar);
					if (suit < 0)
						throw new Exception ("Bad suit " + suitChar);
					cards[cardCount++] = (byte) ((suit << 4) | rank);
				}
			}
			if (cardCount != 5)
				throw new Exception ("Not enough cards.");
 
			FiveHandInfo finfo = new FiveHandInfo (cards);
			System.out.println ("score=" + finfo.evaluateFiveHand ());
		}
		catch (Exception e)
		{
			e.printStackTrace (System.err);
			System.exit (1);
		}
	}
}
