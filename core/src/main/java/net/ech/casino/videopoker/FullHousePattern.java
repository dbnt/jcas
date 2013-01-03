package net.ech.casino.videopoker;

/**
 * Logic for detecting a full house.
 */
public class FullHousePattern
	implements HandPattern
{
	@Override
	public boolean matches (HandInfo handInfo)
	{
		int nranks = 0;
		int maxCount = 0;
		for (int r = NUMBER_OF_RANKS; --r >= 0; ) {
			int rc = handInfo.getRankCount(r);
			if (rc > 0) {
				++nranks;
				maxCount = Math.max(maxCount, rc);
			}
		}
		return nranks <= 2 && maxCount < 4;
	}
}
