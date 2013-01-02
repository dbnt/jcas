package net.ech.casino.videopoker;

/**
 * A hand pattern matcher.
 */
public interface HandPattern
{
	/**
	 * Return true if the given hand matches this pattern.
	 */
	public boolean matches (HandInfo handInfo);
}
