package net.ech.casino.videopoker;

/**
 * Logic for detecting four of a kind plus a specific kicker.
 * Wild cards need not apply.
 */
public class FourAndKickerPattern
	implements HandPattern
{
	private String primaryRanks;
	private String kickerRanks;

	public FourAndKickerPattern (String primaryRanks, String kickerRanks)
	{
		this.primaryRanks = primaryRanks;
		this.kickerRanks = kickerRanks;
	}

	@Override
	public boolean matches (HandInfo handInfo)
	{
		boolean foundQuad = false, foundKicker = false;
		for (int r = NUMBER_OF_RANKS; --r >= 0; ) {
			int rc = handInfo.getRankCount(r);
			if (primaryRanks.indexOf(RANK_CHARS[r]) >= 0 && rc == 4) {
				foundQuad = true;
			}
			else if (kickerRanks.indexOf(RANK_CHARS[r]) >= 0 && rc == 1) {
				foundKicker = true;
			}
		}
		return foundQuad && foundKicker;
	}
}
