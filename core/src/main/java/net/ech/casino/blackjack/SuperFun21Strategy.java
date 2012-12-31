//
// SuperFun21Strategy.java	
// 

package net.ech.casino.blackjack;

/**
 * Wizard of Odds' basic strategy for Super Fun 21.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class SuperFun21Strategy extends Strategy
{
	/**
	 * Choose the next move.  Never called at deal time.
	 */
	public int nextMove (BlackjackGame game)
	{
		PlayerHand playerHand = game.getCurrentPlayerHand();
		int dealerRank = game.getRankOfExposedDealerCard();

		if (game.isSplitOk() && shouldSplit (playerHand, dealerRank))
			return Split;

		if (game.isSurrenderOk() && shouldSurrender (playerHand, dealerRank))
			return Surrender;

		if (game.isDoubleDownOk() && shouldDouble (playerHand, dealerRank))
			return DoubleDown;

		if (game.isHitOk() && shouldHit (playerHand, dealerRank))
			return Hit;

		if (shouldStand (playerHand, dealerRank))
			return Stand;

		// Default: hit up to 17, hit soft 17.
		if (game.isHitOk() &&
			(playerHand.getScore() < 17 ||
			(playerHand.getScore() == 17 && playerHand.isSoft())))
		{
			return Hit;
		}

		return Stand;
	}

	private boolean shouldSplit (PlayerHand playerHand, int dealerRank)
	{
		int splitRank = playerHand.getCardRank(0);
		if (splitRank == 8 || splitRank == Ace)
			return true;
		
		switch (splitRank)
		{
		case 2:
		case 3:
		case 6:
			return dealerRank >= 2 && dealerRank <= 7;
		case 4:
			return dealerRank >= 5 && dealerRank <= 6;
		case 7:
			return dealerRank >= 2 && dealerRank <= 8;
		case 9:
			return dealerRank != 7 && dealerRank < 10;
		}

		return false;
	}

	private boolean shouldSurrender (PlayerHand playerHand, int dealerRank)
	{
		int playerScore = playerHand.getScore();

		// Surrender after double if dealer shows 8-A and score is 12-16.
		if (playerHand.isDoubled())
		{
			return playerScore >= 12 && playerScore <= 16 &&
				(dealerRank == Ace || dealerRank >= 8);
		}

		if (dealerRank != Ace && dealerRank < 10)
			return false;

		if (playerHand.isSoft())
			return false;

		int playerHandSize = playerHand.getSize();

		switch (playerScore)
		{
		case 14:
			// Surrender a pair of 7s against the dealer's 10 or A.
			return playerHandSize == 2 && playerHand.getCardRank(0) == 7;
		case 15:
			// Surrender a 15 against the dealer's A unless 4 or more cards.
			return dealerRank == Ace && playerHandSize < 4;
		case 16:
			// Surrender a 16 against the dealer's 10 or A
			// unless 4 or more cards.
			return playerHandSize < 4;
		case 17:
			// Surrender a 17 (!) against the dealer's A unless 5 or more
			// cards.
			return dealerRank == Ace && playerHandSize < 5;
		}

		return false;
	}

	private boolean shouldDouble (PlayerHand playerHand, int dealerRank)
	{
		if (playerHand.isSoft())
		{
			switch (playerHand.getScore())
			{
			case 13:
				return dealerRank == 5 || dealerRank == 6;
			case 14:
				return dealerRank == 5 || dealerRank == 6 ||
					(dealerRank == 4 && playerHand.getSize() < 3);
			case 15:
			case 16:
				return ((dealerRank == 5 || dealerRank == 6) &&
					playerHand.getSize() < 4) ||
					(dealerRank == 4 && playerHand.getSize() < 3);
			case 17:
				return ((dealerRank == 2 || dealerRank == 3) &&
					playerHand.getSize() < 3) ||
					((dealerRank == 4 || dealerRank == 5) &&
					playerHand.getSize() < 4) ||
					((dealerRank == 6) && playerHand.getSize() < 5);
			case 18:
				return ((dealerRank >= 3 && dealerRank <= 5) &&
					playerHand.getSize() < 6) ||
					((dealerRank == 6) && playerHand.getSize() < 5);
			case 19:
				return (dealerRank == 6) && playerHand.getSize() < 5;
			}
		}
		else
		{
			switch (playerHand.getScore())
			{
			case 9:
				return (dealerRank == 2 && playerHand.getSize() < 3) ||
					((dealerRank == 3 || dealerRank == 4) &&
					playerHand.getSize() < 4) ||
					dealerRank == 5 || dealerRank == 6;
			case 10:
				return (dealerRank >= 10 && dealerRank <= King &&
					playerHand.getSize() < 3) || playerHand.getSize() < 4;
			case 11:
				return playerHand.getSize() < 4;
			}
		}

		return false;
	}

	//
	// Return true if the game is in one of those states where the Wizard's
	// strategy says to hit, while simple blackjack strategy would say no.
	//
	private boolean shouldHit (PlayerHand playerHand, int dealerRank)
	{
		if (playerHand.isSoft())
		{
			// Cases where it's better to hit a high soft hand.
			switch (playerHand.getScore())
			{
			case 18:
				switch (dealerRank)
				{
				case 2:
					return playerHand.getSize() >= 3;
				case 3:
				case 4:
				case 5:
				case 7:
				case 8:
					return playerHand.getSize() >= 4;
				case 6:
					return playerHand.getSize() >= 5;
				}
				break;
			case 19:
				return playerHand.getSize() >= 5 ||
					(playerHand.getSize() == 4 && dealerRank >= 10);
			case 20:
				return playerHand.getSize() >= 5;
			}
		}
		else
		{
			// This one seems a bit odd.
			// Hit a hard 17 if the hand contains 5 or more cards and 
			// the dealer is showing 10 - Ace ??

			return playerHand.getScore() == 17 &&
				playerHand.getSize() >= 5 &&
				(dealerRank == Ace || dealerRank >= 10);
		}

		return false;
	}

	//
	// Return true if the game is in one of those states where the Wizard's
	// strategy says to stand, while simple blackjack strategy would say no.
	//
	private boolean shouldStand (PlayerHand playerHand, int dealerRank)
	{
		if (dealerRank >= 2 && dealerRank <= 6)
		{
			switch (playerHand.getScore())
			{
			case 12:
				return dealerRank >= 4 && playerHand.getSize() < 4;
			case 13:
				return playerHand.getSize() < 4 || 
					(dealerRank >= 4 && playerHand.getSize() < 5);
			case 14:
				return playerHand.getSize() >= 5;
			case 15:
				return dealerRank >= 4 || playerHand.getSize() < 5;
			case 16:
				return true;
			}
		}

		return false;
	}
}
