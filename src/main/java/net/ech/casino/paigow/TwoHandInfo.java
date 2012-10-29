//
// TwoHandInfo.java	 
// 

package net.ech.casino.paigow;

import net.ech.casino.PokerScore;

/**
 * A TwoHandInfo object is the digested pai gow poker high hand.
 * 
 * @author Istina Mannino, imannino@pacificnet.net
 */
public class TwoHandInfo extends PGHandInfo 
{
	//
	// Constructor.
	//
	public TwoHandInfo(byte[] byteHand)
	{
		super(byteHand, 2);
	}

	public PokerScore evaluateTwoHand()
	{
		if (isPair()) {
			// System.out.println("Your two card hand is one pair.");
			return PokerScore.makePair((byte) getHighPairRank());
		}
		else {
			// System.out.println("Your two card hand is " + (getHighRank()) + 
			//					 " high.");
			byte[] ascending = getSortedBytes();			
			return PokerScore.makeHighCard(ascending[1], ascending[0]);
		}
	}

	public static void main (String[] args)
	{
		try
		{
			byte[] cards = new byte [2];
			int cardCount = 0;
			for (int i = 0; i < args.length; ++i)
			{
				String arg = args[i];
				for (int cx = 0; cx < arg.length (); cx += 2)
				{
					char rankChar = arg.charAt (cx);
					char suitChar = arg.charAt (cx + 1);
					if (rankChar == 'j' && suitChar == 'o')
					{
						cards[cardCount++] = Joker;
					}
					else
					{
						int rank = "23456789TJQKA".indexOf (rankChar);
						if (rank < 0)
							throw new Exception ("Bad rank " + rankChar);
						int suit = "DCHS".indexOf (suitChar);
						if (suit < 0)
							throw new Exception ("Bad suit " + suitChar);
						cards[cardCount++] = (byte) ((suit << 4) | rank);
					}
				}
			}
			if (cardCount != 2)
				throw new Exception ("Not enough cards.");
 
			TwoHandInfo tinfo = new TwoHandInfo (cards);
			System.out.println ("score=" + tinfo.evaluateTwoHand ());
		}
		catch (Exception e)
		{
			e.printStackTrace (System.err);
			System.exit (1);
		}
	}
}
