package net.ech.casino.videopoker;

/**
 * Logic for detecting two pairs.
 */
public class TwoPairsPattern
	implements HandPattern
{
	@Override
	public boolean matches (HandInfo handInfo)
	{
		int availableWilds = handInfo.getWildCount();
		int pairCount = 0;

		for (int r = NUMBER_OF_RANKS; --r >= 0; ) {
			int rc = handInfo.getRankCount(r);
			if (rc + availableWilds >= 2) {
				++pairCount;
				availableWilds -= Math.max(2 - rc, 0);
			}
		}

		return pairCount == 2;
	}
}
