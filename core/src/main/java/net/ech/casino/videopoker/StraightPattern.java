package net.ech.casino.videopoker;

/**
 * Logic for detecting a straight.
 */
public class Straight extends Winner
{
	@Override
	public boolean matches (HandInfo handInfo)
	{
		return handInfo.isStraight ();
	}
}
