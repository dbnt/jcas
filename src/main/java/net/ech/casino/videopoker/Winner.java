//
// Winner.java	
// 

package net.ech.casino.videopoker;

import net.ech.casino.Card;
import java.util.*;

/**
 * A Winner represents a winning hand combination.	A Winner encapsulates
 * the logic for matching a hand to a row on a payout chart.
 *
 * Class Winner also maps winning hand id to a Winner object.
 * The hand id can be the label from the pay table row.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public abstract class Winner implements Constants
{
	//===================================================================
	// Static interface
	//===================================================================

	/**
	 * Look up the winner with the given id.
	 * @return a Winner object, or null if not found.
	 */
	public static Winner lookup (String id)
	{
		// Check to see whether id is valid before invoking toUpperCase
		// and replace, both of which create new String objects unconditionally.
		//
		if (id.indexOf ('_') >= 0 || hasLowerCase (id))
			id = id.toUpperCase ().replace ('_', ' ');
		// Look for charged winners
		String chargeString = VideoPokerMachine.getChargeString(id);
		if (chargeString != null)
		{	 
			return new ChargedWinner ( (Winner) table.get (id.substring(chargeString.length()+1)));
		}	 
		return (Winner) table.get (id);
	}

	//===================================================================
	// Object interface
	//===================================================================

	/**
	 * Return true if the given hand matches this payout.
	 */
	public abstract boolean matches (HandInfo handInfo);

	/**
	 * Return a hand of this type.
	 */
	public abstract byte[] getRepresentativeHand ();


	//===================================================================
	// Implementation of lookup.
	//===================================================================

	private static boolean hasLowerCase (String str)
	{
		int len = str.length ();
		for (int i = 0; i < len; ++i)
		{
			if (Character.isLowerCase (str.charAt (i)))
				return true;
		}
		return false;
	}
  
	//
	// The lookup table.
	//
	private static Hashtable table = new Hashtable ();
	static
	{
		putFlushes ("SEQUENTIAL ROYAL", new SequentialRoyalFlushFactory ());
		putFlushes ("ROYAL FLUSH", new RoyalFlushFactory ());
		putFlushes ("STRAIGHT FLUSH", new StraightFlushFactory ());
		putFlushes ("FLUSH", new FlushFactory ());
		table.put ("ROYAL FLUSH WITH JOKER", new WildRoyal ());
		table.put ("ROYAL FLUSH WITH DEUCE", new WildRoyal ());
		table.put ("WILD ROYAL FLUSH", new WildRoyal ());
		table.put ("WILD ROYAL", new WildRoyal ());
		table.put ("LOW ROYAL FLUSH", new LowRoyalFlush ());
		table.put ("LOW ROYAL", new LowRoyalFlush ());
		table.put ("FIVE OF A KIND", new FiveOfAKind ());
		table.put ("5 OF A KIND", new FiveOfAKind ());
		table.put ("FOUR OF A KIND", new FourOfAKind ());
		table.put ("4 OF A KIND", new FourOfAKind ());
		table.put ("FULL HOUSE", new FullHouse ());
		table.put ("STRAIGHT", new Straight ());
		table.put ("THREE OF A KIND", new ThreeOfAKind ());
		table.put ("3 OF A KIND", new ThreeOfAKind ());
		table.put ("TWO PAIRS", new TwoPairs ());
		table.put ("TWO PAIR", new TwoPairs ());
		table.put ("JACKS OR BETTER", new Pair (Jack));
		table.put ("KINGS OR BETTER", new Pair (King));
		table.put ("TENS OR BETTER", new Pair (Ten));
		table.put ("ONE PAIR", new Pair (Deuce));
		table.put ("FOUR DEUCES", new WildHand (4));
		table.put ("FOUR WILD CARDS", new WildHand (4));
		table.put ("4 DEUCES", new WildHand (4));
		table.put ("4 WILD CARDS", new WildHand (4));
		table.put ("FIVE WILD CARDS", new WildHand (5));
		table.put ("5 WILD CARDS", new WildHand (5));
		table.put ("FOUR ACES", new FourOfAKind (Ace));
		table.put ("4 ACES", new FourOfAKind (Ace));
		table.put ("FOUR OF 2-4", new FourOfAKind (Deuce, Four));
		table.put ("FOUR FACES", new FourOfAKind (Jack, King));
		table.put ("FOUR JACKS", new FourOfAKind (Jack));
		table.put ("FOUR EIGHTS", new FourOfAKind (Eight));
		table.put ("4 EIGHTS", new FourOfAKind (Eight));
		table.put ("FOUR SEVENS", new FourOfAKind (Seven));
		table.put ("4 SEVENS", new FourOfAKind (Seven));
	};

	private static void putFlushes (String baseId, FlushFactory factory)
	{
		table.put (baseId, factory.create (NilSuit));
		table.put ("DIAMONDS " + baseId, factory.create (Diamonds));
		table.put ("CLUBS " + baseId, factory.create (Clubs));
		table.put ("HEARTS " + baseId, factory.create (Hearts));
		table.put ("SPADES " + baseId, factory.create (Spades));
	}

	private static class FlushFactory
	{
		public Winner create (byte suit)
		{
			return new Flush (suit);
		}
	}

	private static class StraightFlushFactory extends FlushFactory
	{
		public Winner create (byte suit)
		{
			return new StraightFlush (suit);
		}
	}

	private static class RoyalFlushFactory extends FlushFactory
	{
		public Winner create (byte suit)
		{
			return new RoyalFlush (suit);
		}
	}

	private static class SequentialRoyalFlushFactory extends FlushFactory
	{
		public Winner create (byte suit)
		{
			return new SequentialRoyalFlush (suit);
		}
	}
	
	// Used for charged table entries
	private static class ChargedWinner extends Winner
	{
		private Winner subWinner;
		
		public ChargedWinner (Winner type)
		{
			subWinner = type;
		}	 
		
		/**
		 * Logic for handling charged winners is in VideoPokerGame.java
		 */
		public boolean matches (HandInfo handInfo)
		{
			return false;
		}	 
		
		public byte[] getRepresentativeHand ()
		{
			return subWinner.getRepresentativeHand();
		}	 
	}
}

