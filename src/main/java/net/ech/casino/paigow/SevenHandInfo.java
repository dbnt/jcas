//
// SevenHandInfo.java  
// 

package net.ech.casino.paigow;

/**
 * A SevenHandInfo object is a complete digested pai gow poker hand.
 * 
 * @author Istina Mannino, imannino@pacificnet.net
 */
public class SevenHandInfo extends PGHandInfo 
{
	//
	// Constructor.
	//
	public SevenHandInfo(byte[] byteHand)
	{
		super(byteHand, 7);
	}

	public boolean isThreePairs()
	{
		return getPairCount() == 3 && getTripleCount() == 0;
	}

	public boolean isTwoThreeOfAKind()
	{
		return getTripleCount() == 2;
	}

	public boolean isFlush()
	{
		if (super.isFlush())
			return true;

		for (int i = 0; i < NumberOfSuits; i++) {
			int suitCount = getSuitCount(i);
			if (suitCount >= 5 || (suitCount == 4 && getWildCount() == 1))
				return true;
		}

		return false;
	}
}
