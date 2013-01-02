package net.ech.casino.videopoker;

/**
 * Logic for detecting a royal flush.
 */
public class RoyalFlushPattern
	extends StraightFlushPattern
	implements HandPattern
{
	private boolean wildPermitted;

	public RoyalFlushPattern()
	{
	}

	public RoyalFlushPattern(String suits)
	{
		super(suits);
	}

	public RoyalFlushPattern(boolean wildPermitted)
	{
		this.wildPermitted = wildPermitted;
	}

	public RoyalFlushPattern(String suits, boolean wildPermitted)
	{
		super(suits);
		this.wildPermitted = wildPermitted;
	}

	@Override
	public boolean matches (HandInfo handInfo)
	{
		return (wildPermitted || handInfo.getWildCount == 0) &&
			super.matches(handInfo) &&
			handInfo.getRankCount(0, 8) == 0;
	}
}