//====================================================================
// Winner classes.
//===================================================================

//
// Logic for detecting a winning pair.
//
class Pair extends Winner
{
	private byte minRank;

	public Pair (byte minRank)
	{
		this.minRank = minRank;
	}

	public boolean matches (HandInfo handInfo)
	{
		return (handInfo.getPairCount () > 0 &&
					handInfo.getHighPairRank () >= minRank) ||
			   (handInfo.getWildCount () > 0 &&
					handInfo.getHighRank () >= minRank);
	}

	public byte[] getRepresentativeHand ()
	{
		return new byte [] { Card.value (minRank, Hearts),
							 Card.value (minRank, Clubs),
							 Card.value (Three, Diamonds),
							 Card.value (Four, Diamonds),
							 Card.value (Five, Diamonds)};
	}
}

//
// Logic for detecting two pairs.
//
class TwoPairs extends Winner
{
	public boolean matches (HandInfo handInfo)
	{
		switch (handInfo.getWildCount ())
		{
		case 0:
			return handInfo.getPairCount () == 2 &&
				   handInfo.getTripleCount () == 0;
		case 1:
			return handInfo.getPairCount () == 1;
		default:
			return true;
		}
	}

	public byte[] getRepresentativeHand ()
	{
		return new byte [] { Card.value (Ace, Clubs),
							 Card.value (Ace, Spades),
							 Card.value (Three, Hearts),
							 Card.value (Three, Diamonds),
							 Card.value (Seven, Clubs) };
	}
}

//
// Logic for detecting three of a kind.
//
class ThreeOfAKind extends Winner
{
	public boolean matches (HandInfo handInfo)
	{
		switch (handInfo.getWildCount ())
		{
		case 0:
			return handInfo.getTripleCount () == 1;
		case 1:
			return handInfo.getPairCount () == 1;
		default:
			return true;
		}
	}

