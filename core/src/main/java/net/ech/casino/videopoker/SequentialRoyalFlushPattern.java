package net.ech.casino.videopoker;

/**
 * Logic for detecting a sequential royal flush: 10-J-Q-K-A in order.
 * Wild cards are not permitted.
 */
public class SequentialRoyalFlushPattern
	extends FlushPattern
	implements HandPattern
{
	public SequentialRoyalFlushPattern()
	{
	}

	public SequentialRoyalFlushPattern(String suits)
	{
		super(suits);
	}

	@Override
	public boolean matches (HandInfo handInfo)
	{
		return super.matches(handInfo) && handInfo.getHand().matches("T.J.Q.K.A.");
	}
}
