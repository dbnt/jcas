package net.ech.casino.videopoker;

/**
 * Logic for detecting a flush.
 */
public class FlushPattern
	implements HandPattern 
{
	private String suits;

	public FlushPattern()
	{
		this(SUITS_STRING);
	}

	public FlushPattern(String suits)
	{
		this.suits = suits;
	}

	@Override
	public boolean matches (HandInfo handInfo)
	{
		String hs = handInfo.getSuits();
		return hs.length() == 1 && suits.indexOf(hs.charAt(0)) >= 0;
	}
}
