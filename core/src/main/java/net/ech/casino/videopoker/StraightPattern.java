package net.ech.casino.videopoker;

/**
 * Logic for detecting a straight.
 */
public class StraightPattern
	implements HandPattern
{
	@Override
	public boolean matches (HandInfo handInfo)
	{
		return handInfo.isStraight ();
	}
}