	public byte[] getRepresentativeHand ()
	{
		return new byte [] { Card.value (Ace, Clubs),
							 Card.value (Ace, Spades),
							 Card.value (Ace, Hearts),
							 Card.value (Queen, Clubs),
							 Card.value (Deuce, Clubs)};
	}
}

//
// Logic for detecting a full house.
//
class FullHouse extends Winner 
{
	public boolean matches (HandInfo handInfo)
	{
		return (handInfo.getPairCount () + handInfo.getWildCount () == 3) &&
			   handInfo.getQuadCount () == 0;
	}

	public byte[] getRepresentativeHand ()
	{
		return new byte [] { Card.value (Four, Clubs),
							 Card.value (Four, Spades),
							 Card.value (Four, Hearts),
							 Card.value (Three, Hearts),
							 Card.value (Three, Diamonds) };
	}
}

//
// Logic for detecting four of a kind.
//
class FourOfAKind extends Winner 
{
	private boolean ranged;
	private int loRank;
	private int hiRank;

	public FourOfAKind ()
	{
	}

	public FourOfAKind (int rank)
	{
		this (rank, rank);
	}

	public FourOfAKind (int loRank, int hiRank)
	{
		this.ranged = true;
		this.loRank = loRank;
		this.hiRank = hiRank;
	}

	public boolean matches (HandInfo handInfo)
	{
		switch (handInfo.getWildCount ())
		{
		case 0:
			if (handInfo.getQuadCount () == 0)
				return false;
			break;
		case 1:
			if (handInfo.getTripleCount () == 0)
				return false;
			break;
		case 2:
			if (handInfo.getPairCount () == 0)
				return false;
			break;
		case 3:
			break;
		case 4:
			if (ranged)
			{
				int rank = handInfo.getHighRank ();
				return rank >= loRank && rank <= hiRank;
			}
			return true;
		default:		// 5???
			return true;
		}

		if (ranged)
		{
			int rank = handInfo.getHighPairRank ();
			return rank >= loRank && rank <= hiRank;
		}

		return true;
	}

	public byte[] getRepresentativeHand ()
	{
		byte rank = ranged ? (byte)loRank : Five;
		return new byte [] { Card.value (rank, Clubs),
							 Card.value (rank, Spades),
							 Card.value (rank, Hearts),
							 Card.value (rank, Diamonds),
							 Card.value ((byte) ((rank + 1) % 13), Diamonds) };
	}
}

//
// Logic for detecting five of a kind.
// 
class FiveOfAKind extends Winner 
{
	public boolean matches (HandInfo handInfo)
	{
		return (handInfo.getPairCount () + handInfo.getWildCount ()) == 4;
	}

	public byte[] getRepresentativeHand ()
	{
		return new byte [] { Deuce,
							 Card.value (Three, Hearts),
							 Card.value (Three, Spades),
							 Card.value (Three, Clubs),
							 Card.value (Three, Diamonds) };
	}
}

//
// Logic for detecting a straight.
//
class Straight extends Winner
{
	public boolean matches (HandInfo handInfo)
	{
		return handInfo.isStraight ();
	}

	public byte[] getRepresentativeHand ()
	{
		return new byte [] { Card.value (Ace, Clubs),
							 Card.value (Deuce, Spades),
							 Card.value (Three, Hearts),
							 Card.value (Four, Hearts),
							 Card.value (Five, Diamonds) };
	}
}

//
// Logic for detecting a flush.
//
class Flush extends Winner 
{
	private byte suit;

	public Flush (byte suit)
	{
		this.suit = suit;
	}

	public boolean matches (HandInfo handInfo)
	{
		return handInfo.isFlush (suit);
	}

	public byte[] getRepresentativeHand ()
	{
		byte suit = this.suit == NilSuit ? Clubs : this.suit;
		return new byte [] { Card.value (Four, suit),
							 Card.value (Five, suit),
							 Card.value (Six, suit),
							 Card.value (Seven, suit),
							 Card.value (Jack, suit) };
	}
}

