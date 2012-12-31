//
// Strategy.java  
// 

package net.ech.casino.blackjack;

/**
 * Encapsulation of blackjack strategy.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class Strategy implements Constants
{
	/**
	 * Factory.
	 */
	public static Strategy forName (String name)
		throws Exception
	{
		if (name != null)
		{
			if (name.equalsIgnoreCase ("bdd"))
				return new BetterDoubleDownStrategy ();
			if (name.equalsIgnoreCase ("ldd"))
				return new LessDoubleDownStrategy ();
			return (Strategy) Class.forName("net.ech.casino.blackjack." + name).newInstance();
		}

		// Default, "by the book":
		return new Strategy ();
	}

	/**
	 * What should we bet on this deal?
	 */
	public int nextBet (BlackjackGame game)
	{
		// Default: don't vary bet.
		return 1;
	}

	/**
	 * Where it happens.
	 */
	public int nextMove (BlackjackGame game)
	{
		int dealerRank = game.getRankOfExposedDealerCard ();
		PlayerHand phand = game.getCurrentPlayerHand ();
		int playerScore = phand.getScore ();

		if (game.isDoubleDownOk () && shouldDoubleDown (game))
			return DoubleDown;
		
		if (game.isSplitOk () && shouldSplit (game))
			return Split;

		if (game.isHitOk ())
		{
			int[] table = phand.isSoft () ? softHitTable : hitTable;
			if (playerScore < table[dealerRank])
				return Hit;
		}

		return Stand;
	}

	//
	// Hitting a non-soft hand:
	// If dealer is showing:	Hit while score is under:
	// 7 - A					17
	// 4 - 6					12
	// 3						13
	// 2						14
	//
	private int[] hitTable =
	{
		0, 17, 14, 13, 12, 12, 12, 17, 17, 17, 17, 17, 17, 17
	};

	//
	// Hitting a soft hand:
	// If dealer is showing:	Hit while score is under:
	// 10						18
	// other					17
	//
	private int[] softHitTable =
	{
		0, 17, 17, 17, 17, 17, 17, 17, 17, 17, 18, 18, 18, 18
	};

	//
	// Doubling down:
	// If dealer is showing...	  Player doubles down on...
	// 2 - 9					  10 - 11
	// 5 - 6					  9 or soft 13-18
	//
	protected boolean shouldDoubleDown (BlackjackGame game)
	{
		PlayerHand phand = game.getCurrentPlayerHand ();
		int playerScore = phand.getScore ();
		int dealerRank = game.getRankOfExposedDealerCard ();

		if (phand.isSoft ())
		{
			if (playerScore >= 13 && playerScore <= 18 &&
				dealerRank >= 5 && dealerRank <= 6)
				return true;
		}
		else
		{
			switch (playerScore)
			{
			case 9:
				if (dealerRank >= 5 && dealerRank <= 6)
					return true;
				break;
			case 10:
			case 11:
				if (dealerRank >= 2 && dealerRank <= 9)
					return true;
			}
		}

		return false;
	}

	//
	// Splitting.
	// If dealer is showing:  Player splits a pair of:
	// 2 - 7				  2, 3, 6, 7
	// 5, 6, 8, 9			  9
	// any but 10			  8
	// any					  A
	//
	protected boolean shouldSplit (BlackjackGame game)
	{
		int dealerRank = game.getRankOfExposedDealerCard ();

		PlayerHand phand = game.getCurrentPlayerHand ();
		switch (phand.getCardRank (0))
		{
		case Ace:
			return true;
		case 2:
		case 3:
		case 6:
		case 7:
			return dealerRank >= 2 && dealerRank <= 7;
		case 9:
			switch (dealerRank)
			{
			case 5:
			case 6:
			case 8:
			case 9:
				return true;
			}
			break;
		case 8:
			return dealerRank < 10;
		}

		return false;
	}
}

class BetterDoubleDownStrategy extends Strategy 
{
	//
	// Incorporate the results of Istina's analysis into the double-down
	// strategy.
	// If player has...			  Double down if dealer has...
	// 8						  4, 5, 6
	// 9						  2 - 8
	// 10, 11					  2 - 9
	// soft 12 - 15				  3 - 6
	// soft 16					  4 - 6
	// soft 17, 18				  2 - 7
	//
	protected boolean shouldDoubleDown (BlackjackGame game)
	{
		PlayerHand phand = game.getCurrentPlayerHand ();
		int playerScore = phand.getScore ();
		int dealerRank = game.getRankOfExposedDealerCard ();

		if (phand.isSoft ())
		{
			if (playerScore >= 12 && playerScore <= 15 &&
				dealerRank >= 3 && dealerRank <= 6)
				return true;
			if (playerScore == 16 &&
				dealerRank >= 4 && dealerRank <= 6)
				return true;
			if (playerScore >= 17 && playerScore <= 18 &&
				dealerRank >= 2 && dealerRank <= 7)
				return true;
		}
		else
		{
			switch (playerScore)
			{
			case 8:
				if (dealerRank >= 4 && dealerRank <= 6)
					return true;
				break;
			case 9:
				if (dealerRank >= 2 && dealerRank <= 8)
					return true;
				break;
			case 10:
			case 11:
				if (dealerRank >= 2 && dealerRank <= 9)
					return true;
			}
		}

		return false;
	}
}

class LessDoubleDownStrategy extends Strategy 
{
	//
	// Test Al's theory about restricting double down only on 
	// a player's 9, 10, 11.
	//
	protected boolean shouldDoubleDown (BlackjackGame game)
	{
		PlayerHand phand = game.getCurrentPlayerHand ();
		int playerScore = phand.getScore ();
		int dealerRank = game.getRankOfExposedDealerCard ();

		if (!phand.isSoft ())
		{
			switch (playerScore)
			{
			case 9:
				if (dealerRank >= 2 && dealerRank <= 8)
					return true;
				break;
			case 10:
			case 11:
				if (dealerRank >= 2 && dealerRank <= 9)
					return true;
			}
		}

		return false;
	}
}
