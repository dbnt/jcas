//
// BJAnalysis.java
// 
// Given a standard 52-card deck, this program determines the probability
// of winning a double down for each combination of dealer exposed card and
// player starting score.
//
// author: Istina Mannino, imannino@acm.org
// June 16, 1999

package net.ech.casino.blackjack;

public class BJAnalysis
{
	public static void main(String argv[])
	{
		BJAnalysis bja = new BJAnalysis();
		bja.evaluateGames();
	}

	/**
	 * Print probabilities for various player scores and dealer's visible
	 * card.
	 */
	private void evaluateGames()
	{
		// Loop through all possible non-soft player starting scores. 
		for (int j = 4; j <= 14; j++) {
			System.out.println("Non-soft player score: " + j);
			// Loop through all possible dealer exposed cards.
			for (int i = 1; i <= 10; i++) {
				boolean dsoft = i == 1;

				int count = 0;
				double probsum = 0;
				for (int lowcard = Math.max(2, j - 10);
					 lowcard <= j / 2;
					 lowcard++) {  
					// cards remaining in the deck:
					//				 A	2  3  4	 5	6  7  8	 9	10,J,Q,K
					int[] undealt = {4, 4, 4, 4, 4, 4, 4, 4, 4, 16};	 
					undealt[i-1]--;
					undealt[lowcard-1]--;
					undealt[j-lowcard-1]--;
					int nums = (lowcard == j - lowcard) ? 1 : 2; 
					probsum += nums * playerPlay(undealt, j, i, false, dsoft);
					count += nums;
				}
				double probability = probsum / count;
				System.out.println("Dealer card: " + i +
								   " probability: "	 + probability);
			}
			System.out.println();
		}

		// Loop through all possible non-soft player starting scores. 
		for (int j = 2; j <= 8; j++) {
			System.out.println("Soft player score: " + (j + 10));
			int lowcard = j - 1;

			// Loop through all possible dealer exposed cards.
			for (int i = 1; i <= 10; i++) {
				boolean dsoft = i == 1;

				int count = 0;
				double probsum = 0;
				// cards remaining in the deck:
				//				 A	2  3  4	 5	6  7  8	 9	10,J,Q,K
				int[] undealt = {3, 4, 4, 4, 4, 4, 4, 4, 4, 16};	 
				undealt[i-1]--;
				undealt[lowcard-1]--;
				double probability = playerPlay(undealt, j, i, true, dsoft);
				System.out.println("Dealer card: " + i +
								   " probability: "	 + probability);
			}
			System.out.println();
		}
	}

	/**
	 * Deal cards to the dealer.
	 * Returns the probability of the player winning.  A push is treated as
	 * probability 0.5.
	 * undealt is how many of each kind of card remain in the deck
	 * pscore is the player's score (aces count as 1)
	 * dscore is the dealer's score (aces count as 1)
	 * dsoft is true if the dealer has an ace
	 */
	private double dealerPlay(int[] undealt, int bestpscore, int dscore, 
								 boolean dsoft, int ncards)
	{
		int bestdscore = bestScore(dscore, dsoft);

		if (ncards == 2 && bestdscore == 21) {
			// Dealer blackjacks.
			return 0;
		}
		else if (bestdscore > 21) {
			// Dealer busts.
			return 1;
		}
		else if (bestdscore < 17) {
			// Dealer hits.
			double probsum = 0;
			int count = 0;
			for (int i = 0; i < undealt.length; i++) {
				if (undealt[i] > 0) {
					undealt[i]--;
					double temp = dealerPlay(undealt, bestpscore,
											 dscore + (i + 1),
											 dsoft || i == 0, ncards + 1);
					undealt[i]++;
					probsum += temp * undealt[i];
					count += undealt[i];
				}
			}

			return probsum / count;
		}
		else {
			// Dealer stands.
			if (bestdscore > bestpscore)
				return 0;
			else if (bestdscore < bestpscore)
				return 1;
			else // Push
				return 0.5;
		}
	}

	/**
	 * Deal one card to the player in response to a double down.
	 * Returns the probability of the player winning.  A push is treated as
	 * probability 0.5.
	 * undealt is how many of each kind of card remain in the deck
	 * pscore is the player's score (aces count as 1)
	 * dscore is the dealer's score (aces count as 1)
	 * psoft is true if the player has an ace
	 * dsoft is true if the dealer has an ace
	 */
	private double playerPlay(int[] undealt, int pscore, int dscore, 
								 boolean psoft, boolean dsoft)
	{
		double probsum = 0;
		int count = 0;
		for (int i = 0; i < undealt.length; i++) {
			if (undealt[i] > 0) {
				undealt[i]--;
				int bestpscore = bestScore(pscore + i + 1, psoft || i == 0);
				double temp = 0.0;
				if (bestpscore <= 21)	// if player busts, it's a loss.
					temp = dealerPlay(undealt, bestpscore, dscore, dsoft, 1);
				undealt[i]++;
				probsum += temp * undealt[i];
				count += undealt[i];
			}
		}

		return probsum / count;
	}

	/**
	 * Return the best score for a given hand.	That is, if treating an
	 * ace as 11 will improve the hand (without busting), do so.
	 */
	private int bestScore(int score, boolean isSoft)
	{
		if (score <= 11 && isSoft)
			score += 10;
		return score;
	}

	/** Silly test function. */
	private void test()
	{
		int[] ud = {4, 3, 3, 4, 3, 4, 4, 4, 4, 15};
		System.out.println("dealerPlay="  + dealerPlay(ud, 15, 5, false, 1)); 
	}
}
