//
// HandInfo.java  
// 

package net.ech.casino.videopoker;

import net.ech.casino.Card;

/**
 * A HandInfo object is a digested video poker hand.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class HandInfo implements Constants
{
	private byte[] hand;
	private int wildCount;
	private int lowRank = -1;
	private int highRank = -1;
	private int highPairRank = -1;
	private int[] tupleCounts = new int [3];
	private byte flushSuit = NilSuit;
	private boolean straightFlag;
	private boolean deucesWild;

	//
	// Constructor.	 This pre-computes the results of all the queries.
	//
	public HandInfo (byte[] hand, boolean deucesWild)
	{
		// Save a copy of the original hand.
		this.hand = hand;

		int[] ranks = new int [CardsInHand];
		int rankCount = 0;
		this.deucesWild = deucesWild;
		//
		// Rank the cards, sorting out the wild cards.
		// Detect flush.
		//
		boolean isFlush = true;
		byte firstSuit = NilSuit;
		for (int i = 0; i < CardsInHand; ++i)
		{
			if (!isWild (hand[i]))
			{
				int rank = Card.rankOf (hand[i]);
				ranks[rankCount] = rank;
				++rankCount;

				byte suit = Card.suitOf (hand[i]);
				if (firstSuit == NilSuit)
				{
					firstSuit = suit;
				}
				else if (suit != firstSuit)
				{
					isFlush = false;
				}
			}
		}
		if (isFlush)
		{
			this.flushSuit = firstSuit;
		}

		//
		// Sort the ranks.
		//
		for (int i = 1; i < rankCount; ++i)
		{
			for (int j = 1; j < rankCount; ++j)
			{
				if (ranks[j - 1] > ranks[j])
				{
					int temp = ranks[j - 1];
					ranks[j - 1] = ranks[j];
					ranks[j] = temp;
				}
			}
		}

		//
		// Remember number of wild cards, high and low ranks.
		//
		this.wildCount = CardsInHand - rankCount;
		if (rankCount > 0)
		{
			this.lowRank = ranks[0];
			this.highRank = ranks[rankCount - 1];
		}

		//
		// Count natural pairs, triples, quads.
		// Remember the rank of the high pair.
		//
		for (int sep = 3; sep > 0; --sep)
		{
			for (int i = 0; (i + sep) < rankCount; ++i)
			{
				if (ranks[i] == ranks[i + sep])
				{
					this.tupleCounts[sep - 1] += 1;
					this.highPairRank = ranks[i];
				}
			}
		}

		//
		// Check for straight.
		// Any pair eliminates the possibility of a straight.
		//
		if (tupleCounts[0] == 0)
		{
			this.straightFlag = 
				(rankCount == 0) ||						// all wild
				(highRank - lowRank < CardsInHand) ||	// normal straight
				(highRank == Ace &&				// ace-low straight
					 (rankCount == 1 || ranks[rankCount - 2] <= Five));
		}
	}
	
	public boolean isWild (byte card)
	{
		return card == Joker || (deucesWild && Card.rankOf (card) == Deuce);
	}

	//
	// The queries.
	//

	public int getValue (int index)
	{
		return hand[index];
	}

	public int getRank (int index)
	{
		return Card.rankOf (hand[index]);
	}

	public int getSuit (int index)
	{
		return Card.suitOf (hand[index]);
	}

	public int getWildCount ()
	{
		return wildCount;
	}

	public int getLowRank ()
	{
		return lowRank;
	}

	public int getHighRank ()
	{
		return highRank;
	}

	public int getHighPairRank ()
	{
		return highPairRank;
	}

	public int getPairCount ()
	{
		return tupleCounts[0];
	}

	public int getTripleCount ()
	{
		return tupleCounts[1];
	}

	public int getQuadCount ()
	{
		return tupleCounts[2];
	}

	public boolean isFlush ()
	{
		return isFlush (NilSuit);
	}

	public boolean isFlush (byte ofThisSuit)
	{
		return ofThisSuit == NilSuit ? (flushSuit != NilSuit)
									 : (flushSuit == ofThisSuit);
	}

	public boolean isStraight ()
	{
		return straightFlag;
	}
}
