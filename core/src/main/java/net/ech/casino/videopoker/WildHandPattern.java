package net.ech.casino.videopoker;

/**
 * Logic for detecting a certain number of wild cards in the hand.
 */
public class WildHandPattern
	implements HandPattern
{
	private int cardinality;

	public WildHand (int cardinality)
	{
		this.cardinality = cardinality;
	}

	public boolean matches (HandInfo handInfo)
	{
		return handInfo.getWildCount () == cardinality;
	}
}