//
// Logic for detecting a straight flush.
//
class StraightFlush extends Winner 
{
	private byte suit;

	public StraightFlush (byte suit)
	{
		this.suit = suit;
	}

	public boolean matches (HandInfo handInfo)
	{
		return handInfo.isFlush (suit) && handInfo.isStraight ();
	}

	public byte[] getRepresentativeHand ()
	{
		byte suit = this.suit == NilSuit ? Clubs : this.suit;
		return new byte [] { Card.value (Four, suit),
							 Card.value (Five, suit),
							 Card.value (Six, suit),
							 Card.value (Seven, suit),
							 Card.value (Eight, suit) };
	}
}

//
// Logic for detecting a royal flush, with wild cards allowed.
//
class WildRoyal extends Winner 
{
	public boolean matches (HandInfo handInfo)
	{
		return handInfo.isStraight () &&
			   handInfo.isFlush () &&
			   handInfo.getLowRank () >= Ten;
	}

	public byte[] getRepresentativeHand ()
	{
		return new byte [] { Ten, Jack, Deuce, King, Ace };
	}
}

//
// Logic for detecting a natural royal flush.
//
class RoyalFlush extends Winner 
{
	private byte suit;

	public RoyalFlush (byte suit)
	{
		this.suit = suit;
	}

	public boolean matches (HandInfo handInfo)
	{
		return handInfo.getWildCount () == 0 &&
			   handInfo.isFlush (suit) &&
			   handInfo.getLowRank () == Ten;
	}

	public byte[] getRepresentativeHand ()
	{
		byte suit = this.suit == NilSuit ? Clubs : this.suit;
		return new byte [] { Card.value (Jack, suit),
							 Card.value (Queen, suit),
							 Card.value (King, suit),
							 Card.value (Ace, suit),
							 Card.value (Ten, suit) };
	}
}

//
// Logic for detecting a sequential royal flush.  No wild cards.
//
class SequentialRoyalFlush extends Winner 
{
	private byte suit;

	public SequentialRoyalFlush (byte suit)
	{
		this.suit = suit;
	}

	public boolean matches (HandInfo handInfo)
	{
		return handInfo.isFlush (suit) &&
			   handInfo.getRank (0) == Ace &&
			   handInfo.getRank (1) == King &&
			   handInfo.getRank (2) == Queen &&
			   handInfo.getRank (3) == Jack &&
			   handInfo.getRank (4) == Ten;
	}

	public byte[] getRepresentativeHand ()
	{
		byte suit = this.suit == NilSuit ? Clubs : this.suit;
		return new byte [] { Card.value (Ace, suit), 
							 Card.value (King, suit),
							 Card.value (Queen, suit),
							 Card.value (Jack, suit),
							 Card.value (Ten, suit) };
	}
}

//
// Logic for detecting a "low" royal flush.
//
class LowRoyalFlush extends Winner 
{
	public boolean matches (HandInfo handInfo)
	{
		return handInfo.getWildCount () == 0 &&
			   handInfo.isFlush () &&
			   handInfo.isStraight () &&
			   handInfo.getHighRank () == Six;
	}

	public byte[] getRepresentativeHand ()
	{
		return new byte [] { 0, 1, 2, 3, 4 };
	}
}

//
// Logic for detecting a certain number of wild cards in the hand.
//
class WildHand extends Winner 
{
	private int nwild;

	public WildHand (int nwild)
	{
		this.nwild = nwild;
	}

	public boolean matches (HandInfo handInfo)
	{
		return handInfo.getWildCount () == nwild;
	}

	public byte[] getRepresentativeHand ()
	{
		byte[] hand = new byte [nwild];
		byte suit = Diamonds;
		
		for (int i = 0; i < nwild; ++i)
			hand[i] = Card.value (Deuce, (byte) (suit++ % NumberOfSuits));

		for (int i = nwild; i < hand.length; ++i)
			hand[i] = Card.value ((byte) (Three + i), Spades);

		return hand;
	}
}
