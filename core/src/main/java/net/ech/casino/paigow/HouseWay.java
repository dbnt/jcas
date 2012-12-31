//
// HouseWay.java  
// 

package net.ech.casino.paigow;

import net.ech.casino.*;
import net.ech.casino.PokerScore;

/**
 * Class HouseWay implements common-sense rules for setting hands,
 * and lets the subclass add strategy.
 * 
 * @version 1.1
 * @author James Echmalian, ech@ech.net
 */
public class HouseWay implements Constants
{
	private boolean banker;
	private boolean working;

	// Temporary hack, until this class and WongWay get better structured.
	protected byte[] hand;

	/**
	 * Constructor.
	 */
	public HouseWay ()
	{
	}

	/**
	 * Constructor.
	 */
	public HouseWay (boolean banker)
	{
		this.banker = banker;
	}

	/**
	 * Get the banker flag.
	 */
	public boolean isBanker ()
	{
		return banker;
	}

	/**
	 * Set this hand the "house" way.
	 */
	public void set (byte[] hand)
	{
		if (working)
		{
			throw new RuntimeException ("HouseWay is not synchronized");
		}

		working = true;
		try
		{
			this.hand = hand;
			work();
		}
		finally
		{
			working = false;
		}
	}

	private void work ()
	{
		// Make a list of all possible settings.
		Setting[] settings = Setting.getAllSettings ();
		ScoredSetting[] scoredSettings =
			new ScoredSetting [settings.length];

		// Score each setting.	Keep only the non-foul settings.
		int n = 0;
		for (int i = 0; i < settings.length; ++i)
		{
			byte[] setHand = settings[i].set (hand);
			PokerScore fiveScore = HandInfo.score5 (setHand, 0);
			PokerScore twoScore = HandInfo.score2 (setHand, CardsInFiveHand);
			if (fiveScore.compareTo (twoScore) > 0)
				scoredSettings[n++] = new ScoredSetting (settings[i], 
														  fiveScore,
														  twoScore);
		}
		if (n == 0)
			throw new RuntimeException ("No non-foul settings!!!");

		// Purge "dominated" settings.	A dominated setting is one that
		// another setting beats or equals in both hands.
		//
		for (int i = 0; i < n; ++i)
		{
			ScoredSetting si = scoredSettings[i];
			if (si == null)
				continue;

			for (int j = i + 1; j < n; ++j)
			{
				ScoredSetting sj = scoredSettings[j];
				if (sj == null)
					continue;

				int cmp5 = sj.fiveScore.compareTo (si.fiveScore);
				int cmp2 = sj.twoScore.compareTo (si.twoScore);
				if (cmp5 >= 0 && cmp2 >= 0)
				{
					scoredSettings[i] = null;
					break;
				}
				if (cmp5 <= 0 && cmp2 <= 0)
				{
					scoredSettings[j] = null;
				}
			}
		}

		// Compaction.
		scoredSettings = compact(scoredSettings);
		if (scoredSettings.length == 0)
			throw new RuntimeException ("All settings dominated!!!");

		ScoredSetting finalSetting =
			scoredSettings.length == 1 ? scoredSettings[0] : applyStrategy (scoredSettings);

		byte[] finalHand = finalSetting.set (hand);

		// Final touch: sort the hand by descending rank.
		sortHand (finalHand, finalSetting.fiveScore);

		System.arraycopy (finalHand, 0, hand, 0, CardsInHand);
	}

	/**
	 * Of all usable settings, pick the one to use.  Assume that dominated settings have been removed.
	 *
	 * Subclass must override this method to apply more sophisticated
	 * strategy.
	 */
	protected ScoredSetting applyStrategy (ScoredSetting[] ss)
	{
		// Default strategy: pick the setting with the highest two-card hand.
		return best2Hand(ss);
	}

	protected static ScoredSetting best2Hand(ScoredSetting[] ss)
	{
		if (ss.length == 0) return null;
		int best = 0;
		for (int i = 1; i < ss.length; ++i)
		{
			if (ss[i].twoScore.compareTo (ss[best].twoScore) > 0)
				best = i;
		}

		return ss[best];
	}

	/**
	 * Move non-null entries to the left of the scored setting array.
	 * Return the number.
	 */
	protected static ScoredSetting[] compact(ScoredSetting[] scoredSettings)
	{
		int n = 0;
		for (int i = 0; i < scoredSettings.length; ++i)
		{
			if (scoredSettings[i] != null)
				scoredSettings[n++] = scoredSettings[i];
		}

		if (n == scoredSettings.length) return scoredSettings;

		ScoredSetting[] result = new ScoredSetting[n];
		for (int i = 0; i < n; ++i)
		{
			result[i] = scoredSettings[i];
		}
		return result;
	}

	/**
	 * Sort a 5/2 hand.
	 */
	private static void sortHand (byte[] hand, PokerScore fiveScore)
	{
		// Bubble it for starters.
		for (int pass = 0; pass < CardsInFiveHand - 1; ++pass)
			for (int i = 0; i < CardsInFiveHand - 1; ++i)
				sortPair (hand, i);

		// Sort the twohand too.
		sortPair (hand, CardsInFiveHand);

		// Fix up the order of straights.
		switch (fiveScore.getPrimary ())
		{
		case PokerScore.Straight:
		case PokerScore.StraightFlush:
			if (fiveScore.equals (PokerScore.makeAceLowStraight ()) ||
				fiveScore.equals (PokerScore.makeAceLowStraightFlush ()))
			{
				// It's	   A-5-4-3-2 
				// Make it 5-4-3-2-A 
				byte prev = hand[0];
				for (int i = CardsInFiveHand; --i >= 0; )
				{
					byte temp = hand[i];
					hand[i] = prev;
					prev = temp;
				}
			}
			// Insert joker into proper place in straight.
			else if (hand[0] == Joker)
			{
				for (int i = 1; i < CardsInFiveHand - 1; ++i)
				{
					if (Card.rankOf (hand[i]) - Card.rankOf (hand[i + 1]) != 1)
					{
						reposJoker (hand, i);
						break;
					}
				}

				// Joker can stay up top unless there is an ace.
				if (hand[0] == Joker && Card.rankOf (hand[1]) == Ace)
					reposJoker (hand, CardsInFiveHand - 1);
			}
		}
	}

	private static void sortPair (byte[] hand, int i)
	{
		int r0 = getSortRank (hand[i]);
		int r1 = getSortRank (hand[i + 1]);

		if (r0 < r1 || r0 == r1 && Card.suitOf (hand[i]) < Card.suitOf (hand[i+1]))
		{
			byte temp = hand[i];
			hand[i] = hand[i + 1];
			hand[i + 1] = temp;
		}
	}

	private static int getSortRank (byte card)
	{
		return card == Joker ? (Ace + 1) : Card.rankOf (card);
	}

	private static void reposJoker (byte[] hand, int dest)
	{
		// Slide cards to the left to make room.
		for (int i = 0; i < dest; ++i)
			hand[i] = hand[i + 1];
		hand[dest] = Joker;
	}

	/**
	 * Helper class.
	 */
	protected static class ScoredSetting extends Setting
	{
		PokerScore fiveScore;
		PokerScore twoScore;

		ScoredSetting (Setting setting, PokerScore fiveScore,
					   PokerScore twoScore)
		{
			super (setting);
			this.fiveScore = fiveScore;
			this.twoScore = twoScore;
		}
	}

	protected boolean handContainsJoker ()
	{
		for (int i = 0; i < hand.length; ++i)
		{
			if (hand[i] == Joker)
				return true;
		}
		return false;
	}
}
