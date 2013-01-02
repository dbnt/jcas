package net.ech.casino.videopoker;

/**
 * Logic for detecting a straight flush.
 */
public class StraightFlushPattern
	extends FlushPattern
	implements HandPattern 
{
	public StraightFlushPattern()
	{
	}

	public StraightFlushPattern(String suits)
	{
		super(suits);
	}

	@Override
	public boolean matches (HandInfo handInfo)
	{
		return super.matches(handInfo) && handInfo.isStraight();
	}
}
