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
		int availableWilds = handInfo.getWildCount();
		boolean gotTriple = false;
		boolean gotPair = false;

		for (int r = NUMBER_OF_RANKS; --r >= 0; ) {
			int rc = handInfo.getRankCount(r);
			switch (rc + availableWilds) {
			case 5:
			case 4:
			case 3:
				gotTriple = true;
				availableWilds -= Math.max(3 - rc, 0);
				break;
			case 2:
				gotPair = true;
				availableWilds -= 2 - rc;
			}
		}

		return gotTriple && gotPair;
	}
}
